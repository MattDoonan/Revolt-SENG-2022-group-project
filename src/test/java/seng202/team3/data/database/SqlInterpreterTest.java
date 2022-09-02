package seng202.team3.data.database;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Tests for SqlInterpreter {@link SqlInterpreter} Class
 *
 * @author Matthew Doonan
 * @version 1.0.0, Sep 2
 */
public class SqlInterpreterTest {

    @Test
    public void testNullURL(){
        SqlInterpreter sql =SqlInterpreter.getInstance();
        Assertions.assertNotNull(sql);
        Assertions.assertEquals(sql, SqlInterpreter.getInstance());
    }

    @Test
    void testDatabaseConnection() throws SQLException {
        SqlInterpreter databaseManager = SqlInterpreter.getInstance();
        Connection connection = databaseManager.createConnection();
        Assertions.assertNotNull(connection);
        Assertions.assertNotNull(connection.getMetaData());
        connection.close();
    }
}
