package seng202.team3.data.database;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages data reading and parsing from CSV files
 * 
 * @author Harrison Tyson
 * @version 1.0.0, Aug 22
 */
public class CsvInterpreter implements DataManager {

    private final String defaultFilepath = "src/main/resources/";

    @Override
    public List<Object> readData(Query query, Class<?> objectToInterpretAs)
            throws IOException {
        List<Object> data = new ArrayList<>();
        // Initialize File
        String filepath;
        if (query.getSource().contains("\\") || query.getSource().contains("/")) {
            filepath = query.getSource();
        } else {
            filepath = defaultFilepath + query.getSource() + ".csv";
        }
        FileReader file = new FileReader(filepath);

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
            if (builder.getCapturedExceptions().indexOf(e) != 0) {
                errorMessage += "\n";
            }

            errorMessage += String.format("CSV error reading file %s.csv on line %d: ",
                    query.getSource(), e.getLineNumber());
            switch (e.getClass().getSimpleName()) { // Custom error lines for internal exceptions
                case "CsvRequiredFieldEmptyException": // Missing required field
                    errorMessage += String.format("%s",
                            e.getMessage());
                    break;

                case "CsvDataTypeMismatchException": // Field is incorrect data type
                    CsvDataTypeMismatchException exception = (CsvDataTypeMismatchException) e;
                    errorMessage += String.format(
                            "Value (%s) could not be converted to Type (%s)",
                            exception.getSourceObject(),
                            exception.getDestinationClass().getSimpleName());
                    break;

                default:
                    errorMessage += e.getMessage();
                    break;

            }
        }

        // Throw error if error message has been populated
        if (errorMessage != "") {
            throw new IOException(errorMessage);
        }

        return data;
    }
}
