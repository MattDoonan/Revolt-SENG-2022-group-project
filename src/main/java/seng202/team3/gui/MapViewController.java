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
import seng202.team3.data.entity.Journey;
import seng202.team3.logic.JavaScriptBridge;
import seng202.team3.logic.MapManager;

/**
 * A start into a MapViewController which uses the University UC OSM viewer
 *
 * @author Michelle Hsieh, based off code from Morgan English
 * @version 1.0.2, Aug 22
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
    private boolean routeDisplayed = false;

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
    public void addChargersOnMap() {
        javaScriptConnector.call("clearMarkers");
        for (Charger charger : map.getController().getCloseChargerData()) {
            javaScriptConnector.call("addMarker", charger.getLocation().getAddress(),
                    charger.getLocation().getLat(), charger.getLocation().getLon(),
                    charger.getChargerId());
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
        map.makeCoordinate(coordinate);
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

    /**
     * Check if map has access to javascript
     * 
     * @return true if map has access to javascript
     */
    public boolean getConnectorStatus() {
        return javaScriptConnector != null;
    }


    /**
     * Adds route to map, calling the underlying js function, from the currently
     * selected coordinate (as a coordinate) to the currently selected charger (as a coordinate).
     */
    public void addRouteToCharger() {
        routeDisplayed = true;
        Coordinate coord = map.getController().getPosition();
        Charger charger = map.getController().getSelectedCharger();
        Coordinate chargerCoord = charger.getLocation();
        javaScriptConnector.call("addLocationToRoute", coord.getLat(), coord.getLon(),
                coord.getAddress(), "p", 0);
        javaScriptConnector.call("addLocationToRoute", chargerCoord.getLat(), chargerCoord.getLon(),
                charger.getChargerId(), "c", 1);
        javaScriptConnector.call("addRoute");
    }

    /**
     * removes route from map, calling the underlying js function
     */
    private void removeRoute() {
        routeDisplayed = false;
        javaScriptConnector.call("removeRoute");
    }

    /**
     * Adds a stop into a route, and displays it on JS
     *
     * @param coordinate the coordinate of the stop to add
     */
    public void addStopInRoute(Coordinate coordinate) {
        javaScriptConnector.call("addLocationToRoute", coordinate.getLat(), coordinate.getLon(),
                "Stop", "p", 1);
    }

    /**
     * Simple toggle to hide or display the route on click
     */
    public void toggleRoute() {
        if (routeDisplayed) {
            removeRoute();
        } else {
            addRouteToCharger();
        }
    }


}
