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
     * Contains the MainManager for reference
     */
    private final MainManager manager;

    /**
     * Constructs the MapManager from the {@link MainManager}
     *
     * @param manager the MainManager currently being used
     */
    public MapManager(MainManager manager) {
        this.manager = manager;
    }

    /**
     * Returns the manager object
     *
     * @return {@link MainManager}, the MainManager
     */
    public MainManager getController() {
        return manager;
    }

    /**
     * Associates this map with this coordinate {@link Coordinate}
     *
     * @param coordinate the coordinate which is selected
     */
    public void makeCoordinate(Coordinate coordinate) {
        manager.setPosition(coordinate);
        manager.getCloseChargerData();
    }

}
