package seng202.team3.data.database;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import javax.management.InstanceAlreadyExistsException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.Connector;
import seng202.team3.data.entity.Coordinate;
import seng202.team3.data.entity.Journey;
import seng202.team3.data.entity.Note;
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
    static Note testNote;

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
                db.writeConnector((Connector) objectToTest, DEFAULTID);
                break;
            case "Vehicle":
                db.writeVehicle((Vehicle) objectToTest);
                break;
            case "Journey":
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
        try {
            setup();
        } catch (InstanceAlreadyExistsException e) {
            ;// Do Nothing
        }
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
        testConnector1.setId(DEFAULTID);
        testConnector2 = new Connector("ChardaMo", "AC", "Available", "420", 1);
        testConnector2.setId(DEFAULTID + 1);

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
        testCharger.setChargerId(DEFAULTID);

        testVehicle = new Vehicle(
                "Nissan",
                "Leaf",
                100,
                new ArrayList<String>(Arrays.asList("ChardaMo", "Type 2 Socketed")));
        testVehicle.setVehicleId(DEFAULTID);
        testJourney = new Journey(testVehicle,
                new Coordinate(5.6, 7.7, -36.6543, 174.74532),
                new Coordinate(5.8, 7.2, -37.45543, 176.45652),
                "2020/1/1 00:00:00", "2020/1/3 00:00:00");
        testJourney.addCharger(testCharger);
        testJourney.setJourneyId(DEFAULTID);

        testNote = new Note(DEFAULTID, 4);
        testNote.setPublicText("This charger is great");
        testNote.setReviewId(DEFAULTID);
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
            throws IOException {
        writeSingleEntity(objectToTest); // Write object

        // Get object back from db
        List<Object> result = db.readData(
                new QueryBuilderImpl().withSource(dbTable).build(),
                objectToTest.getClass());

        assertArrayEquals(new Object[] { objectToTest }, result.toArray());
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

    // TODO: del related entities charg/conn journ/vehicle+charg
    // TODO: can't write journey without vehicle/chargers
    // TODO: can't write charger without connectors
    // TODO: update journey modifies vehicle/chargers (separate tests)
    // TODO: update charger modifies connectors
    // TODO: write/del individual relationships charg/conn journ/vehicle+charg

    // /**
    // * Creates a charger then adds a connector to the charger later
    // */
    // @Test
    // public void addLaterConnector() throws IOException {
    // db.writeCharger(testCharger);
    // Query q = new QueryBuilderImpl().withSource("connector").build();
    // List<Object> result = db.readData(q, Connector.class);
    // Connector newConnector = new Connector("ChardaMo", "AC", "Available", "33",
    // 5);
    // newConnector.setId(3);
    // db.writeConnector(newConnector, DEFAULTID);
    // result.add(newConnector);
    // assertArrayEquals(result.toArray(), db.readData(q,
    // Connector.class).toArray());
    // }

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
    public void updateJourneyTest() throws IOException {
        db.writeJourney(testJourney); // Write to database

        // Modify attributes
        testJourney.setEndPosition(new Coordinate(4.56, 9.9, -50.6543, 154.74562));
        testJourney.setStartDate("1/1/1111 00:00:00");

        db.writeJourney(testJourney); // Update

        List<Object> result = db.readData(
                new QueryBuilderImpl().withSource("journey").build(),
                Journey.class);
        assertArrayEquals(new Object[] { testJourney }, result.toArray());
    }
}
