package seng202.team3.logic;

// import netscape.javascript.JSObject;
import seng202.team3.data.entity.Coordinate;
import seng202.team3.data.entity.Journey;
import seng202.team3.gui.MainController;

/**
 * Manages Map-related functionality
 *
 * @author Michelle Hsieh
 * @version 1.0.0, Aug 22
 */
public class MapManager {

    /**
     * Contains the MainController for reference
     */
    private final MainManager controller;

    /**
     * Constructs the MapManager from the {@link MainController}
     *
     * @param controller the MainController currently being used
     */
    public MapManager(MainManager controller) {
        this.controller = controller;
    }

    /**
     * Returns the controller object
     *
     * @return {@link MainController}, the MainController
     */
    public MainManager getController() {
        return controller;
    }

}
