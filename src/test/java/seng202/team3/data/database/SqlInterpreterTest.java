package seng202.team3.data.database;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.management.InstanceAlreadyExistsException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

    /*
     * TODO: database currently has sample data.
     * Test database should be empty and add sample data to it before each READ test
     */

    @BeforeAll
    static void setup() throws InstanceAlreadyExistsException {
        SqlInterpreter.removeInstance();
        db = SqlInterpreter.initialiseInstanceWithUrl(
                "jdbc:sqlite:./src/test/resources/test_database.db");

        testConnector1 = new Connector("ChardaMo", "AC", "Available", "123", 3);
        testConnector1.setId(1);
        testConnector2 = new Connector("ChardaMo", "AC", "Available", "420", 1);
        testConnector2.setId(2);

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
        testCharger.setChargerId(1);

        testVehicle = new Vehicle(
                "Nissan",
                "Leaf",
                100,
                new ArrayList<String>(Arrays.asList("ChardaMo", "Type 2 Socketed")));
        testVehicle.setVehicleId(1);
    }

    @BeforeEach
    void reset() {
        db.defaultDatabase();
        // Connector dummyConnector1 =
        // ArrayList<Connector> connectorList = new ArrayList<>(1);
        // connectorList.add(dummyConnector1);
        // Coordinate coord = new Coordinate(4.8, 6.2, -32.85658, 177.77702,
        // "testAddy1");
        // Charger c = new Charger(connectorList, "Test1", coord, 1, 0.3,
        // "Meridian", "Meridian", true);
        // Connector dummyConnector2 = new Connector("ChardaMo", "AC", "Available",
        // "22", 3);

        // connectorList = new ArrayList<>(1);
        // connectorList.add(dummyConnector2);
        // coord = new Coordinate(4.9, 5.7, -40.86718, 175.66602, "testAddy2");
        // Charger h = new Charger(connectorList, "Test2", coord, 1, 0.3,
        // "Meridian", "Meridian", true);

        // Connector dummyConnector3 = new Connector("ChardaMo", "DC", "Available",
        // "44", 3);
        // connectorList = new ArrayList<>(1);
        // connectorList.add(dummyConnector3);
        // coord = new Coordinate(3.5, 5.5, -36.85918, 174.76602, "testAddy3");
        // Charger a = new Charger(connectorList, "Test3", coord, 1, 0.3,
        // "Meridian", "Meridian", true);
        // db.writeCharger(new ArrayList<>(Arrays.asList(c, h, a)));
    }

    @Test
    public void testNullUrl() {
        Assertions.assertNotNull(db);
        Assertions.assertEquals(db, SqlInterpreter.getInstance());
    }

    @Test
    void testDatabaseConnection() throws SQLException {
        Connection connection = db.createConnection();
        Assertions.assertNotNull(connection);
        Assertions.assertNotNull(connection.getMetaData());
        connection.close();
    }

    @Test
    public void validReadCharger() throws IOException {
        db.writeCharger(testCharger);
        Query q = new QueryBuilderImpl().withSource("charger").build();
        List<Object> result = db.readData(q, Charger.class);

        assertEquals(1, result.size());
        assertEquals(testCharger, (Charger) result.get(0));
    }

    @Test
    public void validReadConnector() throws IOException {
        db.writeConnector(testConnector1, 1);
        Query q = new QueryBuilderImpl().withSource("connector").build();
        List<Object> result = db.readData(q, Connector.class);

        assertEquals(1, result.size());
        assertEquals(testConnector1, result.get(0));
    }

    @Test
    public void interpretAsWrongObject() {
        db.writeCharger(testCharger);
        Query q = new QueryBuilderImpl().withSource("charger").build();
        assertThrows(IOException.class, () -> {
            db.readData(q, Connector.class);
        });
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
     * Tests if the delete function works for chargers
     */
    @Test
    public void deleteCharger() throws IOException {
        db.writeCharger(testCharger);
        Query q = new QueryBuilderImpl().withSource("charger").build();
        db.deleteData("charger", 1);
        List<Object> result = db.readData(q, Charger.class);
        assertFalse(result.contains(testCharger));
    }

    /**
     * Tests if the delete function works for connectors
     */
    @Test
    public void deleteConnector() throws IOException {
        db.writeConnector(testConnector1, 1);
        Query q = new QueryBuilderImpl().withSource("connector").build();

        db.deleteData("connector", 1);
        List<Object> result = db.readData(q, Connector.class);
        assertFalse(result.contains(testConnector1));
    }

    /**
     * Tests what happens if the id doesn't exist and tries to delete
     */
    @Test
    public void deleteConnectorNotReal() throws IOException {
        Query q = new QueryBuilderImpl().withSource("connector").build();
        List<Object> result = db.readData(q, Connector.class);
        db.deleteData("connector", -1);
        assertArrayEquals(result.toArray(), db.readData(q, Connector.class).toArray());
    }

    /**
     * Tests adding a new charger to the database
     */
    @Test
    public void addNewCharger() throws IOException {
        Connector dummyConnector = new Connector("ChardaMo", "AC", "Available", "123", 3);
        ArrayList<Connector> connectorList = new ArrayList<>(1);
        connectorList.add(dummyConnector);
        Query q = new QueryBuilderImpl().withSource("charger").build();
        List<Object> result = db.readData(q, Charger.class);
        db.writeCharger(testCharger);
        assertEquals(result.size() + 1, db.readData(q, Charger.class).size());
    }

    /**
     * Adds many chargers to the database
     */
    @Test
    public void addManyChargers() throws IOException {
        Connector dummyConnector = new Connector("ChardaMo", "AC", "Available",
                "123", 3);
        ArrayList<Connector> connectorList = new ArrayList<>(1);
        connectorList.add(dummyConnector);
        Coordinate coord = new Coordinate(4.5, 5.7, -36.85918, 174.76602,
                "testAddy");
        Charger c = new Charger(connectorList, "Test1", coord, 1, 0.3,
                "Meridian", "Meridian", "2020/1/1 00:00:00", true, true, false, false);
        Charger x = new Charger(connectorList, "Test2", coord, 5, 1.0,
                "Meridian", "Meridian", "2020/1/1 00:00:00", true, true, false, false);
        Query q = new QueryBuilderImpl().withSource("charger").build();
        int res = db.readData(q, Charger.class).size();
        db.writeCharger(new ArrayList<>(Arrays.asList(c, x)));
        assertEquals(res + 2, db.readData(q, Charger.class).size());
    }

    /**
     * Tests if the Charger is also adding the connectors associated to the database
     */
    @Test
    public void addConnectorsWithCharger() throws IOException {
        Query q = new QueryBuilderImpl().withSource("connector").build();
        db.writeCharger(testCharger);
        List<Object> result = db.readData(q, Connector.class);
        assertArrayEquals(testCharger.getConnectors().toArray(), result.toArray());
    }

    /**
     * Creates a charger then adds a connector to the charger later
     */
    @Test
    public void addLaterConnector() throws IOException {
        db.writeCharger(testCharger);
        Query q = new QueryBuilderImpl().withSource("connector").build();
        List<Object> result = db.readData(q, Connector.class);
        Connector newConnector = new Connector("ChardaMo", "AC", "Available", "33", 5);
        newConnector.setId(3);
        db.writeConnector(newConnector, 1);
        result.add(newConnector);
        assertArrayEquals(result.toArray(), db.readData(q, Connector.class).toArray());
    }

    /**
     * Check that a vehicle can be added to the database
     */
    @Test
    public void addVehicle() throws IOException {
        db.writeVehicle(testVehicle);
        Query q = new QueryBuilderImpl().withSource("vehicle").build();
        List<Object> result = db.readData(q, Vehicle.class);
        assertArrayEquals(new Object[] { testVehicle }, result.toArray());
    }

    /**
     * Cannot test until read is implemented
     */
    @Test
    public void addJourney() throws IOException {
        Coordinate start = new Coordinate(5.6, 7.7, -36.6543, 174.74532, "Start");
        Coordinate end = new Coordinate(5.8, 7.2, -37.45543, 176.45652, "Start");
        Journey j = new Journey(start, end);
        db.writeJourney(j);
    }
}
