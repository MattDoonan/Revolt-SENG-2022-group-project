package seng202.team3.unittest.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.*;
import seng202.team3.logic.ChargerHandler;
import seng202.team3.logic.ChargerManager;
import seng202.team3.logic.GeoLocationHandler;
import seng202.team3.logic.JourneyManager;

import javax.management.InstanceAlreadyExistsException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Unit tests for {@link JourneyManager journeyManager} logic class
 *
 * @author Matthew Doonan
 * @version 1.0.0, Oct 22
 */
public class JourneyManagerTest {

    private static final Logger logManager = LogManager.getLogger();

    static Charger testCharger;
    private Coordinate testCoordinateStart;
    private Coordinate testCoordinateEnd;
    private Coordinate testCoordinateCharger;
    private Vehicle testVehicle;
    private Journey testJourneyOne;
    private Connector testConnector1;
    static User testUserOne;
    private JourneyManager journeyManager;


    /**
     * null
     */
    @BeforeEach
    public void setUp() throws InstanceAlreadyExistsException, IOException {
        SqlInterpreter.removeInstance();
        SqlInterpreter.initialiseInstanceWithUrl(
                "jdbc:sqlite:./target/test-classes/test_database.db");
        SqlInterpreter.getInstance().defaultDatabase();

        testCoordinateStart = new Coordinate(2.9342, 5.1247);
        testCoordinateCharger = new Coordinate(2.9442, 5.1347);
        testCoordinateEnd = new Coordinate(2.9882, 5.1447);

        testConnector1 = new Connector("ChardaMo", "AC", "Available", "123", 3);
        testCharger = new Charger(new ArrayList<Connector>(
                Arrays.asList(testConnector1)),
                "Test2",
                testCoordinateCharger,
                1,
                0.3,
                "Meridian",
                "2020/05/01 00:00:00+00",
                false,
                false,
                true,
                false);
        testCharger.setOwner("admin");
        testCharger.setOwnerId(1);
        SqlInterpreter.getInstance().writeCharger(testCharger);

        testUserOne = new User("test@admin.com", "testUser",
                PermissionLevel.USER);
        SqlInterpreter.getInstance().writeUser(testUserOne);

        testVehicle = new Vehicle("TestMake", "TestModel",
                555, new ArrayList<String>(Arrays.asList("Type 1 Tethered")));
        testVehicle.setOwner(testUserOne.getId());
        SqlInterpreter.getInstance().writeVehicle(testVehicle);

        testJourneyOne = new Journey(testVehicle, testCoordinateStart,
                testCoordinateEnd, "10/10/2002", "Name");
        testJourneyOne.addStop(new Stop(testCharger));
        testJourneyOne.setUser(testUserOne.getId());
        SqlInterpreter.getInstance().writeJourney(testJourneyOne);
        journeyManager = new JourneyManager();

    }

    /**
     * AfterEach, sets everything to null.
     */
    @AfterEach
    public void tearDown() {
        testCharger = null;
        testCoordinateStart = null;
        testCoordinateEnd = null;
        testCoordinateCharger = null;
        testVehicle = null;
        testJourneyOne = null;
        testConnector1 = null;
        testUserOne = null;
        journeyManager = null;
    }

    @Test
    public void testRangeChargers() {
        journeyManager.makeAllChargers();
        journeyManager.setCurrentCoordinate(testCoordinateStart);
        journeyManager.setDesiredRange(100.0);
        journeyManager.makeRangeChargers();
        ArrayList<Charger> result = new ChargerManager().getNearbyChargers(
                new ArrayList<Charger>(journeyManager.getData()), testCoordinateStart, 100.0);
        Assertions.assertEquals(result, journeyManager.getRangeChargers());
    }

    @Test
    public void testRangeNullCoordinate() {
        journeyManager.makeAllChargers();
        journeyManager.setDesiredRange(100.0);
        journeyManager.makeRangeChargers();
        Assertions.assertEquals(journeyManager.getData(), journeyManager.getRangeChargers());
    }

    @Test
    public void testNullRange() {
        journeyManager.makeAllChargers();
        journeyManager.setCurrentCoordinate(testCoordinateStart);
        journeyManager.makeRangeChargers();
        Assertions.assertEquals(new ArrayList<Charger>(), journeyManager.getRangeChargers());
    }

    @Test
    public void testMakeCoordinateName() {
        GeoLocationHandler.setCoordinate(testCoordinateStart, "test");
        journeyManager.makeCoordinateName();
        Assertions.assertEquals(testCoordinateStart, journeyManager.getCurrentCoordinate());
    }

    @Test
    public void testMakeCoordinateNamePosNull() {
        GeoLocationHandler.clearCoordinate();
        journeyManager.makeCoordinateName();
        Assertions.assertEquals(GeoLocationHandler.DEFAULT_COORDINATE,
                journeyManager.getCurrentCoordinate());
    }

    @Test
    public void addNoChargerStop() {
        journeyManager.setSelectedJourney(testJourneyOne);
        journeyManager.addNoChargerStop(testCoordinateCharger);
        Stop check = new Stop(testCoordinateCharger);
        Assertions.assertEquals(check, journeyManager.getSelectedJourney().getStops().get(1));
    }
}