package seng202.team3.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team3.data.database.ComparisonType;
import seng202.team3.data.database.CsvInterpreter;
import seng202.team3.data.database.QueryBuilder;
import seng202.team3.data.database.QueryBuilderImpl;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.Connector;
import seng202.team3.data.entity.Coordinate;

/**
 * Unit tests for {@link MainManager} MainManager class in logic
 *
 * @author Matthew Doonan
 * @version 1.0.0, Aug 28
 */
public class MainManagerTest {

    private MainManager manage;
    private ChargerManager charge;

    /**
     * Set up managers for testing purposes
     */
    @BeforeEach
    public void setUp() {
        manage = new MainManager();
        charge = new ChargerManager();

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
        manage.setPosition(coordinate);
        assertEquals(coordinate, manage.getPosition());

    }

    @Test
    public void positionTest2() {
        Coordinate coordinate = new Coordinate(4.4, 6.1, 23.2334, 32.3242);
        manage.setPosition(coordinate);
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
        Charger c = new Charger(connectorList, "Test1", coord, 1, 0.3, "Meridian", true);
        manage.setSelectedCharger(c);
        assertEquals(c.getLocation().getLat(),
                manage.getSelectedCharger().getLocation().getLat());
        assertEquals(c.getLocation().getLon(),
                manage.getSelectedCharger().getLocation().getLon());
    }

    /**
     * Tests for the list of chargers
     */
    @Test
    public void originalListTest() {
        QueryBuilder q = new QueryBuilderImpl().withSource("charger");
        ArrayList<Charger> chargerList = new ArrayList<>();
        try {
            for (Object o : new CsvInterpreter().readData(q.build(), Charger.class)) {
                chargerList.add((Charger) o);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        manage.resetQuery();
        manage.makeAllChargers();
        // Checks the sizes of each list
        assertEquals(chargerList.size(), manage.getData().size());
        // checks the address of each of the elements since they should be unique
        for (int i = 0; i < chargerList.size(); i++) {
            assertEquals(chargerList.get(i).getLocation().getAddress(),
                    manage.getData().get(i).getLocation().getAddress());
        }
    }

    /**
     * tests if the manager is returning the correct list of chargers with Distances
     * Distance set to 50
     */
    @Test
    public void distanceOriginalListTest() {
        QueryBuilder q = new QueryBuilderImpl().withSource("charger");
        ArrayList<Charger> chargerList = new ArrayList<>();
        try {
            for (Object o : new CsvInterpreter().readData(q.build(), Charger.class)) {
                chargerList.add((Charger) o);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Coordinate coordinate = new Coordinate(1.1, 2.3, -43.53418, 172.627572);
        ArrayList<Charger> cc = charge.getNearbyChargers(chargerList, coordinate, 50.0);
        manage.setPosition(coordinate);
        manage.setDistance(50.0);
        manage.resetQuery();
        manage.makeAllChargers();

        ObservableList<Charger> result = FXCollections.observableList(cc);
        ObservableList<Charger> returnVal = manage.getCloseChargerData();

        // Checks the sizes of each list
        assertEquals(result.size(), returnVal.size());
        // checks the address of each of the elements since they should be unique
        for (int i = 0; i < result.size(); i++) {
            assertEquals(result.get(i).getLocation().getAddress(),
                    returnVal.get(i).getLocation().getAddress());
        }
    }

    /**
     * Distance set to 90 instead of 50
     */
    @Test
    public void changeDistanceTest() {
        QueryBuilder q = new QueryBuilderImpl().withSource("charger");
        ArrayList<Charger> chargerList = new ArrayList<>();
        try {
            for (Object o : new CsvInterpreter().readData(q.build(), Charger.class)) {
                chargerList.add((Charger) o);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Coordinate coordinate = new Coordinate(1.1, 2.3, -43.53418, 172.627572);
        ArrayList<Charger> cc = charge.getNearbyChargers(chargerList, coordinate, 90.0);
        manage.setPosition(coordinate);
        manage.setDistance(90.0);
        manage.resetQuery();
        manage.makeAllChargers();

        ObservableList<Charger> result = FXCollections.observableList(cc);
        ObservableList<Charger> returnVal = manage.getCloseChargerData();
        // Checks the sizes of each list
        assertEquals(result.size(), returnVal.size());
        // checks the address of each of the elements since they should be unique
        for (int i = 0; i < result.size(); i++) {
            assertEquals(result.get(i).getLocation().getAddress(),
                    returnVal.get(i).getLocation().getAddress());
        }
    }

    /**
     * Checks the lists of chargers when a QUERY filter connectorcurrent AC is
     * added.
     */
    @Test
    public void addAcTypeQuery() {
        QueryBuilder q = new QueryBuilderImpl().withSource("charger")
                .withFilter("connectorcurrent", "AC", ComparisonType.CONTAINS);
        ArrayList<Charger> chargerList = new ArrayList<>();
        try {
            for (Object o : new CsvInterpreter().readData(q.build(), Charger.class)) {
                chargerList.add((Charger) o);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        manage.resetQuery();
        manage.adjustQuery("connectorcurrent", "AC", ComparisonType.CONTAINS);
        manage.makeAllChargers();
        // Checks the sizes of each list
        assertEquals(chargerList.size(), manage.getData().size());
        // checks the address of each of the elements since they should be unique
        for (int i = 0; i < chargerList.size(); i++) {
            assertEquals(chargerList.get(i).getLocation().getAddress(),
                    manage.getData().get(i).getLocation().getAddress());
        }
    }

    /**
     * Checks the lists of chargers when a QUERY filter connectorcurrent DC is
     * added.
     */
    @Test
    public void addDcTypeQuery() {
        QueryBuilder q = new QueryBuilderImpl().withSource("charger")
                .withFilter("connectorcurrent", "DC", ComparisonType.CONTAINS);
        ArrayList<Charger> chargerList = new ArrayList<>();
        try {
            for (Object o : new CsvInterpreter().readData(q.build(), Charger.class)) {
                chargerList.add((Charger) o);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        manage.resetQuery();
        manage.adjustQuery("connectorcurrent", "DC", ComparisonType.CONTAINS);
        manage.makeAllChargers();
        // Checks the sizes of each list
        assertEquals(chargerList.size(), manage.getData().size());
        // checks the address of each of the elements since they should be unique
        for (int i = 0; i < chargerList.size(); i++) {
            assertEquals(chargerList.get(i).getLocation().getAddress(),
                    manage.getData().get(i).getLocation().getAddress());
        }
    }

    /**
     * Checks the lists of chargers when a QUERY filter hastouristattraction true is
     * added.
     */
    @Test
    public void addAttractionQuery() {
        QueryBuilder q = new QueryBuilderImpl().withSource("charger")
                .withFilter("hastouristattraction", "True", ComparisonType.CONTAINS);
        ArrayList<Charger> chargerList = new ArrayList<>();
        try {
            for (Object o : new CsvInterpreter().readData(q.build(), Charger.class)) {
                chargerList.add((Charger) o);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        manage.resetQuery();
        manage.adjustQuery("hastouristattraction", "True", ComparisonType.CONTAINS);
        manage.makeAllChargers();
        // Checks the sizes of each list
        assertEquals(chargerList.size(), manage.getData().size());
        // checks the address of each of the elements since they should be unique
        for (int i = 0; i < chargerList.size(); i++) {
            assertEquals(chargerList.get(i).getLocation().getAddress(),
                    manage.getData().get(i).getLocation().getAddress());
        }
    }

    /**
     * Checks the lists of chargers when a QUERY filter haschargingcost False is
     * added.
     */
    @Test
    public void addChargingCostQuery() {
        QueryBuilder q = new QueryBuilderImpl().withSource("charger")
                .withFilter("haschargingcost", "False", ComparisonType.CONTAINS);
        ArrayList<Charger> chargerList = new ArrayList<>();
        try {
            for (Object o : new CsvInterpreter().readData(q.build(), Charger.class)) {
                chargerList.add((Charger) o);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        manage.resetQuery();
        manage.adjustQuery("haschargingcost", "False", ComparisonType.CONTAINS);
        manage.makeAllChargers();
        // Checks the sizes of each list
        assertEquals(chargerList.size(), manage.getData().size());
        // checks the address of each of the elements since they should be unique
        for (int i = 0; i < chargerList.size(); i++) {
            assertEquals(chargerList.get(i).getLocation().getAddress(),
                    manage.getData().get(i).getLocation().getAddress());
        }
    }

    /**
     * Checks what happens there are multiple QUERY's added
     */
    @Test
    public void queryChargeCostWithCurrentTest() {
        QueryBuilder q = new QueryBuilderImpl().withSource("charger")
                .withFilter("haschargingcost", "False", ComparisonType.CONTAINS)
                .withFilter("connectorcurrent", "DC", ComparisonType.CONTAINS);
        ArrayList<Charger> chargerList = new ArrayList<>();
        try {
            for (Object o : new CsvInterpreter().readData(q.build(), Charger.class)) {
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
        assertEquals(chargerList.size(), manage.getData().size());
        // checks the address of each of the elements since they should be unique
        for (int i = 0; i < chargerList.size(); i++) {
            assertEquals(chargerList.get(i).getLocation().getAddress(),
                    manage.getData().get(i).getLocation().getAddress());
        }
    }

    @Test
    public void queryTouristAttractionWithCurrentTest() {
        QueryBuilder q = new QueryBuilderImpl().withSource("charger")
                .withFilter("hastouristattraction", "True", ComparisonType.CONTAINS)
                .withFilter("connectorcurrent", "AC", ComparisonType.CONTAINS);
        ArrayList<Charger> chargerList = new ArrayList<>();
        try {
            for (Object o : new CsvInterpreter().readData(q.build(), Charger.class)) {
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
        assertEquals(chargerList.size(), manage.getData().size());
        // checks the address of each of the elements since they should be unique
        for (int i = 0; i < chargerList.size(); i++) {
            assertEquals(chargerList.get(i).getLocation().getAddress(),
                    manage.getData().get(i).getLocation().getAddress());
        }
    }

    /**
     * Checks what happens when the position is null
     */
    @Test
    public void positionNull() {
        QueryBuilder q = new QueryBuilderImpl().withSource("charger");
        ArrayList<Charger> chargerList = new ArrayList<>();
        try {
            for (Object o : new CsvInterpreter().readData(q.build(), Charger.class)) {
                chargerList.add((Charger) o);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        manage.resetQuery();
        manage.makeAllChargers();
        // Checks the sizes of each list
        assertEquals(chargerList.size(), manage.getCloseChargerData().size());
        // checks the address of each of the elements since they should be unique
        for (int i = 0; i < chargerList.size(); i++) {
            assertEquals(chargerList.get(i).getLocation().getAddress(),
                    manage.getCloseChargerData().get(i).getLocation().getAddress());
        }
    }
}
