package seng202.team3.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.Coordinate;
import seng202.team3.data.entity.Vehicle;
import seng202.team3.logic.Calculations;
import seng202.team3.logic.GarageManager;
import seng202.team3.logic.JourneyManager;

/**
 * Controller for journeySidebar.fxml
 * 
 * @author James Billows
 * @version 1.0.0, Aug 22
 */
public class JourneyController {

    /**
     * Button to set start point
     */
    @FXML
    private Button makeStart;

    /**
     * Label of start address
     */
    @FXML
    private Label startLabel;

    /**
     * Button to set end point
     */
    @FXML
    private Button makeEnd;

    /**
     * Label of end address
     */
    @FXML
    private Label endLabel;

    /**
     * Map border pane
     */
    @FXML
    private BorderPane mainWindow;

    /**
     * Used to contain stops along route
     */
    @FXML 
    private VBox journeyTable;

    /**
     * MenuButton containing user vehicles
     */
    @FXML
    private MenuButton vehicles;

    /**
     * Error text if route is invalid
     */
    @FXML
    private Text errorText;

    /**
     * Top level container for journey window
     */
    private Stage stage;

    /**
     * Garage manager for selecting vehicle
     */
    private GarageManager garageManager;

    /**
     * GUI controller for map
     */
    private JourneyMapController mapController;

    /**
     * Logic manager for journeys
     */
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
        loadVehicles();
    }

    /**
     * Gets the logic manager for journeys
     * @return journeyManager
     */
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
     * Calls functions in {@link JourneyMapController}
     * and {@link JourneyManager} for when Set Start button is clicked
     */
    public void setStart() {
        mapController.addStartMarker();
        journeyManager.setStart(journeyManager.getPosition());
        makeStart.setDisable(true);
        startLabel.setText("Start position: " + journeyManager.getPosition().getAddress());
        mapController.addChargersAroundPoint(journeyManager.getPosition());
        if (journeyManager.getEnd() != null) {
            calculateRoute();
        }
    }

    /**
     * Calls functions in {@link JourneyMapController}
     * and {@link JourneyManager} for when Set Destination button is clicked
     */
    public void setDestination() {
        mapController.addEndMarker();
        journeyManager.setEnd(journeyManager.getPosition());
        makeEnd.setDisable(true);
        endLabel.setText("End position: " + journeyManager.getPosition().getAddress());
        if (journeyManager.getStart() != null) {
            calculateRoute();
        }
    }

    /**
     * calls function to add a route going through all markers
     */
    public void calculateRoute() {
        mapController.addRouteToScreen();
        errorTextCheck();
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
        journeyTable.getChildren().clear();
        journeyManager.getSelectedJourney().getChargers().clear();
        startLabel.setText("Start not set");
        endLabel.setText("End not set");
        errorText.setVisible(false);
    }

    /**
     * Adds a charger to the Journey table
     * @param charger charger to add to table
     */
    public void addChargerToDisplay(Charger charger) {
        //TODO fix the calculation or completley remove

        Button btn = new Button("Remove");
        btn.setId(Integer.toString(charger.getChargerId()));
        btn.setOnAction(this::removeFromDisplay);

        VBox text = new VBox(new Text(charger.getName()),
                new Text("\n" + Math.floor(Calculations.calculateDistance(charger.getLocation(),
                        journeyManager.getPosition()) * 100) / 100 + " km Distance"));

        VBox buttonBox = new VBox(btn);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        HBox content = new HBox(text, buttonBox);
        content.setPadding(new Insets(15));
        content.setId(Integer.toString(charger.getChargerId()));
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
     * @param e the event of button being clicked
     */
    public void removeFromDisplay(ActionEvent e) {
        List<Charger> chargers = journeyManager.getSelectedJourney().getChargers();
        String id = ((Node) e.getSource()).getId();
        int iid = Integer.parseInt(id);
        chargers.removeIf(charger -> charger.getChargerId() == iid);
        journeyTable.getChildren().removeIf(box -> Objects.equals(box.getId(), id));
        calculateRoute();
        if (chargers.size() == 0) {
            mapController.addChargersAroundPoint(journeyManager.getStart());
        } else {
            mapController.addChargersAroundPoint(chargers.get(chargers.size() - 1).getLocation());
        }
    }

    /**
     * Saves journey
     */
    public void saveJourney() {
        if (!(errorText.isVisible())) {
            journeyManager.saveJourney();
        } else {
            //TODO
        }
    }

    /**
     * Loads vehicles into the menuBox
     */
    public void loadVehicles() {
        garageManager = new GarageManager();
        garageManager.resetQuery();
        garageManager.getAllVehicles();
        for (Vehicle vehicle : garageManager.getData()) {
            String title = vehicle.getMake() + ' ' + vehicle.getModel();
            vehicles.getItems().add(new MenuItem(title));
        }
        journeyManager.selectVehicle(garageManager.getData().get(0));
    }

    /**
     * Uses {@link JourneyManager JourneyManager} function to toggle
     * error test based on calculations
     */
    public void errorTextCheck() {
        boolean error = journeyManager.checkDistanceBetweenChargers();
        errorText.setVisible(false);
        if (error) {
            errorText.setVisible(true);
        }
    }
}