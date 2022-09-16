package seng202.team3.logic;


import seng202.team3.data.entity.Coordinate;

/**
 * A Table Manager that implements the adding, deleting and editing functionality of a charger
 *
 * @author Michelle Hsieh
 * @version 1.0.0, Sep 22
 */
public class TableManager extends ChargerHandler implements ChargerInterface {

    /**
     * Adds a charger at a specified coordinate
     */
    public void addCharger() {
        Coordinate coordinate = GeoLocationHandler.getInstance().getCoordinate();
        if (coordinate != null) {
            setPosition(coordinate);
        }
        GeoLocationHandler.getInstance().clearCoordinate();
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
