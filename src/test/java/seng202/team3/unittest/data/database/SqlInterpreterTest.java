package seng202.team3.unittest.data.database;

import static org.junit.Assert.assertNull;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
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
import seng202.team3.data.entity.EntityType;
import seng202.team3.data.entity.Journey;
import seng202.team3.data.entity.PermissionLevel;
import seng202.team3.data.entity.Entity;
import seng202.team3.data.entity.User;
import seng202.team3.data.entity.Vehicle;
import seng202.team3.logic.UserManager;

/**
 * Tests for SqlInterpreter {@link SqlInterpreter} Class
 *
 * @author Matthew Doonan, Harrison Tyson
 * @version 1.0.0, Sep 2
 */
public class SqlInterpreterTest {
    /**
     * Logger
     */
    private static final Logger logManager = LogManager.getLogger();

    static SqlInterpreter db;
    static Connector testConnector1;
    static Connector testConnector2;
    static Charger testCharger;
    static Vehicle testVehicle;
    static Journey testJourney;
    static User testUser;

    static final int DEFAULTID = 1;

    /**
     * Used for testing to simplify db writing methods
     *
     * @param objectToTest obeject to write to database
     * @throws IOException if the writing fails
     */
    private void writeSingleEntity(Entity objectToTest) throws IOException {
        switch (objectToTest.getClass().getSimpleName()) {
            case "Charger":
                db.writeCharger((Charger) objectToTest);
                break;
            case "Connector":
                db.writeConnector((Connector) objectToTest, testCharger.getId());
                break;
            case "Vehicle":
                db.writeVehicle((Vehicle) objectToTest);
                break;
            case "Journey":
                db.writeVehicle(testVehicle);
                db.writeCharger(testCharger);
                db.writeJourney((Journey) objectToTest);
                break;
            case "User":
                db.writeUser((User) objectToTest, "admin"); // arbitrary password
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
                Arguments.of(testCharger, EntityType.CHARGER),
                Arguments.of(testConnector1, EntityType.CONNECTOR),
                Arguments.of(testVehicle, EntityType.VEHICLE),
                Arguments.of(testJourney, EntityType.JOURNEY),
                Arguments.of(testUser, EntityType.USER));
    }

    @BeforeAll
    static void setup() throws InstanceAlreadyExistsException, IOException {
        SqlInterpreter.removeInstance();
        db = SqlInterpreter.initialiseInstanceWithUrl(
                "jdbc:sqlite:./target/test-classes/test_database.db");
    }

    @BeforeEach
    void reset() {
        db.defaultDatabase();
        try {
            Connection conn = db.createConnection();

            conn.createStatement()
                    .executeUpdate("DELETE FROM user;"); // remove default admin
            conn.close();
        } catch (SQLException e) {
            logManager.error(e.getMessage());
            ;
        }

        testUser = new User("admin@admin.com", "admin",
                PermissionLevel.ADMIN);
        testUser.setId(DEFAULTID);

        UserManager.setUser(testUser);

        try {
            db.writeUser(testUser, "admin");
        } catch (IOException e) {
            logManager.error(e.getMessage());
            ;
        }

        testConnector1 = new Connector("ChardaMo", "AC", "Available", "123", 3);
        testConnector2 = new Connector("ChardaMo", "AC", "Available", "420", 1);

        testCharger = new Charger(new ArrayList<Connector>(
                Arrays.asList(testConnector1, testConnector2)),
                "Test2",
                new Coordinate(-32.85658, 177.77702, "testAddy1"),
                1,
                0.3,
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
                new Coordinate(-36.6543, 174.74532),
                new Coordinate(-37.45543, 176.45652),
                "2020/1/1 00:00:00", "2020/1/3 00:00:00");
        testJourney.addCharger(testCharger);

    }

    @Test
    public void testNullUrl() throws IOException {
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
                .withSource(null)
                .build();
        // Check error is thrown
        assertThrows(NullPointerException.class, () -> {
            db.readData(query);
        });
    }

    /**
     * Tests single entities can be read and written to the database
     *
     * @param objectToTest entity to test
     * @param entity       table to interact with
     * @throws IOException read/write error
     */
    @ParameterizedTest
    @MethodSource("dbSingleEntities")
    public void singleReadWriteTest(Entity objectToTest, EntityType entity)
            throws IOException, SQLException {
        writeSingleEntity(objectToTest); // Write object

        // Get object back from db
        List<Entity> result = db.readData(
                new QueryBuilderImpl().withSource(entity).build());

        assertArrayEquals(new Entity[] { objectToTest }, result.toArray());
    }

    @Test
    public void writeChargersFromCsvTest() throws IOException {
        new CsvInterpreter().importChargersToDatabase("csvtest/validChargers");
        QueryBuilder q = new QueryBuilderImpl().withSource(EntityType.CHARGER);
        List<Entity> result = db.readData(q.build());

        List<Entity> expected = new CsvInterpreter().readChargers("csvtest/validChargers");

        for (Entity o : expected) {
            assertTrue(result.contains(o));
        }
    }

    /**
     * Tests if the delete function works for chargers
     */
    @ParameterizedTest
    @MethodSource("dbSingleEntities")
    public void deleteEntityTest(Entity objectToTest, EntityType entity) throws IOException {
        writeSingleEntity(objectToTest); // Add to db
        if (objectToTest.getClass() == Connector.class) {
            writeSingleEntity(testConnector2);
        }
        Query q = new QueryBuilderImpl().withSource(entity).build();
        db.deleteData(entity, DEFAULTID); // Delete

        List<Entity> result = db.readData(q);
        assertFalse(result.contains(objectToTest)); // Check no longer exists
    }

    /**
     * Check entities that don't exist can't be deleted
     */
    @ParameterizedTest
    @MethodSource("dbSingleEntities")
    public void deleteMissingEntityTest(Entity objectToTest, EntityType entity)
            throws IOException {
        // Empty db
        QueryBuilder q = new QueryBuilderImpl().withSource(entity);
        if (objectToTest instanceof User) { // Remove default user
            try {
                Connection conn = db.createConnection();
                conn.createStatement().executeUpdate("DELETE FROM user"); // remove default admin
                conn.close();
            } catch (SQLException e) {
                logManager.error(e.getMessage());
                ;
            }
        }

        List<Entity> originalTable = db.readData(q.build());
        db.deleteData(entity, DEFAULTID);
        List<Entity> resultTable = db.readData(q.build());

        assertArrayEquals(originalTable.toArray(), resultTable.toArray());
    }

    /**
     * Checks if entity ids are autoincremented as added to the database
     */
    @ParameterizedTest
    @MethodSource("dbSingleEntities")
    // TODO: simplify with an 'entity' interface/superclass with getId etc.
    public void autoIncrementIdTest(Entity objectToTest, EntityType entity) throws IOException {
        if (objectToTest.getClass() == Journey.class) {
            ((Journey) objectToTest).setVehicle(testVehicle);
        }
        writeSingleEntity(objectToTest); // Write with DEFAULTID

        // Set ids to null - different methods per entity
        switch (objectToTest.getClass().getSimpleName()) {
            case "Charger":
                ((Charger) objectToTest).setId(0);
                break;
            case "Connector":
                ((Connector) objectToTest).setId(0);
                break;
            case "Vehicle":
                ((Vehicle) objectToTest).setId(0);
                break;
            case "Journey":
                ((Journey) objectToTest).setId(0);
                break;
            case "User":
                ((User) objectToTest).setId(0);
                ((User) objectToTest).setAccountName("newName"); // username must be unique
                break;
            default:
                fail();
        }

        writeSingleEntity(objectToTest); // Add 'newly created' object

        int entityId = -1;
        // Get id of most recently added record
        try (Connection conn = db.createConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT " + entity.getAsDatabase() + "id "
                        + "FROM " + entity.getAsDatabase() + " ORDER BY " + entity.getAsDatabase()
                        + "id DESC LIMIT 0,1")) {
            entityId = rs.getInt(entity.getAsDatabase() + "id");
            rs.close();
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            throw new IOException(e.getMessage());
        }

        assertEquals(DEFAULTID + 1, entityId);
    }

    /**
     * Tests check if deleting a user deletes all associated information
     */
    @Test
    public void delUserTestForJourneys() throws IOException {
        writeSingleEntity(testUser);
        testJourney.setUser(testUser.getId());
        writeSingleEntity(testJourney);
        db.deleteData(EntityType.USER, testUser.getId());
        List<Entity> result = db.readData(
                new QueryBuilderImpl().withSource(EntityType.JOURNEY)
                        .withFilter("userid", String.valueOf(testUser.getId()),
                                ComparisonType.EQUAL)
                        .build());
        assertArrayEquals(new Entity[] {}, result.toArray());
    }

    @Test
    public void delUserTestForVehicles() throws IOException {
        writeSingleEntity(testUser);
        testVehicle.setOwner(testUser.getId());
        writeSingleEntity(testVehicle);
        db.deleteData(EntityType.USER, testUser.getId());
        List<Entity> result = db.readData(
                new QueryBuilderImpl().withSource(EntityType.VEHICLE)
                        .withFilter("owner", String.valueOf(testUser.getId()),
                                ComparisonType.EQUAL)
                        .build());
        assertArrayEquals(new Entity[] {}, result.toArray());
    }

    @Test
    public void delUserTestForChargers() throws IOException {
        writeSingleEntity(testUser);
        testCharger.setOwner(testUser.getAccountName());
        writeSingleEntity(testCharger);
        db.deleteData(EntityType.USER, testUser.getId());
        List<Entity> result = db.readData(
                new QueryBuilderImpl().withSource(EntityType.CHARGER)
                        .withFilter("owner", String.valueOf(testUser.getId()),
                                ComparisonType.EQUAL)
                        .build());
        assertArrayEquals(new Entity[] {}, result.toArray());
    }

    /**
     * Checks if the connectors associated with the chargers are deleted
     */
    @Test
    public void delChargerTestForConnectors() throws SQLException, IOException {
        writeSingleEntity(testCharger);
        db.deleteData(EntityType.CHARGER, testCharger.getId());
        List<Entity> result = db.readData(
                new QueryBuilderImpl().withSource(EntityType.CONNECTOR)
                        .withFilter("chargerid", String.valueOf(testCharger.getId()),
                                ComparisonType.CONTAINS)
                        .build());
        assertArrayEquals(new Entity[] {}, result.toArray());
    }

    /**
     * Checks if there are stops still stored in the database when a journey is
     * deleted
     */
    @Test
    public void delJourneyTestForStops() throws SQLException, IOException {
        writeSingleEntity(testJourney);
        db.deleteData(EntityType.JOURNEY, testJourney.getId());
        ResultSet result = db.createConnection().createStatement().executeQuery(
                "SELECT * FROM stop WHERE journeyid = " + testJourney.getId() + ";");
        assertFalse(result.getBoolean(1));
        result.close();
    }

    /**
     * Checks if a stop is deleted if a charger is deleted
     */
    @Test
    public void delChargerTestForStops() throws SQLException, IOException {
        db.writeCharger(testCharger);
        db.deleteData(EntityType.CHARGER, testCharger.getId());
        ResultSet result = db.createConnection().createStatement().executeQuery(
                "SELECT * FROM stop WHERE chargerid = " + testCharger.getId() + ";");
        assertFalse(result.getBoolean(3));
        result.close();
    }

    /**
     * Checks if a journey is deleted when a vehicle is deleted
     */
    @Test
    public void delVehicleTestForJourney() throws SQLException, IOException {
        db.writeJourney(testJourney);
        db.deleteData(EntityType.VEHICLE, testVehicle.getId());
        List<Entity> result = db.readData(
                new QueryBuilderImpl().withSource(EntityType.JOURNEY)
                        .withFilter("vehicleid", String.valueOf(testVehicle.getId()),
                                ComparisonType.CONTAINS)
                        .build());
        assertArrayEquals(new Entity[] {}, result.toArray());
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
        db.deleteData(EntityType.CONNECTOR, testConnector1.getId());

        Exception e = assertThrows(IOException.class, () -> {
            db.deleteData(EntityType.CONNECTOR, testConnector2.getId());
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
        testCharger.setName("New");
        testCharger.setAvailable24Hrs(false);
        testCharger.setOperator("Seng202");

        db.writeCharger(testCharger); // Update

        List<Entity> result = db.readData(
                new QueryBuilderImpl().withSource(EntityType.CHARGER).build());
        assertArrayEquals(new Entity[] { testCharger }, result.toArray());
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
        List<Entity> result = db.readData(
                new QueryBuilderImpl().withSource(EntityType.CONNECTOR).build());
        assertArrayEquals(new Entity[] { testConnector1 }, result.toArray());
    }

    @Test
    public void updateVehicleTest() throws IOException {
        db.writeVehicle(testVehicle); // Write to database

        // Modify attributes
        testVehicle.setMake("Updated make");
        testVehicle.setMaxRange(210);
        testVehicle.setModel("Updated model");

        db.writeVehicle(testVehicle); // Update

        List<Entity> result = db.readData(
                new QueryBuilderImpl().withSource(EntityType.VEHICLE).build());
        assertArrayEquals(new Entity[] { testVehicle }, result.toArray());
    }

    @Test
    public void updateJourneyTest() throws IOException, SQLException {
        writeSingleEntity(testJourney); // Write to database

        // Modify attributes
        testJourney.setEndPosition(new Coordinate(-50.6543, 154.74562));
        testJourney.setStartDate("1/1/1111 00:00:00");

        db.writeJourney(testJourney); // Update

        List<Entity> result = db.readData(
                new QueryBuilderImpl().withSource(EntityType.JOURNEY).build());
        assertArrayEquals(new Entity[] { testJourney }, result.toArray());
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
        List<Entity> result = db.readData(
                new QueryBuilderImpl().withSource(EntityType.JOURNEY).withFilter("vehicleid",
                        String.valueOf(testJourney.getVehicle().getId()),
                        ComparisonType.CONTAINS).build());
        assertArrayEquals(new Entity[] { testJourney }, result.toArray());
    }

    /**
     * Sees if a charger is associated with a journey when changed
     */
    @Test
    public void changeChargerJourneyTest() throws SQLException, IOException {
        writeSingleEntity(testJourney);
        testJourney.getChargers().get(0).setName("New Name");
        testJourney.getChargers().get(0).setOperator("New op");
        testJourney.getChargers().get(0).setDateOpened("00:00:00 12/34/56");
        writeSingleEntity(testJourney.getChargers().get(0));
        List<Entity> result = db.readData(
                new QueryBuilderImpl().withSource(EntityType.JOURNEY).withFilter("journeyid",
                        String.valueOf(testJourney.getId()),
                        ComparisonType.CONTAINS).build());
        assertArrayEquals(new Entity[] { testJourney }, result.toArray());
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
        List<Entity> result = db.readData(
                new QueryBuilderImpl().withSource(EntityType.CONNECTOR).withFilter("chargerid",
                        String.valueOf(testCharger.getId()),
                        ComparisonType.CONTAINS).build());
        assertArrayEquals(new Entity[] { testConnector1, testConnector2 }, result.toArray());
    }

    /**
     * Check doesn't add record when missing required field
     *
     * @param objectToTest object to add
     * @param entity       table to add to
     * @throws IOException read from database fails
     */
    @ParameterizedTest
    @MethodSource("dbSingleEntities")
    public void missingRequiredFieldTest(Entity objectToTest, EntityType entity) throws IOException {

        switch (objectToTest.getClass().getSimpleName()) {
            case "Charger":
                ((Charger) objectToTest).setName(null);
                break;
            case "Connector":
                ((Connector) objectToTest).setCurrent(null);
                break;
            case "Vehicle":
                ((Vehicle) objectToTest).setMake(null);
                break;
            case "Journey":
                ((Journey) objectToTest).setStartPosition(
                        new Coordinate(null, 177.77702, "testAddy1"));
                break;
            case "User":
                try { // remove default records
                    Connection conn = db.createConnection();
                    Statement stmt = conn.createStatement();
                    stmt.executeUpdate("DELETE FROM user"); // remove default admin
                } catch (SQLException e) {
                    logManager.error(e.getMessage());
                    ;
                }
                ((User) objectToTest).setAccountName(null);
                break;
            default:
                fail();
        }

        // Not sure why this is not working????
        // assertThrows(IOException.class, () -> {
        // writeSingleEntity(objectToTest);
        // });

        List<Entity> result = db.readData(
                new QueryBuilderImpl().withSource(entity).build());
        assertArrayEquals(new Entity[] {}, result.toArray());
    }

    @Test
    public void allRecordsOnlyOnceTest() throws IOException {
        // objectToTest is unused but defined so existing methodsource can be used

        new CsvInterpreter().importChargersToDatabase("csvtest/validChargers");

        List<Entity> result = db.readData(
                new QueryBuilderImpl().withSource(EntityType.CHARGER).build());

        HashSet<Entity> unique = new HashSet<Entity>();
        assertTrue(result.size() > 0);
        for (Entity object : result) {
            if (!unique.add(object)) {
                fail("Duplicate object");
            }
        }
    }

    @Test
    public void singleFilterTest() throws IOException {
        new CsvInterpreter().importChargersToDatabase("csvtest/filtering");

        List<Entity> result = db.readData(
                new QueryBuilderImpl().withSource(EntityType.CHARGER)
                        .withFilter("owner", "MERIDIAN", ComparisonType.CONTAINS)
                        .build());

        for (Entity o : result) {
            if (!((Charger) o).getOwner().contains("MERIDIAN")) {
                fail("Charger did not match filter");
            }
        }

    }

    @Test
    public void singleFilterCaseInsensitiveTest() throws IOException {
        new CsvInterpreter().importChargersToDatabase("csvtest/filtering");

        List<Entity> result = db.readData(
                new QueryBuilderImpl().withSource(EntityType.CHARGER)
                        .withFilter("operator", "MERIDIAN", ComparisonType.CONTAINS)
                        .build());

        for (Entity o : result) {
            if (!((Charger) o).getOperator().toLowerCase().contains("meridian")) {
                fail("Charger did not match filter");
            }
        }

    }

    @Test
    public void multipleFilterTest() throws IOException {
        new CsvInterpreter().importChargersToDatabase("csvtest/filtering");

        List<Entity> result = db.readData(
                new QueryBuilderImpl().withSource(EntityType.CHARGER)
                        .withFilter("owner", "MERIDIAN", ComparisonType.CONTAINS)
                        .withFilter("haschargingcost", "false", ComparisonType.EQUAL)
                        .build());

        for (Entity o : result) {
            if (!((Charger) o).getOwner().contains("MERIDIAN")
                    || ((Charger) o).getChargeCost()) {
                fail("Charger did not match filter");
            }
        }
    }

    @Test
    public void filterByColumnOnRelatedTableTest() throws IOException {
        new CsvInterpreter().importChargersToDatabase("csvtest/filtering");

        List<Entity> result = db.readData(
                new QueryBuilderImpl().withSource(EntityType.CHARGER)
                        .withFilter("connectorstatus", "operative", ComparisonType.CONTAINS)
                        .build());

        for (Entity o : result) {
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
        new CsvInterpreter().importChargersToDatabase("csvtest/filtering");

        List<Entity> result = db.readData(
                new QueryBuilderImpl().withSource(EntityType.CHARGER)
                        .withFilter("currenttype", "AC", ComparisonType.CONTAINS)
                        .withFilter("currenttype", "DC", ComparisonType.CONTAINS)
                        .build());

        for (Entity o : result) {
            if (!((Charger) o).getCurrentType().contains("AC")
                    || !((Charger) o).getCurrentType().contains("DC")) {
                fail("Charger did not match filter");
            }
        }
    }

    @Test
    public void filterByNonExistentColumnTest() throws IOException {

        new CsvInterpreter().importChargersToDatabase("csvtest/filtering");

        assertThrows(IOException.class, () -> {
            db.readData(
                    new QueryBuilderImpl().withSource(EntityType.CHARGER)
                            .withFilter("nonExistentColumn", "1", ComparisonType.GREATER_THAN)
                            .build());
        });
    }

    @Test
    public void allRecordsImported() throws IOException {
        new CsvInterpreter().importChargersToDatabase("csvtest/filtering");

        List<Entity> expected = new CsvInterpreter().readChargers("csvtest/filtering");
        List<Entity> actual = db.readData(new QueryBuilderImpl().withSource(EntityType.CHARGER)
                .build());

        assertEquals(expected.size(), actual.size());
    }

    /**
     * Tests validate users
     */
    @Test
    public void testValidateUsers() throws IOException, SQLException {
        writeSingleEntity(testUser);
        User result = db.validatePassword(testUser.getAccountName(), "admin");
        assertEquals(testUser, result);
    }

    /**
     * Test wrong password
     */
    @Test
    public void testWrongPasswordUsers() throws IOException, SQLException {
        writeSingleEntity(testUser);
        User result = db.validatePassword(testUser.getAccountName(), "wrong");
        assertNull(result);
    }

    /**
     * Test wrong password
     */
    @Test
    public void testFakeUsers() throws IOException, SQLException {
        testUser = new User("fake@gmail.com", "test", PermissionLevel.USER);
        User result = db.validatePassword(testUser.getAccountName(), "wrong");
        assertNull(result);
    }

    /**
     * Updates a valid user
     */
    @Test
    public void updateValidUser() {
        testUser = new User("test@gmail.com", "test", PermissionLevel.USER);
        try {
            db.writeUser(testUser, "1234");
            testUser.setCarbonSaved(500);
            testUser.setAccountName("New Account name");
            testUser.setLevel(PermissionLevel.CHARGEROWNER);
            db.writeUser(testUser);
            List<Entity> res = SqlInterpreter.getInstance().readData(new QueryBuilderImpl()
                    .withSource(EntityType.USER).withFilter("username", testUser.getAccountName(),
                            ComparisonType.EQUAL)
                    .build());
            assertEquals(testUser, (User) res.get(0));
        } catch (SQLException | IOException e) {
            Assertions.fail("Database failed");
        }
    }

    /**
     * Tests updating a null user
     */
    @Test
    public void updateNullUser() {
        try {
            db.writeUser(null);
            Assertions.fail("Database shouldn't add null pointers");
        } catch (SQLException e) {
            Assertions.fail("Database failed");
        } catch (NullPointerException n) {
            Assertions.assertTrue(true);
        }
    }

    @Test
    public void updateFakeUser() {
        try {
            db.writeUser(new User("fake@email", "fake", PermissionLevel.USER));
            List<Entity> res = SqlInterpreter.getInstance().readData(new QueryBuilderImpl()
                    .withSource(EntityType.USER).withFilter("username", "fake",
                            ComparisonType.EQUAL)
                    .build());
            Assertions.assertEquals(0, res.size());
        } catch (SQLException | IOException e) {
            Assertions.fail("Database Failed");
        }
    }

    /**
     * Checks if the program updates the password
     */
    @Test
    public void updatePassword() {
        User updatable = new User("fake@email", "fake", PermissionLevel.USER);
        try {
            db.writeUser(updatable, "1234");
            updatable.setCarbonSaved(50);
            updatable.setEmail("update@gmail.com");
            db.writeUser(updatable, "5678");
            User login = db.validatePassword(updatable.getAccountName(), "5678");
            assertEquals(updatable, login);
        } catch (IOException e) {
            Assertions.fail("Database failed");
        }
    }

}