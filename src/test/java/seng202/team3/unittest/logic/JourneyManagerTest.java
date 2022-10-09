package seng202.team3.unittest.logic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.User;
import seng202.team3.logic.ChargerHandler;
import seng202.team3.logic.ChargerManager;
import seng202.team3.logic.GarageManager;
import seng202.team3.logic.JourneyManager;

import java.util.ArrayList;

/**
 * Unit tests for {@link JourneyManager journeyManager} logic class
 *
 * @author James Billows
 * @version 1.0.0, Oct 22
 */
public class JourneyManagerTest {

    private static final Logger logManager = LogManager.getLogger();

    private JourneyManager manager;
    static SqlInterpreter db;
    static User testUser;

    /**
     * null
     */
    @BeforeEach
    public void setUp() {

    }

    /**
     * AfterEach, sets everything to null.
     */
    @AfterEach
    public void tearDown() {

    }

    /**
     * null
     */
    @Test
    public void testRangeChargers() {
        //ChargerHandler chargerHandler = new ChargerHandler();
        //chargerHandler.makeAllChargers();

        //chargerHandler.setPosition(5765876);
        //manager.setDesiredRange(45476);

        //list = new ChargerManager().getNearbyChargers(chargers,
                //currentCoordinate, desiredRange);
    }

    /**
     * null
     */
    @Test
    public void testRemovingCharger() {

    }

    /**
     * null
     */
    @Test
    public void testDistanceBetweenChargers() {

    }

}
