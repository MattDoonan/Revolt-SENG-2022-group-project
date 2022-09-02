package seng202.team3.data.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.*;

/**
 * Class that interacts with the SQLite database
 * @author Matthew Doonan
 * @version 1.0.0
 */
public class SqlInterpreter {

    private final String url;

    private static final Logger logManager = LogManager.getLogger();

    private static SqlInterpreter instance = null;

    /**
     * Initializes the SqlInterpreter and checks if the url is null
     * calls createAFile and defaultDatabase if the database doesn't exist
     * @param db the url sent through
     */
    private SqlInterpreter(String db) {
        if (db==null || db.isEmpty()) {
            url = getDatabasePath();
        } else {
            url = db;
        } if (!checkExist(url)){
            createFile(url);
            defaultDatabase();
        }
    }

    /**
     * Returns the instance of the class
     * @return the instance
     */
    public static SqlInterpreter getInstance() {
        if (instance == null) {
            instance = new SqlInterpreter(null);
        }
        return instance;
    }

    /**
     * Gets and then returns the path of the file name as a String
     * @return String of the file path
     */
    private String getDatabasePath() {
        String path = SqlInterpreter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        path = URLDecoder.decode(path, StandardCharsets.UTF_8);
        File directory = new File((path));
        return "jdbc:sqlite:"+directory.getParentFile()+"/database.db";
    }

    /**
     * Checks if the file already exists in the specified path
     * @param path the url
     * @return Boolean yes or no if it exists
     */
    private Boolean checkExist(String path) {
        File file = new File(path.substring(12));
        return file.exists();
    }

    /**
     * creates a new database file at a location specified
     * @param path the location specified
     */
    private void createFile(String path) {
        try (Connection connection = DriverManager.getConnection(path)) {
            if (connection != null) {
                DatabaseMetaData meta = connection.getMetaData();
                String driverLog = String.format("A new database has been created. The driver name is %s", meta.getDriverName());
                logManager.info(driverLog);
            }
        } catch (SQLException e) {
            logManager.error("Error creating new database file");
            logManager.error(e);
        }
    }

    /**
     * Creates the database if it doesn't exist yet
     * does this by calling the SQL file in resources
     */
    public void defaultDatabase() {
        try{
            InputStream source = getClass().getResourceAsStream("/revoltDatabaseInitializer.sql");
            executeSql(source);
        }catch (NullPointerException e) {
            logManager.error("Error loading database from file source");
        }
    }

    /**
     * Creates connection to the database
     * @return the connection
     */
    public Connection createConnection() {
        Connection connection = null;
        try{
            connection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            logManager.error(e);
        }
        return connection;
    }

    /**
     * Executes all the lines in the SQL file
     * It knows a line ends if it has --SPLIT
     * @param source the SQL file
     */
    private void executeSql(InputStream source) {
        String line;
        StringBuffer buff = new StringBuffer();
        try(BufferedReader read = new BufferedReader(new InputStreamReader(source))) {
            while ((line = read.readLine()) != null) {
                buff.append(line);
            }
            String[] state  =buff.toString().split("--SPLIT");
            try(Connection connection = createConnection();
            Statement statement = connection.createStatement()) {
                for (String single : state) {
                    statement.executeUpdate(single);
                }
            }
        }catch (FileNotFoundException e) {
            logManager.error("File not found");
        }catch (IOException e) {
            logManager.error("Error working with file");
        }catch (SQLException e) {
            logManager.error("Error executing sql statements");
        }
    }

}
