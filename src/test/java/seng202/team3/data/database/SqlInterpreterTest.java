package seng202.team3.data.database;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import seng202.team3.data.entity.*;

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
        testJourney = new Journey(testVehicle,
                new Coordinate(5.6, 7.7, -36.6543, 174.74532),
                new Coordinate(5.8, 7.2, -37.45543, 176.45652),
                "2020/1/1 00:00:00", "2020/1/3 00:00:00");
        testJourney.addCharger(testCharger);
        testJourney.setJourneyId(1);

        testNote = new Note(1,4);
        testNote.setPublicText("This charger is great");
        testNote.setReviewId(1);
    }

    @BeforeEach
    void reset() {
        db.defaultDatabase();
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

    @Test
    public void validReadCharger() throws IOException {
        db.writeCharger(testCharger);
        Query q = new QueryBuilderImpl().withSource("charger").build();
        List<Object> result = db.readData(q, Charger.class);

        assertArrayEquals(new Object[] { testCharger }, result.toArray());
    }

    @Test
    public void validReadConnector() throws IOException {
        db.writeConnector(testConnector1, 1);
        Query q = new QueryBuilderImpl().withSource("connector").build();
        List<Object> result = db.readData(q, Connector.class);

        assertArrayEquals(new Object[] { testConnector1 }, result.toArray());
    }

    @Test
    public void interpretAsWrongObject() throws IOException {
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
     * Adds charger to database then edits some variables anc checks if variables have changed
     */
    @Test
    public void updateCharger() throws IOException {
        db.writeCharger(testCharger);
        testCharger.setOwner("Tesla");
        testCharger.setAvailable24Hrs(false);
        testCharger.setOperator("Seng202");
        db.updateCharger(testCharger);
        Query q = new QueryBuilderImpl().withSource("charger").build();
        List<Object> get = db.readData(q, Charger.class);
        Charger result  = (Charger) get.get(0);
        Assertions.assertEquals("Tesla", result.getOwner());
        Assertions.assertEquals(false, result.getAvailable24Hrs());
        Assertions.assertEquals("Seng202", result.getOperator());
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
     * Checks if updating a connector works
     */
    @Test
    public void updateConnector() throws IOException {
        db.writeCharger(testCharger);
        Query q = new QueryBuilderImpl().withSource("connector").build();
        testConnector1.setPower("UpdatedPower");
        testConnector1.setOperational("out of order");
        testConnector1.setCurrent("UpdatedCurrent");
        db.updateConnector(testConnector1, testCharger.getChargerId());
        List<Object> get = db.readData(q, Connector.class);
        Connector result  = (Connector) get.get(0);
        Assertions.assertEquals("UpdatedPower", result.getPower());
        Assertions.assertEquals("out of order", result.getStatus());
        Assertions.assertEquals("UpdatedCurrent", result.getCurrent());
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

    @Test
    public void updateVehicle() throws IOException {
        db.writeVehicle(testVehicle);
        Query q = new QueryBuilderImpl().withSource("vehicle").build();
        testVehicle.setMake("Updated make");
        testVehicle.setMaxRange(210);
        testVehicle.setModel("Updated model");
        db.updateVehicle(testVehicle);
        List<Object> get = db.readData(q, Vehicle.class);
        Vehicle result = (Vehicle) get.get(0);
        Assertions.assertEquals("Updated make", result.getMake());
        Assertions.assertEquals(210, result.getMaxRange());
        Assertions.assertEquals("Updated model", result.getModel());
    }

    /**
     * Cannot test until read is implemented
     */
    @Test
    public void addJourney() throws IOException {
        db.writeJourney(testJourney);
        Query q = new QueryBuilderImpl().withSource("journey").build();
        List<Object> result = db.readData(q, Journey.class);
        assertArrayEquals(new Object[] { testJourney }, result.toArray());
    }

    /**
     * Cannot implement until read has been added
     * Works checking manually
     */
    @Test
    public void addNote() throws IOException {
        db.writeNote(testNote);
        Query q = new QueryBuilderImpl().withSource("note").build();
    }

    @Test
    public void updateNote() throws IOException {
        db.writeNote(testNote);
        testNote.setPrivateText("Sucks");
        testNote.setRating(2);
        testNote.setPublicText("So bad now");
        db.updateNote(testNote);
    }
}
