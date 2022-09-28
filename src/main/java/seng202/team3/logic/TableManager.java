package seng202.team3.logic;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.Coordinate;

/**
 * A Table Manager that implements the adding, deleting and editing
 * functionality of a charger
 *
 * @author Michelle Hsieh
 * @version 1.0.0, Sep 22
 */
public class TableManager extends ChargerHandler implements ChargerInterface {

    /** ArrayList of charger views */
    Map<Charger, Integer> chargerViews;

    /**
     * unused constructor
     */
    public TableManager() {
        // unused
    }

    /**
     * Gets a list of charger views
     */
    public void chargerViewsList() {
        chargerViews = new HashMap<Charger, Integer>();
        for (Charger charger : getData()) {
            try {
                chargerViews.put(charger, SqlInterpreter.getInstance().getChargerViews(charger));
            } catch (IOException | SQLException e) {
                chargerViews.put(charger, 0);
            }
        }
    }

    /**
     * Gets the views of the current charger
     * @param c the charger object
     * @return the number of views
     */
    public int getChargerViews(Charger c) {
        return chargerViews.get(c);
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

}
