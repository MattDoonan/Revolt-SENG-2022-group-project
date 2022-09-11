package seng202.team3.unitTest.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.Connector;
import seng202.team3.data.entity.Coordinate;
import seng202.team3.logic.ChargerManager;

/**
 * Unit tests for {@link ChargerManager} ChargerManager Class in Logic
 *
 * @author Michelle Hsieh
 * @version 1.0.1, Aug 22
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
    private Charger charger4;

    /**
     * BeforeEach Charger, Coordinate and Connector setup
     */
    @BeforeEach
    public void setUp() {
        Connector dummyConnector = new Connector("ChardaMo", "AC", "Available", "123", 3);
        ArrayList<Connector> connectorList = new ArrayList<>(1);
        connectorList.add(dummyConnector);

        // Christchurch Hospital
        Coordinate coord1 = new Coordinate(1.1, 2.3, -43.53418, 172.627572);
        charger1 = new Charger(connectorList, "Hosp", coord1, 1, 0.3, "Meridian",
                "Meridian", "2020/1/1 00:00:00", true,
                false, false, false);

        // Christchurch Boys High School
        Coordinate coord2 = new Coordinate(3.5, 4.4, -43.52425, 172.60019);
        charger2 = new Charger(connectorList, "Boys", coord2, 2, 3.5, "Someone",
                "Someone", "2020/1/1 00:00:00", true,
                false, false, false);
        // Auckland Grammar School
        Coordinate coord3 = new Coordinate(4.5, 5.7, -36.85918, 174.76602);
        charger3 = new Charger(connectorList, "Grammar", coord3, 5, 1.2, "Else",
                "Else", "2020/1/1 00:00:00", true,
                false, false, false);

        // Otago Boys School
        Coordinate coord4 = new Coordinate(4.8, 7.7, -45.87135, 170.49551);
        charger4 = new Charger(connectorList, "Otago", coord4, 2, 35.1, "Us",
                "Us", "2020/1/1 00:00:00", true, false,
                false, false);

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
        charger4 = null;
        assertNull(manager);
        assertNull(charger1);
        assertNull(charger2);
        assertNull(charger3);
        assertNull(charger4);
    }

    /**
     * distanceBetweenChargers tests
     */
    @Test
    public void testDistanceChargersSameLocation() {
        double distance1 = manager.distanceBetweenChargers(charger1);

        // Checks if the distance from hospital to itself is 0
        assertEquals(0.0, distance1, 0.001);
    }

    @Test
    public void testDistanceChargersFurther() {
        double distance1 = manager.distanceBetweenChargers(charger2);
        double distance2 = manager.distanceBetweenChargers(charger3);

        // Checks if the distance from hospital to boys is less than hospital to grammar
        assertTrue(distance2 > distance1);
    }

    @Test
    public void testDistanceChargersSensibleDistance() {
        manager.setSelectedCharger(charger3);
        double distance = manager.distanceBetweenChargers(charger1);

        // Checks if distance is around 764km (results from online calculator)
        assertEquals(764, distance, 0.2);
    }

    /**
     * Tests for getNearbyChargers
     */
    @Test
    public void testAllChargersNearLocation() {
        manager.setSelectedCharger(charger1);
        double distance = 400.0;

        // Coordinate same as hospital
        Coordinate coordinate = new Coordinate(1.1, 2.3, -43.53418, 172.627572);

        ArrayList<Charger> chargerList = new ArrayList<>();
        chargerList.add(charger1);
        chargerList.add(charger2);
        chargerList.add(charger3);
        chargerList.add(charger4);

        ArrayList<Charger> filteredChargers = manager.getNearbyChargers(
                chargerList, coordinate, distance);
        assertEquals(3, filteredChargers.size());
    }

    @Test
    public void sortedDistanceCorrectOrder() {
        manager.setSelectedCharger(charger1);

        double distance = 1000.0;
        // Coordinate same as hospital
        Coordinate coordinate = new Coordinate(1.1, 2.3, -43.53418, 172.627572);

        ArrayList<Charger> chargerList = new ArrayList<>();
        chargerList.add(charger4);
        chargerList.add(charger3);
        chargerList.add(charger2);
        chargerList.add(charger1);

        ArrayList<Charger> filteredChargers = manager
                .getNearbyChargers(chargerList, coordinate, distance);

        assertTrue(filteredChargers.get(0).equals(charger1)
                && filteredChargers.get(1).equals(charger2)
                && filteredChargers.get(2).equals(charger4)
                && filteredChargers.get(3).equals(charger3));
    }

    /**
     * Tests for toggleWarning
     */
    @Test
    public void testToggleWarning() {
        manager.setSelectedCharger(charger1);
        manager.toggleWarning("low availability", true);
        manager.toggleWarning("high cost", true);
        manager.toggleWarning("long wait", false);
        ArrayList<String> test = new ArrayList<>(Arrays.asList("high cost", "low availability"));
        assertEquals(test, manager.getSelectedCharger().getWarnings());
    }

    @Test
    public void testRemovalToggleWarning() {
        manager.setSelectedCharger(charger1);
        manager.toggleWarning("low availability", true);
        manager.toggleWarning("high cost", true);
        manager.toggleWarning("low availability", false);
        ArrayList<String> test = new ArrayList<>(Arrays.asList("high cost"));
        assertEquals(test, manager.getSelectedCharger().getWarnings());
    }
}