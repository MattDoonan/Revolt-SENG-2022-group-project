package seng202.team3.gui;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.Coordinate;
import seng202.team3.logic.Calculations;
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

    @FXML 
    private VBox journeyTable;

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
     * Gets the map controller associated with this controller
     * @return MapController mapController
     */
    public JourneyMapController getMapController() {
        return this.mapController;
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
        mapController.addChargersAroundPoint(journeyManager.getPosition());
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

    /**
     * Adds a charger to the Journey table
     * @param charger charger to add to table
     */
    public void addChargerToDisplay(Charger charger) {
        VBox text = new VBox(new Text(charger.getName()),
                new Text("\n" + Math.floor(Calculations.calculateDistance(charger.getLocation(), 
                journeyManager.getPosition()) * 100) / 100 + " km Distance"));
        //TODO fix the calculation or completley remove
        Button btn = new Button("Remove");
        // btn.setOnAction(new EventHandler() {
 
        //     @Override
        //     public void handle(ActionEvent event) {
        //         removeFromDisplay();
        //     }
        // });
        VBox buttonBox = new VBox(btn);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        HBox content = new HBox(text, buttonBox);
        content.setPadding(new Insets(15));
        journeyTable.getChildren().add(content);
    }

    /**
     * Adds a location to the Journey table
     * @param coordinate coordinate to add to table
     */
    public void addLocationToDisplay(Coordinate coordinate) {

    }

    /**
     * Button method which removes the charger/location from table
     * and removes from the journey
     */
    public void removeFromDisplay() {

    }

}