package seng202.team3.gui;

import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team3.logic.GeoLocationHandler;
import seng202.team3.logic.JavaScriptBridge;
import seng202.team3.logic.TableManager;

/**
 * This is the controller for the little map to allow you to add coordinates
 *
 * @author Michelle Hsieh
 * @version 1.0.0, Sep 22
 */
public class MiniMapController {
    /**
     * Logger
     */
    private static final Logger logManager = LogManager.getLogger();

    /**
     * WebView to host
     */
    @FXML
    private WebView webView;

    /**
     * WebEngine to launch map
     */
    private WebEngine webEngine;

    /**
     * TableManager manager class associated with controller
     */
    private TableManager manager;

    /**
     * JavaScript object to run JavaScript
     */
    private JSObject javaScriptConnector;

    /**
     * JavaScriptBridge of this map to communicate
     */
    private JavaScriptBridge javaScriptBridge;

    /**
     * Stage of the application
     */
    private Stage stage;

    /**
     * unused constructor
     */
    public MiniMapController() {
        // unused
    }

    /**
     * Initialise the map view
     *
     * @param stage the stage of this
     */
    public void init(Stage stage) {
        this.stage = stage;
        javaScriptBridge = new JavaScriptBridge();
        this.stage.sizeToScene();
        initMap();
    }

    /**
     * Initialises the map by loading the html into the webengine
     */
    private void initMap() {
        webEngine = webView.getEngine();
        webEngine.setJavaScriptEnabled(true);
        webEngine.load(getClass().getClassLoader().getResource("html/mini_map.html")
                .toExternalForm());
        logManager.info("Loading mini map...");
        webEngine.getLoadWorker().stateProperty().addListener(
                (ov, oldState, newState) -> {
                    if (newState == Worker.State.SUCCEEDED) {
                        JSObject window = (JSObject) webEngine.executeScript("window");
                        window.setMember("javaScriptBridge", javaScriptBridge);
                        javaScriptConnector = (JSObject) webEngine.executeScript("jsConnector");
                        javaScriptConnector.call("initMap");
                        logManager.info("Mini map loaded successfully");
                    }
                });
    }

    /**
     * Updates the coordinate in the TableManager
     */
    @FXML
    public void getCoordinateWithAddress() {
        GeoLocationHandler.getInstance().setCoordinate(
                GeoLocationHandler.getInstance().getCoordinate(),
                javaScriptBridge.makeLocationName());
        manager.setPosition();
        stage.close();
    }

    /**
     * Sets the table manager to associate the manager with the
     *
     * @param tableManager the {@link seng202.team3.logic.TableManager} which will
     *                     store the coordinate
     */
    public void setManager(TableManager tableManager) {
        manager = tableManager;
    }
}
