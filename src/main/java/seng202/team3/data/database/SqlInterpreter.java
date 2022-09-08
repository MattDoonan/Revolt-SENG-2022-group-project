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
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javatuples.Triplet;
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
            try {
                addCsvToData();
            } catch (IOException e) {
                logManager.log(Level.WARN, "Could not import default csv data");
            }
        }
    }

    /**
     * Adds all the charger data stored in the CSV file to the database
     */
    public void addCsvToData() throws IOException {
        Query q = new QueryBuilderImpl().withSource("charger").build();
        ArrayList<Charger> chargerList = new ArrayList<>();
        for (Object o : new CsvInterpreter().readData(q, Charger.class)) {
            chargerList.add((Charger) o);
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
        String idName = "" + type.toLowerCase() + "id";
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

        switch (objectToInterpretAs.getSimpleName()) {
            case "Charger":
                sql += " INNER JOIN connector ON connector.chargerid = charger.chargerid";
                break;
            default:
                break;

        }

        for (Triplet<String, String, ComparisonType> filter : query.getFilters()) {
            if (query.getFilters().indexOf(filter) == 0) {
                sql += " WHERE ";
            } else {
                sql += " AND ";
            }

            switch (filter.getValue2()) {
                case CONTAINS:
                    sql += filter.getValue0() + " LIKE '%" + filter.getValue1() + "%'";
                    break;
                case EQUAL:
                    sql += filter.getValue0() + " = " + filter.getValue1();
                    break;
                case GREATER_THAN:
                    sql += filter.getValue0() + " > " + filter.getValue1();
                    break;
                case GREATER_THAN_EQUAL:
                    sql += filter.getValue0() + " >= " + filter.getValue1();
                    break;
                case LESS_THAN:
                    sql += filter.getValue0() + " < " + filter.getValue1();
                    break;
                case LESS_THAN_EQUAL:
                    sql += filter.getValue0() + " <= " + filter.getValue1();
                    break;
                default:
                    break;
            }

        }

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
                case "Journey":
                    objects = asJourney(rs);
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
        List<Integer> observedChargers = new ArrayList<>();
        while (rs.next()) {
            // Skip record if already processed
            if (observedChargers.contains(rs.getInt("chargerid"))) {
                continue;
            }

            // Get connectors
            ResultSet connectorRs = createConnection().createStatement()
                    .executeQuery("SELECT * FROM connector WHERE chargerid = "
                            + rs.getInt("chargerid") + ";");

            connectors = asConnector(connectorRs);

            // Make charger
            Charger charger = new Charger();
            charger.setChargerId(rs.getInt("chargerid"));
            charger.setDateOpened(rs.getString("datefirstoperational"));
            charger.setName(rs.getString("name"));
            charger.setLocation(new Coordinate(
                    rs.getDouble("x"),
                    rs.getDouble("y"),
                    rs.getDouble("latitude"),
                    rs.getDouble("longitude"),
                    rs.getString("address")));
            charger.setAvailableParks(rs.getInt("carparkcount"));
            charger.setTimeLimit(rs.getDouble("maxtimelimit"));
            charger.setOperator(rs.getString("operator"));
            charger.setOwner(rs.getString("owner"));
            charger.setHasAttraction(rs.getBoolean("hastouristattraction"));
            charger.setAvailable24Hrs(rs.getBoolean("is24hours"));
            charger.setParkingCost(rs.getBoolean("hascarparkcost"));
            charger.setChargeCost(rs.getBoolean("haschargingcost"));
            for (Object c : connectors) {
                charger.addConnector((Connector) c);
            }
            observedChargers.add(charger.getChargerId());
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
                    rs.getString("connectortype"),
                    rs.getString("connectorpowerdraw"),
                    rs.getString("connectorstatus"),
                    rs.getString("connectorcurrent"),
                    rs.getInt("count"),
                    rs.getInt("connectorid")));
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
     * Reads ResultSet as journeys
     * 
     * @param rs ResultSet to read from
     * 
     * @return list of journeys
     */
    private List<Object> asJourney(ResultSet rs) throws SQLException {
        List<Object> journeys = new ArrayList<>();

        while (rs.next()) {
            Journey journey = new Journey();
            journey.setJourneyId(rs.getInt("journeyid"));
            journey.setStartPosition(
                    new Coordinate(rs.getDouble("startX"), rs.getDouble("startY"),
                            rs.getDouble("startLat"), rs.getDouble("startLon")));
            journey.setEndPosition(
                    new Coordinate(rs.getDouble("endX"), rs.getDouble("endY"),
                            rs.getDouble("endLat"), rs.getDouble("endLon")));
            journey.setStartDate(rs.getString("startDate"));
            journey.setEndDate(rs.getString("endDate"));

            // Get vehicle
            ResultSet vehicleRs = createConnection().createStatement()
                    .executeQuery("SELECT * FROM vehicle WHERE vehicleID = "
                            + rs.getInt("vehicleID") + ";");

            List<Object> vehicles = asVehicle(vehicleRs);
            if (vehicles.size() == 1) {
                journey.setVehicle((Vehicle) vehicles.get(0));
            } else {
                throw new SQLException("Journey object is missing an associated vehicle");
            }

            // Get stops
            ResultSet stopRs = createConnection().createStatement()
                    .executeQuery("SELECT * FROM stop "
                            + " INNER JOIN charger ON charger.chargerid = stop.chargerid"
                            + " WHERE journeyid = "
                            + rs.getInt("journeyid")
                            + " ORDER BY position ASC;");

            List<Object> stops = asCharger(stopRs);
            for (Object c : stops) {
                journey.addCharger((Charger) c);
            }

            journeys.add(journey);

        }

        return journeys;
    }

    /**
     * Creates connection with the database and executes a charger statement
     * Changes if it is an update or add
     *
     * @param state A string of the sql code
     * @param c the object charger
     * @param update Boolean of whether it is update a charger or adding a new charger
     */
    public void chargerStatement(String state, Charger c, Boolean update) throws IOException {
        try (Connection connection = createConnection();
             PreparedStatement statement = connection.prepareStatement(state)) {
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
            statement.setString(16, c.getCurrentType());
            if (update) {
                statement.setInt(17, c.getChargerId());
            }
            statement.executeUpdate();
            if(!update) {
                c.setChargerId(statement.getGeneratedKeys().getInt(1));
                writeConnector(c.getConnectors(), statement.getGeneratedKeys().getInt(1));
            }
        } catch (SQLException e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * Creates the String for the adding a charge in sql then calls charger statement
     * 
     * @param c charger object
     */
    public void writeCharger(Charger c) throws IOException {
        String toAdd = "INSERT INTO charger (x, y, name, operator, owner, address, is24hours, "
                + "carparkcount, hascarparkcost, maxtimelimit, hastouristattraction, latitude, "
                + "longitude, datefirstoperational, haschargingcost, currenttype)"
                + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
        chargerStatement(toAdd, c, false);
    }

    /**
     * Creates the String for updating a charge in sql then calls charger statement
     *
     * @param c a charger object
     */
    public void updateCharger(Charger c) throws IOException {
        String toUpdate = "update charger set x = ?, y = ?, name = ?, operator = ?, owner = ?, address = ?, is24hours = ?, " +
                "carparkcount = ?, hascarparkcost = ?, maxtimelimit = ?, hastouristattraction = ?, latitude = ?, longitude = ?, " +
                "datefirstoperational = ?, haschargingcost = ?, currenttype = ? where chargerid = ?";
        chargerStatement(toUpdate, c, true);
    }

    /**
     * Receives a list of chargers and sends them to writeCharger
     * 
     * @param chargers array list of charger objects
     */
    public void writeCharger(ArrayList<Charger> chargers) throws IOException {
        for (Charger charger : chargers) {
            writeCharger(charger);
        }
    }

    /**
     * Connects to the database and then creates a statement to add or update a connector
     * 
     * @param state the statement String
     * @param c Connector object 
     * @param chargerId integer for the ID of the charger that it is owned by
     * @param update Boolean true if it is an update
     */
    public void connectorStatement(String state, Connector c, int chargerId, Boolean update) throws IOException {
        try (Connection connection = createConnection();
             PreparedStatement statement = connection.prepareStatement(state)) {
            statement.setString(1, c.getCurrent());
            statement.setString(2, c.getPower());
            statement.setInt(3, c.getCount());
            statement.setString(4, c.getStatus());
            statement.setInt(5, chargerId);
            statement.setString(6, c.getType());
            if(update) {
                statement.setInt(7, c.getId());
            }
            statement.executeUpdate();
            if(!update) {
                c.setId(statement.getGeneratedKeys().getInt(1));
            }
        } catch (SQLException e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * Adds connectors to the database
     * 
     * @param c a connector object
     * @param chargerId an Integer with the specified charger id
     */
    public void writeConnector(Connector c, int chargerId) throws IOException {
        String toAdd = "INSERT INTO connector (connectorcurrent, connectorpowerdraw, "
                + "count, connectorstatus, chargerid, connectortype) values(?,?,?,?,?,?)";
        connectorStatement(toAdd, c, chargerId, false);

    }

    /**
     * Updates a connector in the database
     *
     * @param c the connector object
     * @param chargerId the charger id associated with the connector
     */
    public void updateConnector(Connector c, int chargerId) throws IOException {
        String toUpdate = "update connector set connectorcurrent = ?, connectorpowerdraw = ?, count = ?," +
                "connectorstatus = ?, chargerid = ?, connectortype = ? where connectorID = ?";
        connectorStatement(toUpdate, c, chargerId, true);
    }

    /**
     * Recievies a list of connectors and calls writeConnector to add it to the
     * database
     * 
     * @param connectors an Array list of Connector objects
     * @param chargerId  Integer representing the associated charger
     */
    public void writeConnector(ArrayList<Connector> connectors, int chargerId) throws IOException {
        for (Connector connector : connectors) {
            writeConnector(connector, chargerId);
        }
    }

    public void vehicleStatement(String state, Vehicle v, Boolean update) throws IOException {
        try (Connection connection = createConnection();
             PreparedStatement statement = connection.prepareStatement(state)) {
            statement.setString(1, v.getMake());
            statement.setString(2, v.getModel());
            statement.setInt(3, v.getMaxRange());
            StringBuilder connectors = new StringBuilder();
            int i = 0;
            for (; i < v.getConnectors().size() - 1; i++) {
                connectors.append(v.getConnectors().get(i)).append(",");
            }
            connectors.append(v.getConnectors().get(i));
            statement.setString(4, connectors.toString());
            if(update) {
                statement.setInt(5, v.getVehicleId());
            }
            statement.executeUpdate();
            if(!update) {
                v.setVehicleId(statement.getGeneratedKeys().getInt(1));
            }
        } catch (SQLException e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * Adds an object Vehicle to the database
     * 
     * @param v the object Vehicle
     */
    public void writeVehicle(Vehicle v) throws IOException {
        String toAdd = "INSERT INTO vehicle (make, model, rangeKM, connectorType) values(?,?,?,?)";
        vehicleStatement(toAdd, v, false);
    }

    public void updateVehicle(Vehicle v) throws IOException {
        String toUpdate = "update vehicle set make = ?, model = ?, rangekm = ?, connectorType = ? where vehicleid = ?";
        vehicleStatement(toUpdate, v, true);
    }

    /**
     * Adds an object Journey to the database
     * 
     * @param j the object journey
     */
    public void writeJourney(Journey j) throws IOException {
        writeVehicle(j.getVehicle());
        String toAdd = "INSERT INTO journey (vehicleid, startLat, startLon, startX, startY, "
                + "endLat, endLon, endX, endY, startDate, endDate) "
                + "values(?,?,?,?,?,?,?,?,?,?,?);";
        try (Connection connection = createConnection();
                PreparedStatement addJourney = connection.prepareStatement(toAdd)) {
            addJourney.setInt(1, j.getVehicle().getVehicleId());
            addJourney.setDouble(2, j.getStartPosition().getLat());
            addJourney.setDouble(3, j.getStartPosition().getLon());
            addJourney.setDouble(4, j.getStartPosition().getXpos());
            addJourney.setDouble(5, j.getStartPosition().getYpos());
            addJourney.setDouble(6, j.getEndPosition().getLat());
            addJourney.setDouble(7, j.getEndPosition().getLon());
            addJourney.setDouble(8, j.getEndPosition().getXpos());
            addJourney.setDouble(9, j.getEndPosition().getYpos());
            addJourney.setString(10, j.getStartDate());
            addJourney.setString(11, j.getEndDate());
            addJourney.executeUpdate();

            writeCharger(j.getChargers());
            String addStops = "INSERT INTO stop (journeyid, chargerid, position) values ";
            for (int i = 0; i < j.getChargers().size(); i++) {

                if (i != 0) {
                    addStops += ",";
                }
                addStops += "(" + j.getJourneyId() + ","
                        + j.getChargers().get(i).getChargerId() + ","
                        + i + ")";
            }
            addStops += ";";

            if (j.getChargers().size() > 0) {
                connection.createStatement().executeUpdate(addStops);
            } else {
                throw new SQLException("Error writing journey. No stops found.");
            }

        } catch (SQLException e) {
            throw new IOException(e.getMessage());
        }
    }

}
