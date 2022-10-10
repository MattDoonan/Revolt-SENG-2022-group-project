package seng202.team3.unittest.logic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.*;
import seng202.team3.logic.JourneyManager;
import seng202.team3.logic.JourneyUpdateManager;
import seng202.team3.logic.UserManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link JourneyUpdateManager JourneyUpdateManager}
 * class in Logic
 *
 * @author Angus Kirtlan
 * @version 1.0.0, Oct 22
 */
public class JourneyUpdateManagerTest {
    /**
     * Logger
     */
    private static final Logger logManager = LogManager.getLogger();

    /**
     * Creates three {@link Journey Journeys} to test with
     * Creates classes of things needed to create a journey
     */
    private JourneyManager journeyManager = new JourneyManager();
    private JourneyUpdateManager manager = new JourneyUpdateManager();
    private Journey testJourneyOne;
    private Journey testJourneyTwo;
    private Journey testJourneyThree;
    private Vehicle testVehicle;

    static User testUserOne;
    static User testUserTwo;

    static Charger testCharger;

    private Coordinate testCoordinateStart;
    private Coordinate testCoordinateEnd;
    private Coordinate testCoordinateCharger;

    /**
     * BeforeEach create {@link Journey Journeys} to add to the database
     * Creates classes of things needed to create a journey
     */
    @BeforeEach
    public void setUp() {
        testCoordinateStart = new Coordinate(1.0, 1.0, 2.9342, 5.1247);
        testCoordinateEnd = new Coordinate(1.0, 1.0, 3.9342, 4.1247);
        testCoordinateCharger = new Coordinate(1.0, 1.0, 3.92523, 2.23423);
        testCharger = new Charger();
        testUserOne = new User("admin@admin.com", "adminNew",
                PermissionLevel.USER);
        testUserOne.setUserid(1);
        testUserTwo = new User("admin@admin.com", "adminNew",
                PermissionLevel.USER);
        testUserTwo.setUserid(2);
        testVehicle = new Vehicle("TestMake", "TestModel",
                555, new ArrayList<String>(Arrays.asList("Type 1 Tethered")));
        testVehicle.setImgPath("null");
        testJourneyOne = new Journey(testVehicle, testCoordinateStart,
                testCoordinateEnd, "10/10/2002", "Name");
        testJourneyOne.addCharger(testCharger);
        testJourneyTwo = new Journey(testVehicle, testCoordinateStart,
                testCoordinateEnd, "10/10/2002", "Name");
        testJourneyTwo.addCharger(testCharger);
        testJourneyThree = new Journey(testVehicle, testCoordinateStart,
                testCoordinateEnd, "10/10/2002", "Name");
        testJourneyThree.addCharger(testCharger);
        try {
            SqlInterpreter.getInstance().defaultDatabase();
        } catch(IOException e) {
            logManager.error(e.getMessage());
        }
        try {
            SqlInterpreter.getInstance().writeVehicle(testVehicle);
        } catch(IOException e) {
            logManager.error(e.getMessage());
        }
        manager.resetQuery();
    }

    /**
     * AfterEach, sets everything to null.
     */
    @AfterEach
    public void tearDown() {
        testCoordinateStart = null;
        testCoordinateEnd = null;
        testCoordinateCharger = null;
        testJourneyThree = null;
        testJourneyTwo = null;
        testJourneyOne = null;
        testUserOne = null;
        testUserTwo = null;
        journeyManager = null;
        manager = null;
        testVehicle = null;
        testCharger = null;
        assertNull(testCoordinateStart);
        assertNull(testCoordinateEnd);
        assertNull(testCoordinateCharger);
        assertNull(testJourneyThree);
        assertNull(testJourneyTwo);
        assertNull(testJourneyOne);
        assertNull(testUserOne);
        assertNull(testUserTwo);
        assertNull(journeyManager);
        assertNull(manager);
        assertNull(testVehicle);
        assertNull(testCharger);
    }

    /**
     * Test that Journey is successfully added to database and attached to User
     */
    @Test
    public void testUserJourneys() {
        UserManager.setUser(testUserOne);
        journeyManager.setSelectedJourney(testJourneyOne);
        journeyManager.saveJourney();

        assertEquals(1, manager.getData().size());
    }

    /**
     * Tests that each user only sees the Journeys that belong to them
     */
    @Test
    public void testMultipleUsers() {
        UserManager.setUser(testUserTwo);
        journeyManager.setSelectedJourney(testJourneyTwo);
        journeyManager.saveJourney();
        journeyManager.setSelectedJourney(testJourneyThree);
        journeyManager.saveJourney();

        UserManager.setUser(testUserOne);
        journeyManager.setSelectedJourney(testJourneyOne);
        journeyManager.saveJourney();

        assertEquals(1, manager.getData().size());
    }

    /**
     * Tests that if journeys are made, that when logging in again you still have your journeys
     * and are only given your journeys
     */
    @Test
    public void testLogout() {
        UserManager.setUser(testUserTwo);
        journeyManager.setSelectedJourney(testJourneyTwo);
        journeyManager.saveJourney();
        journeyManager.setSelectedJourney(testJourneyThree);
        journeyManager.saveJourney();

        UserManager.setUser(testUserOne);
        journeyManager.setSelectedJourney(testJourneyOne);
        journeyManager.saveJourney();

        UserManager.setUser(testUserTwo);
        assertEquals(2, manager.getData().size());
    }




}
