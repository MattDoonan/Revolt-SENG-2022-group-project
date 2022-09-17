package seng202.team3.gui;

import javafx.stage.Stage;
import seng202.team3.logic.JavaScriptBridge;
import seng202.team3.logic.JourneyManager;

/**
 * A Journey Map Controller that extends the abstract MapHandler class
 *
 * @author Michelle Hsieh
 * @version 1.0.0, Sep 22
 */
public class JourneyMapController extends MapHandler {

    private JourneyManager map;

    /**
     * Initialise the map view
     *
     * @param map Map view to interact with
     */
    public void init(Stage stage, JourneyManager map) {
        this.stage = stage;
        javaScriptBridge = new JavaScriptBridge();
        this.map = map;
        initMap();
        this.stage.sizeToScene();
    }

    /**
     * Implements adding a list of all eligible chargers on the map
     */
    @Override
    public void addChargersOnMap() {
    }
}
