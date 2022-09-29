package seng202.team3.logic;

import java.io.IOException;
import seng202.team3.data.database.ComparisonType;
import seng202.team3.data.database.QueryBuilderImpl;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.Coordinate;
import seng202.team3.data.entity.PermissionLevel;
import seng202.team3.data.entity.User;

/**
 * A Table Manager that implements the adding, deleting and editing
 * functionality of a charger
 *
 * @author Michelle Hsieh
 * @version 1.0.0, Sep 22
 */
public class TableManager extends ChargerHandler implements ChargerInterface {

    /**
     * The current user of this table
     */
    private User user;

    /**
     * unused constructor
     */
    public TableManager() {
        // unused
    }

    /**
     * {@inheritDoc}
     * Adds a charger at a specified coordinate
     */
    @Override
    public void addCharger() {
        Coordinate coordinate = GeoLocationHandler.getInstance().getCoordinate();
        if (coordinate != null) {
            setPosition();
        }
        GeoLocationHandler.getInstance().clearCoordinate();
    }

    /**
     * {@inheritDoc}
     * Removes the selected charger and replaces it with null
     */
    @Override
    public void deleteCharger() {
        if (getSelectedCharger() != null) {
            try {
                SqlInterpreter.getInstance().deleteData("charger",
                        getSelectedCharger().getChargerId());
                setSelectedCharger(null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Uses the JavaScript Bridge to load the charger edit functionality of the
     * selected charger
     */
    public void editCharger() {
        if (getSelectedCharger() != null) {
            JavaScriptBridge bridge = new JavaScriptBridge();
            bridge.loadChargerEdit(selectedCharger, selectedCoordinate);
        }
    }


    /**
     * Load the initial query; overwritten to have a filter according to permission level
     */
    @Override
    public void resetQuery() {
        mainDataQuery = new QueryBuilderImpl().withSource("charger");
        if (UserManager.getUser().getLevel() == PermissionLevel.CHARGEROWNER) {
            mainDataQuery.withFilter("owner",
                    Integer.toString(UserManager.getUser().getUserid()), ComparisonType.EQUAL);
        }
    }
}
