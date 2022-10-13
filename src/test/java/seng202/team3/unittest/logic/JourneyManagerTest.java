package seng202.team3.unittest.logic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team3.data.database.ComparisonType;
import seng202.team3.data.database.QueryBuilderImpl;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.*;
import seng202.team3.logic.*;

import javax.management.InstanceAlreadyExistsException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
        SqlInterpreter.getInstance().writeUser(testUserOne, "1234");

        testVehicle = new Vehicle("TestMake", "TestModel",
                200, new ArrayList<String>(Arrays.asList("Type 1 Tethered")));
        testVehicle.setOwner(testUserOne.getId());
        SqlInterpreter.getInstance().writeVehicle(testVehicle);

        testJourneyOne = new Journey(testVehicle, testCoordinateStart,
                testCoordinateEnd, "10/10/2002", "Name");
        testJourneyOne.addStop(new Stop(testCharger));
        testJourneyOne.setUser(testUserOne.getId());
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

    @Test
    public void removeLastStopTest() {
        journeyManager.setSelectedJourney(testJourneyOne);
        journeyManager.addNoChargerStop(testCoordinateCharger);
        int size = journeyManager.getSelectedJourney().getStops().size();
        journeyManager.removeLastStop();
        Assertions.assertEquals(size-1,
                journeyManager.getSelectedJourney().getStops().size());
    }

    @Test
    public void noStopToRemove() {
        journeyManager.setSelectedJourney(testJourneyOne);
        journeyManager.removeLastStop();
        journeyManager.removeLastStop();
        Assertions.assertEquals(0,
                journeyManager.getSelectedJourney().getStops().size());
    }

    @Test
    public void saveJourney() throws IOException {
        UserManager.setUser(testUserOne);
        journeyManager.setStart(testCoordinateStart);
        journeyManager.setEnd(testCoordinateEnd);
        journeyManager.selectVehicle(testVehicle);
        journeyManager.getSelectedJourney().setTitle("Test Journey");
        journeyManager.addStop(new Stop(testCharger));
        journeyManager.saveJourney();
        List<Entity> journeys = SqlInterpreter.getInstance().readData(new QueryBuilderImpl().withSource(EntityType.JOURNEY)
                .withFilter("userid", Integer.toString(testUserOne.getId()), ComparisonType.EQUAL).build());
        if (journeys.size() == 1) {
            Journey compare = (Journey) journeys.get(0);
            Assertions.assertEquals(journeyManager.getSelectedJourney(), compare);
        } else {
            fail("More than one journey added");
        }
    }

    @Test
    public void saveInvalidJourneyTest() throws IOException {
        UserManager.setUser(testUserOne);
        journeyManager.saveJourney();
        List<Entity> journeys = SqlInterpreter.getInstance().readData(new QueryBuilderImpl().withSource(EntityType.JOURNEY)
                .withFilter("userid", Integer.toString(testUserOne.getId()), ComparisonType.EQUAL).build());
        assertEquals(0, journeys.size());
    }

    @Test
    public void testCheckValidDistanceBetweenChargers() {
        journeyManager.setSelectedJourney(testJourneyOne);
        Assertions.assertFalse(journeyManager.checkDistanceBetweenChargers());
    }

    @Test
    public void testCheckInvalidDistanceBetweenChargers() {
        Coordinate failStart = new Coordinate(3.4, 7.0);
        Journey failJourney = new Journey(testVehicle, failStart,
                testCoordinateEnd, "10/10/2002", "Name");
        failJourney.addStop(new Stop(testCharger));
        journeyManager.setSelectedJourney(failJourney);
        assertTrue(journeyManager.checkDistanceBetweenChargers());
    }
}