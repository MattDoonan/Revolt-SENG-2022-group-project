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

    /** {@inheritDoc} */
    @Override
    public List<Object> readData(Query query, Class<?> objectToInterpretAs)
            throws IOException {
        List<Object> data;

        // Initialize the raw data to object converter
        CsvToBean<Object> builder = new CsvToBeanBuilder<Object>(
                readFile(query.getSource()))
                .withThrowExceptions(false) // ignore exceptions to handle later
                .withType(objectToInterpretAs)
                .build();

        // Interpret filters for CSV
        ArrayList<BeanVerifier<Object>> csvFilters = new ArrayList<>();
        switch (objectToInterpretAs.getSimpleName()) {
            case "Charger": // Convert to charger filter
                for (Triplet<String, String, ComparisonType> filter : query.getFilters()) {
                    csvFilters.add(new ChargerFilter(filter.getValue0(), // field
                            filter.getValue1(), // criteria
                            filter.getValue2())); // comparison method
                }
                break;
            default:
                break;
        }

        // Apply filters
        builder.setVerifiers(csvFilters);

        // Convert the data
        try {
            data = builder.parse(); // Throws a Throwable on error, not Exception
        } catch (Throwable e) {
            // Lethal errors - most likely missing headers. Convert all errors to
            // IOException to be handled by higher layer/displayed to user
            e = e.getCause();
            throw new IOException(e.getMessage());
        }

        // Throws exception if any data is invalid
        checkForProcessingErrors(query.getSource(), builder.getCapturedExceptions());

        return data;
    }
}
