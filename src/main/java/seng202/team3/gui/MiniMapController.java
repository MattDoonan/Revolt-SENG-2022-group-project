package seng202.team3.gui;

import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import seng202.team3.data.entity.Coordinate;
import seng202.team3.logic.JavaScriptBridge;
import seng202.team3.logic.MapManager;
import seng202.team3.logic.TableManager;

/**
 * This is the controller for the little map to allow you to add coordinates
 *
 * @author Michelle Hsieh
 * @version 1.0.0, Sep 22
 */
public class MiniMapController {

    @FXML
    private WebView webView;

    private WebEngine webEngine;
    private TableManager manager;
    private JSObject javaScriptConnector;
    private JavaScriptBridge javaScriptBridge;
    private Stage stage;

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

        webEngine.getLoadWorker().stateProperty().addListener(
                (ov, oldState, newState) -> {
                    if (newState == Worker.State.SUCCEEDED) {
                        JSObject window = (JSObject) webEngine.executeScript("window");
                        window.setMember("javaScriptBridge", javaScriptBridge);
                        javaScriptConnector = (JSObject) webEngine.executeScript("jsConnector");
                        javaScriptConnector.call("initMap");

                    }
                });
    }


    /**
     * Updates the coordinate in the TableManager
     *
     */
    @FXML
    public void getCoordinateWithAddress() {
        manager.setPosition();
        javaScriptConnector.call("setCoordinate");
        stage.close();
    }



    /**
     * Sets the table manager to associate the manager with the
     *
     * @param tableManager the {@link TableManager} which will store the coordinate
     */
    public void setManager(TableManager tableManager) {
        manager = tableManager;
    }
}
