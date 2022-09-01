package seng202.team3.data.database;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.SQLException;

public class SqlInterpreterTest {

    @Test
    public void testNullURL(){
        SqlInterpreter sql =SqlInterpreter.getInstance();
        Assertions.assertNotNull(sql);
        Assertions.assertEquals(sql, SqlInterpreter.getInstance());
    }
}
