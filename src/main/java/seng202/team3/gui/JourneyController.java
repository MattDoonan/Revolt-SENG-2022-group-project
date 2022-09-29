package seng202.team3.gui;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import seng202.team3.logic.JourneyManager;

/**
 * Controller for journeySidebar.fxml
 * 
 * @author James Billows
 * @version 1.0.0, Aug 22
 */
public class JourneyController {

    @FXML
    private Button makeStart;

    @FXML
    private Label startLabel;

    @FXML
    private Button makeEnd;

    @FXML
    private Label endLabel;

    @FXML
    private BorderPane mainWindow;

    private Stage stage;
    private JourneyMapController mapController;
    private JourneyManager journeyManager;

    /**
     * Initialize the window
     *
     * @param stage Top level container for this window
     */
    public void init(Stage stage) {
        this.stage = stage;
        journeyManager = new JourneyManager();
        loadMapView(stage);
    }

    public JourneyManager getManager() {
        return this.journeyManager;
    }

    /**
     * Loads the map view into the main part of the main window
     *
     * @param stage stage to load with
     */
    private void loadMapView(Stage stage) {
        try {
            FXMLLoader webViewLoader = new FXMLLoader(getClass()
                    .getResource("/fxml/journeyMap.fxml"));
            Parent mapViewParent = webViewLoader.load();
            mapController = webViewLoader.getController();
            mapController.init(stage, journeyManager);
            mainWindow.setCenter(mapViewParent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Calls functions in {@Link JourneyMapController}
     * and {@Link JourneyManager} for when Set Start button is clicked
     */
    public void setStart() {
        mapController.addStartMarker();
        journeyManager.setStart(journeyManager.getPosition());
        makeStart.setDisable(true);
        startLabel.setText("Start position: " + journeyManager.getPosition().getAddress());
        mapController.addChargersOnMap();
    }

    /**
     * Calls functions in {@Link JourneyMapController}
     * and {@Link JourneyManager} for when Set Destination button is clicked
     */
    public void setDestination() {
        mapController.addEndMarker();
        journeyManager.setEnd(journeyManager.getPosition());
        makeEnd.setDisable(true);
        endLabel.setText("End position: " + journeyManager.getPosition().getAddress());
    }

    /**
     * calls function to add a route going through all markers
     */
    public void calculateRoute() {
        mapController.addRouteToScreen();
    }

    /**
     * Resets the journey entity and GUI
     */
    public void resetJourney() {
        //mapController.javaScriptConnector.call("removeRoute");
        //mapController.javaScriptConnector.call("removeCoordinate");
        journeyManager.clearJourney();
        init(this.stage);
        makeStart.setDisable(false);
        makeEnd.setDisable(false);
        startLabel.setText("Start not set");
        endLabel.setText("End not set");
    }
}