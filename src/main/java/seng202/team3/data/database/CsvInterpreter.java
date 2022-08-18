package seng202.team3.data.database;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import seng202.team3.data.entity.Charger;

/**
 * Manages data reading and parsing from CSV files
 * 
 * @author Harrison Tyson
 * @version 1.0.0, Aug 22
 */
public class CsvInterpreter implements DataManager {

    private final String filepath = "src/main/resources/";

    @Override
    public List<Object> readData(Query query, Class<?> objectToInterpretAs)
            throws IOException {
        List<Object> data = new ArrayList<>();
        // Initialize File
        FileReader file = new FileReader(filepath + query.getSource() + ".csv");

        // Initialize the raw data to object converter
        CsvToBean<Object> builder = new CsvToBeanBuilder<Object>(file)
                .withThrowExceptions(false) // ignore exceptions to handle later
                .withType(objectToInterpretAs)
                .build();
        // Convert the data
        try {
            data = builder.parse();
        } catch (Throwable e) { // Lethal errors - most likely missing headers
            e = e.getCause();
            throw new IOException(e.getMessage());
        }

        // Compile messages from non lethal errors to send back
        String errorMessage = "";
        for (CsvException e : builder.getCapturedExceptions()) {
            switch (e.getClass().getSimpleName()) { // Custom error lines for internal exceptions
                case "CsvRequiredFieldEmptyException": // Missing required field
                    errorMessage += String.format("CSV error reading file %s.csv on line %d: %s\n",
                            query.getSource(), e.getLineNumber(), e.getMessage());
                    break;

                case "CsvDataTypeMismatchException": // Field is incorrect data type
                    errorMessage += String.format(
                            "CSV error reading file %s.csv on line %d: Value (%s) could not be converted to Type (%s)",
                            query.getSource(), e.getLineNumber(),
                            ((CsvDataTypeMismatchException) e).getSourceObject(),
                            ((CsvDataTypeMismatchException) e).getDestinationClass().getSimpleName());
                    break;

                default:
                    errorMessage += e.getMessage() + "\n";
                    break;

            }
        }

        // Throw error if error message has been populated
        if (errorMessage != "") {
            throw new IOException(errorMessage);
        }

        return data;
    }

    /**
     * Example usage for csv-interpreter
     * TODO: Currently using for testing - eventually remove
     */
    public static void main(String[] args) throws IOException {
        List<Object> test = new CsvInterpreter().readData(
                new QueryBuilderImpl()
                        .withSource("charger")
                        .build(),
                Charger.class);
        test.forEach(System.out::println);
    }

}
