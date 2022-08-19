package seng202.team3.data.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.Connector;

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
        System.out.println(exception.getMessage() + "test");
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
    public void convertsToValidObjectTest() throws IOException {
        Query query = new QueryBuilderImpl()
                .withSource("src/test/resources/csvtest/validChargers.csv")
                .build();
        List<Object> result = new CsvInterpreter().readData(query, Charger.class);

        for (Object o : result) {
            assertEquals(Charger.class, o.getClass());
        }
    }

    /**
     * Converted objects have all fields defined
     */
    @Test
    public void allAttributesConvertedTest() throws IOException {
        Query query = new QueryBuilderImpl()
                .withSource("src/test/resources/csvtest/validChargers.csv")
                .build();
        List<Object> result = new CsvInterpreter().readData(query, Charger.class);

        Charger c = (Charger) result.get(0);
        List<Connector> expectedConnectors = Arrays
                .asList(new Connector("Type 2 Socketed", "22 kW", "Operative", "AC", 1));
        assertEquals(expectedConnectors.size(), c.getConnectors().size());
        for (int i = 0; i < expectedConnectors.size(); i++) {
            assertEquals(expectedConnectors.get(i).getType(), c.getConnectors().get(i).getType());
            assertEquals(expectedConnectors.get(i).getPower(), c.getConnectors().get(i).getPower());
            assertEquals(expectedConnectors.get(i).getStatus(),
                    c.getConnectors().get(i).getStatus());
            assertEquals(expectedConnectors.get(i).getCurrent(),
                    c.getConnectors().get(i).getCurrent());
            assertEquals(expectedConnectors.get(i).getCount(), c.getConnectors().get(i).getCount());
        }

        assertEquals(1, c.getAvailableParks());
        assertEquals(1, c.getChargerId());
        assertEquals("2020/05/01 00:00:00+00", c.getDateOpened());
        assertEquals(false, c.getHasAttraction());
        assertEquals(true, c.getChargeCost());
        assertEquals(false, c.getParkingCost());
        assertEquals(1, c.getAvailableParks());
        assertEquals(1366541.235400, c.getLocation().getXpos());
        assertEquals(5153202.164200, c.getLocation().getYpos());
        assertEquals(-43.737450, c.getLocation().getLat());
        assertEquals(170.100913, c.getLocation().getLon());
        assertEquals("4 Kitchener Dr, Mount Cook National Park 7999, New Zealand", 
                        c.getLocation().getAddress());
        assertEquals("YHA MT COOK", c.getName());
        assertEquals("MERIDIAN ENERGY LIMITED", c.getOperator());
        assertEquals("MERIDIAN ENERGY LIMITED", c.getOwner());
        assertEquals(0, c.getTimeLimit());

    }

}
