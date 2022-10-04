package seng202.team3.logic;

import seng202.team3.data.entity.Coordinate;
import seng202.team3.gui.MenuController;

/**
 * Manages Map-related functionality
 *
 * @author Michelle Hsieh
 * @version 1.0.0, Aug 22
 */
public class MapManager {

    /**
     * Contains the MainManager for reference
     */
    private final MainManager manager;

    /**
     * Constructs the MapManager from the {@link seng202.team3.logic.MainManager}
     *
     * @param manager the MainManager currently being used
     */
    public MapManager(MainManager manager) {
        this.manager = manager;
    }

    /**
     * Returns the manager object
     *
     * @return {@link seng202.team3.logic.MainManager}, the MainManager
     */
    public MainManager getController() {
        return manager;
    }

    /**
     * Associates this map with the geolocation coordinate, refreshes the Main table
     * {@link seng202.team3.data.entity.Coordinate}
     * 
     * @param coordinate the coordinate associated
     */
    public void makeCoordinate(Coordinate coordinate) {
        GeoLocationHandler.setCoordinate(coordinate,
                coordinate.getAddress());

        manager.setPosition();
        MenuController.getController().refreshTable();
    }

}
