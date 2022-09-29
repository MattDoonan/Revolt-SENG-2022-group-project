package seng202.team3.gui;

import com.maxmind.geoip2.exception.GeoIp2Exception;
import java.io.IOException;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import seng202.team3.logic.GeoLocationHandler;
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
     * Represents response to location services request
     */
    private static Boolean locationAccepted = null;

    /**
     * unused constructor
     */
    protected MapHandler() {
        // unused
    }

    /**
     * Initialises the map by loading the html into the webengine
     */
    public void initMap() {

        getUserLocation();

        webEngine = webView.getEngine();
        webEngine.setJavaScriptEnabled(true);
        webEngine.load(getClass().getClassLoader().getResource("html/map.html").toExternalForm());

        webEngine.getLoadWorker().stateProperty().addListener(
                (ov, oldState, newState) -> {
                    if (newState == Worker.State.SUCCEEDED) {
                        JSObject window = (JSObject) webEngine.executeScript("window");
                        window.setMember("javaScriptBridge", javaScriptBridge);
                        javaScriptConnector = (JSObject) webEngine.executeScript("jsConnector");

                        javaScriptConnector.call("initMap",
                                GeoLocationHandler.getInstance().getCoordinate().getLat(),
                                GeoLocationHandler.getInstance().getCoordinate().getLon(),
                                GeoLocationHandler.getInstance()
                                        .getCoordinate() != GeoLocationHandler.DEFAULT_COORDINATE);

                        addChargersOnMap();
                    }
                });
    }

    /**
     * Pop up to get the current users location for the map start point
     */
    private void getUserLocation() {
        if (locationAccepted == null) {
            Alert locationRequest = new Alert(Alert.AlertType.CONFIRMATION,
                    "Allow the program to access your location?",
                    ButtonType.YES, ButtonType.NO);

            // Set id of no button for textfx only
            Button noButton = (Button) locationRequest.getDialogPane().lookupButton(ButtonType.NO);
            noButton.setId("no");

            locationRequest.showAndWait();

            setLocationAccepted(locationRequest.getResult() == ButtonType.YES);
        }

        if (locationAccepted) {
            try {
                GeoLocationHandler.setCurrentLocation();
            } catch (IOException | GeoIp2Exception e) {
                new Alert(Alert.AlertType.ERROR,
                        "Your location was unable to be found.");
            }
        }

    }

    /**
     * Sets the location to the accepted permission
     *
     * @param permission boolean true if permission granted, else false
     */
    public static void setLocationAccepted(Boolean permission) {
        locationAccepted = permission;
    }

    /**
     * Adds all chargers on the map
     */
    public abstract void addChargersOnMap();

    /**
     * Resets the geolocation permissions
     */
    public static void resetPermission() {
        locationAccepted = null;
    }
}
