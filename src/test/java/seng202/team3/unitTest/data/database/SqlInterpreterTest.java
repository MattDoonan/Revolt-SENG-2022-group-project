package seng202.team3.unittest.data.database;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;
import javax.management.InstanceAlreadyExistsException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import seng202.team3.data.database.ComparisonType;
import seng202.team3.data.database.CsvInterpreter;
import seng202.team3.data.database.Query;
import seng202.team3.data.database.QueryBuilder;
import seng202.team3.data.database.QueryBuilderImpl;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.Connector;
import seng202.team3.data.entity.Coordinate;
import seng202.team3.data.entity.Journey;
import seng202.team3.data.entity.Vehicle;

/**
 * Tests for SqlInterpreter {@link SqlInterpreter} Class
 *
 * @author Matthew Doonan
 * @version 1.0.0, Sep 2
 */
public class SqlInterpreterTest {
    static SqlInterpreter db;
    static Connector testConnector1;
    static Connector testConnector2;
    static Charger testCharger;
    static Vehicle testVehicle;
    static Journey testJourney;

    static final int DEFAULTID = 1;

    /**
     * Used for testing to simplify db writing methods
     * 
     * @param objectToTest obeject to write to database
     * @throws IOException if the writing fails
     */
    private void writeSingleEntity(Object objectToTest) throws IOException {
        switch (objectToTest.getClass().getSimpleName()) {
            case "Charger":
                db.writeCharger((Charger) objectToTest);
                break;
            case "Connector":
                db.writeConnector((Connector) objectToTest, testCharger.getChargerId());
                break;
            case "Vehicle":
                db.writeVehicle((Vehicle) objectToTest);
                break;
            case "Journey":
                db.writeVehicle(testVehicle);
                db.writeCharger(testCharger);
                db.writeJourney((Journey) objectToTest);
                break;
            default:
                fail();
        }
    }

    /**
     * Arguments to pass to parameterized tests
     * 
     * @return stream of arguments for parameterized tests
     */
    private static Stream<Arguments> dbSingleEntities() {
        return Stream.of(
                Arguments.of(testCharger, "charger"),
                Arguments.of(testConnector1, "connector"),
                Arguments.of(testVehicle, "vehicle"),
                Arguments.of(testJourney, "journey"));
    }

    @BeforeAll
    static void setup() throws InstanceAlreadyExistsException {
        SqlInterpreter.removeInstance();
        db = SqlInterpreter.initialiseInstanceWithUrl(
                "jdbc:sqlite:./src/test/resources/test_database.db");
    }

    @BeforeEach
    void reset() {
        db.defaultDatabase();
        testConnector1 = new Connector("ChardaMo", "AC", "Available", "123", 3);
        testConnector2 = new Connector("ChardaMo", "AC", "Available", "420", 1);

        testCharger = new Charger(new ArrayList<Connector>(
                Arrays.asList(testConnector1, testConnector2)),
                "Test2",
                new Coordinate(4.8, 6.2, -32.85658, 177.77702, "testAddy1"),
                1,
                0.3,
                "Meridian",
                "Meridian",
                "2020/05/01 00:00:00+00",
                false,
                false,
                true,
                false);

        testVehicle = new Vehicle(
                "Nissan",
                "Leaf",
                100,
                new ArrayList<String>(Arrays.asList("ChardaMo", "Type 2 Socketed")));
        testJourney = new Journey(testVehicle,
                new Coordinate(5.6, 7.7, -36.6543, 174.74532),
                new Coordinate(5.8, 7.2, -37.45543, 176.45652),
                "2020/1/1 00:00:00", "2020/1/3 00:00:00");
        testJourney.addCharger(testCharger);

    }

    @Test
    public void testNullUrl() {
        assertNotNull(db);
        assertEquals(db, SqlInterpreter.getInstance());
    }

    @Test
    void testDatabaseConnection() throws SQLException {
        Connection connection = db.createConnection();
        assertNotNull(connection);
        assertNotNull(connection.getMetaData());
        connection.close();
    }

    /**
     * Check that IOException is thrown if table does not exist
     */
    @Test
    public void invalidFilePathTest() {
        Query query = new QueryBuilderImpl()
                .withSource("NonExistentFilePath")
                .build();
        // Check error is thrown
        assertThrows(IOException.class, () -> {
            db.readData(query, Charger.class);
        });
    }

    /**
     * Tests single entities can be read and written to the database
     * 
     * @param objectToTest entity to test
     * @param dbTable      table to interact with
     * @throws IOException read/write error
     */
    @ParameterizedTest
    @MethodSource("dbSingleEntities")
    public void singleReadWriteTest(Object objectToTest, String dbTable)
            throws IOException, SQLException {
        writeSingleEntity(objectToTest); // Write object

        // Get object back from db
        List<Object> result = db.readData(
                new QueryBuilderImpl().withSource(dbTable).build(),
                objectToTest.getClass());

        assertArrayEquals(new Object[] { objectToTest }, result.toArray());
    }

    @Test
    public void writeChargersFromCsvTest() throws IOException {
        db.addChargerCsvToData("src/test/resources/csvtest/validChargers.csv");
        QueryBuilder q = new QueryBuilderImpl().withSource("charger");
        List<Object> result = db.readData(q.build(), Charger.class);

        List<Object> expected = new CsvInterpreter().readData(
                q.withSource("src/test/resources/csvtest/validChargers.csv").build(),
                Charger.class);

        for (Object o : expected) {
            assertTrue(result.contains(o));
        }
    }

    /**
     * Tests errors are thrown correctly when unparsable object is provided
     */
    @ParameterizedTest
    @MethodSource("dbSingleEntities")
    public void interpretAsWrongObjectTest(Object objectToTest, String dbTable) throws IOException {
        writeSingleEntity(objectToTest); // Write to db

        assertThrows(IOException.class, () -> { // Interpret as abstract Object
            db.readData(
                    new QueryBuilderImpl().withSource(dbTable).build(),
                    Object.class);
        });
    }

    /**
     * Tests if the delete function works for chargers
     */
    @ParameterizedTest
    @MethodSource("dbSingleEntities")
    public void deleteEntityTest(Object objectToTest, String dbTable) throws IOException {
        writeSingleEntity(objectToTest); // Add to db
        if (objectToTest.getClass() == Connector.class) {
            writeSingleEntity(testConnector2);
        }
        Query q = new QueryBuilderImpl().withSource(dbTable).build();
        db.deleteData(dbTable, DEFAULTID); // Delete

        List<Object> result = db.readData(q, objectToTest.getClass());
        assertFalse(result.contains(objectToTest)); // Check no longer exists
    }

    /**
     * Check entities that don't exist can't be deleted
     */
    @ParameterizedTest
    @MethodSource("dbSingleEntities")
    public void deleteMissingEntityTest(Object objectToTest, String dbTable) throws IOException {
        // Empty db
        QueryBuilder q = new QueryBuilderImpl().withSource(dbTable);

        List<Object> originalTable = db.readData(q.build(), objectToTest.getClass());
        db.deleteData(dbTable, DEFAULTID);
        List<Object> resultTable = db.readData(q.build(), objectToTest.getClass());

        assertArrayEquals(originalTable.toArray(), resultTable.toArray());
    }

    /**
     * Checks if entity ids are autoincremented as added to the database
     */
    @ParameterizedTest
    @MethodSource("dbSingleEntities")
    // TODO: simplify with an 'entity' interface/superclass with getId etc.
    public void autoIncrementIdTest(Object objectToTest, String dbTable) throws IOException {
        if (objectToTest.getClass() == Journey.class) {
            ((Journey) objectToTest).setVehicle(testVehicle);
        }
        writeSingleEntity(objectToTest); // Write with DEFAULTID

        // Set ids to null - different methods per entity
        switch (objectToTest.getClass().getSimpleName()) {
            case "Charger":
                ((Charger) objectToTest).setChargerId(0);
                break;
            case "Connector":
                ((Connector) objectToTest).setId(0);
                break;
            case "Vehicle":
                ((Vehicle) objectToTest).setVehicleId(0);
                break;
            case "Journey":
                ((Journey) objectToTest).setJourneyId(0);
                break;
            default:
                fail();
        }

        writeSingleEntity(objectToTest); // Add 'newly created' object

        int entityId = -1;
        // Get id of most recently added record
        try (Connection conn = db.createConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT " + dbTable + "id "
                        + "FROM " + dbTable + " ORDER BY " + dbTable + "id DESC LIMIT 0,1")) {
            entityId = rs.getInt(dbTable + "id");

        } catch (SQLException e) {
            throw new IOException(e.getMessage());
        }

        assertEquals(DEFAULTID + 1, entityId);
    }

    /**
     * Checks if the connectors associated with the chargers are deleted
     */
    @Test
    public void delChargerTestForConnectors() throws SQLException, IOException {
        writeSingleEntity(testCharger);
        db.deleteData("charger", testCharger.getChargerId());
        List<Object> result = db.readData(
                new QueryBuilderImpl().withSource("connector")
                        .withFilter("chargerid", String.valueOf(testCharger.getChargerId()),
                                ComparisonType.CONTAINS)
                        .build(),
                Connector.class);
        assertArrayEquals(new Object[] {}, result.toArray());
    }

    /**
     * Checks if there are stops still stored in the database when a journey is
     * deleted
     */
    @Test
    public void delJourneyTestForStops() throws SQLException, IOException {
        writeSingleEntity(testJourney);
        db.deleteData("journey", testJourney.getJourneyId());
        ResultSet result = db.createConnection().createStatement().executeQuery(
                "SELECT * FROM stop WHERE journeyid = " + testJourney.getJourneyId() + ";");
        assertFalse(result.getBoolean(1));
    }

    /**
     * Checks if a stop is deleted if a charger is deleted
     */
    @Test
    public void delChargerTestForStops() throws SQLException, IOException {
        db.writeCharger(testCharger);
        db.deleteData("charger", testCharger.getChargerId());
        ResultSet result = db.createConnection().createStatement().executeQuery(
                "SELECT * FROM stop WHERE chargerid = " + testCharger.getChargerId() + ";");
        assertFalse(result.getBoolean(3));
    }

    /**
     * Checks if a journey is deleted when a vehicle is deleted
     */
    @Test
    public void delVehicleTestForJourney() throws SQLException, IOException {
        db.writeJourney(testJourney);
        db.deleteData("vehicle", testVehicle.getVehicleId());
        List<Object> result = db.readData(
                new QueryBuilderImpl().withSource("journey")
                        .withFilter("vehicleid", String.valueOf(testVehicle.getVehicleId()),
                                ComparisonType.CONTAINS)
                        .build(),
                Journey.class);
        assertArrayEquals(new Object[] {}, result.toArray());
    }

    /**
     * Checks what happens if a vehicle is null
     */
    @Test
    public void noVehicleForJourneyTest() throws IOException {
        testJourney.setVehicle(null);

        Exception e = assertThrows(IOException.class, () -> {
            db.writeJourney(testJourney);
        });

        assertTrue(e.getMessage().contains("Error writing journey. No Vehicle Attached."));
    }

    /**
     * Checks what happens if there are no chargers in a journey
     */
    @Test
    public void noChargersForJourneyTest() throws IOException {
        testJourney.removeCharger(testCharger);

        Exception e = assertThrows(IOException.class, () -> {
            db.writeJourney(testJourney);
        });

        assertTrue(e.getMessage().contains("Error writing journey. No stops found."));
    }

    /**
     * Checks the requirement for chargers to have one connector
     */
    @Test
    public void removingTooManyConnectorsTest() throws SQLException, IOException {
        writeSingleEntity(testCharger);
        db.deleteData("connector", testConnector1.getId());

        Exception e = assertThrows(IOException.class, () -> {
            db.deleteData("connector", testConnector2.getId());
        });

        assertTrue(e.getMessage()
                .contains("Cannot delete connector. Charger must have 1 connector"));
    }

    /**
     * Adds charger to database then edits some variables and checks if variables
     * have changed
     */
    @Test
    public void updateChargerTest() throws IOException {
        db.writeCharger(testCharger); // Write to database

        // Modify attributes
        testCharger.setOwner("Tesla");
        testCharger.setAvailable24Hrs(false);
        testCharger.setOperator("Seng202");

        db.writeCharger(testCharger); // Update

        List<Object> result = db.readData(
                new QueryBuilderImpl().withSource("charger").build(),
                Charger.class);
        assertArrayEquals(new Object[] { testCharger }, result.toArray());
    }

    /**
     * Checks if updating a connector works
     */
    @Test
    public void updateConnectorTest() throws IOException {
        db.writeConnector(testConnector1, 1); // Write to database

        // Modify attributes
        testConnector1.setPower("UpdatedPower");
        testConnector1.setOperational("out of order");
        testConnector1.setCurrent("UpdatedCurrent");

        db.writeConnector(testConnector1, 1); // Update
        List<Object> result = db.readData(
                new QueryBuilderImpl().withSource("connector").build(),
                Connector.class);
        assertArrayEquals(new Object[] { testConnector1 }, result.toArray());
    }

    @Test
    public void updateVehicleTest() throws IOException {
        db.writeVehicle(testVehicle); // Write to database

        // Modify attributes
        testVehicle.setMake("Updated make");
        testVehicle.setMaxRange(210);
        testVehicle.setModel("Updated model");

        db.writeVehicle(testVehicle); // Update

        List<Object> result = db.readData(
                new QueryBuilderImpl().withSource("vehicle").build(),
                Vehicle.class);
        assertArrayEquals(new Object[] { testVehicle }, result.toArray());
    }

    @Test
    public void updateJourneyTest() throws IOException, SQLException {
        writeSingleEntity(testJourney); // Write to database

        // Modify attributes
        testJourney.setEndPosition(new Coordinate(4.56, 9.9, -50.6543, 154.74562));
        testJourney.setStartDate("1/1/1111 00:00:00");

        db.writeJourney(testJourney); // Update

        List<Object> result = db.readData(
                new QueryBuilderImpl().withSource("journey").build(),
                Journey.class);
        assertArrayEquals(new Object[] { testJourney }, result.toArray());
    }

    /**
     * Sees if the vehicle returned changes on update with Journey
     */
    @Test
    public void changeVehicleJourneyTest() throws SQLException, IOException {
        writeSingleEntity(testJourney);
        testJourney.setVehicle(testVehicle = new Vehicle(
                "Tesla",
                "X",
                260,
                new ArrayList<String>(Arrays.asList("ChardaMo", "Type 2 Socketed"))));
        writeSingleEntity(testJourney.getVehicle());
        writeSingleEntity(testJourney);
        List<Object> result = db.readData(
                new QueryBuilderImpl().withSource("journey").withFilter("vehicleid",
                        String.valueOf(testJourney.getVehicle().getVehicleId()),
                        ComparisonType.CONTAINS).build(),
                Journey.class);
        assertArrayEquals(new Object[] { testJourney }, result.toArray());
    }

    /**
     * Sees if a charger is associated with a journey when changed
     */
    @Test
    public void changeChargerJourneyTest() throws SQLException, IOException {
        writeSingleEntity(testJourney);
        testJourney.getChargers().get(0).setName("New Name");
        testJourney.getChargers().get(0).setOperator("New op");
        testJourney.getChargers().get(0).setOwner("New owner");
        writeSingleEntity(testJourney.getChargers().get(0));
        List<Object> result = db.readData(
                new QueryBuilderImpl().withSource("journey").withFilter("journeyid",
                        String.valueOf(testJourney.getJourneyId()),
                        ComparisonType.CONTAINS).build(),
                Journey.class);
        assertArrayEquals(new Object[] { testJourney }, result.toArray());
    }

    /**
     * Tests if connectors associated with a charger update when a charger updates
     */
    @Test
    public void updateConnectorWithCharger() throws SQLException, IOException {
        writeSingleEntity(testCharger);
        testConnector2.setPower("New Power");
        testConnector2.setCount(10);
        testConnector2.setOperational("New Operation");
        testConnector1.setType("New Type");
        testConnector1.setCurrent(testConnector1.getCurrent());
        writeSingleEntity(testCharger);
        List<Object> result = db.readData(
                new QueryBuilderImpl().withSource("connector").withFilter("chargerid",
                        String.valueOf(testCharger.getChargerId()),
                        ComparisonType.CONTAINS).build(),
                Connector.class);
        assertArrayEquals(new Object[] { testConnector1, testConnector2 }, result.toArray());
    }

    /**
     * Check doesn't add record when missing required field
     * 
     * @param objectToTest object to add
     * @param dbTable      table to add to
     * @throws IOException read from database fails
     */
    @ParameterizedTest
    @MethodSource("dbSingleEntities")
    public void missingRequiredFieldTest(Object objectToTest, String dbTable) throws IOException {

        switch (objectToTest.getClass().getSimpleName()) {
            case "Charger":
                ((Charger) objectToTest).setOwner(null);
                break;
            case "Connector":
                ((Connector) objectToTest).setCurrent(null);
                break;
            case "Vehicle":
                ((Vehicle) objectToTest).setBatteryPercent(null);
                break;
            case "Journey":
                ((Journey) objectToTest).setStartPosition(
                        new Coordinate(4.8, 6.2, null, 177.77702, "testAddy1"));
                break;
            default:
                fail();
        }

        assertThrows(IOException.class, () -> {
            writeSingleEntity(objectToTest);
        });

        List<Object> result = db.readData(
                new QueryBuilderImpl().withSource(dbTable).build(),
                objectToTest.getClass());
        assertArrayEquals(new Object[] {}, result.toArray());
    }

    @Test
    public void allRecordsOnlyOnceTest() throws IOException {
        // objectToTest is unused but defined so existing methodsource can be used

        db.addChargerCsvToData("src/test/resources/csvtest/validChargers.csv");

        List<Object> result = db.readData(
                new QueryBuilderImpl().withSource("charger").build(),
                Charger.class);

        HashSet<Object> unique = new HashSet<Object>();
        assertTrue(result.size() > 0);
        for (Object object : result) {
            if (!unique.add(object)) {
                fail("Duplicate object");
            }
        }
    }

    @Test
    public void singleFilterTest() throws IOException {
        db.addChargerCsvToData("src/test/resources/csvtest/filtering.csv");

        List<Object> result = db.readData(
                new QueryBuilderImpl().withSource("charger")
                        .withFilter("owner", "MERIDIAN", ComparisonType.CONTAINS)
                        .build(),
                Charger.class);

        for (Object o : result) {
            if (!((Charger) o).getOwner().contains("MERIDIAN")) {
                fail("Charger did not match filter");
            }
        }

    }

    @Test
    public void singleFilterCaseInsensitiveTest() throws IOException {
        db.addChargerCsvToData("src/test/resources/csvtest/filtering.csv");

        List<Object> result = db.readData(
                new QueryBuilderImpl().withSource("charger")
                        .withFilter("operator", "MERIDIAN", ComparisonType.CONTAINS)
                        .build(),
                Charger.class);

        for (Object o : result) {
            if (!((Charger) o).getOperator().toLowerCase().contains("meridian")) {
                fail("Charger did not match filter");
            }
        }

    }

    @Test
    public void multipleFilterTest() throws IOException {
        db.addChargerCsvToData("src/test/resources/csvtest/filtering.csv");

        List<Object> result = db.readData(
                new QueryBuilderImpl().withSource("charger")
                        .withFilter("owner", "MERIDIAN", ComparisonType.CONTAINS)
                        .withFilter("haschargingcost", "false", ComparisonType.EQUAL)
                        .build(),
                Charger.class);

        for (Object o : result) {
            if (!((Charger) o).getOwner().contains("MERIDIAN")
                    || ((Charger) o).getChargeCost()) {
                fail("Charger did not match filter");
            }
        }
    }

    @Test
    public void filterByColumnOnRelatedTableTest() throws IOException {
        db.addChargerCsvToData("src/test/resources/csvtest/filtering.csv");

        List<Object> result = db.readData(
                new QueryBuilderImpl().withSource("charger")
                        .withFilter("connectorstatus", "operative", ComparisonType.CONTAINS)
                        .build(),
                Charger.class);

        for (Object o : result) {
            boolean valid = false;
            for (Connector c : ((Charger) o).getConnectors()) {
                if (c.getStatus().toLowerCase().contains("operative")) {
                    valid = true;
                    break;
                }
            }

            if (!valid) {
                fail("Charger did not match filter");
            }
        }

    }

    @Test
    public void multipleFilterSameAttributeTest() throws IOException {
        db.addChargerCsvToData("src/test/resources/csvtest/filtering.csv");

        List<Object> result = db.readData(
                new QueryBuilderImpl().withSource("charger")
                        .withFilter("currenttype", "AC", ComparisonType.CONTAINS)
                        .withFilter("currenttype", "DC", ComparisonType.CONTAINS)
                        .build(),
                Charger.class);

        for (Object o : result) {
            if (!((Charger) o).getCurrentType().contains("AC")
                    || !((Charger) o).getCurrentType().contains("DC")) {
                fail("Charger did not match filter");
            }
        }
    }

    @Test
    public void filterByNonExistentColumnTest() throws IOException {

        db.addChargerCsvToData("src/test/resources/csvtest/filtering.csv");

        assertThrows(IOException.class, () -> {
            db.readData(
                    new QueryBuilderImpl().withSource("charger")
                            .withFilter("nonExistentColumn", "1", ComparisonType.GREATER_THAN)
                            .build(),
                    Charger.class);
        });
    }

    @Test
    public void allRecordsImported() throws IOException {
        db.addChargerCsvToData("src/test/resources/csvtest/filtering.csv");

        QueryBuilder q = new QueryBuilderImpl()
                .withSource("src/test/resources/csvtest/filtering.csv");

        List<Object> expected = new CsvInterpreter().readData(q.build(), Charger.class);
        List<Object> actual = db.readData(q.withSource("charger").build(), Charger.class);

        assertEquals(expected.size(), actual.size());
    }
}