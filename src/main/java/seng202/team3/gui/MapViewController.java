package seng202.team3.gui;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.Coordinate;
import seng202.team3.data.entity.PermissionLevel;
import seng202.team3.logic.GeoLocationHandler;
import seng202.team3.logic.JavaScriptBridge;
import seng202.team3.logic.MapManager;
import seng202.team3.logic.UserManager;

/**
 * A start into a MapViewController which uses the University UC OSM viewer
 *
 * @author Michelle Hsieh, based off code from Morgan English
 * @version 1.0.2, Aug 22
 */
public class MapViewController extends MapHandler {

    /**
     * Logger
     */
    private static final Logger logManager = LogManager.getLogger();

    /**
     * The MapManager of this controller
     */
    private MapManager map;

    /**
     * Boolean; true if the route is displayed; false if not
     */
    private boolean routeDisplayed = false;

    /**
     * The routing button
     */
    @FXML
    private Button routing;

    /**
     * The add Charger button
     */
    @FXML
    private Button addButton;

    /**
     * unused constructor
     */
    public MapViewController() {
        // unused
    }

    /**
     * Initialise the map view
     *
     * @param map   Map view to interact with
     * @param stage a {@link javafx.stage.Stage} object
     */
    public void init(Stage stage, MapManager map) {
        path = "html/map.html";
        this.stage = stage;
        javaScriptBridge = new JavaScriptBridge();
        this.map = map;
        addButton.setOpacity(0.0);
        initMap();
        this.stage.sizeToScene();

    }

    /**
     * {@inheritDoc}
     * Adds all chargers on the map
     */
    @Override
    public void addChargersOnMap() {
        if (UserManager.getUser().getLevel() != PermissionLevel.ADMIN
                && UserManager.getUser().getLevel() != PermissionLevel.CHARGEROWNER) {
            addButton.setOpacity(0.0);
        } else {
            addButton.setOpacity(100.0);
        }
        int userId = 0;
        if (UserManager.getUser().getLevel() == PermissionLevel.ADMIN) {
            userId = -1;
        } else if (UserManager.getUser().getLevel() == PermissionLevel.CHARGEROWNER) {
            userId = UserManager.getUser().getUserid();
        }

        if (!MapHandler.MAP_REQUEST || javaScriptConnector == null) {
            return;
        }

        javaScriptConnector.call("clearMarkers");

        if (UserManager.getUser().getLevel() == PermissionLevel.ADMIN) {
            javaScriptConnector.call("givePermission");
        }
        for (Charger charger : map.getController().getCloseChargerData()) {
            boolean hasPermission = false;
            if (userId == -1 || userId == charger.getOwnerId()) {
                hasPermission = true;
            }
            javaScriptConnector.call("addMarker", charger.getLocation().getAddress(),
                    charger.getLocation().getLat(), charger.getLocation().getLon(),
                    charger.getChargerId(), hasPermission);
        }

    }

    /**
     * Adds just one Coordinate {@link seng202.team3.data.entity.Coordinate} onto
     * the map, and associates
     * this map with this coordinate
     *
     * @param coordinate the coordinate which is clicked
     */
    public void makeCoordinate(Coordinate coordinate) {
        if (MapHandler.MAP_REQUEST) {
            javaScriptConnector.call("removeCoordinate");
            javaScriptConnector.call("addCoordinate", "Current Coordinate: ",
                    coordinate.getLat(), coordinate.getLon());
            javaScriptConnector.call("addCoordinateName");
            if (locationAccepted) {
                javaScriptConnector.call("changeZoom");
            }
            changePosition(coordinate);
            map.makeCoordinate(coordinate);

            logManager.info("Point created on map");
        } else {
            logManager.info("Map Unavailable: Could not add point to map");
        }
    }

    /**
     * Adds the coordinate name to the selected point
     */
    public void addCoordinateName() {
        if (MapHandler.MAP_REQUEST) {
            javaScriptConnector.call("addCoordinate",
                    GeoLocationHandler.getInstance().getCoordinate().getAddress(),
                    GeoLocationHandler.getInstance().getCoordinate().getLat(),
                    GeoLocationHandler.getInstance().getCoordinate().getLon());
        } else {
            logManager.info("Map Unavailable: Could not name point");
        }
    }

    /**
     * Takes one Coordinate {@link seng202.team3.data.entity.Coordinate} onto the
     * map, and moves
     * this map to this coordinate
     *
     * @param coordinate a {@link seng202.team3.data.entity.Coordinate} object
     */
    public void changePosition(Coordinate coordinate) {
        if (MapHandler.MAP_REQUEST) {
            javaScriptConnector.call("movePosition",
                    coordinate.getLat(), coordinate.getLon());
        }
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
     * selected coordinate (as a coordinate) to the currently selected charger (as a
     * coordinate).
     */
    public void addRouteToCharger() {
        routeDisplayed = true;
        if (MapHandler.MAP_REQUEST) {
            Coordinate coord = map.getController().getPosition();
            Charger charger = map.getController().getSelectedCharger();
            if (charger != null) {
                Coordinate chargerCoord = charger.getLocation();
                javaScriptConnector.call("addLocationToRoute", coord.getLat(), coord.getLon(),
                        coord.getAddress(), "p", 0);
                javaScriptConnector.call("addLocationToRoute",
                        chargerCoord.getLat(), chargerCoord.getLon(),
                        charger.getChargerId(), "c", 1);
                javaScriptConnector.call("addRoute");
            }
            logManager.info("Route added to map");
        } else {
            logManager.info("Map Unavailable: Could not add route");
        }

    }

    /**
     * removes route from map, calling the underlying js function
     */
    private void removeRoute() {
        routeDisplayed = false;
        if (MapHandler.MAP_REQUEST) {
            javaScriptConnector.call("removeRoute");
        } else {
            logManager.info("Map Unavailable: Could not remove route");
        }
    }

    /**
     * Adds a stop into a route, and displays it on JS
     *
     * @param coordinate the coordinate of the stop to add
     */
    public void addStopInRoute(Coordinate coordinate) {
        if (MapHandler.MAP_REQUEST) {
            javaScriptConnector.call("addLocationToRoute", coordinate.getLat(), coordinate.getLon(),
                    "Stop", "p", 1);
            logManager.info("Stop added to route");
        } else {
            logManager.info("Map Unavailable: Could not add stop");
        }
    }

    /**
     * Simple toggle to hide or display the route on click
     */
    public void toggleRoute() {
        if (routeDisplayed) {
            removeRoute();

            if (UserManager.getUser().getLevel() == PermissionLevel.ADMIN
                    || UserManager.getUser().getLevel() == PermissionLevel.CHARGEROWNER) {
                addButton.setOpacity(100.0);
            }
            routing.setText("Route To Charger");
            routing.setStyle("-fx-background-color:#3ea055;");
        } else {
            addButton.setOpacity(0.0);
            addRouteToCharger();
            routing.setText("Stop Routing");
            routing.setStyle("-fx-background-color:#FF3131;");
        }
    }

    /**
     * Gets the routeDisplayed field
     *
     * @return a boolean of true, if route is displayed, else false.
     */
    public boolean isRouteDisplayed() {
        return routeDisplayed;
    }

    /**
     * Loads a generic prompt screen pop-up {@link seng202.team3.gui.PromptPopUp}
     *
     * @param prompt a String of the instructions
     * @param type   a {@link java.lang.String} object
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
            logManager.error(e.getMessage());
        } finally {
            if (MapHandler.MAP_REQUEST) {
                addChargersOnMap();
                addCoordinateName();
            }
            MenuController.getController().viewChargers(null);
        }
    }

    /**
     * Executes an add prompt
     */
    @FXML
    public void addCharger() {
        if (addButton.getOpacity() != 100.0) {
            return;
        }

        if (MapHandler.MAP_REQUEST) {
            loadPromptScreens("Search an address or click on the map\n"
                    + "and confirm to add a charger: \n\n", "add");
        } else {
            javaScriptBridge.loadChargerEdit(null);
        }
    }

    /**
     * Toggles from the relocate position button
     */
    @FXML
    public void getLocation() {
        if (locationAccepted == null || !locationAccepted) {
            setLocationAccepted(null);
        }
        this.getUserLocation();
        map.getController().setPosition();

        makeCoordinate(GeoLocationHandler.getInstance().getCoordinate());
    }

}
