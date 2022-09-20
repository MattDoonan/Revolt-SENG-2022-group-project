package seng202.team3.gui;

import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import seng202.team3.logic.JavaScriptBridge;

/**
 * An abstract class that has basic JS initialisation for the MapView and
 * JourneyController
 *
 * @author Michelle Hsieh
 * @version 1.0.0, Sep 22
 */
public abstract class MapHandler {

    /**
     * FXML components
     */
    @FXML
    protected WebView webView;

    /**
     * WebEngine to load the map
     */
    protected WebEngine webEngine;

    /**
     * Stage for the application
     */
    protected Stage stage;

    /**
     * The JavaScript bridge to Java
     */
    protected JavaScriptBridge javaScriptBridge;

    /**
     * The JavaScript connector object
     */
    protected JSObject javaScriptConnector;

    /**
     * unused constructor
     */
    public MapHandler() {
        // unused
    }

    /**
     * Initialises the map by loading the html into the webengine
     */
    public void initMap() {
        webEngine = webView.getEngine();
        webEngine.setJavaScriptEnabled(true);
        webEngine.load(getClass().getClassLoader().getResource("html/map.html").toExternalForm());

        webEngine.getLoadWorker().stateProperty().addListener(
                (ov, oldState, newState) -> {
                    if (newState == Worker.State.SUCCEEDED) {
                        JSObject window = (JSObject) webEngine.executeScript("window");
                        window.setMember("javaScriptBridge", javaScriptBridge);
                        javaScriptConnector = (JSObject) webEngine.executeScript("jsConnector");
                        javaScriptConnector.call("initMap");

                        addChargersOnMap();
                    }
                });
    }

    /**
     * Adds all chargers on the map
     */
    public abstract void addChargersOnMap();

}
