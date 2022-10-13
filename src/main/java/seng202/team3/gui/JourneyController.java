package seng202.team3.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team3.data.database.ComparisonType;
import seng202.team3.data.database.QueryBuilderImpl;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.Coordinate;
import seng202.team3.data.entity.EntityType;
import seng202.team3.data.entity.Journey;
import seng202.team3.data.entity.Stop;
import seng202.team3.data.entity.Vehicle;
import seng202.team3.logic.Calculations;
import seng202.team3.logic.GeoLocationHandler;
import seng202.team3.logic.JavaScriptBridge;
import seng202.team3.logic.JourneyManager;
import seng202.team3.logic.JourneyUpdateManager;
import seng202.team3.logic.UserManager;

/**
 * Controller for the journey; contains the journey manager
 * and the journey map controller
 * 
 * @author James Billows, Michelle Hsieh, Angus Kirtlan
 * @version 1.0.3, Sep 22
 */
public class JourneyController {

    /**
     * Logger
     */
    private static final Logger logManager = LogManager.getLogger();

    /**
     * Label of the maximum range
     */
    @FXML
    private Label maxRange;

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
     * The border pane of the controller
     */
    @FXML
    private BorderPane borderPane;

    /**
     * Table of previous journeys
     */
    @FXML
    private TableView<Journey> previousJourneyTable;

    /**
     * Table of previous journeys
     */
    @FXML
    private TableColumn<Journey, String> journeyNameCol;

    /**
     * Table of previous journeys
     */
    @FXML
    private TableColumn<Journey, String> journeyVehicleCol;

    /**
     * Table of previous journeys
     */
    @FXML
    private TableColumn<Journey, String> startCoordinateCol;

    /**
     * Table of previous journeys
     */
    @FXML
    private TableColumn<Journey, String> endCoordinateCol;

    /**
     * Table of previous journeys
     */
    @FXML
    private TableColumn<Journey, String> journeyDateCol;

    /**
     * List of user input errors for adding/editing vehicles
     */
    private ArrayList<String> errors = new ArrayList<>();

    /**
     * List of vehicles available for the journey
     */
    private ObservableList<Vehicle> vehicleList = FXCollections.observableArrayList();

    /**
     * Boolean if there is an error with the routing distances
     */
    private boolean distanceError = false;

    /**
     * GUI controller for map
     */
    private JourneyMapController mapController;

    /**
     * Logic manager for journeys
     */
    private JourneyManager journeyManager;

    /**
     * Logic manager for journey loader
     */
    private JourneyUpdateManager journeyUpdateManager;

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
     * Sets the borderPane
     *
     * @param borderPane sets the borderpane of this application
     */
    public void setBorderPane(BorderPane borderPane) {
        this.borderPane = borderPane;
    }

    /**
     * Add vehicle button text
     */
    private static final String ADD_VEHICLE = "Add Vehicle...";

    /**
     * Gets the logic manager for journeys
     * 
     * @return journeyManager
     */
    public JourneyManager getManager() {
        return this.journeyManager;
    }

    /**
     * Initialize the window
     *
     * @param stage Top level container for this window
     */
    public void init(Stage stage) {
        journeyManager = new JourneyManager();
        journeyUpdateManager = new JourneyUpdateManager();
        populateTable();
        loadMapView(stage);
        populateVehicles();
        configureSlider();
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
        rangeSlider.valueChangingProperty().addListener(
                (observableValue, wasChanging, changing) -> {
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
        journeyManager.setCurrentCoordinate(GeoLocationHandler.getCoordinate());
        journeyManager.makeCoordinateName();
        Coordinate position = journeyManager.getPosition();
        if (position != null && journeyManager.getSelectedJourney().getVehicle() != null) {
            journeyManager.setDesiredRange(rangeSlider.getValue()
                    * journeyManager.getSelectedJourney().getVehicle().getMaxRange() / 100.0);
            journeyManager.setStart(position);
            mapController.positionMarker("Start");
            makeStart.setDisable(true);
            startLabel.setText(position.getAddress());
            vehicles.setDisable(true);
            journeyManager.makeRangeChargers();
            mapController.addChargersOnMap();
            calculateRoute();
        } else if (journeyManager.getSelectedJourney().getVehicle() == null) {
            errors.add("Please select a vehicle.");
            displayErrorPopups();
        }
    }

    /**
     * Calls functions in {@link JourneyMapController}
     * and {@link JourneyManager} for when Set Destination button is clicked
     */
    @FXML
    public void setDestination() {
        journeyManager.setCurrentCoordinate(GeoLocationHandler.getCoordinate());
        journeyManager.makeCoordinateName();
        Coordinate position = journeyManager.getPosition();
        if (position != null && journeyManager.getSelectedJourney().getVehicle() != null) {
            journeyManager.setEnd(position);
            mapController.positionMarker("Destination");
            makeEnd.setDisable(true);
            vehicles.setDisable(true);
            endLabel.setText(position.getAddress());
            if (tripName.getText().isEmpty()) {
                tripName.setText("Trip to " + position.getAddress().split(",")[0]);
            }
            calculateRoute();
        } else if (journeyManager.getSelectedJourney().getVehicle() == null) {
            errors.add("Please select a vehicle.");
        }
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
        populateVehicles();
        rangeSlider.setValue(100.0);
        resetChargerDisplay();
    }

    /**
     * Adds a charger to the Journey table
     *
     * @param charger charger
     */
    public void addCharger(Charger charger) {
        Stop stop = new Stop(charger);
        if (!journeyManager.getSelectedJourney().getStops().isEmpty()) {
            if (journeyManager.getSelectedJourney().getStops()
                    .get(journeyManager.getSelectedJourney().getStops().size() - 1)
                    .getCharger() != null) {
                if (journeyManager.getSelectedJourney().getStops()
                        .get(journeyManager.getSelectedJourney().getStops().size() - 1)
                        .getCharger().getId() == charger.getId()) {
                    errors.add("Cannot add the same charger consecutively.");
                }
            }
        }
        if (errors.isEmpty()) {
            journeyManager.addStop(stop);
            journeyManager.setCurrentCoordinate(charger.getLocation());
            resetChargerDisplay();
            addWaypointsToDisplay();
            journeyManager.makeRangeChargers();
            mapController.addChargersOnMap();
            calculateRoute();
        } else {
            displayErrorPopups();
            errors.clear();
        }
    }

    /**
     * Adds a stop which is not a charger; checks to see if in range of last
     * coordinate or not first
     *
     * @param coordinate the stop to be added
     */
    public void addStop(Coordinate coordinate) {
        if (journeyManager.getStart() == null) {
            errors.add("Please select a start point");
        } else if (coordinate.equals(journeyManager.getCurrentCoordinate())) {
            errors.add("Cannot add the same stop consecutively.");
        } else if (Calculations.calculateDistance(coordinate,
                journeyManager.getCurrentCoordinate()) <= journeyManager
                        .getDesiredRange()) {
            journeyManager.addNoChargerStop(coordinate);
            journeyManager.setCurrentCoordinate(coordinate);
            logManager.info("Coordinate: " + coordinate.getAddress());
            mapController.positionMarker("Stop");
            resetChargerDisplay();
            addWaypointsToDisplay();
            journeyManager.makeRangeChargers();
            mapController.addChargersOnMap();
            calculateRoute();
        } else {
            errors.add("Selected stop is out of range");
            logManager.info("Selected Stop is out of range for vehicle. ");
        }

        if (!errors.isEmpty()) {
            displayErrorPopups();
            errors.clear();
        }
    }

    /**
     * Shows all the added waypoints to the display
     */
    public void addWaypointsToDisplay() {

        double remainingCharge = 100.0;
        Stop lastStop = null;

        rangeSlider.setDisable(false);

        List<Stop> stops = journeyManager.getSelectedJourney().getStops();

        for (int i = 0; i < stops.size(); i++) {
            double dist = Math.floor(Calculations.calculateDistance(stops.get(i).getLocation(),
                    journeyManager.getStart()) * 100) / 100;
            if (i != 0) {
                dist = Math.floor(Calculations.calculateDistance(stops.get(i - 1).getLocation(),
                        stops.get(i).getLocation()) * 100) / 100;
            }

            remainingCharge = Math.ceil(dist / journeyManager.getSelectedJourney()
                    .getVehicle().getMaxRange() * 100);

            Label nameBox = new Label();
            Label addressBox = new Label();

            if (stops.get(i).getCharger() != null) {
                nameBox.setText("\n" + stops.get(i).getCharger().getName());
            } else {
                nameBox.setText("\nStop:");
            }
            addressBox.setText("\n" + stops.get(i).getLocation().getAddress());
            nameBox.setWrapText(true);
            addressBox.setWrapText(true);

            VBox text = new VBox(nameBox,
                    addressBox,
                    new Text("\n" + dist + " km Distance"),
                    new Text("\n" + (int) remainingCharge + "% Battery Used\n"));

            journeyChargerTable.getChildren().add(text);

            lastStop = stops.get(i);
        }

        Button btn = new Button("Remove Last Point");
        btn.setOnAction(this::removeFromDisplay);

        if (lastStop != null) {
            journeyChargerTable.getChildren().add(btn);
        }

        adjustRanges(lastStop, remainingCharge);
    }

    /**
     * Adjusts the range sliders sets the desired ranges appropriately
     *
     * @param lastStop        The last stop on the list of chargers
     * @param remainingCharge the remaining charger percentage
     */
    private void adjustRanges(Stop lastStop, double remainingCharge) {
        if (lastStop != null) {
            double previousRange = journeyManager.getDesiredRange();

            journeyManager.setCurrentCoordinate(lastStop.getLocation());
            journeyManager.setDesiredRange(previousRange - remainingCharge
                    * journeyManager.getSelectedJourney().getVehicle().getMaxRange() / 100.0);
            rangeSlider.setValue(journeyManager.getDesiredRange()
                    / journeyManager.getSelectedJourney().getVehicle().getMaxRange() * 100.0);

            if (lastStop.getCharger() == null) {
                rangeSlider.setDisable(true);
            }

        } else if (journeyManager.getStart() != null) {
            journeyManager.setCurrentCoordinate(journeyManager.getStart());
            journeyManager.setDesiredRange((double) journeyManager
                    .getSelectedJourney().getVehicle().getMaxRange());
            rangeSlider.setValue(100);
        }
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
     * 
     * @param e the event of button being clicked
     */
    public void removeFromDisplay(ActionEvent e) {

        double desiredRange = journeyManager.getDesiredRange();
        journeyManager.removeLastStop();
        resetChargerDisplay();
        addWaypointsToDisplay();
        if (journeyManager.getSelectedJourney().getStops().isEmpty()) {
            journeyChargerTable.getChildren().clear();
            mapController.removeRoute();
        } else {
            journeyManager.setDesiredRange(desiredRange);
        }
        journeyManager.makeRangeChargers();
        mapController.addChargersOnMap();
        journeyManager.checkDistanceBetweenChargers();
        mapController.addRouteToScreen();
    }

    /**
     * Saves journey to database
     */
    @FXML
    public void saveJourney() {
        journeyManager.checkDistanceBetweenChargers();
        if (!(distanceError) && (journeyManager.getStart() != null)
                && (journeyManager.getEnd() != null)) {
            journeyManager.getSelectedJourney().setTitle(tripName.getText());
            journeyManager.saveJourney();
            populateTable();
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
            errors.clear();
        }
    }

    /**
     * Populates the table
     */
    public void populateTable() {
        journeyUpdateManager.resetQuery();
        addToDisplay(journeyUpdateManager.getData());
        // TODO: setIdForTesting();
    }

    /**
     * adds stops to the display
     *
     * @param journeysToAdd Observable list of journey objects
     */
    private void addToDisplay(ObservableList<Journey> journeysToAdd) {

        final Coordinate currentPosition = GeoLocationHandler.getCoordinate();

        previousJourneyTable.getItems().clear();
        previousJourneyTable.setItems(journeysToAdd);
        journeyNameCol.setCellValueFactory(journey -> new ReadOnlyStringWrapper(
                journey.getValue().getTitle()));
        journeyVehicleCol.setCellValueFactory(journey -> new ReadOnlyStringWrapper(
                journey.getValue().getVehicle().getMake() + " "
                        + journey.getValue().getVehicle().getModel()));
        startCoordinateCol.setCellValueFactory(journey -> new ReadOnlyStringWrapper(
                journey.getValue().getStartPosition().getAddress()));
        endCoordinateCol.setCellValueFactory(journey -> new ReadOnlyStringWrapper(
                journey.getValue().getEndPosition().getAddress()));
        journeyDateCol.setCellValueFactory(journey -> new ReadOnlyStringWrapper(
                journey.getValue().getStartDate()));
        previousJourneyTable.getSortOrder().add(journeyDateCol);
        previousJourneyTable.sort();

        GeoLocationHandler.setCoordinate(currentPosition, currentPosition.getAddress());
    }

    /**
     * Deletes the journey selected from the table
     */
    public void deleteJourney() {
        if (previousJourneyTable.getSelectionModel()
                .getSelectedItem() != null) {
            journeyUpdateManager.deleteJourney(previousJourneyTable
                    .getSelectionModel().getSelectedItem());
            populateTable();
            // TODO feedback after clicking button
        }
    }

    /**
     * Loads the journey selected from the table into the map and sidebar
     */
    public void loadJourney() {
        // TODO warning that current journey will be lost
        if (previousJourneyTable.getSelectionModel()
                .getSelectedItem() != null) {
            mapController.removeRoute();
            makeStart.setDisable(true);
            makeEnd.setDisable(true);
            journeyManager.setSelectedJourney(previousJourneyTable
                    .getSelectionModel().getSelectedItem());
            journeyManager.setCurrentCoordinate(journeyManager
                    .getSelectedJourney().getStops()
                    .get(journeyManager.getSelectedJourney()
                            .getStops().size() - 1)
                    .getLocation());

            Coordinate currentPosition = GeoLocationHandler.getCoordinate();

            // reverse geolocates if there is a map
            if (MapHandler.isMapRequested()) {

                GeoLocationHandler.setCoordinate(journeyManager.getSelectedJourney()
                        .getStartPosition(), "Start Position");
                startLabel.setText(GeoLocationHandler.getCoordinate().getAddress());
                GeoLocationHandler.setCoordinate(journeyManager.getSelectedJourney()
                        .getStartPosition(), "End Position");
                endLabel.setText(GeoLocationHandler.getCoordinate().getAddress());

                for (Stop stop : journeyManager.getStops()) {
                    if (stop.getCharger() == null) {
                        GeoLocationHandler.setCoordinate(stop.getLocation(), "Stop");
                        stop.setLocation(GeoLocationHandler.getCoordinate());
                    }
                }

                GeoLocationHandler.setCoordinate(currentPosition, currentPosition.getAddress());
            }

            resetChargerDisplay();
            addWaypointsToDisplay();
            populateTable();
            mapController.addRouteToScreen();

            startLabel.setText(journeyManager.getSelectedJourney()
                    .getStartPosition().getAddress());
            endLabel.setText(journeyManager.getSelectedJourney()
                    .getEndPosition().getAddress());
            tripName.setText(journeyManager.getSelectedJourney().getTitle());
            vehicles.setText(journeyManager.getSelectedJourney()
                    .getVehicle().getMake()
                    + " " + journeyManager.getSelectedJourney()
                            .getVehicle().getModel());
            maxRange.setText(Integer.toString(journeyManager
                    .getSelectedJourney().getVehicle().getMaxRange()));
            // TODO feedback after clicking button
        }
    }

    /**
     * Loads vehicles into the menuBox
     */
    public void populateVehicles() {

        vehicles.getItems().clear();

        try {
            List<Vehicle> vehicleData = new ArrayList<>();
            for (Object o : SqlInterpreter.getInstance()
                    .readData(new QueryBuilderImpl().withSource(EntityType.VEHICLE)
                            .withFilter("owner",
                                    Integer.toString(UserManager.getUser().getId()),
                                    ComparisonType.EQUAL)
                            .build())) {
                vehicleData.add((Vehicle) o);
            }
            vehicleList = FXCollections.observableList(vehicleData);
        } catch (IOException e) {
            logManager.error(e.getMessage());
        }

        Vehicle favVehicle = vehicleList.stream().filter(
                element -> Boolean.TRUE.equals(element.getCurrVehicle()))
                .findFirst().orElse(null);

        if (favVehicle != null) {
            vehicleList.remove(favVehicle);
            vehicleList.add(0, favVehicle);
        } else {
            vehicles.setText(ADD_VEHICLE);
        }

        if (!vehicleList.isEmpty() && vehicleList.get(0) != null) {
            vehicles.setText(vehicleList.get(0).getMake() + ' ' + vehicleList.get(0).getModel());
            rangeSlider.setDisable(false);
            journeyManager.selectVehicle(vehicleList.get(0));
            maxRange.setText(vehicleList.get(0).getMaxRange() + " km");
            for (Vehicle vehicle : vehicleList) {
                String title = vehicle.getMake() + ' ' + vehicle.getModel();
                MenuItem item = new MenuItem(title);
                item.setId(Integer.toString(vehicle.getId()));
                item.setOnAction(this::configureVehicleItem);
                vehicles.getItems().add(item);
            }
        }
        MenuItem custom = new MenuItem(ADD_VEHICLE);
        custom.setId("add");
        custom.setOnAction(this::configureVehicleItem);
        vehicles.getItems().add(custom);
    }

    /**
     * Configures the vehicles MenuItem
     * Adds text to vehicles MenuButton and selects vehicle
     * 
     * @param e the event of menu item being selected
     */
    public void configureVehicleItem(ActionEvent e) {
        MenuItem item = ((MenuItem) e.getSource());
        vehicles.setText(item.getText());
        if (item.getText().equals(ADD_VEHICLE)) {
            loadVehicleScreen();
        } else {
            for (Vehicle vehicle : vehicleList) {
                rangeSlider.setDisable(false);
                if (vehicle.getId() == Integer.parseInt(item.getId())) {
                    journeyManager.selectVehicle(vehicle);
                    maxRange.setText(vehicle.getMaxRange() + " km");
                }
            }
        }
    }

    /**
     * Switch to the vehicle screen
     */
    public void loadVehicleScreen() {
        try {
            FXMLLoader vehicleEdit = new FXMLLoader(getClass().getResource(
                    "/fxml/vehicle_update.fxml"));
            AnchorPane root = vehicleEdit.load();
            Scene modalScene = new Scene(root);
            Stage editPopup = new Stage();
            editPopup.setScene(modalScene);
            editPopup.setResizable(false);
            editPopup.setTitle("Vehicle Information");
            editPopup.initModality(Modality.APPLICATION_MODAL);
            editPopup.showAndWait();
        } catch (IOException e) {
            logManager.error(e.getMessage());
        } finally {
            populateVehicles();
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
        if (journeyManager.getSelectedJourney().getEndPosition() == journeyManager
                .getCurrentCoordinate() && journeyManager
                        .getSelectedJourney().getStops().isEmpty()) {
            journeyManager.setCurrentCoordinate(journeyManager.getStart());
        }
        journeyManager.setDesiredRange(rangeSlider.getValue()
                * journeyManager.getSelectedJourney().getVehicle().getMaxRange() / 100.0);
        if (journeyManager.getSelectedJourney().getStartPosition() != null) {
            List<Stop> stops = journeyManager.getSelectedJourney().getStops();
            if (stops.isEmpty()) {
                mapController.addChargersOnMap();
            } else {
                journeyManager.setCurrentCoordinate(stops.get((stops.size() - 1)).getLocation());
                journeyManager.makeRangeChargers();
                mapController.addChargersOnMap();
            }
            if (journeyManager.getSelectedJourney().getEndPosition() != null) {
                mapController.addRouteToScreen();
            }
        }
    }
}