package seng202.team3.gui;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.Coordinate;
import seng202.team3.logic.JavaScriptBridge;
import seng202.team3.logic.MapManager;

/**
 * A start into a MapViewController which uses the University UC OSM viewer
 *
 * @author Michelle Hsieh, based off code from Morgan English
 * @version 1.0.0, Aug 22
 */
public class MapViewController {

    /**
     * FXML components
     */
    @FXML
    private WebView webView;
    private WebEngine webEngine;
    private Stage stage;
    private MapManager map;
    private JavaScriptBridge javaScriptBridge;
    private JSObject javaScriptConnector;
    // private boolean routeDisplayed = false;

    /**
     * Initialise the map view
     * 
     * @param map Map view to interact with
     */
    public void init(Stage stage, MapManager map) {
        this.stage = stage;
        javaScriptBridge = new JavaScriptBridge();
        this.map = map;
        initMap();
        this.stage.sizeToScene();
    }

    /**
     * Function to load map html file
     *
     * @return String of html file
     */
    private String getHtml() {
        InputStream is = getClass().getResourceAsStream("/html/map.html");
        return new BufferedReader(
                new InputStreamReader(is, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
    }

    /**
     * Initialises the map by loading the html into the webengine
     */
    private void initMap() {
        webEngine = webView.getEngine();
        webEngine.setJavaScriptEnabled(true);
        webEngine.loadContent(getHtml());

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
    private void addChargersOnMap() {
        for (Charger charger : map.getController().getChargerData()) {
            javaScriptConnector.call("addMarker", charger.getLocation().getAddress(),
                    charger.getLocation().getLat(), charger.getLocation().getLon());
        }
    }

    /**
     * Adds just one Coordinate {@link Coordinate} onto the map, and associates
     * this map with this coordinate
     *
     * @param coordinate the coordinate which is clicked
     */
    public void makeCoordinate(Coordinate coordinate) {
        javaScriptConnector.call("addCoordinate", "Current Coordinate: ",
                coordinate.getLat(), coordinate.getLon());
        map.getController().setPosition(coordinate);
    }

    /**
     * Takes one Coordinate {@link Coordinate} onto the map, and moves
     * this map to this coordinate
     *
     * @param coordinate , the coordinate which is selected
     */
    public void changePosition(Coordinate coordinate) {
        javaScriptConnector.call("movePosition",
                coordinate.getLat(), coordinate.getLon());

    }

}
