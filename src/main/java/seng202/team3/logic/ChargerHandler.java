package seng202.team3.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team3.data.database.ComparisonType;
import seng202.team3.data.database.QueryBuilder;
import seng202.team3.data.database.QueryBuilderImpl;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.Coordinate;
import seng202.team3.data.entity.Entity;
import seng202.team3.data.entity.EntityType;

/**
 * A class that deals with querying charger data and positional data
 *
 * @author Michelle Hsieh
 * @version 1.0.0, Sep 22
 */
public class ChargerHandler {
    /**
     * Logger
     */
    private static final Logger logManager = LogManager.getLogger();

    /**
     * Main query to be modified to retrieve chargers
     */
    protected QueryBuilder mainDataQuery;

    /**
     * List to store active chargers
     */
    protected ObservableList<Charger> chargerData;

    /**
     * Actively selected charger
     */
    protected Charger selectedCharger;

    /**
     * Coordinate of selected charger
     */
    protected Coordinate selectedCoordinate;

    /**
     * Unused constructor
     */
    public ChargerHandler() {
        // unused
    }

    /**
     * Sets the selected charger
     *
     * @param selectedCharger {@link seng202.team3.data.entity.Charger} which is
     *                        being currently selected
     */
    public void setSelectedCharger(Charger selectedCharger) {
        this.selectedCharger = selectedCharger;
    }

    /**
     * Gets the selected charger
     *
     * @return {@link seng202.team3.data.entity.Charger} the charger which is
     *         currently selected
     */
    public Charger getSelectedCharger() {
        return selectedCharger;
    }

    /**
     * Sets the position using a {@link seng202.team3.data.entity.Coordinate} from
     * GeolocationHandler
     */
    public void setPosition() {
        selectedCoordinate = GeoLocationHandler.getCoordinate();
    }

    /**
     * Gets the position of the {@link seng202.team3.data.entity.Coordinate}
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
        mainDataQuery = new QueryBuilderImpl().withSource(EntityType.CHARGER);
    }

    /**
     * Create the charger list from the main Query
     */
    public void makeAllChargers() {

        try {
            List<Charger> chargerList = new ArrayList<>();
            for (Entity o : SqlInterpreter.getInstance()
                    .readData(mainDataQuery.build())) {
                chargerList.add((Charger) o);
            }

            chargerData = FXCollections
                    .observableList(chargerList);
        } catch (IOException e) {
            logManager.error(e.getMessage());
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
     *
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

    /**
     * Updates the current views on java object and in database
     * Chargers are reloaded on page refresh without event action so both need to
     * be updated simultaneously or data will be lost
     */
    public void updateSelectedViews() {
        selectedCharger.incrementViews();
        try {
            SqlInterpreter.getInstance().updateChargerViews(selectedCharger);
        } catch (IOException e) {
            logManager.warn(e.getMessage());
        }
    }
}
