package seng202.team3.logic;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import seng202.team3.data.database.ComparisonType;
import seng202.team3.data.database.CsvInterpreter;
import seng202.team3.data.database.QueryBuilder;
import seng202.team3.data.database.QueryBuilderImpl;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.Coordinate;
import seng202.team3.gui.MainController;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Unit tests for {@link MainManager} MainManager class in logic
 *
 * @author Matthre Doonan
 * @version 1.0.0, Aug 28
 */
public class MainManagerTest {

    @Mock
    private MainController test;
    private MainManager manage;
    private ChargerManager charge;


    @BeforeEach
    public void setUp() {
        test = Mockito.mock((new MainController()).getClass());
        manage = new MainManager(test, 50.0 );
        charge = new ChargerManager();

    }

    @AfterEach
    public void remove() {
        manage = null;
        test = null;
        charge = null;
        assertNull(manage);
        assertNull(test);
        assertNull(charge);
    }

    /**
     * Checks if it is returning the {@link MainController}
     */
    @Test
    public void CheckMainControllerClass(){
        assertEquals(test, manage.getController());
    }

    /**
     * Test for getPosition
     */
    @Test
    public void positionTest1() {
        Coordinate coordinate = new Coordinate(1.1, 2.3,  -43.53418, 172.627572);
        manage.setPosition(coordinate);
        assertEquals(coordinate, manage.getPosition());
    }

    @Test
    public void positionTest2() {
        Coordinate coordinate = new Coordinate(4.4, 6.1,  23.2334, 32.3242);
        manage.setPosition(coordinate);
        assertEquals(coordinate, manage.getPosition());
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
        Coordinate coordinate = new Coordinate(1.1, 2.3,  -43.53418, 172.627572);
        manage.setPosition(coordinate);
        manage.createOriginalQuery();
        assertEquals(chargerList, manage.getData());
    }

    /**
     * tests if the manager is returning the correct list of chargers with Distances
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
        Coordinate coordinate = new Coordinate(1.1, 2.3,  -43.53418, 172.627572);
        ArrayList<Charger> cc = charge.getNearbyChargers(chargerList, coordinate, 50.0);
        ObservableList<Charger> result = FXCollections.observableList(cc);
        manage.setPosition(coordinate);
        manage.createOriginalQuery();

        assertEquals(result, manage.getChargerData());
    }

    /**
     * Test the closerChargers list is the same when the position changes
     */
    @Test
    public void sliderChange() {
        QueryBuilder q = new QueryBuilderImpl().withSource("charger");
        ArrayList<Charger> chargerList = new ArrayList<>();
        try {
            for (Object o : new CsvInterpreter().readData(q.build(), Charger.class)) {
                chargerList.add((Charger) o);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Coordinate coordinate = new Coordinate(1.1, 2.3,  -43.53418, 172.627572);
        ArrayList<Charger> cc = charge.getNearbyChargers(chargerList, coordinate, 90);
        ObservableList<Charger> result = FXCollections.observableList(cc);
        manage.setPosition(coordinate);
        manage.sliderChange(90);
        manage.createOriginalQuery();

        assertEquals(result, manage.getChargerData());
    }

    /**
     * Adds a haschargingcost filter and checks if it updates
     */
    @Test
    public void addChargerCostQuery() {
        QueryBuilder q = new QueryBuilderImpl().withSource("charger").withFilter("haschargingcost", "true", ComparisonType.CONTAINS);

        ArrayList<Charger> chargerList = new ArrayList<>();
        try {
            for (Object o : new CsvInterpreter().readData(q.build(), Charger.class)) {
                chargerList.add((Charger) o);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Coordinate coordinate = new Coordinate(1.1, 2.3,  -43.53418, 172.627572);
        ArrayList<Charger> cc = charge.getNearbyChargers(chargerList, coordinate, 50);
        ObservableList<Charger> result = FXCollections.observableList(cc);
        manage.setPosition(coordinate);
        manage.createOriginalQuery();
        manage.noChargeQuery(true);

        assertEquals(result, manage.getChargerData());
        assertEquals(chargerList, manage.getData());

    }

    /**
     * Adds a haschargingcost false filter then removes the filter and checks the lists
     */
    @Test
    public void removeChargerCostQuery() {
        QueryBuilder q = new QueryBuilderImpl().withSource("charger");

        ArrayList<Charger> chargerList = new ArrayList<>();
        try {
            for (Object o : new CsvInterpreter().readData(q.build(), Charger.class)) {
                chargerList.add((Charger) o);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Coordinate coordinate = new Coordinate(1.1, 2.3,  -43.53418, 172.627572);
        ArrayList<Charger> cc = charge.getNearbyChargers(chargerList, coordinate, 50);
        ObservableList<Charger> result = FXCollections.observableList(cc);
        manage.setPosition(coordinate);
        manage.createOriginalQuery();
        manage.noChargeQuery(true);
        manage.noChargeQuery(false);

        assertEquals(result, manage.getChargerData());
        assertEquals(chargerList, manage.getData());

    }

    /**
     * Adds a hastouristattraction true filter and checks the lists
     */
    @Test
    public void addNearbyAttractionQuery() {
        QueryBuilder q = new QueryBuilderImpl().withSource("charger").withFilter("hastouristattraction", "true", ComparisonType.CONTAINS);

        ArrayList<Charger> chargerList = new ArrayList<>();
        try {
            for (Object o : new CsvInterpreter().readData(q.build(), Charger.class)) {
                chargerList.add((Charger) o);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Coordinate coordinate = new Coordinate(1.1, 2.3,  -43.53418, 172.627572);
        ArrayList<Charger> cc = charge.getNearbyChargers(chargerList, coordinate, 50);
        ObservableList<Charger> result = FXCollections.observableList(cc);
        manage.setPosition(coordinate);
        manage.createOriginalQuery();
        manage.attractionQuery(true);

        assertEquals(result, manage.getChargerData());
        assertEquals(chargerList, manage.getData());

    }

    /**
     * Adds a hastouristattraction true filter then removes the filter and checks the lists
     */
    @Test
    public void removeNearbyAttractionQuery() {
        QueryBuilder q = new QueryBuilderImpl().withSource("charger");

        ArrayList<Charger> chargerList = new ArrayList<>();
        try {
            for (Object o : new CsvInterpreter().readData(q.build(), Charger.class)) {
                chargerList.add((Charger) o);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Coordinate coordinate = new Coordinate(1.1, 2.3,  -43.53418, 172.627572);
        ArrayList<Charger> cc = charge.getNearbyChargers(chargerList, coordinate, 50);
        ObservableList<Charger> result = FXCollections.observableList(cc);
        manage.setPosition(coordinate);
        manage.createOriginalQuery();
        manage.attractionQuery(true);
        manage.noChargeQuery(false);

        assertEquals(result, manage.getChargerData());
        assertEquals(chargerList, manage.getData());
    }

    /**
     * Adds a connectorcurrent DC filter and checks the lists
     */
    @Test
    public void addDcConnectorQuery() {
        QueryBuilder q = new QueryBuilderImpl().withSource("charger").withFilter("connectorcurrent", "DC", ComparisonType.CONTAINS);

        ArrayList<Charger> chargerList = new ArrayList<>();
        try {
            for (Object o : new CsvInterpreter().readData(q.build(), Charger.class)) {
                chargerList.add((Charger) o);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Coordinate coordinate = new Coordinate(1.1, 2.3,  -43.53418, 172.627572);
        ArrayList<Charger> cc = charge.getNearbyChargers(chargerList, coordinate, 50);
        ObservableList<Charger> result = FXCollections.observableList(cc);
        manage.setPosition(coordinate);
        manage.createOriginalQuery();
        manage.dcQuery(true);

        assertEquals(result, manage.getChargerData());
        assertEquals(chargerList, manage.getData());
    }

    /**
     * Adds a connectorcurrent DC filter then removes the filter and checks the lists
     */
    @Test
    public void removeDcConnectorQuery() {
        QueryBuilder q = new QueryBuilderImpl().withSource("charger");

        ArrayList<Charger> chargerList = new ArrayList<>();
        try {
            for (Object o : new CsvInterpreter().readData(q.build(), Charger.class)) {
                chargerList.add((Charger) o);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Coordinate coordinate = new Coordinate(1.1, 2.3,  -43.53418, 172.627572);
        ArrayList<Charger> cc = charge.getNearbyChargers(chargerList, coordinate, 50);
        ObservableList<Charger> result = FXCollections.observableList(cc);
        manage.setPosition(coordinate);
        manage.createOriginalQuery();
        manage.dcQuery(true);
        manage.dcQuery(false);

        assertEquals(result, manage.getChargerData());
        assertEquals(chargerList, manage.getData());
    }

    /**
     * Adds a connectorcurrent AC filter and checks the lists
     */
    @Test
    public void addAcConnectorQuery() {
        QueryBuilder q = new QueryBuilderImpl().withSource("charger").withFilter("connectorcurrent", "AC", ComparisonType.CONTAINS);

        ArrayList<Charger> chargerList = new ArrayList<>();
        try {
            for (Object o : new CsvInterpreter().readData(q.build(), Charger.class)) {
                chargerList.add((Charger) o);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Coordinate coordinate = new Coordinate(1.1, 2.3,  -43.53418, 172.627572);
        ArrayList<Charger> cc = charge.getNearbyChargers(chargerList, coordinate, 50);
        ObservableList<Charger> result = FXCollections.observableList(cc);
        manage.setPosition(coordinate);
        manage.createOriginalQuery();
        manage.acQuery(true);

        assertEquals(result, manage.getChargerData());
        assertEquals(chargerList, manage.getData());
    }

    /**
     * Adds a connectorcurrent AC filter then removes the filter and checks the lists
     */
    @Test
    public void removeAcConnectorQuery() {
        QueryBuilder q = new QueryBuilderImpl().withSource("charger");

        ArrayList<Charger> chargerList = new ArrayList<>();
        try {
            for (Object o : new CsvInterpreter().readData(q.build(), Charger.class)) {
                chargerList.add((Charger) o);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Coordinate coordinate = new Coordinate(null, null, -43.522518157958984, 172.5811767578125);
        ArrayList<Charger> cc = charge.getNearbyChargers(chargerList, coordinate, 50);
        ObservableList<Charger> result = FXCollections.observableList(cc);
        manage.setPosition(coordinate);
        manage.createOriginalQuery();
        manage.acQuery(true);
        manage.acQuery(false);

        assertEquals(result, manage.getChargerData());
        assertEquals(chargerList, manage.getData());
    }

}
