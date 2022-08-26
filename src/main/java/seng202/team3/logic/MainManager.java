package seng202.team3.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seng202.team3.data.database.ComparisonType;
import seng202.team3.data.database.CsvInterpreter;
import seng202.team3.data.database.QueryBuilder;
import seng202.team3.data.database.QueryBuilderImpl;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.Coordinate;
import seng202.team3.gui.MainController;

/**
 * Logic layer for the main Controller
 *
 * @author Matthew Doonan
 * @version 1.0.0, Aug 26
 *
 */

public class MainManager {

    private final MainController main;
    private QueryBuilder mainDataQuery;
    private Coordinate position = new Coordinate(1574161.4056, 5173542.4743, -43.5097, 172.5452);
    private final ChargerManager chargerManager = new ChargerManager();
    private ObservableList<Charger> chargerData;
    private ObservableList<Charger> closerChargers;
    private double distance;
    private int click;

    /**
     * Saves the MainController to call later
     * 
     * @param home  the main window object
     * @param slide the current slider value
     */
    public MainManager(MainController home, double slide) {
        main = home;
        distance = slide;
        click = 0;
        TempData.setController(this);
    }

    /**
     * Returns the list of chargers
     * 
     * @return chargerData of the required charger
     */
    public ObservableList<Charger> getData() {
        return chargerData;
    }

    /**
     * Load the initial query
     */
    public void createOriginalQuery() {
        mainDataQuery = new QueryBuilderImpl().withSource("charger");
        makeAllChargers();
    }

    /**
     * Create the charger list from the main Query
     */
    public void makeAllChargers() {
        try {
            List<Charger> chargerList = new ArrayList<>();
            for (Object o : new CsvInterpreter().readData(mainDataQuery.build(), Charger.class)) {
                chargerList.add((Charger) o);
            }

            chargerData = FXCollections
                    .observableList(chargerList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        compareDistance();
    }

    /**
     * Send chargers within range of the selected location to the table and temp
     * data
     */
    public void compareDistance() {
        ArrayList<Charger> arrayCloserChargers = chargerManager.getNearbyChargers(
                new ArrayList<>(chargerData), position, distance);
        closerChargers = FXCollections.observableList(arrayCloserChargers);
        TempData.setChargerList(closerChargers);
        main.addChargersToDisplay(closerChargers);
    }

    /**
     * Receives boolean result about if the AC box is ticked
     * if it is it creates a filter on mainDataQuery otherwise it removes it
     * Then it calls makeAllChargers
     * 
     * @param selected True if the ac tick box is selected else false
     */
    public void acQuery(Boolean selected) {
        if (selected) {
            mainDataQuery.withFilter("connectorcurrent", "AC", ComparisonType.CONTAINS);
        } else {
            mainDataQuery.withoutFilter("connectorcurrent");
        }
        makeAllChargers();
    }

    /**
     * Receives boolean result about if the DC box is ticked
     * if it is it creates a filter on mainDataQuery otherwise it removes it.
     * Then it calls makeAllChargers
     * 
     * @param selected True if the dc tick box is selected else false
     */
    public void dcQuery(Boolean selected) {
        if (selected) {
            mainDataQuery.withFilter("connectorcurrent", "DC", ComparisonType.CONTAINS);
        } else {
            mainDataQuery.withoutFilter("connectorcurrent");
        }
        makeAllChargers();
    }

    /**
     * Receives boolean result about if the Attraction box is ticked
     * if it is it creates a filter on mainDataQuery otherwise it removes it.
     * Then it calls makeAllChargers
     * 
     * @param selected True if the attraction tick box is selected else false
     */
    public void attractionQuery(Boolean selected) {
        if (selected) {
            mainDataQuery.withFilter("hastouristattraction", "True", ComparisonType.CONTAINS);
        } else {
            mainDataQuery.withoutFilter("hastouristattraction");
        }
        makeAllChargers();
    }

    /**
     * Receives boolean result about if the noChargingCost box is ticked
     * if it is it creates a filter on mainDataQuery otherwise it removes it.
     * Then it calls makeAllChargers
     * 
     * @param selected True if the noChargingCost tick box is selected else false
     */
    public void noChargeQuery(Boolean selected) {
        if (selected) {
            mainDataQuery.withFilter("haschargingcost", "False", ComparisonType.CONTAINS);
        } else {
            mainDataQuery.withoutFilter("haschargingcost");
        }
        makeAllChargers();
    }

    /**
     * Clears the search bar on the first click
     */
    public void clearSearchCharger() {
        if (click == 0) {
            click += 1;
            main.clearSearchBar();
        }

    }

    /**
     * Receives a text and creates a new Query for address with the text in it
     * 
     * @param text String from the Search bar
     */
    public void addressQuery(String text) {
        mainDataQuery.withoutFilter("address");
        mainDataQuery.withFilter("address", text, ComparisonType.CONTAINS);
        makeAllChargers();
    }

    /**
     * Receives a distance and sets the distance to it.
     * It then calls makeAllChargers to change the chargers
     * 
     * @param newDistance number from the slider
     */
    public void sliderChange(double newDistance) {
        distance = newDistance;
        makeAllChargers();
    }

    /**
     * Returns the closer charger data
     * 
     * @return an Observable list of chargers that are displayed
     */
    public ObservableList<Charger> getChargerData() {
        return closerChargers;
    }

    /**
     * Sets the position using a {@link Coordinate}
     *
     * @param position a Coordinate of the selected position
     */
    public void setPosition(Coordinate position) {
        this.position = position;
        compareDistance();
    }

    /**
     * Gets the position of the {@link Coordinate}
     *
     * @return Coordinate of position
     */
    public Coordinate getPosition() {
        return position;
    }

    public MainController getController() {
        return main;
    }

}
