package seng202.team3.data.database;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import seng202.team3.data.entity.Charger;

/**
 * Testing suite for CSV importing and parsing
 * 
 * @author Harrison Tyson
 * @version 1.0.0, Aug 22
 */
public class CsvInterpreterTest {

    /**
     * Check that IOException is thrown if filepath does not exist
     */
    @Test
    public void invalidFilePathTest() {
        assertThrows(IOException.class, () -> {
            Query query = new QueryBuilderImpl()
                    .withSource("NonExistentFilePath")
                    .build();
            new CsvInterpreter().readData(query, Charger.class);
        });
    }

}
