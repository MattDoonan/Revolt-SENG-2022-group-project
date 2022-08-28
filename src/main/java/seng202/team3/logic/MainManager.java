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

/**
 * Logic layer for the main Controller
 *
 * @author Matthew Doonan, Michelle Hsieh
 * @version 1.0.1, Aug 22
 *
 */

public class MainManager {

    private QueryBuilder mainDataQuery;
    private Coordinate position;
    private double distance = 0;
    private final ChargerManager chargerManager = new ChargerManager();
    private ObservableList<Charger> chargerData;
    private Charger selectedCharger;

    /**
     * Saves the MainController to call later
     */
    public MainManager() {
        position = new Coordinate(null, null, -43.522518157958984, 172.5811767578125);
    }


    /**
     * Sets the selected charger
     *
     * @param selectedCharger {@link Charger} which is being currently selected
     */
    public void setSelectedCharger(Charger selectedCharger) {
        this.selectedCharger = selectedCharger;
    }

    /**
     * Gets the selected charger
     *
     * @return {@link Charger} the charger which is currently selected
     */
    public Charger getSelectedCharger() {
        return selectedCharger;
    }

    /**
     * Sets the distance
     *
     * @param distance a double of the set distance
     */
    public void setDistance(double distance) {
        this.distance = distance;
    }


    /**
     * Load the initial query
     */
    public void resetQuery() {
        mainDataQuery = new QueryBuilderImpl().withSource("charger");
    }

    /**
     * Create the charger list from the main Query
     *
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
    }

    /**
     * Send chargers within range of the selected location to the table and temp
     * data.
     *
     * @return an ObservableList of {@link Charger}s which are nearby
     */
    public ObservableList<Charger> compareDistance() {
        ArrayList<Charger> arrayChargers = new ArrayList<>(chargerData);
        if (distance != 0) {
            arrayChargers = chargerManager.getNearbyChargers(
                    arrayChargers, position, distance);
        }
        ObservableList<Charger> closerChargers = FXCollections.observableList(arrayChargers);
        TempData.setChargerList(closerChargers);

        return closerChargers;
    }

    /**
     * Takes an input of field, criteria and type to adjust the main data query.
     * 
     * @param field a String of the field for the query
     * @param criteria a String of the criterial for the query
     * @param type an enum of the comparison type of the query
     */
    public void adjustQuery(String field, String criteria, ComparisonType type) {
        mainDataQuery.withFilter(field, criteria, type);
    }



    /**
     * Returns a list of the closest chargers according to set distance from position
     *
     * @return an Observable list of chargers
     */
    public ObservableList<Charger> getCloseChargerData() {
        if (position == null) {
            return getData();
        }
        return compareDistance();
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
     * Sets the position using a {@link Coordinate}
     *
     * @param coordinate a Coordinate of the selected position
     */
    public void setPosition(Coordinate coordinate) {
        position = coordinate;
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



}
