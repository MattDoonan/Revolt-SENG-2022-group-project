package seng202.team3.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seng202.team3.data.database.ComparisonType;
import seng202.team3.data.database.QueryBuilder;
import seng202.team3.data.database.QueryBuilderImpl;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.Coordinate;

/**
 * An abstract class that deals with querying charger data and positional data
 *
 * @author Michelle Hsieh
 * @version 1.0.0, Sep 22
 */
public class ChargerHandler {

    protected QueryBuilder mainDataQuery;
    protected ObservableList<Charger> chargerData;
    protected Charger selectedCharger;
    protected Coordinate selectedCoordinate;


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
     * Sets the position using a {@link Coordinate}
     *
     * @param coordinate a Coordinate of the selected position
     */
    public void setPosition(Coordinate coordinate) {
        selectedCoordinate = coordinate;
    }

    /**
     * Gets the position of the {@link Coordinate}
     *
     * @return Coordinate of position
     */
    public Coordinate getPosition() {
        return selectedCoordinate;
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
            for (Object o : SqlInterpreter.getInstance()
                    .readData(mainDataQuery.build(), Charger.class)) {
                chargerList.add((Charger) o);
            }

            chargerData = FXCollections
                    .observableList(chargerList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Takes an input of field, criteria and type to adjust the main data query.
     *
     * @param field    a String of the field for the query
     * @param criteria a String of the criteria for the query
     * @param type     an enum of the comparison type of the query
     */
    public void adjustQuery(String field, String criteria, ComparisonType type) {
        mainDataQuery.withFilter(field, criteria, type);
    }

    /**
     * Returns a string of connectors names;
     * @param c a charger object
     * @return a String of connector names
     */
    public String getConnectors(Charger c) {
        StringBuilder word = new StringBuilder();
        ArrayList<String> check = new ArrayList<>();
        for (int i = 0; i < c.getConnectors().size(); i++) {
            if (!check.contains(c.getConnectors().get(i).getCurrent())) {
                word.append(" ").append(c.getConnectors().get(i).getCurrent());
                check.add(c.getConnectors().get(i).getCurrent());
            }
        }
        return word.toString();
    }


}
