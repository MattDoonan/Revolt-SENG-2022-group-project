package seng202.team3.unittest.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.Connector;
import seng202.team3.data.entity.Coordinate;
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
    private Charger charger1;
    private Charger charger2;


    /**
     * null
     */
    @BeforeEach
    public void setUp() {

        manager = new JourneyManager();

        Connector dummyConnector = new Connector("ChardaMo", "AC", "Available", "123", 3);
        ArrayList<Connector> connectorList = new ArrayList<>(1);
        connectorList.add(dummyConnector);
        Coordinate coord = new Coordinate(4.5, 5.7, -36.85918, 174.76602);
        
        charger1 = new Charger(connectorList, "Test1", coord, 1, 0.3,
                "Meridian", "2020/1/1 00:00:00", true, true, true, true);
        charger2 = new Charger(connectorList, "Test1", coord, 1, 0.3,
                "Meridian", "2020/1/1 00:00:00", true, true, true, true);
    }


    /**
     * AfterEach, sets everything to null.
     */
    @AfterEach
    public void tearDown() {
        charger1 = null;
        charger2 = null;
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

        manager.addCharger(charger1);
        manager.addCharger(charger2);
        manager.removeLastCharger(); // Removing with list size of 2
        assertEquals(manager.getChargers().size(), 1);
        //assertFalse(manager.getChargers().contains(charger2)); // TODO change to check if charger2 in list (was asserting true)

        manager.removeLastCharger(); // Removing with list size of 1
        assertEquals(manager.getChargers().size(), 0);

        manager.removeLastCharger(); // Removing with list size of 0
        assertEquals(manager.getChargers().size(), 0);

    }

    /**
     * null
     */
    @Test
    public void testDistanceBetweenChargers() {

    }

}
