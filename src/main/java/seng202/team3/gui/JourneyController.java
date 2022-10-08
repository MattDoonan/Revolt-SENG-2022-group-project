package seng202.team3.gui;

import static java.lang.Math.round;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
     * Logger
     */
    private static final Logger logManager = LogManager.getLogger();


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
    private VBox journeyChargerTable;

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
     * Name for trip
     */
    @FXML
    private TextField tripName;

    /**
     * Slider for selecting vehicle range
     */
    @FXML
    private Slider rangeSlider;

    /**
     * List of user input errors for adding/editing vehicles
     */
    private ArrayList<String> errors = new ArrayList<>();

    /**
     * Boolean if there is an error with the routing distances
     */
    private boolean distanceError = false;
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
     * The border pane of the controller
     */
    private BorderPane borderPane;

    /**
     * Constructor for this class
     */
    public JourneyController() {
        // unused
    }

    /**
     * Gets this border pane
     *
     * @return the {@link BorderPane} of this border pane
     */
    public BorderPane getBorderPane() {
        return borderPane;
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
     * Gets the range slider for circle radius
     * @return Slider rangeSlider
     */
    public Slider getRangeSlider() {
        return this.rangeSlider;
    }
    
    /**
     * Initialize the window
     *
     * @param stage Top level container for this window
     */
    public void init(Stage stage) {
        this.stage = stage;
        journeyManager = new JourneyManager();
        loadMapView(stage);
        populateVehicles();
        configureSlider();
    }

    /**
     * Loads the map view into the main part of the main window
     * @param stage stage to load with
     */
    private void loadMapView(Stage stage) {
        try {
            FXMLLoader webViewLoader = new FXMLLoader(getClass()
                    .getResource("/fxml/journeyMap.fxml"));
            Parent mapViewParent = webViewLoader.load();
            mapController = webViewLoader.getController();
            mapController.init(stage, this);
            mainWindow.setCenter(mapViewParent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets an action listener for the range slider
     */
    private void configureSlider() {
        rangeSlider.valueChangingProperty().addListener((observableValue, wasChanging, changing)
                -> {
            if (!rangeSlider.isValueChanging()) {
                sliderUpdated();
            }
        });
    }

    /**
     * Calls functions in {@link JourneyMapController}
     * and {@link JourneyManager} for when Set Start button is clicked
     */
    @FXML
    public void setStart() {
        journeyManager.makeCoordinateName();
        Coordinate position = journeyManager.getPosition();
        if (position != null) {
            journeyManager.setStart(position);
            mapController.positionMarker("Start");
            makeStart.setDisable(true);
            startLabel.setText(position.getAddress());
            rangeSlider.setDisable(true);
            vehicles.setDisable(true);
            journeyManager.makeRangeChargers();
            mapController.addChargersOnMap();
        }
        calculateRoute();
    }

    /**
     * Calls functions in {@link JourneyMapController}
     * and {@link JourneyManager} for when Set Destination button is clicked
     */
    @FXML
    public void setDestination() {
        journeyManager.makeCoordinateName();
        Coordinate position = journeyManager.getPosition();
        if (position != null) {
            journeyManager.setEnd(position);
            makeEnd.setDisable(true);
            endLabel.setText(position.getAddress());
            mapController.positionMarker("Destination");
            if (tripName.getText().isEmpty()) {
                tripName.setText("Trip to " + position.getAddress().split(",")[0]);
            }
        }
        calculateRoute();
    }

    /**
     * calls function to add a route going through all markers
     */
    public void calculateRoute() {
        if (journeyManager.getSelectedJourney().getEndPosition() != null
                && journeyManager.getSelectedJourney().getStartPosition() != null) {
            distanceError = journeyManager.checkDistanceBetweenChargers();
            errorText.setVisible(false);
            if (distanceError) {
                errorText.setVisible(true);
            }
            mapController.addRouteToScreen();

        }
    }

    /**
     * Resets the journey entity and GUI
     */
    public void resetJourney() {
        mapController.removeRoute();
        journeyManager.clearChargers();
        journeyManager.clearJourney();
        journeyManager.setCurrentCoordinate(null);
        journeyManager.makeRangeChargers();
        mapController.addChargersOnMap();
        makeStart.setDisable(false);
        makeEnd.setDisable(false);
        startLabel.setText("Start not set");
        endLabel.setText("End not set");
        errorText.setVisible(false);
        distanceError = false;
        rangeSlider.setDisable(false);
        vehicles.setDisable(false);
        resetChargerDisplay();
    }

    /**
     * Adds a charger to the Journey table
     *
     * @param charger charger
     */
    public void addCharger(Charger charger) {

        //TODO decide on maximum number of stops in a journey and implement handle
        journeyManager.addCharger(charger);
        mapController.addChargersOnMap();
        resetChargerDisplay();
        addWaypointsToDisplay();
        calculateRoute();
    }

    /**
     * Shows all the added waypoints to the display
     */
    public void addWaypointsToDisplay() {


        List<Charger> chargers = journeyManager.getSelectedJourney().getChargers();

        for (int i = 0; i < chargers.size(); i++) {
            double dist = Math.floor(Calculations.calculateDistance(chargers.get(i).getLocation(),
                    journeyManager.getStart()) * 100) / 100;
            if (i != 0) {
                dist = Math.floor(Calculations.calculateDistance(chargers.get(i - 1).getLocation(),
                        chargers.get(i).getLocation()) * 100) / 100;
            }

            Label addressBox = new Label(chargers.get(i).getLocation().getAddress());
            addressBox.setWrapText(true);

            Label nameBox = new Label(chargers.get(i).getName());
            nameBox.setWrapText(true);

            VBox text = new VBox(nameBox,
                    addressBox,
                    new Text("\n" + dist + " km Distance"),
                    new Text("\n" + (int) Math.ceil(dist / journeyManager.getSelectedJourney()
                    .getVehicle().getMaxRange() * 100) + "% Battery Used"));

            journeyChargerTable.getChildren().add(text);
        }

        Button btn = new Button("Remove Last Point");
        btn.setOnAction(this::removeFromDisplay);
        journeyChargerTable.getChildren().add(btn);
    }

    /**
     * Resets the charger display nodes
     */
    private void resetChargerDisplay() {
        journeyChargerTable.getChildren().clear();
    }

    /**
     * Button method which removes the charger/location from table
     * and removes from the journey
     * @param e the event of button being clicked
     */
    public void removeFromDisplay(ActionEvent e) {
        journeyManager.removeLastCharger();
        resetChargerDisplay();
        addWaypointsToDisplay();
        if (journeyManager.getSelectedJourney().getChargers().isEmpty()) {
            journeyChargerTable.getChildren().clear();
        }
    } 

    /**
     * Saves journey to database
     */
    public void saveJourney() {
        if (!(distanceError) && (journeyManager.getStart() != null)
                && (journeyManager.getEnd() != null)) {
            journeyManager.saveJourney();
        } else {
            if (journeyManager.getStart() == null) {
                errors.add("No start location added");
            }
            if (journeyManager.getEnd() == null) {
                errors.add("No end location added");
            }
            if (distanceError) {
                errors.add("Some of your locations are out of range of each other");
            }
            displayErrorPopups();
        }
    }

    /**
     * Loads vehicles into the menuBox
     */
    public void populateVehicles() {
        garageManager = new GarageManager();
        garageManager.resetQuery();
        garageManager.getAllVehicles();
        vehicles.getItems().clear();
        
        MenuItem custom = new MenuItem("Add Vehicle...");
        custom.setOnAction(this::configureVehicleItem);
        vehicles.getItems().add(custom);

        for (Vehicle vehicle : garageManager.getData()) {
            String title = vehicle.getMake() + ' ' + vehicle.getModel();
            MenuItem item = new MenuItem(title);
            item.setId(Integer.toString(vehicle.getVehicleId()));
            item.setOnAction(this::configureVehicleItem);
            vehicles.getItems().add(item);
        }

    }

    /**
     * Configures the vehicles MenuItem
     * Adds text to vehicles MenuButton and selects vehicle
     * @param e the event of menu item being selected
     */
    public void configureVehicleItem(ActionEvent e) {
        MenuItem item = ((MenuItem) e.getSource());
        vehicles.setText(item.getText());
        if (item.getText().equals("Add Vehicle...")) {
            //Prompts you to add a vehicle
            //TODO handle
            //loadInternalVehicleScreen();
        } else {
            for (Vehicle vehicle : garageManager.getData()) {
                if (vehicle.getVehicleId() == Integer.parseInt(item.getId())) {
                    journeyManager.selectVehicle(vehicle);
                    rangeSlider.setMax(vehicle.getMaxRange());
                    rangeSlider.setMajorTickUnit(round(vehicle.getMaxRange() / 5.0));
                    sliderUpdated();
                }
            }
        }
    }


    /**
     * Displays popup error message
     */
    public void displayErrorPopups() {
        try {
            FXMLLoader error = new FXMLLoader(getClass().getResource(
                    "/fxml/error_popup.fxml"));
            AnchorPane base = error.load();
            Scene modalScene = new Scene(base);
            Stage errorPopup = new Stage();
            errorPopup.setScene(modalScene);
            errorPopup.setResizable(false);
            errorPopup.setTitle("Error With Journey:");
            errorPopup.initModality(Modality.APPLICATION_MODAL);
            ErrorController controller = error.getController();
            controller.init();
            controller.setErrors(errors);
            controller.setPromptType("error");
            controller.displayErrors();
            errorPopup.setAlwaysOnTop(true);
            errorPopup.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Handles updates of vehicle range slider
     */
    public void sliderUpdated() {
        journeyManager.setDesiredRange(rangeSlider.getValue());
        if (journeyManager.getSelectedJourney().getStartPosition() != null) {
            List<Charger> chargers = journeyManager.getSelectedJourney().getChargers();
            if (chargers.size() == 0) {
                mapController.addChargersOnMap();
            } else {
                journeyManager.setCurrentCoordinate(chargers.get(chargers.size() - 1)
                        .getLocation());
                journeyManager.makeRangeChargers();
                mapController.addChargersOnMap();
            }
        }
    }
}