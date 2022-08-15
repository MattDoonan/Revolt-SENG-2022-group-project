package seng202.team3.logic;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seng202.team3.data.entity.Charger;
import seng202.team3.logic.ChargerManager;
import seng202.team3.data.entity.Coordinate;
import seng202.team3.data.entity.Connector;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ChargerManager Class in Logic
 *
 * @author Michelle Hsieh
 * @version 1.0.0, Aug 22
 */
public class ChargerManagerTest {

    /**
     * Creates a ChargerManager {@link ChargerManager} to test
     * and three chargers.
     */
    private ChargerManager manager;
    private Charger charger1;
    private Charger charger2;
    private Charger charger3;

    /**
     * BeforeEach Charger, Coordinate and Connector setup
     */
    @BeforeEach
    public void setUp() {
        Connector dummyConnector = new Connector("ChardaMo", "AC", true, false);
        ArrayList<Connector> connectorList = new ArrayList<>(1);
        connectorList.add(dummyConnector);

        //Christchurch Hospital
        Coordinate coord1 = new Coordinate(1.1, 2.3,  -43.53418, 172.627572);
        //Christchurch Boys High School
        Coordinate coord2 = new Coordinate(3.5, 4.4, -43.52425, 172.60019);
        //Auckland Grammar School
        Coordinate coord3 = new Coordinate(4.5, 5.7, -36.85918, 174.76602);

        charger1 = new Charger(connectorList, coord1, 2, 1.2, "Hosp", true, true);
        charger2 = new Charger(connectorList, coord2, 2, 34.2, "Boys", true, false);
        charger3 = new Charger(connectorList, coord3, 3, 23.3, "Grammar", true, true);

        manager = new ChargerManager(charger1);
    }

    /**
     * AfterEach, tears it all down.
     */
    @AfterEach
    public void tearDown() {
        manager = null;
        charger1 = null;
        charger2 = null;
        charger3 = null;
        assertNull(manager);
        assertNull(charger1);
        assertNull(charger2);
        assertNull(charger3);
    }

    /**
     * distanceBetweenChargers test
     */
    @Test
    public void testDistanceChargers() {
        double distance1 = manager.distanceBetweenChargers(charger2);
        double distance2 = manager.distanceBetweenChargers(charger3);
        double distance3 = manager.distanceBetweenChargers(charger1);

        manager.setSelectedCharger(charger3);

        double distance4 = manager.distanceBetweenChargers(charger3);

        //Checks if the distance from hospital to itself is 0
        assertEquals(0.0,distance3, 0.001);

        //Checks if the distance from hospital to boys is less than hospital to grammar
        assertTrue(distance2 > distance1);

        //Changes selected charger and checks if it's equal to itself
        assertEquals(0.0,distance4, 0.001);

        //Checks if distance2 is around 764km (results from online calculator)
        assertEquals(764, distance2, 2.0);
    }

}
