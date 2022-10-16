package seng202.team3.gui.controller.map;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team3.logic.manager.TableManager;
import seng202.team3.logic.util.GeoLocationHandler;
import seng202.team3.logic.util.JavaScriptBridge;

/**
 * This is the controller for the little map to allow you to add coordinates
 *
 * @author Michelle Hsieh
 * @version 1.0.0, Sep 22
 */
public class MiniMapController extends MapHandler {
    /**
     * Logger
     */
    private static final Logger logManager = LogManager.getLogger();

    /**
     * TableManager manager class associated with controller
     */
    private TableManager manager;

    /**
     * unused constructor
     */
    public MiniMapController() {
        // unused
    }

    /**
     * Initialise the map view
     *
     * @param stage   the stage of this
     * @param manager the manager of table view
     */
    public void init(Stage stage, TableManager manager) {
        path = "html/mini_map.html";
        this.manager = manager;
        this.stage = stage;
        javaScriptBridge = new JavaScriptBridge();
        this.stage.sizeToScene();
        manager.setAdding(false);
        initMap();
    }

    /**
     * Adds chargers on Map. Will currently be nothing; consider interface
     */
    @Override
    public void addChargersOnMap() {
        logManager.info("No chargers to load");
    }

    /**
     * Updates the coordinate in the TableManager
     */
    @FXML
    public void getCoordinateWithAddress() {
        GeoLocationHandler.getCoordinate().setAddress(javaScriptBridge.makeLocationName());
        GeoLocationHandler.setCoordinate(GeoLocationHandler.getCoordinate());
        manager.setAdding(true);
        stage.close();
    }

    /**
     * Sets the table manager to associate the manager with the
     *
     * @param tableManager the {@link seng202.team3.logic.manager.TableManager}
     *                     which will
     *                     store the coordinate
     */
    public void setManager(TableManager tableManager) {
        manager = tableManager;
    }
}
