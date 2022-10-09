package seng202.team3.unittest.logic;

import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.Connector;
import seng202.team3.data.entity.Coordinate;
import seng202.team3.data.entity.User;
import seng202.team3.logic.ChargerHandler;
import seng202.team3.logic.ChargerManager;
import seng202.team3.logic.GarageManager;
import seng202.team3.logic.JourneyManager;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

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
    private Charger charger1;
    private Charger charger2;
    private ObservableList<Charger> chargerList;

    /**
     * null
     */
    @BeforeEach
    public void setUp() {
        Connector dummyConnector = new Connector("ChardaMo", "AC", "Available", "123", 3);
        ArrayList<Connector> connectorList = new ArrayList<>(1);
        connectorList.add(dummyConnector);
        Coordinate coord = new Coordinate(4.5, 5.7, -36.85918, 174.76602);
        charger1 = new Charger(connectorList, "Test1", coord, 1, 0.3,
                "Meridian", "2020/1/1 00:00:00", true, true, true, true);
        charger2 = new Charger(connectorList, "Test2", coord, 1, 0.3,
                "Meridian", "2020/1/1 00:00:00", true, true, true, true);

        chargerList.add(charger1);
        chargerList.add(charger2);
    }

    /**
     * AfterEach, sets everything to null.
     */
    @AfterEach
    public void tearDown() {
        charger1 = null;
        charger2 = null;
        chargerList = null;
    }

    /**
     * null
     */
    @Test
    public void testRangeChargers() {
        //ChargerHandler chargerHandler = new ChargerHandler();
        //chargerHandler.makeAllChargers();

        //chargerHandler.setPosition(5765876);
        //manager.setDesiredRange(50.0);
        //manager.setCurrentCoordinate(new Coordinate());

        //list = new ChargerManager().getNearbyChargers(chargers,
                //currentCoordinate, desiredRange);
    }

    /**
     * Tests removeLastCharger() works as intended
     * Utilizes boundary value of charger list size 1
     */
    @Test
    public void testRemovingCharger() {

        //manager.addCharger(chargerList.get(0));
        //manager.addCharger(chargerList.get(1));
        //manager.removeLastCharger(); // Removing with list size of 2
        //assertFalse(manager.getChargers().contains(chargerList.get(1)));

        //manager.removeLastCharger(); // Removing with list size of 1
        //assertNull(manager.getChargers());

        //manager.removeLastCharger(); // Removing with list size of 0
        //assertNull(manager.getChargers());

    }

    /**
     * null
     */
    @Test
    public void testDistanceBetweenChargers() {

    }

}
