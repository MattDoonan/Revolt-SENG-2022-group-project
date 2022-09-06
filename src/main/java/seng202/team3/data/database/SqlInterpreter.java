package seng202.team3.data.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.management.InstanceAlreadyExistsException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.Connector;
import seng202.team3.data.entity.Coordinate;
import seng202.team3.data.entity.Journey;
import seng202.team3.data.entity.Vehicle;

/**
 * Class that interacts with the SQLite database
 * 
 * @author Matthew Doonan
 * @version 1.0.0
 */
public class SqlInterpreter implements DataManager {

    private final String url;

    private static final Logger logManager = LogManager.getLogger();

    private static SqlInterpreter instance = null;

    /**
     * Initializes the SqlInterpreter and checks if the url is null
     * calls createAFile and defaultDatabase if the database doesn't exist
     * 
     * @param db the url sent through
     */
    private SqlInterpreter(String db) {
        if (db == null || db.isEmpty()) {
            url = getDatabasePath();
        } else {
            url = db;
        }
        if (!checkExist(url)) {
            createFile(url);
            defaultDatabase();
            addCsvToData();
        }
    }

    /**
     * Adds all the charger data stored in the CSV file to the database
     */
    public void addCsvToData() {
        Query q = new QueryBuilderImpl().withSource("charger").build();
        ArrayList<Charger> chargerList = new ArrayList<>();
        try {
            for (Object o : new CsvInterpreter().readData(q, Charger.class)) {
                chargerList.add((Charger) o);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        writeCharger(chargerList);
    }

    /**
     * Returns the instance of the class
     * 
     * @return the instance
     */
    public static SqlInterpreter getInstance() {
        if (instance == null) {
            instance = new SqlInterpreter(null);
        }
        return instance;
    }

    /**
     * WARNING Allows for setting specific database url (currently only needed for
     * test databases, but may be useful
     * in future) USE WITH CAUTION. This does not override the current singleton
     * instance so must be the first call.
     * 
     * @param url string url of database to load (this needs to be full url e.g.
     *            "jdbc:sqlite:./src/...")
     * @return current singleton instance
     * @throws InstanceAlreadyExistsException if there is already a
     *                                        singleton
     *                                        instance
     * @author Morgan English, Dec 21
     */
    public static SqlInterpreter initialiseInstanceWithUrl(String url)
            throws InstanceAlreadyExistsException {
        if (instance == null) {
            instance = new SqlInterpreter(url);
        } else {
            throw new InstanceAlreadyExistsException(
                    "Database Manager instance already exists, cannot create with url: " + url);
        }
        return instance;
    }

    /**
     * Gets and then returns the path of the file name as a String
     * 
     * @return String of the file path
     */
    private String getDatabasePath() {
        String path = SqlInterpreter.class.getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath();
        path = URLDecoder.decode(path, StandardCharsets.UTF_8);
        File directory = new File((path));
        return "jdbc:sqlite:" + directory.getParentFile() + "/database.db";
    }

    /**
     * WARNING Sets the current singleton instance to null
     * 
     * @author Morgan English, Dec 21
     */
    public static void removeInstance() {
        instance = null;
    }

    /**
     * Checks if the file already exists in the specified path
     * 
     * @param path the url
     * @return Boolean yes or no if it exists
     */
    private Boolean checkExist(String path) {
        File file = new File(path.substring(12));
        return file.exists();
    }

    /**
     * creates a new database file at a location specified
     * 
     * @param path the location specified
     */
    private void createFile(String path) {
        try (Connection connection = DriverManager.getConnection(path)) {
            if (connection != null) {
                DatabaseMetaData meta = connection.getMetaData();
                String driverLog = String
                        .format("A new database has been created. The driver name is %s",
                                meta.getDriverName());
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
        try {
            InputStream source = getClass().getResourceAsStream("/revoltDatabaseInitializer.sql");
            executeSql(source);
        } catch (NullPointerException e) {
            logManager.error("Error loading database from file source");
        }
    }

    /**
     * Creates connection to the database
     * 
     * @return the connection
     */
    public Connection createConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            logManager.error(e);
        }
        return connection;
    }

    /**
     * Executes all the lines in the SQL file
     * It knows a line ends if it has --SPLIT
     * 
     * @param source the SQL file
     */
    private void executeSql(InputStream source) {
        String line;
        StringBuffer buff = new StringBuffer();
        try (BufferedReader read = new BufferedReader(new InputStreamReader(source))) {
            while ((line = read.readLine()) != null) {
                buff.append(line);
            }
            String[] state = buff.toString().split("--SPLIT");
            try (Connection connection = createConnection();
                    Statement statement = connection.createStatement()) {
                for (String single : state) {
                    statement.executeUpdate(single);
                }
            }
        } catch (FileNotFoundException e) {
            logManager.error("File not found");
        } catch (IOException e) {
            logManager.error("Error working with file");
        } catch (SQLException e) {
            logManager.error("Error executing sql statements");
        }
    }

    /**
     * Deletes data from the database
     * 
     * @param type String of the name of the table
     * @param id   Integer of the id number of the entity
     */
    public void deleteData(String type, int id) {
        String idName = "" + type.toLowerCase() + "ID";
        String delete = "DELETE FROM " + type.toLowerCase() + " WHERE " + idName + " = " + id + ";";
        try (Connection connection = createConnection();
                Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(delete);
        } catch (SQLException e) {
            logManager.error(e);
        }
    }

    @Override
    public List<Object> readData(Query query, Class<?> objectToInterpretAs) throws IOException {
        List<Object> objects = new ArrayList<>();
        String sql = "SELECT * FROM " + query.getSource();

        // TODO: add filters

        sql += ";";
        try (Connection conn = createConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            switch (objectToInterpretAs.getSimpleName()) {
                case "Charger":
                    objects = asCharger(rs);
                    break;
                case "Connector":
                    objects = asConnector(rs);
                    break;
                case "Vehicle":
                    objects = asVehicle(rs);
                    break;
                default: // Gets fields as list of strings
                    while (rs.next()) {
                        ArrayList<String> list = new ArrayList<>();
                        for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                            list.add(rs.getString(i));
                        }
                        objects.add(list);
                    }
            }

        } catch (SQLException e) {
            throw new IOException(e.getMessage());
        }

        return objects;
    }

    /**
     * Reads ResultSet as chargers
     * 
     * @param rs ResultSet to read from
     * 
     * @return list of chargers
     */
    private List<Object> asCharger(ResultSet rs) throws SQLException {
        List<Object> chargers = new ArrayList<>();
        List<Object> connectors = new ArrayList<>();
        while (rs.next()) {
            // Get connectors
            ResultSet connectorRs = createConnection().createStatement()
                    .executeQuery("SELECT * FROM connector WHERE chargerID = "
                            + rs.getInt("chargerID") + ";");

            connectors = asConnector(connectorRs);

            // Make charger
            Charger charger = new Charger();
            charger.setChargerId(rs.getInt("chargerID"));
            charger.setDateOpened(rs.getString("dateOpened"));
            charger.setName(rs.getString("name"));
            charger.setLocation(new Coordinate(
                    rs.getDouble("xPos"),
                    rs.getDouble("yPos"),
                    rs.getDouble("latPos"),
                    rs.getDouble("lonPos"),
                    rs.getString("address")));
            charger.setAvailableParks(rs.getInt("numCarParks"));
            charger.setTimeLimit(rs.getDouble("timeLimit"));
            charger.setOperator(rs.getString("operator"));
            charger.setOwner(rs.getString("owner"));
            charger.setHasAttraction(rs.getBoolean("attraction"));
            charger.setAvailable24Hrs(rs.getBoolean("is24Hrs"));
            charger.setParkingCost(rs.getBoolean("hasCarParkCost"));
            charger.setChargeCost(rs.getBoolean("chargingCost"));
            for (Object c : connectors) {
                charger.addConnector((Connector) c);
            }

            chargers.add(charger);
        }

        return chargers;
    }

    /**
     * Reads ResultSet as connectors
     * 
     * @param rs ResultSet to read from
     * 
     * @return list of connectors
     */
    private List<Object> asConnector(ResultSet rs) throws SQLException {
        List<Object> connectors = new ArrayList<>();
        while (rs.next()) {
            connectors.add(new Connector(
                    rs.getString("type"),
                    rs.getString("power"),
                    rs.getString("isOperational"),
                    rs.getString("currentType"),
                    rs.getInt("count"),
                    rs.getInt("connectorID")));
        }
        return connectors;
    }

    /**
     * Reads ResultSet as vehicles
     * 
     * @param rs ResultSet to read from
     * 
     * @return list of vehicles
     */
    private List<Object> asVehicle(ResultSet rs) throws SQLException {
        List<Object> vehicles = new ArrayList<>();
        while (rs.next()) {
            Vehicle v = new Vehicle();
            v.setMake(rs.getString("make"));
            v.setModel(rs.getString("model"));
            v.setBatteryPercent(rs.getInt("batteryPercent"));
            v.setMaxRange(rs.getInt("rangeKM"));
            if (rs.getString("imgPath") == null) {
                v.setImgPath(Vehicle.defaultImgPath);
            } else {
                v.setImgPath(rs.getString("imgPath"));
            }
            v.setVehicleId(rs.getInt("vehicleID"));
            v.setConnectors(
                    new ArrayList<String>(Arrays.asList(rs.getString("connectorType").split(","))));
            vehicles.add(v);
        }

        return vehicles;

    }

    /**
     * Adds a new charger to the database from a charger object
     * 
     * @param c charger object
     */
    public void writeCharger(Charger c) {
        String toAdd = "INSERT INTO charger (xPos, yPos, name, operator, owner, address, is24Hrs, "
                + "numCarParks, hasCarParkCost, timeLimit, attraction, latPos, lonPos, dateOpened,"
                + " chargingCost) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
        try (Connection connection = createConnection();
                PreparedStatement statement = connection.prepareStatement(toAdd)) {
            statement.setDouble(1, c.getLocation().getXpos());
            statement.setDouble(2, c.getLocation().getYpos());
            statement.setString(3, c.getName());
            statement.setString(4, c.getOperator());
            statement.setString(5, c.getOwner());
            statement.setString(6, c.getLocation().getAddress());
            statement.setBoolean(7, c.getAvailable24Hrs());
            statement.setInt(8, c.getAvailableParks());
            statement.setBoolean(9, c.getParkingCost());
            statement.setDouble(10, c.getTimeLimit());
            statement.setBoolean(11, c.getHasAttraction());
            statement.setDouble(12, c.getLocation().getLat());
            statement.setDouble(13, c.getLocation().getLon());
            statement.setString(14, c.getDateOpened());
            statement.setBoolean(15, c.getChargeCost());
            statement.executeUpdate();
            writeConnector(c.getConnectors(), statement.getGeneratedKeys().getInt(1));
        } catch (SQLException e) {
            logManager.error(e);
        }
    }

    /**
     * Receives a list of chargers and sends them to writeCharger
     * 
     * @param chargers array list of charger objects
     */
    public void writeCharger(ArrayList<Charger> chargers) {
        for (Charger charger : chargers) {
            writeCharger(charger);
        }
    }

    /**
     * Adds connectors to the database
     * 
     * @param c         a connector object
     * @param chargerId an Integer with the specified charger id
     */
    public void writeConnector(Connector c, int chargerId) {
        String toAdd = "INSERT INTO connector (currentType, power, count, isOperational, "
                + "chargerID, type) values(?,?,?,?,?,?)";
        try (Connection connection = createConnection();
                PreparedStatement statement = connection.prepareStatement(toAdd)) {
            statement.setString(1, c.getCurrent());
            statement.setString(2, c.getPower());
            statement.setInt(3, c.getCount());
            statement.setString(4, c.getStatus());
            statement.setInt(5, chargerId);
            statement.setString(6, c.getType());
            statement.executeUpdate();
        } catch (SQLException e) {
            logManager.error(e);
        }

    }

    /**
     * Recievies a list of connectors and calls writeConnector to add it to the
     * database
     * 
     * @param connectors an Array list of Connector objects
     * @param chargerId  Integer representing the associated charger
     */
    public void writeConnector(ArrayList<Connector> connectors, int chargerId) {
        for (Connector connector : connectors) {
            writeConnector(connector, chargerId);
        }
    }

    /**
     * Adds an object Vehicle to the database
     * 
     * @param v the object Vehicle
     */
    public void writeVehicle(Vehicle v) {
        String toAdd = "INSERT INTO vehicle (make, model, rangeKM, connectorType) values(?,?,?,?)";
        try (Connection connection = createConnection();
                PreparedStatement statement = connection.prepareStatement(toAdd)) {
            statement.setString(1, v.getMake());
            statement.setString(2, v.getModel());
            statement.setInt(3, v.getMaxRange());
            String connectors = "";
            int i = 0;
            for (; i < v.getConnectors().size() - 1; i++) {
                connectors += v.getConnectors().get(i) + ",";
            }
            connectors += v.getConnectors().get(i);
            statement.setString(4, connectors);
            statement.executeUpdate();
        } catch (SQLException e) {
            logManager.error(e);
        }
    }

    /**
     * Adds an object Journey to the database
     * 
     * @param j the object journey
     */
    public void writeJourney(Journey j) {
        String toAdd = "INSERT INTO journey (vehicleID, startLat, startLon, endLat, endLon,"
                + " startDate, finishDate) values(?,?,?,?,?,?,?)";
        try (Connection connection = createConnection();
                PreparedStatement statement = connection.prepareStatement(toAdd)) {
            statement.setInt(1, j.getVehicle().getVehicleId());
            statement.setDouble(2, j.getStartPosition().getLat());
            statement.setDouble(3, j.getStartPosition().getLon());
            statement.setDouble(4, j.getEndPosition().getLat());
            statement.setDouble(5, j.getEndPosition().getLon());
            statement.setString(6, j.getStartDate());
            statement.setString(7, j.getEndDate());
            statement.executeUpdate();
        } catch (SQLException e) {
            logManager.error(e);
        }
    }

}
