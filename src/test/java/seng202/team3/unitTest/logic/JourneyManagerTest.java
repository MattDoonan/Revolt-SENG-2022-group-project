package seng202.team3.unitTest.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.Connector;
import seng202.team3.data.entity.Coordinate;
import seng202.team3.data.entity.Vehicle;
import seng202.team3.logic.JourneyManager;

/**
 * Unit tests for {@link JourneyManager JourneyManager} class in Logic
 *
 * @author Angus Kirtlan
 * @version 1.0.0, Aug 22
 */
public class JourneyManagerTest {

    /**
     * Creates a {@link JourneyManager JourneyManager} to test
     * and four chargers.
     */
    private JourneyManager manager;
    private Vehicle car;
    private Charger charger1;
    private Charger charger2;
    private Charger charger3;
    private Charger charger4;

    /**
     * BeforeEach Journey, Charger and vehicle setup
     * coord 2 is between coord 1 and coord 3
     * TODO fix tests
     *
     */
    @BeforeEach
    public void setUp() {
        Connector dummyConnector = new Connector("ChardaMo", "powerDraw", "Not in use", "AC", 2);
        ArrayList<Connector> connectorList = new ArrayList<>(1);
        connectorList.add(dummyConnector);

        // Christchurch Hospital
        Coordinate coord1 = new Coordinate(1.1, 2.3, -43.53418, 172.627572);
        charger1 = new Charger(connectorList, "Hosp", coord1, 2, 1.2, "operator", "owner","01-01-2000", true,true,true,true);

        // Christchurch Boys High School
        Coordinate coord2 = new Coordinate(3.5, 4.4, -43.52425, 172.60019);
        charger2 = new Charger(connectorList, "Boys", coord2, 2, 1.2, "operator", "owner","01-01-2000", true,true,true,true);

        // Canterbury Uni
        Coordinate coord3 = new Coordinate(2.2, 2.2, -43.521764, 172.579985);
        charger3 = new Charger(connectorList, "Uni", coord3, 2, 1.2, "operator", "owner","01-01-2000", true,true,true,true);

        // Otago Boys School
        Coordinate coord4 = new Coordinate(4.8, 7.7, -45.87135, 170.49551);
        charger4 = new Charger(connectorList, "Otago", coord4, 2, 1.2, "operator", "owner","01-01-2000", true,true,true,true);

        manager = new JourneyManager();
        manager.setStart(coord1);
        manager.setEnd(coord3);
    }

    /**
     * AfterEach, teras it all down.
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
     * Test that vehicles are successfully added to journey
     * TODO fix
     */
    @Test
    public void testSelectVehicle() {
        ArrayList<String> connectors = new ArrayList<>();
        connectors.add("temp");
        //car = new Vehicle("CGN781", "temp", "temp", 100, 10000, connectors);
        manager.selectVehicle(car);
        //assertEquals("CGN781", manager.getSelectedJourney().getVehicle().getLicensePlate());
    }

    @Test
    public void testAddCharger() {
        manager.addCharger(charger1);
        ArrayList<Charger> test = new ArrayList<>();
        test.add(charger1);
        assertEquals(test, manager.getSelectedJourney().getChargers());
    }

    /**
     * Tests callCalculations returns correct arrayList
     * Coord 2 is between Coord 1 and Coord 3, the start and end
     * Coord 1 through 3 should be correct
     */
    @Test
    public void testCallCalculations() {
        ArrayList<Charger> allTestChargers = new ArrayList<>();
        allTestChargers.add(charger1);
        allTestChargers.add(charger2);
        allTestChargers.add(charger3);
        allTestChargers.add(charger4);

        ArrayList<Charger> correctTestChargers = new ArrayList<>();
        correctTestChargers.add(charger1);
        correctTestChargers.add(charger2);
        correctTestChargers.add(charger3);

        manager.makeCandidateChargers(allTestChargers);
        assertEquals(FXCollections.observableList(correctTestChargers),manager.getCandidateChargers());
    }
}