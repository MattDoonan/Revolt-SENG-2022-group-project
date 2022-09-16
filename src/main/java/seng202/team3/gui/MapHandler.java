package seng202.team3.gui;

import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import seng202.team3.data.entity.Charger;
import seng202.team3.logic.JavaScriptBridge;
import seng202.team3.logic.MapManager;

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

    protected WebEngine webEngine;
    protected Stage stage;
    protected JavaScriptBridge javaScriptBridge;
    protected JSObject javaScriptConnector;


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
