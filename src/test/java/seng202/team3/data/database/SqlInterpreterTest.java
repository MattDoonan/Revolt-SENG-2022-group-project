package seng202.team3.data.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.management.InstanceAlreadyExistsException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.Connector;
import seng202.team3.data.entity.Coordinate;

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
     * @throws IOException
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
     * @throws IOException
     */
    @Test
    public void deleteConnector() throws IOException {
        Query q = new QueryBuilderImpl().withSource("connector").build();
        List<Object> object = db.readData(q,Connector.class);
        db.deleteData("connector", 2);
        assertEquals(object.size()-1, db.readData(q,Connector.class).size());
    }

    /**
     * Tests what happens if the id doesn't exist and tries to delete
     * @throws IOException
     */
    @Test
    public void deleteConnectorNotReal() throws IOException {
        Query q = new QueryBuilderImpl().withSource("connector").build();
        List<Object> object = db.readData(q,Connector.class);
        db.deleteData("connector", 10);
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
}
