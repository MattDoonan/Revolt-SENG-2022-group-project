package seng202.team3.gui.controller.map;

import static java.lang.Thread.sleep;

import com.maxmind.geoip2.exception.GeoIp2Exception;
import java.io.IOException;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSException;
import netscape.javascript.JSObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team3.logic.util.GeoLocationHandler;
import seng202.team3.logic.util.JavaScriptBridge;

/**
 * An abstract class that has basic JS initialisation for the MapView and
 * JourneyController
 *
 * @author Michelle Hsieh
 * @version 1.0.0, Sep 22
 */
public abstract class MapHandler {
    /**
     * Logger
     */
    private static final Logger logManager = LogManager.getLogger();

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
     * Path for the map
     */
    protected String path;

    /**
     * Represents response to location services request
     */
    private static Boolean locationAccepted = null;

    /**
     * Threshold for map connection reattempts
     */
    public static final int THRESHOLD = 10;

    /**
     * Time until next reattempt (ms)
     */
    public static final long REATTEMPT_TIME = (long) 100.0;

    /**
     * Represents users decision to load the map
     */
    private static boolean mapRequest = true;

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

        if (Boolean.FALSE.equals(isMapRequested())) {
            logManager.info("Map loading has been skipped");
            addChargersOnMap();
            return;
        }

        loadMap();

    }

    /**
     * Loads the map and handles the javascript
     */
    private void loadMap() {
        webEngine = webView.getEngine();
        webEngine.setJavaScriptEnabled(true);

        webEngine.load(getClass().getClassLoader().getResource(path).toExternalForm());

        logManager.info("Loading map...");

        webEngine.getLoadWorker().stateProperty().addListener(

                (ov, oldState, newState) -> {
                    if (newState == Worker.State.SUCCEEDED) {
                        JSObject window = (JSObject) webEngine.executeScript("window");
                        window.setMember("javaScriptBridge", javaScriptBridge);

                        while (mapRequest && javaScriptConnector == null) {
                            connectMap();
                        }

                    }
                });
    }

    /**
     * Prompts User to reload the map
     */
    public void promptForReattempt() {
        Alert reattemptPrompt = new Alert(AlertType.WARNING,
                "Unstable Internet Connection: Unable to load map view"
                        + "\n Would you like to try again?",
                ButtonType.NO, ButtonType.YES);
        reattemptPrompt.showAndWait();

        setMapRequested(reattemptPrompt.getResult() == ButtonType.YES);

        if (Boolean.TRUE.equals(isMapRequested())) {
            logManager.info("Re-attempting to load map...");
        } else {
            logManager.info("User has disabled map view");
            logManager.info("Loading screen without map");
        }
    }

    /**
     * Attempts to connect to the map
     * Alerts user after a timeout period
     */
    private void connectMap() {
        int count = 0;
        while (count < THRESHOLD) {
            try {
                javaScriptConnector = (JSObject) webEngine.executeScript("jsConnector");

                javaScriptConnector.call("initMap",
                        GeoLocationHandler.getCoordinate().getLat(),
                        GeoLocationHandler.getCoordinate().getLon(),
                        MapHandler.getLocationAccepted() != null
                                && MapHandler.getLocationAccepted());
                // throw new JSException("NO INTERNET"); // For 'no internet connection'
            } catch (JSException e) {
                if (javaScriptConnector != null) {
                    break;
                }
                count += 1;
                logManager.warn(e.getMessage());

                try {
                    sleep(REATTEMPT_TIME);
                } catch (InterruptedException e1) {
                    Thread.currentThread().interrupt();
                    logManager.warn(e.getMessage());
                }
            }
        }

        if (javaScriptConnector == null) {
            logManager.error("Unstable Connection: unable to connect to JavaScript");
            logManager.error("Process Aborted");
            promptForReattempt();
        } else {
            logManager.info("Map loaded successfully");
            addChargersOnMap();
        }
    }

    /**
     * Pop up to get the current users location for the map start point
     */
    public void getUserLocation() {
        if (Boolean.FALSE.equals(MapHandler.isMapRequested())) {
            logManager.info("Current Location was aborted due to no map");
            return;
        }
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
                logManager.info("Current user location has been set");
            } catch (IOException | GeoIp2Exception e) {
                new Alert(Alert.AlertType.ERROR,
                        "Your location was unable to be found.");
                logManager.error(e.getMessage());
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
     * Gets the location permission setting
     *
     * @return true if permission granted, else false
     */
    public static Boolean getLocationAccepted() {
        return locationAccepted;
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
        logManager.info("User location permission setting cleared");
    }

    /**
     * Sets map request state
     *
     * @param mapRequested true to load map
     */
    public static void setMapRequested(boolean mapRequested) {
        MapHandler.mapRequest = mapRequested;
    }

    /**
     * Gets bool represented if map features should be loaded
     *
     * @return if map features should be loaded
     */
    public static Boolean isMapRequested() {
        return MapHandler.mapRequest;
    }
}
