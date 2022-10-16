package seng202.team3.unittest.logic.manager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.management.InstanceAlreadyExistsException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.database.util.ComparisonType;
import seng202.team3.data.database.util.QueryBuilder;
import seng202.team3.data.database.util.QueryBuilderImpl;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.Connector;
import seng202.team3.data.entity.Coordinate;
import seng202.team3.data.entity.Entity;
import seng202.team3.data.entity.User;
import seng202.team3.data.entity.util.EntityType;
import seng202.team3.data.entity.util.PermissionLevel;
import seng202.team3.logic.manager.ChargerManager;
import seng202.team3.logic.manager.UserManager;

/**
 * Unit tests for {@link ChargerManager} ChargerManager Class in Logic
 *
 * @author Michelle Hsieh
 * @version 1.0.1, Aug 22
 */
public class ChargerManagerTest {
    /**
     * Logger
     */
    private static final Logger logManager = LogManager.getLogger();

    private ChargerManager manager;
    static Charger charge1;
    static Charger charge2;
    static Charger charge3;
    static Charger charge4;
    static SqlInterpreter db;
    static User testUser;

    /**
     * Makes a test database
     *
     * @throws InstanceAlreadyExistsException If already exists
     * @throws IOException
     */
    @BeforeAll
    static void intialize() throws InstanceAlreadyExistsException, IOException {
        testUser = new User("admin@admin.com", "admin",
                PermissionLevel.USER);
        testUser.setId(1);

        UserManager.setUser(testUser);

        SqlInterpreter.removeInstance();
        db = SqlInterpreter.initialiseInstanceWithUrl(
                "jdbc:sqlite:./target/test-classes/test_database.db");
        db.defaultDatabase();

        Connector dummyConnector1 = new Connector("ChardaMo", "AC", "Available", "123", 3);

        // Christchurch Hospital
        Coordinate coord1 = new Coordinate(-43.53418, 172.627572);
        charge1 = new Charger(
                new ArrayList<>(List.of(dummyConnector1)), "Hosp", coord1, 1, 0.3, "Meridian",
                "2020/1/1 00:00:00", true,
                false, false, false);

        Connector dummyConnector2 = new Connector("ChardaMo", "AC", "Available", "123", 3);

        // Christchurch Boys High School
        Coordinate coord2 = new Coordinate(-43.52425, 172.60019);
        charge2 = new Charger(
                new ArrayList<>(List.of(dummyConnector2)), "Boys", coord2, 2, 3.5, "Someone",
                "2020/1/1 00:00:00", true,
                false, false, false);

        Connector dummyConnector3 = new Connector("ChardaMo", "AC", "Available", "123", 3);

        // Auckland Grammar School
        Coordinate coord3 = new Coordinate(-36.85918, 174.76602);
        charge3 = new Charger(
                new ArrayList<>(List.of(dummyConnector3)), "Grammar", coord3, 5, 1.2, "Else",
                "2020/1/1 00:00:00", true,
                false, false, false);

        Connector dummyConnector4 = new Connector("ChardaMo", "AC", "Available", "123", 3);

        // Otago Boys School
        Coordinate coord4 = new Coordinate(-45.87135, 170.49551);
        charge4 = new Charger(
                new ArrayList<>(List.of(dummyConnector4)), "Otago", coord4, 2, 35.1, "Us",
                "2020/1/1 00:00:00", true, false,
                false, false);

        ArrayList<Entity> chargers = new ArrayList<>();
        chargers.add(charge1);
        chargers.add(charge2);
        chargers.add(charge3);
        chargers.add(charge4);

        db.writeCharger(chargers);

        ArrayList<String> strings = new ArrayList<>();
        strings.add("Hosp");
        strings.add("Boys");
        strings.add("Gramm");
        strings.add("Otago");

        for (int i = 0; i < strings.size(); i++) {
            QueryBuilder mainQuery = new QueryBuilderImpl().withSource(EntityType.CHARGER)
                    .withFilter("name", strings.get(i), ComparisonType.CONTAINS);
            try {
                switch (i) {
                    case 0 -> {
                        charge1 = (Charger) db.readData(mainQuery.build()).get(0);
                    }
                    case 1 -> {
                        charge2 = (Charger) db.readData(mainQuery.build()).get(0);
                    }
                    case 2 -> {
                        charge3 = (Charger) db.readData(mainQuery.build()).get(0);
                    }
                    case 3 -> {
                        charge4 = (Charger) db.readData(mainQuery.build()).get(0);
                    }
                    default -> {
                        break;
                    }
                }
            } catch (IOException e) {
                logManager.error(e.getMessage());
                ;
            }
        }

    }

    /**
     * BeforeEach Charger, Coordinate and Connector setup
     */
    @BeforeEach
    public void setUp() {
        manager = new ChargerManager(charge1);
    }

    /**
     * AfterEach, tears it all down.
     */
    @AfterEach
    public void tearDown() {
        manager = null;
        assertNull(manager);
    }

    /**
     * distanceBetweenChargers tests
     */
    @Test
    public void testDistanceChargersSameLocation() {
        double distance1 = manager.distanceBetweenChargers(charge1);

        // Checks if the distance from hospital to itself is 0
        assertEquals(0.0, distance1, 0.001);
    }

    @Test
    public void testDistanceChargersFurther() {
        double distance1 = manager.distanceBetweenChargers(charge2);
        double distance2 = manager.distanceBetweenChargers(charge3);

        // Checks if the distance from hospital to boys is less than hospital to grammar
        assertTrue(distance2 > distance1);
    }

    @Test
    public void testDistanceChargersSensibleDistance() {
        manager.setSelectedCharger(charge3);
        double distance = manager.distanceBetweenChargers(charge1);

        // Checks if distance is around 764km (results from online calculator)
        assertEquals(764, distance, 0.2);
    }

    /**
     * Tests for getNearbyChargers
     */
    @Test
    public void testAllChargersNearLocation() {
        manager.setSelectedCharger(charge1);
        double distance = 400.0;

        // Coordinate same as hospital
        Coordinate coordinate = new Coordinate(-43.53418, 172.627572);

        ArrayList<Charger> chargerList = new ArrayList<>();
        chargerList.add(charge1);
        chargerList.add(charge2);
        chargerList.add(charge3);
        chargerList.add(charge4);

        ArrayList<Charger> filteredChargers = manager.getNearbyChargers(
                chargerList, coordinate, distance);
        assertEquals(3, filteredChargers.size());
    }

    @Test
    public void sortedDistanceCorrectOrder() {
        manager.setSelectedCharger(charge1);

        double distance = 1000.0;
        // Coordinate same as hospital
        Coordinate coordinate = new Coordinate(-43.53418, 172.627572);

        ArrayList<Charger> chargerList = new ArrayList<>();
        chargerList.add(charge4);
        chargerList.add(charge3);
        chargerList.add(charge2);
        chargerList.add(charge1);

        ArrayList<Charger> filteredChargers = manager
                .getNearbyChargers(chargerList, coordinate, distance);

        assertTrue(filteredChargers.get(0).equals(charge1)
                && filteredChargers.get(1).equals(charge2)
                && filteredChargers.get(2).equals(charge4)
                && filteredChargers.get(3).equals(charge3));
    }
}