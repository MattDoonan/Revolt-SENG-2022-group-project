package seng202.team3.unitTest.logic;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.management.InstanceAlreadyExistsException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team3.data.database.ComparisonType;
import seng202.team3.data.database.QueryBuilder;
import seng202.team3.data.database.QueryBuilderImpl;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.Connector;
import seng202.team3.data.entity.Coordinate;
import seng202.team3.logic.ChargerManager;
import seng202.team3.logic.GeoLocationHandler;
import seng202.team3.logic.MainManager;

/**
 * Unit tests for {@link MainManager} MainManager class in logic
 *
 * @author Matthew Doonan
 * @version 1.0.0, Aug 28
 */
public class MainManagerTest {

    private MainManager manage;
    private ChargerManager charge;
    static SqlInterpreter db;

    @BeforeAll
    static void intialize() throws InstanceAlreadyExistsException {
        SqlInterpreter.removeInstance();
        db = SqlInterpreter.initialiseInstanceWithUrl(
                "jdbc:sqlite:./src/test/resources/test_database.db");
    }

    /**
     * Set up managers for testing purposes
     */
    @BeforeEach
    public void setUp() {
        manage = new MainManager();
        charge = new ChargerManager();
        db.defaultDatabase();
    }

    /** Tears down the initialized managers after test */
    @AfterEach
    public void remove() {
        manage = null;
        charge = null;
        assertNull(manage);
        assertNull(charge);
    }

    /**
     * Test for getPosition
     */
    @Test
    public void positionTest1() {
        Coordinate coordinate = new Coordinate(1.1, 2.3, -43.53418, 172.627572);
        GeoLocationHandler.getInstance().setCoordinate(coordinate, "empty");
        manage.setPosition();
        assertEquals(coordinate, manage.getPosition());

    }

    @Test
    public void positionTest2() {
        Coordinate coordinate = new Coordinate(4.4, 6.1, 23.2334, 32.3242);
        GeoLocationHandler.getInstance().setCoordinate(coordinate, "empty");
        manage.setPosition();
        assertEquals(coordinate, manage.getPosition());
    }

    /**
     * Checks if the manager saves the selected charger correctly
     */
    @Test
    public void selectedChargerTest() {
        Connector dummyConnector = new Connector("ChardaMo", "AC", "Available", "123", 3);
        ArrayList<Connector> connectorList = new ArrayList<>(1);
        connectorList.add(dummyConnector);
        Coordinate coord = new Coordinate(4.5, 5.7, -36.85918, 174.76602);
        Charger c = new Charger(connectorList, "Test1", coord, 1, 0.3,
                "Meridian", "Meridian", "2020/1/1 00:00:00", true, true, true, true);
        manage.setSelectedCharger(c);
        assertEquals(c.getLocation().getLat(),
                manage.getSelectedCharger().getLocation().getLat());
        assertEquals(c.getLocation().getLon(),
                manage.getSelectedCharger().getLocation().getLon());
    }

    /**
     * Tests for the list of chargers
     * 
     * @throws IOException if data cannot be read
     */
    @Test
    public void originalListTest() throws IOException {
        db.addChargerCsvToData("src/test/resources/csvtest/filtering.csv");

        QueryBuilder q = new QueryBuilderImpl().withSource("charger");
        ArrayList<Charger> chargerList = new ArrayList<>();
        for (Object o : SqlInterpreter.getInstance().readData(q.build(), Charger.class)) {
            chargerList.add((Charger) o);
        }

        manage.resetQuery();
        manage.makeAllChargers();
        // Checks the sizes of each list
        assertArrayEquals(chargerList.toArray(), manage.getData().toArray());
    }

    /**
     * tests if the manager is returning the correct list of chargers with Distances
     * Distance set to 50
     * 
     * @throws IOException if db interaction fails
     */
    @Test
    public void distanceOriginalListTest() throws IOException {
        db.addChargerCsvToData("src/test/resources/csvtest/filtering.csv");

        QueryBuilder q = new QueryBuilderImpl().withSource("charger");
        ArrayList<Charger> chargerList = new ArrayList<>();

        for (Object o : SqlInterpreter.getInstance().readData(q.build(), Charger.class)) {
            chargerList.add((Charger) o);
        }

        Coordinate coordinate = new Coordinate(1.1, 2.3, -43.53418, 172.627572);
        ArrayList<Charger> cc = charge.getNearbyChargers(chargerList, coordinate, 50.0);
        GeoLocationHandler.getInstance().setCoordinate(coordinate, "empty");
        manage.setPosition();
        manage.setDistance(50.0);
        manage.resetQuery();
        manage.makeAllChargers();

        ObservableList<Charger> result = FXCollections.observableList(cc);
        ObservableList<Charger> returnVal = manage.getCloseChargerData();

        assertArrayEquals(result.toArray(), returnVal.toArray());

    }

    /**
     * Distance set to 90 instead of 50
     * 
     * @throws IOException if db interaction fails
     */
    @Test
    public void changeDistanceTest() throws IOException {
        db.addChargerCsvToData("src/test/resources/csvtest/filtering.csv");

        QueryBuilder q = new QueryBuilderImpl().withSource("charger");
        ArrayList<Charger> chargerList = new ArrayList<>();

        for (Object o : SqlInterpreter.getInstance().readData(q.build(), Charger.class)) {
            chargerList.add((Charger) o);
        }

        Coordinate coordinate = new Coordinate(1.1, 2.3, -43.53418, 172.627572);
        ArrayList<Charger> cc = charge.getNearbyChargers(chargerList, coordinate, 90.0);
        GeoLocationHandler.getInstance().setCoordinate(coordinate, "empty");
        manage.setPosition();
        manage.setDistance(90.0);
        manage.resetQuery();
        manage.makeAllChargers();

        ObservableList<Charger> result = FXCollections.observableList(cc);
        ObservableList<Charger> returnVal = manage.getCloseChargerData();
        // Checks the sizes of each list
        assertArrayEquals(result.toArray(), returnVal.toArray());

    }

    /**
     * Checks the lists of chargers when a QUERY filter connectorcurrent AC is
     * added.
     * 
     * @throws IOException if db interaction fails
     */
    @Test
    public void addAcTypeQuery() throws IOException {
        db.addChargerCsvToData("src/test/resources/csvtest/filtering.csv");

        QueryBuilder q = new QueryBuilderImpl().withSource("charger")
                .withFilter("connectorcurrent", "AC", ComparisonType.CONTAINS);
        ArrayList<Charger> chargerList = new ArrayList<>();
        for (Object o : SqlInterpreter.getInstance().readData(q.build(), Charger.class)) {
            chargerList.add((Charger) o);
        }

        manage.resetQuery();
        manage.adjustQuery("connectorcurrent", "AC", ComparisonType.CONTAINS);
        manage.makeAllChargers();
        // Checks the sizes of each list
        assertArrayEquals(chargerList.toArray(), manage.getData().toArray());

    }

    /**
     * Checks the lists of chargers when a QUERY filter connectorcurrent DC is
     * added.
     * 
     * @throws IOException if db interaction fails
     */
    @Test
    public void addDcTypeQuery() throws IOException {
        db.addChargerCsvToData("src/test/resources/csvtest/filtering.csv");

        QueryBuilder q = new QueryBuilderImpl().withSource("charger")
                .withFilter("connectorcurrent", "DC", ComparisonType.CONTAINS);
        ArrayList<Charger> chargerList = new ArrayList<>();
        try {
            for (Object o : SqlInterpreter.getInstance().readData(q.build(), Charger.class)) {
                chargerList.add((Charger) o);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        manage.resetQuery();
        manage.adjustQuery("connectorcurrent", "DC", ComparisonType.CONTAINS);
        manage.makeAllChargers();
        // Checks the sizes of each list
        assertArrayEquals(chargerList.toArray(), manage.getData().toArray());
    }

    /**
     * Checks the lists of chargers when a QUERY filter hastouristattraction true is
     * added.
     * 
     * @throws IOException if db interaction fails
     */
    @Test
    public void addAttractionQuery() throws IOException {
        db.addChargerCsvToData("src/test/resources/csvtest/filtering.csv");
        QueryBuilder q = new QueryBuilderImpl().withSource("charger")
                .withFilter("hastouristattraction", "True", ComparisonType.CONTAINS);
        ArrayList<Charger> chargerList = new ArrayList<>();
        try {
            for (Object o : SqlInterpreter.getInstance().readData(q.build(), Charger.class)) {
                chargerList.add((Charger) o);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        manage.resetQuery();
        manage.adjustQuery("hastouristattraction", "True", ComparisonType.CONTAINS);
        manage.makeAllChargers();
        // Checks the sizes of each list
        assertArrayEquals(chargerList.toArray(), manage.getData().toArray());

    }

    /**
     * Checks the lists of chargers when a QUERY filter haschargingcost False is
     * added.
     * 
     * @throws IOException if db interaction fails
     */
    @Test
    public void addChargingCostQuery() throws IOException {
        db.addChargerCsvToData("src/test/resources/csvtest/filtering.csv");
        QueryBuilder q = new QueryBuilderImpl().withSource("charger")
                .withFilter("haschargingcost", "False", ComparisonType.CONTAINS);
        ArrayList<Charger> chargerList = new ArrayList<>();
        try {
            for (Object o : SqlInterpreter.getInstance().readData(q.build(), Charger.class)) {
                chargerList.add((Charger) o);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        manage.resetQuery();
        manage.adjustQuery("haschargingcost", "False", ComparisonType.CONTAINS);
        manage.makeAllChargers();
        // Checks the sizes of each list
        assertArrayEquals(chargerList.toArray(), manage.getData().toArray());

    }

    /**
     * Checks what happens there are multiple QUERY's added
     * 
     * @throws IOException if db interaction fails
     */
    @Test
    public void queryChargeCostWithCurrentTest() throws IOException {
        db.addChargerCsvToData("src/test/resources/csvtest/filtering.csv");
        QueryBuilder q = new QueryBuilderImpl().withSource("charger")
                .withFilter("haschargingcost", "False", ComparisonType.CONTAINS)
                .withFilter("connectorcurrent", "DC", ComparisonType.CONTAINS);
        ArrayList<Charger> chargerList = new ArrayList<>();
        try {
            for (Object o : SqlInterpreter.getInstance().readData(q.build(), Charger.class)) {
                chargerList.add((Charger) o);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        manage.resetQuery();
        manage.adjustQuery("haschargingcost", "False", ComparisonType.CONTAINS);
        manage.adjustQuery("connectorcurrent", "DC", ComparisonType.CONTAINS);
        manage.makeAllChargers();
        // Checks the sizes of each list
        assertArrayEquals(chargerList.toArray(), manage.getData().toArray());

    }

    @Test
    public void queryTouristAttractionWithCurrentTest() throws IOException {
        db.addChargerCsvToData("src/test/resources/csvtest/filtering.csv");
        QueryBuilder q = new QueryBuilderImpl().withSource("charger")
                .withFilter("hastouristattraction", "True", ComparisonType.CONTAINS)
                .withFilter("connectorcurrent", "AC", ComparisonType.CONTAINS);
        ArrayList<Charger> chargerList = new ArrayList<>();
        try {
            for (Object o : SqlInterpreter.getInstance().readData(q.build(), Charger.class)) {
                chargerList.add((Charger) o);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        manage.resetQuery();
        manage.adjustQuery("hastouristattraction", "True", ComparisonType.CONTAINS);
        manage.adjustQuery("connectorcurrent", "AC", ComparisonType.CONTAINS);
        manage.makeAllChargers();
        // Checks the sizes of each list
        assertArrayEquals(chargerList.toArray(), manage.getData().toArray());

    }

    /**
     * Checks what happens when the position is null
     * 
     * @throws IOException if db interaction fails
     */
    @Test
    public void positionNull() throws IOException {
        db.addChargerCsvToData("src/test/resources/csvtest/filtering.csv");
        QueryBuilder q = new QueryBuilderImpl().withSource("charger");
        ArrayList<Charger> chargerList = new ArrayList<>();
        try {
            for (Object o : SqlInterpreter.getInstance().readData(q.build(), Charger.class)) {
                chargerList.add((Charger) o);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        manage.resetQuery();
        manage.makeAllChargers();
        // Checks the sizes of each list
        assertArrayEquals(chargerList.toArray(), manage.getCloseChargerData().toArray());

    }
}
