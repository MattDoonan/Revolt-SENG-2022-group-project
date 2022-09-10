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
                addChargerCsvToData("charger");
            } catch (IOException e) {
                logManager.log(Level.WARN, "Could not import default csv data");
            }
        }
    }

    /**
     * Adds all the charger data stored in the CSV file to the database
     */
    public void addChargerCsvToData(String source) throws IOException {
        Query q = new QueryBuilderImpl().withSource(source).build();
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

        if (objectToInterpretAs == Charger.class) {
            sql += " INNER JOIN connector ON connector.chargerid = charger.chargerid";
        }

        for (Triplet<String, String, ComparisonType> filter : query.getFilters()) {
            if (query.getFilters().indexOf(filter) == 0) {
                sql += " WHERE ";
            } else {
                sql += " AND ";
            }

            switch (filter.getValue2()) {
                case CONTAINS:
                    sql += "UPPER(" + filter.getValue0() + ") LIKE UPPER('%"
                            + filter.getValue1() + "%')";
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
            v.setBatteryPercent(rs.getDouble("batteryPercent"));
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
     * Adds a new charger to the database from a charger object
     * 
     * @param c charger object
     */
    public void writeCharger(Charger c) throws IOException {
        String toAdd = "INSERT INTO charger "
                + "(chargerid, x, y, name, operator, owner, address, is24hours, "
                + "carparkcount, hascarparkcost, maxtimelimit, hastouristattraction, latitude, "
                + "longitude, datefirstoperational, haschargingcost)"
                + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ON CONFLICT(chargerid) DO UPDATE SET"
                + " x = ?, y = ?, name = ?, operator = ?, owner = ?, address = ?, is24hours = ?, "
                + "carparkcount = ?, hascarparkcost = ?, maxtimelimit = ?, hastouristattraction = ?"
                + ", latitude = ?, longitude = ?, datefirstoperational = ?, haschargingcost = ?";

        try (Connection connection = createConnection();
                PreparedStatement statement = connection.prepareStatement(toAdd)) {
            if (c.getChargerId() == 0) {
                statement.setNull(1, 0);
            } else {
                statement.setInt(1, c.getChargerId());
            }
            statement.setDouble(2, c.getLocation().getXpos());
            statement.setDouble(3, c.getLocation().getYpos());
            statement.setString(4, c.getName());
            statement.setString(5, c.getOperator());
            statement.setString(6, c.getOwner());
            statement.setString(7, c.getLocation().getAddress());
            statement.setBoolean(8, c.getAvailable24Hrs());
            statement.setInt(9, c.getAvailableParks());
            statement.setBoolean(10, c.getParkingCost());
            statement.setDouble(11, c.getTimeLimit());
            statement.setBoolean(12, c.getHasAttraction());
            statement.setDouble(13, c.getLocation().getLat());
            statement.setDouble(14, c.getLocation().getLon());
            statement.setString(15, c.getDateOpened());
            statement.setBoolean(16, c.getChargeCost());
            statement.setDouble(17, c.getLocation().getXpos());
            statement.setDouble(18, c.getLocation().getYpos());
            statement.setString(19, c.getName());
            statement.setString(20, c.getOperator());
            statement.setString(21, c.getOwner());
            statement.setString(22, c.getLocation().getAddress());
            statement.setBoolean(23, c.getAvailable24Hrs());
            statement.setInt(24, c.getAvailableParks());
            statement.setBoolean(25, c.getParkingCost());
            statement.setDouble(26, c.getTimeLimit());
            statement.setBoolean(27, c.getHasAttraction());
            statement.setDouble(28, c.getLocation().getLat());
            statement.setDouble(29, c.getLocation().getLon());
            statement.setString(30, c.getDateOpened());
            statement.setBoolean(31, c.getChargeCost());

            statement.executeUpdate();
            writeConnector(c.getConnectors(), c.getChargerId());
        } catch (SQLException | NullPointerException e) {
            throw new IOException(e.getMessage());
        }
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
     * Adds connectors to the database
     * Defaults to the most recent charger if chargerId is null (0)
     * 
     * @param c         a connector object
     * @param chargerId an Integer with the specified charger id
     */
    public void writeConnector(Connector c, int chargerId) throws IOException {
        String toAdd = "INSERT INTO connector (connectorid, connectorcurrent, connectorpowerdraw, "
                + "count, connectorstatus, chargerid, connectortype) "
                + "values(?,?,?,?,?,?,?) ON CONFLICT(connectorid) DO UPDATE SET "
                + "connectorcurrent = ?, connectorpowerdraw = ?, count = ?,"
                + "connectorstatus = ?, chargerid = ?, connectortype = ?";

        if (chargerId == 0) {
            try (Connection conn = createConnection();
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT chargerid "
                            + "FROM charger ORDER BY chargerid DESC LIMIT 0,1")) {
                chargerId = rs.getInt("chargerid");

            } catch (SQLException | NullPointerException e) {
                throw new IOException(e.getMessage());
            }
        }

        try (Connection connection = createConnection();
                PreparedStatement statement = connection.prepareStatement(toAdd)) {

            if (c.getId() == 0) {
                statement.setNull(1, 0);
            } else {
                statement.setInt(1, c.getId());
            }
            statement.setString(2, c.getCurrent());
            statement.setString(3, c.getPower());
            statement.setInt(4, c.getCount());
            statement.setString(5, c.getStatus());
            statement.setInt(6, chargerId);
            statement.setString(7, c.getType());
            statement.setString(8, c.getCurrent());
            statement.setString(9, c.getPower());
            statement.setInt(10, c.getCount());
            statement.setString(11, c.getStatus());
            statement.setInt(12, chargerId);
            statement.setString(13, c.getType());
            statement.executeUpdate();
        } catch (SQLException | NullPointerException e) {
            throw new IOException(e.getMessage());
        }

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

    /**
     * Adds an object Vehicle to the database
     * 
     * @param v the object Vehicle
     */
    public void writeVehicle(Vehicle v) throws IOException {
        String toAdd = "INSERT INTO vehicle (vehicleid, make, model, rangekm, "
                + "connectorType, batteryPercent, imgPath) values(?,?,?,?,?,?,?)"
                + "ON CONFLICT(vehicleid) DO UPDATE SET make = ?, model = ?, "
                + "rangekm = ?, connectorType = ?, batteryPercent = ?, imgPath = ?";
        try (Connection connection = createConnection();
                PreparedStatement statement = connection.prepareStatement(toAdd)) {
            if (v.getVehicleId() == 0) {
                statement.setNull(1, 0);
            } else {
                statement.setInt(1, v.getVehicleId());
            }
            statement.setString(2, v.getMake());
            statement.setString(3, v.getModel());
            statement.setInt(4, v.getMaxRange());
            String connectors = "";
            int i = 0;
            for (; i < v.getConnectors().size() - 1; i++) {
                connectors += v.getConnectors().get(i) + ",";
            }
            connectors += v.getConnectors().get(i);
            statement.setString(5, connectors);
            statement.setDouble(6, v.getBatteryPercent());
            statement.setString(7, v.getImgPath());
            statement.setString(8, v.getMake());
            statement.setString(9, v.getModel());
            statement.setInt(10, v.getMaxRange());
            statement.setString(11, connectors);
            statement.setDouble(12, v.getBatteryPercent());
            statement.setString(13, v.getImgPath());

            statement.executeUpdate();
        } catch (SQLException | NullPointerException e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * Adds list of vehicle objects to the database
     * 
     * @param v the list of vehicle objects
     * @throws IOException cannot write to database
     */
    public void writeVehicle(List<Vehicle> v) throws IOException {
        for (Vehicle vehicle : v) {
            writeVehicle(vehicle);
        }
    }

    /**
     * Adds an object Journey to the database
     * 
     * @param j the object journey
     */
    public void writeJourney(Journey j) throws IOException {
        String toAdd = "INSERT INTO journey (journeyid, vehicleID, startLat, "
                + "startLon, startX, startY, "
                + "endLat, endLon, endX, endY, startDate, endDate) "
                + "values(?,?,?,?,?,?,?,?,?,?,?,?) ON CONFLICT(journeyid) DO UPDATE SET "
                + "vehicleid = ?, startLat = ?, startLon = ?, startX = ?,"
                + " startY = ?, endLat = ?, endLon = ?, endX = ?, endY = ?, "
                + "startDate = ?, endDate = ?";

        try (Connection connection = createConnection();
                PreparedStatement addJourney = connection.prepareStatement(toAdd)) {
            if (j.getJourneyId() == 0) {
                addJourney.setNull(1, 0);
            } else {
                addJourney.setInt(1, j.getJourneyId());
            }
            addJourney.setInt(2, j.getVehicle().getVehicleId());
            addJourney.setDouble(3, j.getStartPosition().getLat());
            addJourney.setDouble(4, j.getStartPosition().getLon());
            addJourney.setDouble(5, j.getStartPosition().getXpos());
            addJourney.setDouble(6, j.getStartPosition().getYpos());
            addJourney.setDouble(7, j.getEndPosition().getLat());
            addJourney.setDouble(8, j.getEndPosition().getLon());
            addJourney.setDouble(9, j.getEndPosition().getXpos());
            addJourney.setDouble(10, j.getEndPosition().getYpos());
            addJourney.setString(11, j.getStartDate());
            addJourney.setString(12, j.getEndDate());
            addJourney.setInt(13, j.getVehicle().getVehicleId());
            addJourney.setDouble(14, j.getStartPosition().getLat());
            addJourney.setDouble(15, j.getStartPosition().getLon());
            addJourney.setDouble(16, j.getStartPosition().getXpos());
            addJourney.setDouble(17, j.getStartPosition().getYpos());
            addJourney.setDouble(18, j.getEndPosition().getLat());
            addJourney.setDouble(19, j.getEndPosition().getLon());
            addJourney.setDouble(20, j.getEndPosition().getXpos());
            addJourney.setDouble(21, j.getEndPosition().getYpos());
            addJourney.setString(22, j.getStartDate());
            addJourney.setString(23, j.getEndDate());

            if (j.getChargers().size() <= 0) {
                throw new SQLException("Error writing journey. No stops found.");
            }

            addJourney.executeUpdate();
            writeVehicle(j.getVehicle());
            writeCharger(j.getChargers());

            String stopQuery = "INSERT INTO stop (journeyid, chargerid, position) "
                    + "values (?,?,?) ON CONFLICT(journeyid, position) DO UPDATE SET "
                    + "journeyid = ?, chargerid = ?, position = ?";
            PreparedStatement statement = connection.prepareStatement(stopQuery);
            int journeyIdForStop = j.getJourneyId();
            if (j.getJourneyId() == 0) {
                try (Connection conn = createConnection();
                        Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery("SELECT journeyid "
                                + "FROM journey ORDER BY journeyid DESC LIMIT 0,1")) {
                    journeyIdForStop = rs.getInt("journeyid");

                } catch (SQLException e) {
                    throw new IOException(e.getMessage());
                }
            }

            for (int i = 0; i < j.getChargers().size(); i++) {
                statement.setInt(1, journeyIdForStop);
                statement.setInt(2, j.getChargers().get(i).getChargerId());
                statement.setInt(3, i);
                statement.setInt(4, j.getJourneyId());
                statement.setInt(5, j.getChargers().get(i).getChargerId());
                statement.setInt(6, i);
                statement.executeUpdate();
            }

        } catch (SQLException | NullPointerException e) {
            throw new IOException(e.getMessage());
        }
    }

}
