package seng202.team3.data.database;

import com.opencsv.bean.BeanVerifier;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.javatuples.Triplet;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.Entity;

/**
 * Manages data reading and parsing from CSV files
 *
 * @author Harrison Tyson
 * @version 1.3.0, Aug 22
 */
public class CsvInterpreter implements DataReader {

    /**
     * Unused constructor
     */
    public CsvInterpreter() {
        // Unused constructor
    }

    /**
     * Helper function to read in a csv file object
     * 
     * @param filename name of file to read
     * @return csv file object
     * 
     * @throws IOException if file cannot be read successfully
     */
    private InputStreamReader readFile(String filename) throws IOException {
        // Initialize File
        if (!filename.startsWith("/")) {
            filename = "/" + filename + ".csv";
        }
        return new InputStreamReader(getClass().getResourceAsStream(filename));
    }

    /**
     * Helper function to check if non lethal exceptions occured during parsing
     * 
     * @param filename   name of the file for feedback messages
     * @param exceptions list of exceptions to compile and throw
     * 
     * @throws IOException if an error occurs while parsing the file
     */
    private void checkForProcessingErrors(String filename, List<CsvException> exceptions)
            throws IOException {

        if (exceptions.isEmpty()) { // Exit if no exceptions
            return;
        }

        // Compile messages from non lethal errors to send back
        String errorMessage = "";
        for (CsvException e : exceptions) {
            if (exceptions.indexOf(e) != 0) {
                errorMessage += "\n";
            }
            // Add file name and line to message
            errorMessage += String.format("CSV error reading file %s.csv on line %d: ",
                    filename, e.getLineNumber());

            // Switch is used here so other cases can be handled easily without refactor
            switch (e.getClass().getSimpleName()) { // Custom error lines for internal exceptions
                case "CsvDataTypeMismatchException": // Field is incorrect data type
                    CsvDataTypeMismatchException exception = (CsvDataTypeMismatchException) e;
                    errorMessage += String.format(
                            "Value (%s) could not be converted to Type (%s)",
                            exception.getSourceObject(),
                            exception.getDestinationClass().getSimpleName());
                    break;

                default: // Missing required field
                    errorMessage += e.getMessage();
                    break;
            }
        }

        // Throw error if error message has been populated
        throw new IOException(errorMessage);
    }

    /**
     * Imports the chargers in the specified file into the database
     * Currently only used in testing
     * 
     * @param filename filename to import into the database
     * @throws IOException if the read/write operation fails
     */
    public void importChargersToDatabase(String filename) throws IOException {
        SqlInterpreter.getInstance().writeCharger(new ArrayList<>(readChargers(filename)));
    }

    /**
     * Reads in chargers from a user specified file
     * Currently only used in testing
     * 
     * @param filename file to read from
     * @return list of chargers that have been read
     * @throws IOException if read/write operation fails
     */
    public List<Entity> readChargers(String filename) throws IOException {
        List<Entity> data;

        // Initialize the raw data to object converter
        CsvToBean<Entity> builder = new CsvToBeanBuilder<Entity>(
                readFile(filename))
                .withThrowExceptions(false) // ignore exceptions to handle later
                .withType(Charger.class)
                .build();

        // Convert the data
        try {
            data = builder.parse();
        } catch (Exception e) {
            // Lethal errors - most likely missing headers. Convert all errors to
            // IOException to be handled by higher layer/displayed to user
            throw new IOException(e.getCause().getMessage());
        }

        // Throws exception if any data is invalid
        checkForProcessingErrors(filename, builder.getCapturedExceptions());

        return data;
    }

    /** {@inheritDoc} */
    @Override
    public List<Entity> readData(Query query)
            throws IOException {
        List<Entity> data;

        // Initialize the raw data to object converter
        CsvToBean<Entity> builder = new CsvToBeanBuilder<Entity>(
                readFile(query.getSource().getAsCsv()))
                .withThrowExceptions(false) // ignore exceptions to handle later
                .withType(query.getSource().getAsClass())
                .build();

        // Interpret filters for CSV
        ArrayList<BeanVerifier<Entity>> csvFilters = new ArrayList<>();
        switch (query.getSource()) {
            case CHARGER: // Convert to charger filter
                for (Triplet<String, String, ComparisonType> filter : query.getFilters()) {
                    csvFilters.add(new ChargerFilter(filter.getValue0(), // field
                            filter.getValue1(), // criteria
                            filter.getValue2())); // comparison method
                }
                break;
            case CONNECTOR:
                break; // Not implemented
            case JOURNEY:
                break; // Not implemented
            case USER:
                break; // Not implemented
            case VEHICLE:
                break; // Not implemented
            default:
                break;
        }

        // Apply filters
        builder.setVerifiers(csvFilters);

        // Convert the data
        try {
            data = builder.parse();
        } catch (IllegalStateException e) {
            // Lethal errors - most likely missing headers. Convert all errors to
            // IOException to be handled by higher layer/displayed to user
            throw new IOException(e.getCause().getMessage());
        }

        // Throws exception if any data is invalid
        checkForProcessingErrors(query.getSource().getAsCsv(), builder.getCapturedExceptions());

        return data;
    }
}
