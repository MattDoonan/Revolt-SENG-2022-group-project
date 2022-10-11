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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.management.InstanceAlreadyExistsException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javatuples.Triplet;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.Connector;
import seng202.team3.data.entity.Coordinate;
import seng202.team3.data.entity.Entity;
import seng202.team3.data.entity.EntityType;
import seng202.team3.data.entity.Journey;
import seng202.team3.data.entity.PermissionLevel;
import seng202.team3.data.entity.Stop;
import seng202.team3.data.entity.User;
import seng202.team3.data.entity.Vehicle;
import seng202.team3.logic.UserManager;

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
     * Logger
     */
    private static final Logger logManager = LogManager.getLogger();

    /**
     * Singleton instance
     */
    private static SqlInterpreter instance = null;

    /**
     * Number of threads
     */
    private static int threadCount = 6;

    /**
     * Username field constant
     */
    private static final String USERNAME_FIELD = "username";

    /**
     * Owner field constant
     */
    private static final String OWNER_FIELD = "owner";

    /**
     * Userid field constant
     */
    private static final String USERID_FIELD = "userid";

    /**
     * Chargerid field constant
     */
    private static final String CHARGERID_FIELD = "chargerid";

    /**
     * Journeyid field constant
     */
    private static final String JOURNEYID_FIELD = "journeyid";

    /**
     * Initializes the SqlInterpreter and checks if the url is null
     * calls createAFile and defaultDatabase if the database doesn't exist
     * 
     * @param db the url sent through
     * @throws IOException if default data cannot be populated
     * @author Morgan English
     */
    private SqlInterpreter(String db) throws IOException {
        if (db == null || db.isEmpty()) {
            url = getDatabasePath();
            logManager.info(getDatabasePath());
        } else {
            url = db;
        }
        if (Boolean.FALSE.equals(checkExist(url))) {
            createFile(url);
            defaultDatabase();
            if (db == null) {
                importDemoData();
                logManager.info("Demo data has been imported successfully");
            }
        }
    }

    /**
     * Adds all the charger data stored in the CSV file to the database
     *
     * @throws java.io.IOException if any chargers cannot be written.
     * @author Morgan English
     */
    public void addChargerCsvToData() throws IOException {
        Query q = new QueryBuilderImpl().withSource(EntityType.CHARGER).build();
        writeCharger(new CsvInterpreter().readData(q));
    }

    /**
     * Imports default demo data from the CSV into the database
     * Adds user stubs for each owner
     *
     * @throws IOException db read/write fails
     * @author Morgan English
     */
    public void importDemoData() throws IOException {
        List<Entity> chargersToImport = new CsvInterpreter().readData(
                new QueryBuilderImpl().withSource(EntityType.CHARGER).build());
        List<String> owners = new ArrayList<>();
        for (Entity o : chargersToImport) {
            if (!owners.contains(((Charger) o).getDemoOwner())) {
                owners.add(((Charger) o).getDemoOwner());
            }
        }

        for (int i = 0; i < owners.size(); i++) {
            writeUser(
                    new User(
                            "example" + i + "@fake.com", owners.get(i),
                            PermissionLevel.CHARGEROWNER),
                    UserManager.encryptThisString("demo"));
        }

        for (int i = 0; i < chargersToImport.size(); i++) {
            Charger c = (Charger) chargersToImport.get(i);
            // +2 to account for 0 index and default admin user at userid = 1
            c.setOwnerId(owners.indexOf(c.getDemoOwner()) + 2);
        }

        // rewrites Admin with no deformed password
        writeUser((User) readData(new QueryBuilderImpl().withSource(EntityType.USER)
                .withFilter(USERNAME_FIELD, "admin",
                        ComparisonType.EQUAL)
                .build()).get(0), UserManager.encryptThisString("admin"));

        writeCharger(chargersToImport);
    }

    /**
     * Returns the instance of the class
     *
     * @return the instance
     * @throws IOException if the database cannot be reached/populated
     * @author Morgan English
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
     * @author Morgan English
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
     * @author Morgan English
     */
    private Boolean checkExist(String path) {
        File file = new File(path.substring(12));
        return file.exists();
    }

    /**
     * creates a new database file at a location specified
     * 
     * @param path the location specified
     * @author Morgan English
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
     *
     * @author Morgan English
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
     * @author Morgan English
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
     * @author Morgan English
     */
    private void executeSql(InputStream source) {
        String line;
        StringBuilder buff = new StringBuilder();
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
     * @param entity the object to delete
     * @param id     Integer of the id number of the entity
     * @throws java.io.IOException if any.
     */
    public void deleteData(EntityType entity, int id) throws IOException {
        String idName = "" + entity.getAsDatabase() + "id";
        String delete = "DELETE FROM " + entity.getAsDatabase()
                + " WHERE " + idName + " = " + id + ";";
        try (Connection connection = createConnection();
                Statement stmt = connection.createStatement()) {

            switch (entity) {
                case CHARGER:
                    stmt.executeUpdate("DELETE FROM connector WHERE chargerid = " + id + ";");
                    stmt.executeUpdate("DELETE FROM stop WHERE chargerid = " + id + ";");
                    break;
                case CONNECTOR:
                    if (readData(new QueryBuilderImpl()
                            .withSource(EntityType.CONNECTOR).build()).size() == 1) {
                        throw new SQLException(
                                "Cannot delete connector. Charger must have 1 connector");
                    }
                    break;
                case JOURNEY:
                    stmt.executeUpdate("DELETE FROM stop WHERE journeyid = " + id + ";");
                    break;
                case VEHICLE:
                    stmt.executeUpdate("DELETE FROM journey WHERE vehicleid = " + id + ";");
                    break;

                case USER:
                    List<Entity> charger = readData(new QueryBuilderImpl()
                            .withSource(EntityType.CHARGER)
                            .withFilter(OWNER_FIELD, " " + id + " ",
                                    ComparisonType.EQUAL)
                            .build());
                    for (Entity o : charger) {
                        deleteData(EntityType.CHARGER, ((Charger) o).getId());
                    }

                    List<Entity> journey = readData(new QueryBuilderImpl()
                            .withSource(EntityType.JOURNEY)
                            .withFilter(USERID_FIELD, "" + id + "",
                                    ComparisonType.EQUAL)
                            .build());

                    for (Entity o : journey) {
                        deleteData(EntityType.JOURNEY, ((Journey) o).getId());
                    }

                    List<Entity> vehicles = readData(new QueryBuilderImpl()
                            .withSource(EntityType.VEHICLE)
                            .withFilter(OWNER_FIELD, "" + id + "",
                                    ComparisonType.EQUAL)
                            .build());
                    for (Entity o : vehicles) {
                        deleteData(EntityType.VEHICLE, ((Vehicle) o).getId());
                    }
                    break;

                case STOP:
                    stmt.executeUpdate("DELETE FROM stop WHERE stopid = " + id + ";");
                    break;
                default:
                    break;
            }
            stmt.executeUpdate(delete);

        } catch (SQLException e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * Converts filter from query to sql string
     * 
     * @param filter filter to convert
     * @return string representation
     */
    private String filterToString(Triplet<String, String, ComparisonType> filter) {
        String result;
        switch (filter.getValue2()) {
            case CONTAINS:
                result = "UPPER(" + filter.getValue0() + ") LIKE UPPER('%"
                        + filter.getValue1() + "%')";
                break;
            case EQUAL:
                try {
                    Integer.parseInt(filter.getValue1());
                    result = filter.getValue0() + " = " + filter.getValue1();
                } catch (NumberFormatException e) {
                    if (filter.getValue1().equalsIgnoreCase("False")
                            || filter.getValue1().equalsIgnoreCase("True")) {
                        result = filter.getValue0() + " = " + filter.getValue1();
                    } else {
                        result = filter.getValue0() + " = " + "'" + filter.getValue1() + "'";
                    }
                }
                break;
            case GREATER_THAN:
                result = filter.getValue0() + " > " + filter.getValue1();
                break;
            case GREATER_THAN_EQUAL:
                result = filter.getValue0() + " >= " + filter.getValue1();
                break;
            case LESS_THAN:
                result = filter.getValue0() + " < " + filter.getValue1();
                break;
            case LESS_THAN_EQUAL:
                result = filter.getValue0() + " <= " + filter.getValue1();
                break;
            default:
                result = null;
                break;
        }

        return result;
    }

    /** {@inheritDoc} */
    @Override
    public List<Entity> readData(Query query) throws IOException {
        List<Entity> objects = new ArrayList<>();
        StringBuilder sql = new StringBuilder()
                .append("SELECT * FROM " + query.getSource().getAsDatabase());

        if (query.getSource().equals(EntityType.CHARGER)) { // Add connectors to charger reading
            sql.append(" INNER JOIN connector ON connector.chargerid = charger.chargerid");
        }

        for (Triplet<String, String, ComparisonType> filter : query.getFilters()) {
            if (query.getFilters().indexOf(filter) == 0) {
                sql.append(" WHERE ");
            } else {
                sql.append(" AND ");
            }

            sql.append(filterToString(filter));

        }

        sql.append(";");
        try (Connection conn = createConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql.toString())) {

            switch (query.getSource()) {
                case CHARGER:
                    objects = asCharger(rs);
                    break;
                case CONNECTOR:
                    objects = asConnector(rs);
                    break;
                case VEHICLE:
                    objects = asVehicle(rs);
                    break;
                case JOURNEY:
                    objects = asJourney(rs);
                    break;
                case USER:
                    objects = asUser(rs);
                    break;
                case STOP:
                    objects = asStop(rs);
                    break;
                default: // Gets fields as list of strings
                    throw new IOException("Query source not defined");
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
    private List<Entity> asCharger(ResultSet rs) throws SQLException {
        List<Entity> chargers = new ArrayList<>();
        List<Integer> observedChargers = new ArrayList<>();
        while (rs.next()) {
            // Skip record if already processed
            if (observedChargers.contains(rs.getInt(CHARGERID_FIELD))) {
                continue;
            }
            List<Entity> connectors;
            try (Connection connection = createConnection();
                    Statement additional = connection.createStatement()) {
                ResultSet userRs;
                userRs = additional
                        .executeQuery("SELECT username FROM user WHERE userid = "
                                + rs.getInt(OWNER_FIELD) + ";");

                // Make charger
                Charger charger = new Charger();
                charger.setId(rs.getInt(CHARGERID_FIELD));
                charger.setDateOpened(rs.getString("datefirstoperational"));
                charger.setName(rs.getString("name"));
                charger.setLocation(new Coordinate(
                        rs.getDouble("latitude"),
                        rs.getDouble("longitude"),
                        rs.getString("address")));
                charger.setAvailableParks(rs.getInt("carparkcount"));
                charger.setTimeLimit(rs.getDouble("maxtimelimit"));
                charger.setOperator(rs.getString("operator"));
                charger.setOwnerId(rs.getInt(OWNER_FIELD));
                charger.setOwner(userRs.getString(USERNAME_FIELD));
                charger.setHasAttraction(rs.getBoolean("hastouristattraction"));
                charger.setAvailable24Hrs(rs.getBoolean("is24hours"));
                charger.setParkingCost(rs.getBoolean("hascarparkcost"));
                charger.setChargeCost(rs.getBoolean("haschargingcost"));
                charger.setViews(rs.getInt("views"));
                userRs.close();

                // // Get connectors
                ResultSet connectorRs = additional
                        .executeQuery("SELECT * FROM connector WHERE chargerid = "
                                + rs.getInt(CHARGERID_FIELD) + ";");

                connectors = asConnector(connectorRs);
                connectorRs.close();

                for (Entity c : connectors) {
                    charger.addConnector((Connector) c);
                }
                charger.setCurrentType();

                observedChargers.add(charger.getId());
                chargers.add(charger);
            } catch (SQLException e) {
                logManager.error(e.getMessage());
            }

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
    private List<Entity> asConnector(ResultSet rs) throws SQLException {
        List<Entity> connectors = new ArrayList<>();
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
    private List<Entity> asVehicle(ResultSet rs) throws SQLException {
        List<Entity> vehicles = new ArrayList<>();
        while (rs.next()) {
            Vehicle v = new Vehicle();
            v.setOwner(rs.getInt(OWNER_FIELD));
            v.setMake(rs.getString("make"));
            v.setModel(rs.getString("model"));
            v.setMaxRange(rs.getInt("rangeKM"));
            v.setcurrVehicle(rs.getBoolean("currVehicle"));
            if (rs.getString("imgPath") == null) {
                v.setImgPath(Vehicle.DEFAULTIMGPATH);
            } else {
                v.setImgPath(rs.getString("imgPath"));
            }
            v.setId(rs.getInt("vehicleid"));
            v.setConnectors(
                    new ArrayList<>(Arrays.asList(rs.getString("connectorType").split(","))));
            vehicles.add(v);
        }

        return vehicles;

    }

    /**
     * Reads ResultSet as stops
     * 
     * @param rs ResultSet to read from
     * 
     * @return list of stops
     * @throws SQLException if sql interaction fails
     */
    private List<Entity> asStop(ResultSet rs) throws SQLException {
        List<Entity> stops = new ArrayList<>();
        while (rs.next()) {

            rs.getInt(CHARGERID_FIELD);

            if (rs.wasNull()) { // if chargerid is null
                stops.add(new Stop(rs.getDouble("lat"), rs.getDouble("lon")));
            } else {
                try (Connection connection = createConnection();
                        Statement stmt = connection.createStatement();
                        ResultSet chargerRs = stmt.executeQuery("SELECT * FROM charger "
                                + " WHERE chargerid = "
                                + rs.getInt(CHARGERID_FIELD) + ";")) {
                    Stop stop = new Stop((Charger) asCharger(chargerRs).get(0));
                    stop.setId(rs.getInt("stopid"));
                    stops.add(stop);

                } catch (SQLException e) {
                    logManager.error(e.getMessage());
                }
            }

        }

        return stops;

    }

    /**
     * Reads ResultSet as journeys
     * 
     * @param rs ResultSet to read from
     * 
     * @return list of journeys
     * @throws SQLException if sql interaction fails
     */
    private List<Entity> asJourney(ResultSet rs) throws SQLException {
        List<Entity> journeys = new ArrayList<>();

        while (rs.next()) {
            Journey journey = new Journey();
            journey.setUser(rs.getInt(USERID_FIELD));
            journey.setId(rs.getInt(JOURNEYID_FIELD));
            journey.setStartPosition(
                    new Coordinate(
                            rs.getDouble("startLat"), rs.getDouble("startLon")));
            journey.setEndPosition(
                    new Coordinate(
                            rs.getDouble("endLat"), rs.getDouble("endLon")));
            journey.setStartDate(rs.getString("startDate"));
            journey.setTitle(rs.getString("title"));

            List<Entity> stops;
            try (Connection connection = createConnection();
                    Statement statement = connection.createStatement();

                    // Get vehicle
                    ResultSet vehicleRs = statement
                            .executeQuery("SELECT * FROM vehicle WHERE vehicleid = "
                                    + rs.getInt("vehicleid") + ";")) {

                List<Entity> vehicles = asVehicle(vehicleRs);

                if (vehicles.size() == 1) {
                    journey.setVehicle((Vehicle) vehicles.get(0));
                } else {
                    throw new SQLException("Journey object is missing an associated vehicle");
                }

                // Get stops
                ResultSet stopRs = statement
                        .executeQuery("SELECT * FROM stop "
                                + " WHERE journeyid = "
                                + rs.getInt(JOURNEYID_FIELD)
                                + " ORDER BY position ASC;");

                stops = asStop(stopRs);
                stopRs.close();
            }

            for (Entity s : stops) {
                journey.addStop((Stop) s);
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
    private List<Entity> asUser(ResultSet rs) throws SQLException {
        List<Entity> users = new ArrayList<>();
        while (rs.next()) {
            User user = new User();
            user.setId(rs.getInt(USERID_FIELD));
            user.setAccountName(rs.getString(USERNAME_FIELD));
            user.setEmail(rs.getString("email"));
            user.setCarbonSaved(rs.getInt("carbonSaved"));
            user.setLevel(PermissionLevel.fromValue(rs.getInt("permissions")));

            users.add(user);
        }
        return users;
    }

    /**
     * Updates the view count for the given charger
     * Does nothing if charger does not exist in db
     * 
     * @param c charger to update
     * @throws IOException if charger does not exist
     */
    public void updateChargerViews(Charger c) throws IOException {
        try (Connection conn = createConnection();
                Statement stmt = conn.createStatement();) {
            stmt.executeUpdate("UPDATE " + EntityType.CHARGER.getAsDatabase()
                    + " SET views = " + c.getViews()
                    + " WHERE chargerid = " + c.getId());
        } catch (SQLException e) {
            throw new IOException(e.getMessage());
        }
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
                + "(chargerid, name, operator, owner, address, is24hours, "
                + "carparkcount, hascarparkcost, maxtimelimit, hastouristattraction, latitude, "
                + "longitude, datefirstoperational, haschargingcost, currenttype, views)"
                + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ON CONFLICT(chargerid) DO UPDATE SET"
                + " name = ?, operator = ?, owner = ?, address = ?, is24hours = ?, "
                + "carparkcount = ?, hascarparkcost = ?, maxtimelimit = ?, hastouristattraction = ?"
                + ", latitude = ?, longitude = ?, datefirstoperational = ?, haschargingcost = ?, "
                + "currenttype = ?, views = ?";

        try (PreparedStatement statement = connection.prepareStatement(toAdd)) {

            if (c.getId() == 0) {
                statement.setNull(1, 0);
            } else {
                statement.setInt(1, c.getId());
            }
            double time;
            if (c.getTimeLimit() == 0.0) {
                time = Double.POSITIVE_INFINITY;
            } else {
                time = c.getTimeLimit();
            }
            statement.setString(2, c.getName());
            statement.setString(3, c.getOperator());
            statement.setInt(4, c.getOwnerId());
            statement.setString(5, c.getLocation().getAddress());
            statement.setBoolean(6, c.getAvailable24Hrs());
            statement.setInt(7, c.getAvailableParks());
            statement.setBoolean(8, c.getParkingCost());
            statement.setDouble(9, time);
            statement.setBoolean(10, c.getHasAttraction());
            statement.setDouble(11, c.getLocation().getLat());
            statement.setDouble(12, c.getLocation().getLon());
            statement.setString(13, c.getDateOpened());
            statement.setBoolean(14, c.getChargeCost());
            statement.setString(15, c.getCurrentType());
            statement.setInt(16, c.getViews());
            statement.setString(17, c.getName());
            statement.setString(18, c.getOperator());
            statement.setInt(19, c.getOwnerId());
            statement.setString(20, c.getLocation().getAddress());
            statement.setBoolean(21, c.getAvailable24Hrs());
            statement.setInt(22, c.getAvailableParks());
            statement.setBoolean(23, c.getParkingCost());
            statement.setDouble(24, time);
            statement.setBoolean(25, c.getHasAttraction());
            statement.setDouble(26, c.getLocation().getLat());
            statement.setDouble(27, c.getLocation().getLon());
            statement.setString(28, c.getDateOpened());
            statement.setBoolean(29, c.getChargeCost());
            statement.setString(30, c.getCurrentType());
            statement.setInt(31, c.getViews());

            statement.executeUpdate();
            if (c.getId() == 0) {
                c.setId(statement.getGeneratedKeys().getInt(1));
            }

            writeConnector(connection, c.getConnectors(), c.getId());

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

        try (Connection conn = createConnection()) {
            writeCharger(conn, c);

        } catch (SQLException e) {
            logManager.error(e.getMessage());
        }
    }

    /**
     * Receives a list of chargers writes them using threading
     *
     * @param chargers array list of charger objects
     */
    public void writeCharger(List<Entity> chargers) {
        int sizePerThread = chargers.size() / threadCount;
        WriteChargerThread[] activeThreads = new WriteChargerThread[threadCount];

        // Split array into sub arrays per thread
        int i = 0;
        for (; i < threadCount; i++) {
            activeThreads[i] = new WriteChargerThread(
                    new ArrayList<>(chargers.subList(i * sizePerThread,
                            (i + 1) * sizePerThread)));
        }

        // Distribute remaining chargers evenly
        List<Entity> remChargers = chargers.subList(i * sizePerThread, chargers.size());
        for (int j = 0; j < remChargers.size(); j++) {
            activeThreads[j % threadCount]
                    .addCharger((Charger) remChargers.get(j));
        }

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        for (WriteChargerThread t : activeThreads) {
            Runnable worker = t;
            executor.execute(worker);
        }

        executor.shutdown();
        while (!executor.isTerminated()) {
            // Wait for threads
        }
        logManager.info("All charger write threads have terminated");
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

                chargerId = rs.getInt(CHARGERID_FIELD);

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
     * @param connectors a List of Connector objects
     * @param chargerId  Integer representing the associated charger
     * @param connection a Connection object
     * @throws java.io.IOException if any.
     */
    public void writeConnector(Connection connection, List<Connector> connectors,
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
     * @param connectors a {@link java.util.List} object
     * @param chargerId  a int
     * @throws java.io.IOException if any.
     */
    public void writeConnector(List<Connector> connectors, int chargerId) throws IOException {

        try (Connection conn = createConnection()) {
            writeConnector(conn, connectors, chargerId);

        } catch (SQLException e) {
            logManager.error(e.getMessage());
        }
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
        try (Connection conn = createConnection()) {
            writeConnector(conn, connector, chargerId);

        } catch (SQLException e) {
            logManager.error(e.getMessage());
        }
    }

    /**
     * Adds an object Vehicle to the database
     *
     * @param v the object Vehicle
     * @throws java.io.IOException if any.
     */
    public void writeVehicle(Vehicle v) throws IOException {
        String toAdd = "INSERT INTO vehicle (vehicleid, make, model, rangekm, "
                + "connectorType, imgPath, owner, currVehicle) "
                + "values(?,?,?,?,?,?,?,?)"
                + "ON CONFLICT(vehicleid) DO UPDATE SET make = ?, model = ?, "
                + "rangekm = ?, connectorType = ?, imgPath = ?, owner = ?, "
                + "currVehicle = ?";
        try (Connection connection = createConnection();
                PreparedStatement statement = connection.prepareStatement(toAdd)) {

            if (v.getId() == 0) {
                statement.setNull(1, 0);
            } else {
                statement.setInt(1, v.getId());
            }
            statement.setString(2, v.getMake());
            statement.setString(3, v.getModel());
            statement.setInt(4, v.getMaxRange());
            StringBuilder connectors = new StringBuilder();
            int i = 0;
            for (; i < v.getConnectors().size() - 1; i++) {
                connectors.append(v.getConnectors().get(i) + ",");
            }
            connectors.append(v.getConnectors().get(i));
            statement.setString(5, connectors.toString());
            statement.setString(6, v.getImgPath());
            statement.setInt(7, v.getOwner());
            statement.setBoolean(8, v.getCurrVehicle());
            statement.setString(9, v.getMake());
            statement.setString(10, v.getModel());
            statement.setInt(11, v.getMaxRange());
            statement.setString(12, connectors.toString());
            statement.setString(13, v.getImgPath());
            statement.setInt(14, v.getOwner());
            statement.setBoolean(15, v.getCurrVehicle());

            statement.executeUpdate();
            if (v.getId() == 0) {
                v.setId(statement.getGeneratedKeys().getInt(1));
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
                + "startLon, "
                + "endLat, endLon, startDate, title, userid) "
                + "values(?,?,?,?,?,?,?,?,?) ON CONFLICT(journeyid) DO UPDATE SET "
                + "vehicleid = ?, startLat = ?, startLon = ?, endLat = ?, endLon = ?, "
                + "startDate = ?, title = ?, userid = ?";
        if (j.getVehicle() == null) {
            throw new IOException("Error writing journey. No Vehicle Attached.");
        }
        if (j.getStartPosition() == null || j.getEndPosition() == null) {
            throw new IOException("Error writing journey. Start or end");
        }
        try (Connection connection = createConnection();
                PreparedStatement addJourney = connection.prepareStatement(toAdd)) {

            if (j.getId() == 0) {
                addJourney.setNull(1, 0);
            } else {
                addJourney.setInt(1, j.getId());
            }
            addJourney.setInt(2, j.getVehicle().getId());
            addJourney.setDouble(3, j.getStartPosition().getLat());
            addJourney.setDouble(4, j.getStartPosition().getLon());
            addJourney.setDouble(5, j.getEndPosition().getLat());
            addJourney.setDouble(6, j.getEndPosition().getLon());
            addJourney.setString(7, j.getStartDate());
            addJourney.setString(8, j.getTitle());
            addJourney.setInt(9, j.getUser());
            addJourney.setInt(10, j.getVehicle().getId());
            addJourney.setDouble(11, j.getStartPosition().getLat());
            addJourney.setDouble(12, j.getStartPosition().getLon());
            addJourney.setDouble(13, j.getEndPosition().getLat());
            addJourney.setDouble(14, j.getEndPosition().getLon());
            addJourney.setString(15, j.getStartDate());
            addJourney.setString(16, j.getTitle());
            addJourney.setInt(17, j.getUser());
            addJourney.executeUpdate();
            if (j.getId() == 0) {
                j.setId(addJourney.getGeneratedKeys().getInt(1));
            }

            int journeyIdForStop = j.getId();
            if (j.getId() == 0) {
                journeyIdForStop = getMostRecentJourneyId(connection);
            }

            writeStops(connection, j.getStops(), journeyIdForStop);

        } catch (SQLException | NullPointerException e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * Writes stops to the database
     * 
     * @param connection connection to the database
     * @param stops      stops to write
     * @param journeyId  journey to attach stops to
     * @throws SQLException if db write doesn't exist
     */
    private void writeStops(Connection connection, List<Stop> stops, int journeyId)
            throws SQLException {
        String stopQuery = "INSERT INTO stop (stopid, journeyid, chargerid, position,"
                + " lat, lon) "
                + "values (?,?,?,?,?,?) ON CONFLICT(stopid) DO UPDATE SET "
                + "stopid = ?, journeyid = ?, chargerid = ?, position = ?, lat = ?, lon = ?";
        try (PreparedStatement statement = connection.prepareStatement(stopQuery)) {

            for (int i = 0; i < stops.size(); i++) {
                if (stops.get(i).getId() == 0) {
                    statement.setNull(1, 0);
                } else {
                    statement.setInt(1, stops.get(i).getId());
                }
                statement.setInt(2, journeyId);

                if (stops.get(i).getCharger() != null) {
                    statement.setInt(3, stops.get(i).getCharger().getId());
                } else {
                    statement.setNull(3, 0);
                }

                statement.setInt(4, i);
                statement.setDouble(5, stops.get(i).getLocation().getLat());
                statement.setDouble(6, stops.get(i).getLocation().getLon());
                if (stops.get(i).getId() == 0) {
                    statement.setNull(7, 0);
                } else {
                    statement.setInt(7, stops.get(i).getId());
                }
                statement.setInt(8, journeyId);

                if (stops.get(i).getCharger() != null) {
                    statement.setInt(9, stops.get(i).getCharger().getId());
                } else {
                    statement.setNull(9, 0);
                }

                statement.setInt(10, i);
                statement.setDouble(11, stops.get(i).getLocation().getLat());
                statement.setDouble(12, stops.get(i).getLocation().getLon());
                statement.executeUpdate();
            }
        }
    }

    /**
     * Gets the most recent journey id
     * 
     * @param conn connection the the database
     * @return id of the most recent journey
     * @throws IOException if db interaction fails
     */
    public int getMostRecentJourneyId(Connection conn) throws IOException {
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT journeyid "
                        + "FROM journey ORDER BY journeyid DESC LIMIT 0,1")) {

            return rs.getInt(JOURNEYID_FIELD);

        } catch (SQLException e) {
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

            if (user.getId() == 0) {
                statement.setNull(1, 0);
            } else {
                statement.setInt(1, user.getId());
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
            if (user.getId() == 0) {
                user.setId(statement.getGeneratedKeys().getInt(1));
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

            statement.setString(1, user.getEmail());
            statement.setString(2, user.getAccountName());
            statement.setInt(3, user.getLevel().ordinal());
            statement.setDouble(4, user.getCarbonSaved());
            statement.setInt(5, user.getId());
            statement.executeUpdate();
            if (user.getId() == 0) {
                user.setId(statement.getGeneratedKeys().getInt(1));
            }

        } catch (SQLException e) {
            logManager.error(e.getMessage());
            throw new IOException(e);
        }

    }

    /**
     * Checks a password against the db for the login screen
     *
     * @param username user to log in
     * @param password requested password
     * @return if passwords match
     * @throws IOException if interaction fails
     */
    public User validatePassword(String username, String password)
            throws IOException {
        String correctPassword = null;
        try (Connection conn = createConnection();
                Statement stmt = conn.createStatement();
                ResultSet userRs = stmt.executeQuery("SELECT password FROM user WHERE username = '"
                        + username + "' ")) {

            correctPassword = userRs.getString("password");

        } catch (SQLException e) {
            throw new IOException(e.getMessage());
        }
        if (correctPassword == null) {
            return null;
        }
        if (password.equals(correctPassword)) {
            List<Entity> result = readData(new QueryBuilderImpl().withSource(EntityType.USER)
                    .withFilter(USERNAME_FIELD, username, ComparisonType.EQUAL)
                    .build());
            if (result.size() == 1) {
                return (User) result.get(0);
            }
        }
        return null;
    }

    /**
     * Allows threading for writing chargers to db to improve performance
     * 
     * @author Harrison Tyson
     * @version 1.2.0, Sep 22
     */
    private class WriteChargerThread extends Thread {

        /**
         * List of chargers to write
         */
        private List<Entity> chargersToWrite;

        /**
         * Initialize thread
         * 
         * @param chargersToWrite sublist of chargers for thread to write
         */
        private WriteChargerThread(List<Entity> chargersToWrite) {
            this.chargersToWrite = chargersToWrite;
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
            try (Connection conn = createConnection()) {
                conn.setAutoCommit(false);

                for (Entity c : chargersToWrite) { // Create SQL write statements
                    writeCharger(conn, (Charger) c);
                }

                // Pseudo-batch write all accumulated statements
                conn.commit();

            } catch (IOException | SQLException e) {
                logManager.error(e.getMessage());
                Thread.currentThread().interrupt();
            }

        }
    }
}
