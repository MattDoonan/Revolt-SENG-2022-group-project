package seng202.team3.gui;

import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.Coordinate;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.stream.Collectors;


/**
 * A start into a MapViewController which uses the University UC OSM viewer
 *
 * @author Michelle Hsieh
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
    private Coordinate coordinate;
    private ArrayList<Charger> chargers;
    private JSObject javaScriptConnector;
    private boolean routeDisplayed = false;

    /**
     * Initialise the map view
     * @param stage current stage
     */
    void init(Stage stage) {
        this.stage = stage;
        coordinate = new Coordinate();
        initMap();
        stage.sizeToScene();
    }

    /**
     * Function to load map html file into string and insert the api key (as in .env) where needed by matching the
     * default placeholder 'YOUR_KEY_HERE'
     *
     * @return String of html file, with api key inserted
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
                    // if javascript loads successfully
                    if (newState == Worker.State.SUCCEEDED) {
                        // get a reference to the js object that has a reference to the js methods we need to use in java
                        javaScriptConnector = (JSObject) webEngine.executeScript("jsConnector");

                        javaScriptConnector.call("initMap");

                        addCoordinateFromMap();
                    }
                });
    }

    /**
     * Adds coordinates on map if lat and lon not 0
     */
    private void addCoordinateFromMap() {
        return;
    }
}
