package seng202.team3.unitTest.data.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team3.data.database.CsvInterpreter;
import seng202.team3.data.database.Query;
import seng202.team3.data.database.QueryBuilderImpl;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.Connector;
import seng202.team3.data.entity.Coordinate;

/**
 * Testing suite for CSV importing and parsing
 * 
 * @author Harrison Tyson, Michelle Hsieh
 * @version 1.0.1, Aug 22
 */
public class CsvInterpreterTest {
    /**
     * Sets up a functional version of CsvInterpreter Query object
     * and List for results.
     */
    Query query;
    List<Object> result;

    /**
     * Before each set-up a valid version
     */
    @BeforeEach
    public void setUp() throws IOException {
        query = new QueryBuilderImpl()
                .withSource("src/test/resources/csvtest/validChargers.csv")
                .build();
        result = new CsvInterpreter().readData(query, Charger.class);
    }

    /**
     * Tears down each created object
     */
    @AfterEach
    public void tearDown() {
        query = null;
        result = null;
        assertNull(query);
        assertNull(result);
    }

    /**
     * Check that IOException is thrown if filepath does not exist
     */
    @Test
    public void invalidFilePathTest() {
        // Check error is thrown
        assertThrows(IOException.class, () -> {
            Query query = new QueryBuilderImpl()
                    .withSource("NonExistentFilePath")
                    .build();
            new CsvInterpreter().readData(query, Charger.class);
        });
    }

    /**
     * Check that IOException with correct message is thrown when missing a header
     */
    @Test
    public void missingHeaderTest() {
        // Check exception is thrown
        IOException exception = assertThrows(IOException.class, () -> {
            Query query = new QueryBuilderImpl()
                    .withSource("src/test/resources/csvtest/missingHeader.csv")
                    .build();
            new CsvInterpreter().readData(query, Charger.class);
        });

        // Check header is missing
        assertTrue(exception.getMessage().contains("Header is missing required fields"));
        // Check only one error is in message
        assertFalse(exception.getMessage().contains("\n"));
    }

    /**
     * Check that IOException with correct message is thrown when data is wrong type
     */
    @Test
    public void invalidDataTypeTest() {
        // Check exception is thrown
        IOException exception = assertThrows(IOException.class, () -> {
            Query query = new QueryBuilderImpl()
                    .withSource("src/test/resources/csvtest/invalidDataType.csv")
                    .build();
            new CsvInterpreter().readData(query, Charger.class);
        });

        // Check data cannot be converted
        assertTrue(exception.getMessage().contains("could not be converted to"));
        // Check only one error is in message
        assertFalse(exception.getMessage().contains("\n"));
    }

    /**
     * Check that IOException with correct message is thrown when data is missing
     */
    @Test
    public void missingDataValueTest() {
        // Check exception is thrown
        IOException exception = assertThrows(IOException.class, () -> {
            Query query = new QueryBuilderImpl()
                    .withSource("src/test/resources/csvtest/missingDataValue.csv")
                    .build();
            new CsvInterpreter().readData(query, Charger.class);
        });

        // Check data is missing
        assertTrue(exception.getMessage().contains("is mandatory but no value was provided"));
        // Check only one error is in message
        assertFalse(exception.getMessage().contains("\n"));
    }

    /**
     * Check that IOException with concatenated messages when multiple errors are
     * thrown
     */
    @Test
    public void multipleErrorMessagesCombineTest() {
        // Check exception is thrown
        IOException exception = assertThrows(IOException.class, () -> {
            Query query = new QueryBuilderImpl()
                    .withSource("src/test/resources/csvtest/multipleErrorMessagesCombine.csv")
                    .build();
            new CsvInterpreter().readData(query, Charger.class);
        });

        // Check invalid data type
        assertTrue(exception.getMessage().contains("could not be converted to"));
        // Check missing field
        assertTrue(exception.getMessage().contains("is mandatory but no value was provided"));
    }

    /**
     * Check that csv data is converted into target objects
     */
    @Test
    public void convertsToValidObjectTest() {
        for (Object o : result) {
            assertEquals(Charger.class, o.getClass());
        }
    }

    /**
     * Converted objects have all fields defined
     */
    @Test
    public void checkConnectorsSizeTest() {
        Charger c = (Charger) result.get(0);
        List<Connector> expectedConnectors = Arrays
                .asList(new Connector("Type 2 Socketed", "22 kW", "Operative", "AC", 1));
        assertEquals(expectedConnectors.size(), c.getConnectors().size());

    }

    @Test
    public void checkChargersSame() {
        ArrayList<Connector> expectedConnectors = new ArrayList<>(
                Arrays.asList(new Connector("Type 2 Socketed", "22 kW", "Operative", "AC", 1)));
        Charger c = new Charger(expectedConnectors, "YHA MT COOK",
                new Coordinate(1366541.235400,
                        5153202.164200,
                        -43.737450,
                        170.100913,
                        "4 Kitchener Dr, Mount Cook National Park 7999, New Zealand"),
                1, 0.0, "MERIDIAN ENERGY LIMITED",
                "MERIDIAN ENERGY LIMITED", "2020/05/01 00:00:00+00",
                false, true, true, false);
        c.setChargerId(1);
        assertEquals(c, (Charger) result.get(0));
    }
}