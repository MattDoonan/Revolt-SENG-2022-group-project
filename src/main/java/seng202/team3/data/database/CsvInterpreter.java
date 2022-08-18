package seng202.team3.data.database;

import com.opencsv.bean.CsvToBeanBuilder;
import java.io.FileReader;
import java.io.IOException;
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
        return new CsvToBeanBuilder<Object>(new FileReader(filepath + query.toString() + ".csv"))
                .withType(objectToInterpretAs)
                .build()
                .parse();

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
