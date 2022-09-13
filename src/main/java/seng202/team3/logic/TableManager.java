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
 * A Table Manager that implements the adding, deleting and editing functionality of a charger
 *
 * @author Michelle Hsieh
 * @version 1.0.0, Sep 22
 */
public class TableManager implements ChargerInterface {

    private QueryBuilder mainDataQuery;
    private ObservableList<Charger> chargerData;
    private Charger selectedCharger;
    private Coordinate coordinate;

    /**
     * Load the initial query
     */
    public void resetQuery() {
        mainDataQuery = new QueryBuilderImpl().withSource("charger");
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
     * Sets the coordinate to be added
     *
     * @param coordinate {@link Coordinate} to be added
     */
    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }


    /**
     * Sets the selectedCharger
     *
     * @param charger the {@link Charger} that is to be set
     */
    public void setSelectedCharger(Charger charger) {
        selectedCharger = charger;
    }

    /**
     * Gets the selectedCharger
     *
     * @return the {@link Charger} that is being selected
     */
    public Charger getSelectedCharger() {
        return selectedCharger;
    }

    /**
     * Adds a charger at a specified coordinate
     */
    public void addCharger() {

    }

    /**
     * Removes the selected charger and replaces it with null
     *
     */
    public void deleteCharger() {
    }

    /**
     * Uses the JavaScript Bridge to load the charger edit functionality of the
     * selected charger
     */
    public void editCharger() {
    }

}
