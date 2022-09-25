package seng202.team3.unittest.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import javax.management.InstanceAlreadyExistsException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.Connector;
import seng202.team3.data.entity.Coordinate;
import seng202.team3.logic.ChargerHandler;
import seng202.team3.logic.MainManager;

/**
 * Unit tests for a {@link ChargerHandler ChargerHandler} Class in Logic
 *
 * @author Angus Kirtlan
 * @version 1.0.0, Sep 22
 */
public class ChargerHandlerTest {

    /**
     * Creates new {@link ChargerHandler ChargerHandler} manager and
     * {@link MainManager MainManager} manager which extends ChargerHandler
     */
    ChargerHandler manager;
    MainManager mainManager;

    @BeforeAll
    static void intialize() throws InstanceAlreadyExistsException {
        SqlInterpreter.removeInstance();
        SqlInterpreter.initialiseInstanceWithUrl(
                "jdbc:sqlite:./target/test-classes/test_database.db");
    }

    /**
     * BeforeEach managers setup
     * 
     * @throws IOException cannot import data into database
     */
    @BeforeEach
    public void setUp() throws IOException {
        manager = new ChargerHandler();
        mainManager = new MainManager();
        SqlInterpreter.getInstance().defaultDatabase();
        SqlInterpreter.getInstance()
                .addChargerCsvToData("csvtest/filtering");
    }

    /**
     * AfterEach, sets managers to null
     */
    @AfterEach
    public void tearDown() {
        manager = null;
        mainManager = null;
    }

    /**
     * Tests the creation of a {@link Charger Charger} ArrayList using
     * makeAllChargers
     * Tests edge case of size, makes sure greater than 48 elements
     */
    @Test
    public void makeAllChargersTestSizeGreater() {
        mainManager.resetQuery();
        mainManager.makeAllChargers();
        assertTrue(mainManager.getCloseChargerData().size() > 48);
    }

    /**
     * Tests the creation of a {@link Charger Charger} ArrayList using
     * makeAllChargers
     * Tests edge case of size, makes sure less than 346 elements
     */
    @Test
    public void makeAllChargersTestSizeLess() {
        mainManager.resetQuery();
        mainManager.makeAllChargers();
        assertTrue(mainManager.getCloseChargerData().size() < 346);
    }

    /**
     * Tests the use of resetQuery
     */
    @Test
    public void resetQueryTest() {

    }

    /**
     * Test the use of getConnecters which gets {@link Connector Connectors}
     * of a {@link Charger Charger}
     */
    @Test
    public void getConnectersTest() {
        Connector dummyConnector1 = new Connector("ChardaMo", "powerDraw", "Not in use", "AC", 2);
        Connector dummyConnector2 = new Connector("ChardaMo", "powerDraw", "Not in use", "DC", 4);

        ArrayList<Connector> connectorList = new ArrayList<>(2);
        connectorList.add(dummyConnector1);
        connectorList.add(dummyConnector2);

        Coordinate testCoord = new Coordinate(1.1, 2.3, -43.53418, 172.627572, "CHHosp");
        Charger testCharger = new Charger(connectorList, "Hosp", testCoord, 2, 1.2,
                "operator", "01-01-2000",
                true, true, true, true);
        assertEquals(" AC DC", manager.getConnectors(testCharger));
    }

}
