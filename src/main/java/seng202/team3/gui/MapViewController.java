package seng202.team3.gui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
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
public class MapViewController extends MapHandler {

    private MapManager map;
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
     * Adds all chargers on the map
     */
    @Override
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
        map.makeCoordinate();
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
        if (charger != null) {
            Coordinate chargerCoord = charger.getLocation();
            javaScriptConnector.call("addLocationToRoute", coord.getLat(), coord.getLon(),
                    coord.getAddress(), "p", 0);
            javaScriptConnector.call("addLocationToRoute",
                    chargerCoord.getLat(), chargerCoord.getLon(), charger.getChargerId(), "c", 1);
            javaScriptConnector.call("addRoute");
        }
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


    /**
     * Loads a generic prompt screen pop-up {@link PromptPopUp}
     *
     * @param prompt a String of the instructions
     */
    public void loadPromptScreens(String prompt, String type) {
        try {
            FXMLLoader popUp = new FXMLLoader(getClass().getResource(
                    "/fxml/prompt_popup.fxml"));
            VBox root = popUp.load();
            Scene modalScene = new Scene(root);
            Stage modal = new Stage();
            modal.setScene(modalScene);
            modal.setResizable(false);
            modal.setTitle("Click or cancel:");
            modal.initModality(Modality.WINDOW_MODAL);
            PromptPopUp popController = popUp.getController();
            popController.setType(type);
            popController.addPrompt(prompt);
            modal.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            addChargersOnMap();
            new MenuController().getController().viewChargers(null);
        }
    }

    /**
     * Executes an add prompt
     */
    @FXML
    public void addCharger() {
        if (new MenuController().getController().getManager().getPosition().getAddress() == null) {
            javaScriptConnector.call("addCoordinateName");
        }
        loadPromptScreens("Search an address or click on the map\n"
                + "and confirm to add a charger: \n\n", "add");
    }

    /**
     * Executes an edit prompt
     */
    @FXML
    public void editCharger() {
        loadPromptScreens("Click on a charger on the map and\n"
                + "confirm to edit a charger: \n\n", "edit");
    }

    /**
     * Executes a delete prompt
     */
    @FXML
    public void deleteCharger() {
        loadPromptScreens("Click on a charger on the map and\n"
                + "confirm to DELETE a charger: \n\n", "delete");
    }


}
