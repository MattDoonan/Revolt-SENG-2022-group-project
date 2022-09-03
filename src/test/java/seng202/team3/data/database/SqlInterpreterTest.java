package seng202.team3.data.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    /*
     * TODO: database currently has sample data.
     * Test database should be empty and add sample data to it before each READ test
     */

    @BeforeAll
    static void setup() throws InstanceAlreadyExistsException {
        SqlInterpreter.removeInstance();
        db = SqlInterpreter.initialiseInstanceWithUrl(
                "jdbc:sqlite:./src/test/resources/test_database.db");
    }

    @BeforeEach
    void reset() {
        db.defaultDatabase();
        Connector dummyConnector1 = new Connector("ChardaMo", "AC", "Available", "123", 3);
        ArrayList<Connector> connectorList = new ArrayList<>(1);
        connectorList.add(dummyConnector1);
        Coordinate coord = new Coordinate(4.8, 6.2, -32.85658, 177.77702, "testAddy1");
        Charger c = new Charger(connectorList, "Test1", coord, 1, 0.3,
                "Meridian", "Meridian", true);
        Connector dummyConnector2 = new Connector("ChardaMo", "AC", "Available", "22", 3);

        connectorList = new ArrayList<>(1);
        connectorList.add(dummyConnector2);
        coord = new Coordinate(4.9, 5.7, -40.86718, 175.66602, "testAddy2");
        Charger h = new Charger(connectorList, "Test2", coord, 1, 0.3,
                "Meridian", "Meridian", true);

        Connector dummyConnector3 = new Connector("ChardaMo", "DC", "Available", "44", 3);
        connectorList = new ArrayList<>(1);
        connectorList.add(dummyConnector3);
        coord = new Coordinate(3.5, 5.5, -36.85918, 174.76602, "testAddy3");
        Charger a = new Charger(connectorList, "Test3", coord, 1, 0.3,
                "Meridian", "Meridian", true);
        db.writeCharger(new ArrayList<>(Arrays.asList(c,h,a)));
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
        // TODO: Add single charger then see if the attributes match
        Query q = new QueryBuilderImpl().withSource("charger").build();
        List<Object> result = db.readData(q, Charger.class);

        assertTrue(result.size() > 0);
        for (Object o : result) {
            assertEquals(o.getClass(), Charger.class);
        }
    }

    @Test
    public void validReadConnector() throws IOException {
        // TODO: Add single connector then see if the attributes match
        Query q = new QueryBuilderImpl().withSource("connector").build();
        List<Object> result = db.readData(q, Connector.class);

        assertTrue(result.size() > 0);
        for (Object o : result) {
            assertEquals(o.getClass(), Connector.class);
        }
    }

    @Test
    public void interpretAsWrongObject() {
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
        Query q = new QueryBuilderImpl().withSource("charger").build();
        List<Object> object = db.readData(q,Charger.class);
        db.deleteData("charger", 1);
        assertEquals(object.size()-1, db.readData(q,Charger.class).size());
    }

    /**
     * Tests if the delete function works for connectors
     */
    @Test
    public void deleteConnector() throws IOException {
        Query q = new QueryBuilderImpl().withSource("connector").build();
        List<Object> object = db.readData(q,Connector.class);
        db.deleteData("connector", 1);
        assertEquals(object.size()-1, db.readData(q,Connector.class).size());
    }

    /**
     * Tests what happens if the id doesn't exist and tries to delete
     */
    @Test
    public void deleteConnectorNotReal() throws IOException {
        Query q = new QueryBuilderImpl().withSource("connector").build();
        List<Object> object = db.readData(q,Connector.class);
        db.deleteData("connector", 10000);
        assertEquals(object.size(), db.readData(q,Connector.class).size());
    }

    /**
     * Tests adding a new charger to the database
     */
    @Test
    public void addNewCharger() throws IOException {
        Connector dummyConnector = new Connector("ChardaMo", "AC", "Available", "123", 3);
        ArrayList<Connector> connectorList = new ArrayList<>(1);
        connectorList.add(dummyConnector);
        Coordinate coord = new Coordinate(4.5, 5.7, -36.85918, 174.76602, "testAddy");
        Charger c = new Charger(connectorList, "Test1", coord, 1, 0.3,
                "Meridian", "Meridian", true);
        Query q = new QueryBuilderImpl().withSource("charger").build();
        List<Object> object = db.readData(q,Charger.class);
        db.writeCharger(c);
        assertEquals(object.size()+1, db.readData(q,Charger.class).size());
    }

    /**
     * Adds many chargers to the database
     */
    @Test
    public void addManyChargers() throws IOException {
        Connector dummyConnector = new Connector("ChardaMo", "AC", "Available", "123", 3);
        ArrayList<Connector> connectorList = new ArrayList<>(1);
        connectorList.add(dummyConnector);
        Coordinate coord = new Coordinate(4.5, 5.7, -36.85918, 174.76602, "testAddy");
        Charger c = new Charger(connectorList, "Test1", coord, 1, 0.3,
                "Meridian", "Meridian", true);
        Charger x = new Charger(connectorList, "Test2", coord, 5, 1.0,
                "Meridian", "Meridian", true);
        Query q = new QueryBuilderImpl().withSource("charger").build();
        int res = db.readData(q,Charger.class).size();
        db.writeCharger(new ArrayList<>(Arrays.asList(c,x)));
        Assertions.assertEquals(res+2, db.readData(q,Charger.class).size());
    }

    /**
     * Tests if the Charger is also adding the connectors associated to the database
     */
    @Test
    public void addConnectorsWithCharger() throws IOException {
        Connector dummyConnector1 = new Connector("ChardaMo", "DC", "Available", "69", 4);
        Connector dummyConnector2 = new Connector("ChardaMo", "AC", "Available", "420", 1);
        ArrayList<Connector> connectorList = new ArrayList<>(2);
        connectorList.add(dummyConnector1);
        connectorList.add(dummyConnector2);
        Coordinate coord = new Coordinate(4.5, 5.7, -36.85918, 174.76602, "testAddy");
        Charger c = new Charger(connectorList, "Test1", coord, 1, 0.3,
                "Meridian", "Meridian", true);
        Query q = new QueryBuilderImpl().withSource("connector").build();
        int result = db.readData(q,Connector.class).size();
        db.writeCharger(c);
        Assertions.assertEquals(result+2, db.readData(q,Connector.class).size());
    }

    /**
     * Creates a charger then adds a connector to the charger later
     */
    @Test
    public void addLaterConnector() throws IOException {
        Connector dummyConnector = new Connector("ChardaMo", "AC", "Available", "123", 3);
        ArrayList<Connector> connectorList = new ArrayList<>(1);
        connectorList.add(dummyConnector);
        Coordinate coord = new Coordinate(4.5, 5.7, -36.85918, 174.76602, "testAddy");
        Charger c = new Charger(connectorList, "Test1", coord, 1, 0.3,
                "Meridian", "Meridian", true);
        db.writeCharger(c);
        Query q = new QueryBuilderImpl().withSource("connector").build();
        int result = db.readData(q,Connector.class).size();
        Connector newConnector = new Connector("ChardaMo", "AC", "Available", "33", 5);
        db.writeConnector(newConnector, 1);
        Assertions.assertEquals(result+1, db.readData(q,Connector.class).size());
    }

    /**
     * Cannot test until read function is implemented
     */
    @Test
    public void addVehicle() throws IOException {
        // Query q = new QueryBuilderImpl().withSource("vehicle").build();
        // int result = db.readData(q,Vehicle.class).size();
        ArrayList<String> con = new ArrayList<String>();
        con.add("Con1");
        con.add("Con2");
        Vehicle v = new Vehicle("Telsa", "Y", 300, con);
        db.writeVehicle(v);
        // Assertions.assertEquals(result+1,db.readData(q,Vehicle.class).size());
    }

    /**
     * Cannot test until read is implemented
     */
    @Test
    public void addJourney() throws IOException {
        Coordinate start = new Coordinate(5.6, 7.7, -36.6543, 174.74532, "Start");
        Coordinate end = new Coordinate(5.8, 7.2, -37.45543, 176.45652, "Start");
        Journey j = new Journey(start,end);
        db.writeJourney(j);
    }
}
