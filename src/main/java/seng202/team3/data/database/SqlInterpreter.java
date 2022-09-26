
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
import java.util.concurrent.Semaphore;
import javax.management.InstanceAlreadyExistsException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javatuples.Triplet;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.Connector;
import seng202.team3.data.entity.Coordinate;
import seng202.team3.data.entity.Journey;
import seng202.team3.data.entity.PermissionLevel;
import seng202.team3.data.entity.User;
import seng202.team3.data.entity.Vehicle;

/**
 * Class that interacts with the SQLite database
 *
 * @author Matthew Doonan, Harrison Tyson
 * @version 2.1.2
 */
public class SqlInterpreter implements DataReader {

    /**
     * URL of database path
     */
    private final String url;

    /**
     * Logger for SQL interpreter
     */
    private static final Logger logManager = LogManager.getLogger();

    /**
     * Singleton instance
     */
    private static SqlInterpreter instance = null;

    /**
     * Control db write access
     */
    static Semaphore mutex = new Semaphore(1);

    /**
     * Initializes the SqlInterpreter and checks if the url is null
     * calls createAFile and defaultDatabase if the database doesn't exist
     * 
     * @param db the url sent through
     * @throws IOException if default data cannot be populated
     */
    private SqlInterpreter(String db) throws IOException {
        if (db == null || db.isEmpty()) {
            url = getDatabasePath();
            logManager.info(getDatabasePath());
        } else {
            url = db;
        }
        if (!checkExist(url)) {
            createFile(url);
            defaultDatabase();
            if (db == null) {
                try {
                    importDemoData();
                } catch (IOException e) {
                    throw e;
                }
            }
        }
    }

    /**
     * Adds all the charger data stored in the CSV file to the database
     *
     * @param source the name of the resource
     * @throws java.io.IOException if any chargers cannot be written.
     */
    public void addChargerCsvToData(String source) throws IOException {
        Query q = new QueryBuilderImpl().withSource(source).build();
        writeCharger(new ArrayList<>(new CsvInterpreter().readData(q, Charger.class)));
    }

    /**
     * Imports default demo data from the CSV into the database
     * Adds user stubs for each owner
     * 
     * @throws IOException db read/write fails
     */
    public void importDemoData() throws IOException {
        List<Object> chargersToImport = new CsvInterpreter().readData(
                new QueryBuilderImpl().withSource("charger").build(), Charger.class);
        ArrayList<String> owners = new ArrayList<>();
        for (Object o : chargersToImport) {
            if (!owners.contains(((Charger) o).getDemoOwner())) {
                owners.add(((Charger) o).getDemoOwner());
            }
        }

        for (int i = 0; i < owners.size(); i++) {
            writeUser(
                    new User(
                            "example" + i + "@fake.com", owners.get(i),
                            PermissionLevel.CHARGEROWNER),
                    "demo");
        }

        for (int i = 0; i < chargersToImport.size(); i++) {
            Charger c = (Charger) chargersToImport.get(i);
            // +2 to account for 0 index and default admin user at userid = 1
            c.setOwnerId(owners.indexOf(c.getDemoOwner()) + 2);
        }

        writeCharger(new ArrayList<>(chargersToImport));
    }

    /**
     * Returns the instance of the class
     *
     * @return the instance
     * @throws IOException if the database cannot be reached/populated
     */
    public static SqlInterpreter getInstance() throws IOException {
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
     * @throws javax.management.InstanceAlreadyExistsException if there is already a
     *                                                         singleton
     *                                                         instance
     * @author Morgan English, Dec 21
     * @throws IOException if an error occurs connecting to the database
     */
    public static SqlInterpreter initialiseInstanceWithUrl(String url)
            throws InstanceAlreadyExistsException, IOException {
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
     * @return the new connection
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
     * @throws java.io.IOException if any.
     */
    public void deleteData(String type, int id) throws IOException {
        String idName = "" + type.toLowerCase() + "id";
        String delete = "DELETE FROM " + type.toLowerCase() + " WHERE " + idName + " = " + id + ";";
        try (Connection connection = createConnection();
                Statement stmt = connection.createStatement()) {
            switch (type) {
                case "charger":
                    connection.createStatement()
                            .executeUpdate("DELETE FROM connector WHERE chargerid = " + id + ";");
                    connection.createStatement()
                            .executeUpdate("DELETE FROM stop WHERE chargerid = " + id + ";");
                    break;
                case "connector":
                    if (readData(new QueryBuilderImpl().withSource("connector").build(),
                            Connector.class)
                            .size() == 1) {
                        throw new SQLException(
                                "Cannot delete connector. Charger must have 1 connector");
                    }
                    break;
                case "journey":
                    connection.createStatement()
                            .executeUpdate("DELETE FROM stop WHERE journeyid = " + id + ";");
                    break;
                case "vehicle":
                    connection.createStatement()
                            .executeUpdate("DELETE FROM journey WHERE vehicleid = " + id + ";");
                    break;

                case "user":
                    List<Object> user = readData(new QueryBuilderImpl().withSource("user")
                            .withFilter("userid", " " + id + " ", ComparisonType.EQUAL)
                            .build(), User.class);
                    if (user.size() == 0) {
                        return;
                    }
                    List<Object> charger = readData(new QueryBuilderImpl().withSource("charger")
                            .withFilter("owner", " " + ((User) user.get(0)).getAccountName() + " ",
                                    ComparisonType.CONTAINS)
                            .build(), Charger.class);
                    for (Object o : charger) {
                        if (((Charger) o).getOwner().equals(((User) user.get(0))
                                .getAccountName())) {
                            deleteData("charger", ((Charger) o).getChargerId());
                        }
                    }
                    List<Object> journey = readData(new QueryBuilderImpl().withSource("journey")
                            .withFilter("userid", "" + id + "",
                                    ComparisonType.EQUAL)
                            .build(), Journey.class);
                    for (Object o : journey) {
                        deleteData("journey", ((Journey) o).getJourneyId());
                    }
                    List<Object> vehicles = readData(new QueryBuilderImpl().withSource("vehicle")
                            .withFilter("owner", "" + id + "",
                                    ComparisonType.EQUAL)
                            .build(), Vehicle.class);
                    for (Object o : vehicles) {
                        deleteData("vehicle", ((Vehicle) o).getVehicleId());
                    }
                    break;
                default:
                    break;
            }
            stmt.executeUpdate(delete);
        } catch (SQLException e) {
            throw new IOException(e.getMessage());
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<Object> readData(Query query, Class<?> objectToInterpretAs) throws IOException {
        List<Object> objects = new ArrayList<>();
        String sql = "SELECT * FROM " + query.getSource();

        if (objectToInterpretAs == Charger.class) { // Add connectors to charger reading
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
                case "User":
                    objects = asUser(rs);
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
     * @throws SQLException if sql interaction fails
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
            Statement additional = createConnection().createStatement();

            // // Get connectors
            ResultSet connectorRs = additional
                    .executeQuery("SELECT * FROM connector WHERE chargerid = "
                            + rs.getInt("chargerid") + ";");

            connectors = asConnector(connectorRs);
            connectorRs.close();

            ResultSet userRs = additional
                    .executeQuery("SELECT username FROM user WHERE userid = "
                            + rs.getInt("owner") + ";");

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
            charger.setOwnerId(rs.getInt("owner"));
            charger.setOwner(userRs.getString("username"));
            charger.setHasAttraction(rs.getBoolean("hastouristattraction"));
            charger.setAvailable24Hrs(rs.getBoolean("is24hours"));
            charger.setParkingCost(rs.getBoolean("hascarparkcost"));
            charger.setChargeCost(rs.getBoolean("haschargingcost"));
            for (Object c : connectors) {
                charger.addConnector((Connector) c);
            }
            charger.setCurrentType();

            userRs.close();
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
     * @throws SQLException if sql interaction fails
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
     * @throws SQLException if sql interaction fails
     */
    private List<Object> asVehicle(ResultSet rs) throws SQLException {
        List<Object> vehicles = new ArrayList<>();
        while (rs.next()) {
            Vehicle v = new Vehicle();
            v.setOwner(rs.getInt("owner"));
            v.setMake(rs.getString("make"));
            v.setModel(rs.getString("model"));
            v.setBatteryPercent(rs.getDouble("batteryPercent"));
            v.setMaxRange(rs.getInt("rangeKM"));
            if (rs.getString("imgPath") == null) {
                v.setImgPath(Vehicle.DEFAULTIMGPATH);
            } else {
                v.setImgPath(rs.getString("imgPath"));
            }
            v.setVehicleId(rs.getInt("vehicleid"));
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
     * @throws SQLException if sql interaction fails
     */
    private List<Object> asJourney(ResultSet rs) throws SQLException {
        List<Object> journeys = new ArrayList<>();

        while (rs.next()) {
            Journey journey = new Journey();
            journey.setUser(rs.getInt("userid"));
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
                    .executeQuery("SELECT * FROM vehicle WHERE vehicleid = "
                            + rs.getInt("vehicleid") + ";");

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
     * Reads ResultSet as user
     * 
     * @param rs ResultSet to read from
     * 
     * @return list of users
     * @throws SQLException if sql interaction fails
     */
    private List<Object> asUser(ResultSet rs) throws SQLException {
        List<Object> users = new ArrayList<>();
        while (rs.next()) {
            User user = new User();
            user.setUserid(rs.getInt("userid"));
            user.setAccountName(rs.getString("username"));
            user.setEmail(rs.getString("email"));
            user.setCarbonSaved(rs.getInt("carbonSaved"));
            user.setLevel(PermissionLevel.fromValue(rs.getInt("permissions")));

            users.add(user);
        }
        return users;
    }

    /**
     * Adds a new charger to the database from a charger object
     *
     * @param c          charger object
     * @param connection db connection
     * @throws java.io.IOException if db writing fails
     */
    public void writeCharger(Connection connection, Charger c) throws IOException {
        String toAdd = "INSERT INTO charger "
                + "(chargerid, x, y, name, operator, owner, address, is24hours, "
                + "carparkcount, hascarparkcost, maxtimelimit, hastouristattraction, latitude, "
                + "longitude, datefirstoperational, haschargingcost, currenttype)"
                + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ON CONFLICT(chargerid) DO UPDATE SET"
                + " x = ?, y = ?, name = ?, operator = ?, owner = ?, address = ?, is24hours = ?, "
                + "carparkcount = ?, hascarparkcost = ?, maxtimelimit = ?, hastouristattraction = ?"
                + ", latitude = ?, longitude = ?, datefirstoperational = ?, haschargingcost = ?, "
                + "currenttype = ?";

        try (PreparedStatement statement = connection.prepareStatement(toAdd)) {
            if (c.getChargerId() == 0) {
                statement.setNull(1, 0);
            } else {
                statement.setInt(1, c.getChargerId());
            }
            double time;
            if (c.getTimeLimit() == 0.0) {
                time = Double.POSITIVE_INFINITY;
            } else {
                time = c.getTimeLimit();
            }
            statement.setDouble(2, c.getLocation().getXpos());
            statement.setDouble(3, c.getLocation().getYpos());
            statement.setString(4, c.getName());
            statement.setString(5, c.getOperator());
            statement.setInt(6, c.getOwnerId());
            statement.setString(7, c.getLocation().getAddress());
            statement.setBoolean(8, c.getAvailable24Hrs());
            statement.setInt(9, c.getAvailableParks());
            statement.setBoolean(10, c.getParkingCost());
            statement.setDouble(11, time);
            statement.setBoolean(12, c.getHasAttraction());
            statement.setDouble(13, c.getLocation().getLat());
            statement.setDouble(14, c.getLocation().getLon());
            statement.setString(15, c.getDateOpened());
            statement.setBoolean(16, c.getChargeCost());
            statement.setString(17, c.getCurrentType());
            statement.setDouble(18, c.getLocation().getXpos());
            statement.setDouble(19, c.getLocation().getYpos());
            statement.setString(20, c.getName());
            statement.setString(21, c.getOperator());
            statement.setInt(22, c.getOwnerId());
            statement.setString(23, c.getLocation().getAddress());
            statement.setBoolean(24, c.getAvailable24Hrs());
            statement.setInt(25, c.getAvailableParks());
            statement.setBoolean(26, c.getParkingCost());
            statement.setDouble(27, time);
            statement.setBoolean(28, c.getHasAttraction());
            statement.setDouble(29, c.getLocation().getLat());
            statement.setDouble(30, c.getLocation().getLon());
            statement.setString(31, c.getDateOpened());
            statement.setBoolean(32, c.getChargeCost());
            statement.setString(33, c.getCurrentType());

            statement.executeUpdate();
            if (c.getChargerId() == 0) {
                c.setChargerId(statement.getGeneratedKeys().getInt(1));
            }
            writeConnector(connection, c.getConnectors(), c.getChargerId());
        } catch (SQLException | NullPointerException e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * Write a charger using the default connection
     *
     * @param c charger to write
     * @throws java.io.IOException fails to write to db
     */
    public void writeCharger(Charger c) throws IOException {
        writeCharger(createConnection(), c);
    }

    /**
     * Receives a list of chargers writes them using threading
     *
     * @param chargers array list of charger objects
     */
    public void writeCharger(ArrayList<Object> chargers) {
        int sizePerThread = chargers.size() / WriteChargerThread.threadCount;
        WriteChargerThread[] activeThreads = new WriteChargerThread[WriteChargerThread.threadCount];

        // Split array into sub arrays per thread
        int i = 0;
        for (; i < WriteChargerThread.threadCount; i++) {
            activeThreads[i] = new WriteChargerThread(new ArrayList<>(
                    chargers.subList(i * sizePerThread,
                            (i + 1) * sizePerThread)));
        }

        // Distribute remaining chargers evenly
        List<Object> remChargers = chargers.subList(i * sizePerThread, chargers.size());
        for (int j = 0; j < remChargers.size(); j++) {
            activeThreads[j % WriteChargerThread.threadCount]
                    .addCharger((Charger) remChargers.get(j));
        }

        for (Thread t : activeThreads) { // Run threads
            t.start();
        }

        for (Thread t : activeThreads) { // Wait for threads to finish
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace(); // TODO: handle exception
            }
        }
    }

    /**
     * Adds connectors to the database
     * Defaults to the most recent charger if chargerId is null (0)
     *
     * @param connection connection to db
     * @param c          a connector object
     * @param chargerId  an Integer with the specified charger id
     * @throws java.io.IOException if any.
     */
    public void writeConnector(Connection connection, Connector c, int chargerId)
            throws IOException {
        String toAdd = "INSERT INTO connector (connectorid, connectorcurrent, connectorpowerdraw, "
                + "count, connectorstatus, chargerid, connectortype) "
                + "values(?,?,?,?,?,?,?) ON CONFLICT(connectorid) DO UPDATE SET "
                + "connectorcurrent = ?, connectorpowerdraw = ?, count = ?,"
                + "connectorstatus = ?, chargerid = ?, connectortype = ?";

        if (chargerId == 0) {
            try (Statement stmt = connection.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT chargerid "
                            + "FROM charger ORDER BY chargerid DESC LIMIT 0,1")) {
                chargerId = rs.getInt("chargerid");

            } catch (SQLException | NullPointerException e) {
                throw new IOException(e.getMessage());
            }
        }

        try (PreparedStatement statement = connection.prepareStatement(toAdd)) {

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
            if (c.getId() == 0) {
                c.setId(statement.getGeneratedKeys().getInt(1));
            }
        } catch (SQLException | NullPointerException e) {
            throw new IOException(e.getMessage());
        }

    }

    /**
     * Recievies a list of connectors and calls writeConnector to add it to the
     * database
     *
     * @see #writeConnector(Connection, Connector, int)
     * @param connectors an Array list of Connector objects
     * @param chargerId  Integer representing the associated charger
     * @param connection a Connection object
     * @throws java.io.IOException if any.
     */
    public void writeConnector(Connection connection, ArrayList<Connector> connectors,
            int chargerId)
            throws IOException {
        for (Connector connector : connectors) {
            writeConnector(connection, connector, chargerId);
        }
    }

    /**
     * Write list of connectors with new connection
     *
     * @see #writeConnector(Connection, Connector, int)
     * @param connectors a {@link java.util.ArrayList} object
     * @param chargerId  a int
     * @throws java.io.IOException if any.
     */
    public void writeConnector(ArrayList<Connector> connectors, int chargerId) throws IOException {
        writeConnector(createConnection(), connectors, chargerId);
    }

    /**
     * Write single connector with new connection
     *
     * @see #writeConnector(Connection, Connector, int)
     * @param connector a {@link seng202.team3.data.entity.Connector} object
     * @param chargerId a int
     * @throws java.io.IOException if any.
     */
    public void writeConnector(Connector connector, int chargerId) throws IOException {
        writeConnector(createConnection(), connector, chargerId);
    }

    /**
     * Adds an object Vehicle to the database
     *
     * @param v the object Vehicle
     * @throws java.io.IOException if any.
     */
    public void writeVehicle(Vehicle v) throws IOException {
        String toAdd = "INSERT INTO vehicle (vehicleid, make, model, rangekm, "
                + "connectorType, batteryPercent, imgPath, owner) values(?,?,?,?,?,?,?,?)"
                + "ON CONFLICT(vehicleid) DO UPDATE SET make = ?, model = ?, "
                + "rangekm = ?, connectorType = ?, batteryPercent = ?, imgPath = ?, owner = ?";
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
            statement.setInt(8, v.getOwner());
            statement.setString(9, v.getMake());
            statement.setString(10, v.getModel());
            statement.setInt(11, v.getMaxRange());
            statement.setString(12, connectors);
            statement.setDouble(13, v.getBatteryPercent());
            statement.setString(14, v.getImgPath());
            statement.setInt(15, v.getOwner());

            statement.executeUpdate();
            if (v.getVehicleId() == 0) {
                v.setVehicleId(statement.getGeneratedKeys().getInt(1));
            }
        } catch (SQLException | NullPointerException e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * Adds list of vehicle objects to the database
     *
     * @param v the list of vehicle objects
     * @throws java.io.IOException cannot write to database
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
     * @throws java.io.IOException if any.
     */
    public void writeJourney(Journey j) throws IOException {
        String toAdd = "INSERT INTO journey (journeyid, vehicleid, startLat, "
                + "startLon, startX, startY, "
                + "endLat, endLon, endX, endY, startDate, endDate, userid) "
                + "values(?,?,?,?,?,?,?,?,?,?,?,?,?) ON CONFLICT(journeyid) DO UPDATE SET "
                + "vehicleid = ?, startLat = ?, startLon = ?, startX = ?,"
                + " startY = ?, endLat = ?, endLon = ?, endX = ?, endY = ?, "
                + "startDate = ?, endDate = ?, userid = ?";
        if (j.getChargers().size() < 1) {
            throw new IOException("Error writing journey. No stops found.");
        } else if (j.getVehicle() == null) {
            throw new IOException("Error writing journey. No Vehicle Attached.");
        }
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
            addJourney.setInt(13, j.getUser());
            addJourney.setInt(14, j.getVehicle().getVehicleId());
            addJourney.setDouble(15, j.getStartPosition().getLat());
            addJourney.setDouble(16, j.getStartPosition().getLon());
            addJourney.setDouble(17, j.getStartPosition().getXpos());
            addJourney.setDouble(18, j.getStartPosition().getYpos());
            addJourney.setDouble(19, j.getEndPosition().getLat());
            addJourney.setDouble(20, j.getEndPosition().getLon());
            addJourney.setDouble(21, j.getEndPosition().getXpos());
            addJourney.setDouble(22, j.getEndPosition().getYpos());
            addJourney.setString(23, j.getStartDate());
            addJourney.setString(24, j.getEndDate());
            addJourney.setInt(25, j.getUser());
            addJourney.executeUpdate();
            if (j.getJourneyId() == 0) {
                j.setJourneyId(addJourney.getGeneratedKeys().getInt(1));
            }

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

    /**
     * Adds object user to the database with password
     * 
     * @param user     the user object
     * @param password the new password for the user
     * @throws IOException to check for errors
     */
    public void writeUser(User user, String password) throws IOException {
        String toAdd = "INSERT INTO user (userid, email, username, password, "
                + "permissions, carbonSaved) "
                + "values(?,?,?,?,?,?) ON CONFLICT(userid) DO UPDATE SET "
                + "email = ?, username = ?, password = ?,"
                + " permissions = ?, carbonSaved = ?";
        try (Connection connection = createConnection();
                PreparedStatement statement = connection.prepareStatement(toAdd)) {
            if (user.getUserid() == 0) {
                statement.setNull(1, 0);
            } else {
                statement.setInt(1, user.getUserid());
            }
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getAccountName());
            statement.setString(4, password);
            statement.setInt(5, user.getLevel().ordinal());
            statement.setDouble(6, user.getCarbonSaved());
            statement.setString(7, user.getEmail());
            statement.setString(8, user.getAccountName());
            statement.setString(9, password);
            statement.setInt(10, user.getLevel().ordinal());
            statement.setDouble(11, user.getCarbonSaved());
            statement.executeUpdate();
            if (user.getUserid() == 0) {
                user.setUserid(statement.getGeneratedKeys().getInt(1));
            }
        } catch (SQLException | NullPointerException e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * Updates existing user (does not require password)
     * 
     * @param user the user object
     * @throws IOException to check for errors
     */
    public void writeUser(User user) throws IOException {
        String toAdd = "UPDATE user SET "
                + "email = ?, username = ?,"
                + " permissions = ?, carbonSaved = ? WHERE userid = ?;";
        try (Connection connection = createConnection();
                PreparedStatement statement = connection.prepareStatement(toAdd)) {
            if (user.getUserid() == 0) {
                statement.setNull(1, 0);
            } else {
                statement.setInt(1, user.getUserid());
            }
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getAccountName());
            statement.setInt(4, user.getLevel().ordinal());
            statement.setDouble(5, user.getCarbonSaved());
            statement.setInt(6, user.getUserid());
            statement.executeUpdate();
            if (user.getUserid() == 0) {
                user.setUserid(statement.getGeneratedKeys().getInt(1));
            }
        } catch (SQLException | NullPointerException e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * Checks a password against the db for the login screen
     * 
     * @param user     user to log in
     * @param password requested password
     * @return if passwords match
     */
    public boolean validatePassword(User user, String password) {
        String correctPassword = null;

        try {
            ResultSet userRs = createConnection().createStatement()
                    .executeQuery("SELECT password FROM user WHERE userid = "
                            + user.getUserid());
            correctPassword = userRs.getString("password");
        } catch (SQLException e) {
            return false;
        }

        return password.equals(correctPassword);
    }

    /**
     * Allows threading for writing chargers to db to improve performance
     * 
     * @author Harrison Tyson
     * @version 1.2.0, Sep 22
     */
    private class WriteChargerThread extends Thread {
        /**
         * Number of threads
         */
        private static int threadCount = 1;

        /**
         * List of chargers to write
         */
        private ArrayList<Object> chargersToWrite;

        /**
         * Thread db connection
         */
        private Connection conn;

        /**
         * Initialize thread
         * 
         * @param chargersToWrite sublist of chargers for thread to write
         */
        private WriteChargerThread(ArrayList<Object> chargersToWrite) {
            this.chargersToWrite = chargersToWrite;
            this.conn = createConnection();
        }

        /**
         * Add a charger to the thread to write
         * 
         * @param c new charger to write
         */
        public void addCharger(Charger c) {
            chargersToWrite.add(c);
        }

        /**
         * Code to execute in the thread
         */
        @Override
        public void run() {
            // Disable auto commiting
            try {
                this.conn.setAutoCommit(false);
            } catch (SQLException e) {
                e.printStackTrace(); // TODO: handle exception
            }

            for (Object c : chargersToWrite) { // Create SQL write statements
                try {
                    writeCharger(conn, (Charger) c);
                } catch (IOException e) {
                    e.printStackTrace(); // TODO: handle this exception
                }
            }

            // Pseudo-batch write all accumulated statements
            try {
                mutex.acquire();
                this.conn.commit();
                mutex.release();
                this.conn.close();
            } catch (SQLException | InterruptedException e) {
                e.printStackTrace(); // TODO: handle exception
            }
        }
    }
}
