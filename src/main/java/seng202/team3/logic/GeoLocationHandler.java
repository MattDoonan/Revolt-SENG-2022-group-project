package seng202.team3.logic;

import seng202.team3.data.entity.Coordinate;

/**
 * A Singleton Class to handle the geolocation parsing
 *
 * @author Michelle Hsieh
 * @version 1.0.0, Sep 22
 */
public class GeoLocationHandler {

    private static GeoLocationHandler instance = new GeoLocationHandler();
    private Coordinate coordinate;

    /**
     * Private initaliser for the geolocation handler
     */
    private GeoLocationHandler() {}

    /**
     * The way to get the instance of the geolocation handler
     *
     * @return a GeoLocationHandler
     */
    public static GeoLocationHandler getInstance() {
        if (instance == null) {
            synchronized (GeoLocationHandler.class) {
                if (instance == null) {
                    instance = new GeoLocationHandler();
                }
            }
        }
        return instance;
    }

    /**
     * Gets the coordinate
     *
     * @return {@link Coordinate} the selected coordinate
     */
    public Coordinate getCoordinate() {
        return coordinate;
    }

    /**
     * Sets the coordinate
     *
     * @param coordinate the {@link Coordinate} to set the coordinate
     */
    public void setCoordinate(Coordinate coordinate, String name) {
        this.coordinate = coordinate;
        coordinate.setAddress(name);
    }

    /**
     * Clears the coordinate
     */
    public void clearCoordinate() {
        coordinate = null;
    }
}
