package seng202.team3.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
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
     * Constructor for this class
     */
    public JourneyController() {
        // unused
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
            mapController.init(stage, journeyManager, this);
            mainWindow.setCenter(mapViewParent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets an action listener for the range slider
     */
    private void configureSlider() {
        rangeSlider.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(
                ObservableValue<? extends Boolean> observableValue,
                Boolean wasChanging,
                Boolean changing) {
                if (!rangeSlider.isValueChanging()) {
                    sliderUpdated();
                }
            }
        });
    }

    /**
     * Calls functions in {@link JourneyMapController}
     * and {@link JourneyManager} for when Set Start button is clicked
     */
    public void setStart() {
        Coordinate position = journeyManager.getPosition();
        if (position != null) {
            mapController.addStartMarker();
            journeyManager.getSelectedJourney().setStartPosition(position);
            makeStart.setDisable(true);
            String address = position.getAddress();
            if (address.contains(",")) {
                startLabel.setText(address.substring(0, address.indexOf(",")));
            } else {
                startLabel.setText(address);
            }     
            mapController.addChargersAroundPoint(journeyManager.getPosition());
            if (journeyManager.getSelectedJourney().getEndPosition() != null) {
                calculateRoute();
            }
        }
    }

    /**
     * Calls functions in {@link JourneyMapController}
     * and {@link JourneyManager} for when Set Destination button is clicked
     */
    public void setDestination() {
        Coordinate position = journeyManager.getPosition();
        if (position != null) {
            String address = position.getAddress();
            journeyManager.getSelectedJourney().setEndPosition(journeyManager.getPosition());
            makeEnd.setDisable(true);
            if (address.contains(",")) {
                endLabel.setText(address.substring(0, address.indexOf(",")));
                if (tripName.getText() == "") {
                    tripName.setText("Trip to " + address.substring(0, address.indexOf(",")));
                }
            } else {
                endLabel.setText(address);
                tripName.setText("Trip to " + address);
            }        
            if (journeyManager.getSelectedJourney().getStartPosition() != null) {
                if (tripName.getText() == "") {
                    calculateRoute();
                }
            }
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
        for (int i = 1; i < journeyTable.getChildren().size() - 1; i++) {
            journeyTable.getChildren().remove(i);
        }
        journeyManager.getSelectedJourney().getChargers().clear();
        startLabel.setText("Start not set");
        endLabel.setText("End not set");
        errorText.setVisible(false);
        distanceError = false;
    }

    /**
     * Adds a charger to the Journey table
     * @param charger charger to add to table
     */
    public void addChargerToDisplay(Charger charger) {
        Button btn = new Button("Remove");
        btn.setId(Integer.toString(charger.getChargerId()));
        btn.setOnAction(this::removeFromDisplay);
        double dist;
        int i = 0;
        Integer index = null;
        for (Charger c : journeyManager.getSelectedJourney().getChargers()) {
            if (c.getChargerId() == charger.getChargerId()) {
                index = i;
            }
            i++;
        }
        if (index == 0) {
            dist = Math.floor(Calculations.calculateDistance(charger.getLocation(),
                    journeyManager.getSelectedJourney().getStartPosition()) * 100) / 100;
        } else {
            dist = Math.floor(Calculations.calculateDistance(charger.getLocation(),
                    journeyManager.getSelectedJourney().getChargers()
                            .get(index - 1).getLocation()) * 100) / 100;
        }
        VBox text = new VBox(new Text(charger.getName()),
                new Text("\n" + dist + " km Distance"));

        if (journeyManager.getSelectedJourney().getVehicle() != null) {
            text.getChildren().add(
                new Text("\n" + (int) Math.ceil(dist / journeyManager.getSelectedJourney()
                    .getVehicle().getMaxRange() * 100) + "% Battery Used"));
        }

        VBox buttonBox = new VBox(btn);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        HBox content = new HBox(text, buttonBox);
        content.setPadding(new Insets(15));
        content.setId(Integer.toString(charger.getChargerId()));
        journeyTable.getChildren().add((journeyTable.getChildren()).size() - 1, content);
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
        String ids = ((Node) e.getSource()).getId();
        int idi = Integer.parseInt(ids);
        chargers.removeIf(charger -> charger.getChargerId() == idi);
        journeyTable.getChildren().removeIf(box -> Objects.equals(box.getId(), ids));
        calculateRoute();
        if (chargers.size() == 0) {
            mapController.addChargersAroundPoint(journeyManager.getSelectedJourney()
                .getStartPosition());
        } else {
            mapController.addChargersAroundPoint(chargers.get(chargers.size() - 1).getLocation());
        }
        //TODO remove all others and add to display again to recalculate distances
    } 

    /**
     * Saves journey to database
     */
    public void saveJourney() {
        if (!(distanceError) && (journeyManager.getSelectedJourney().getStartPosition() != null)
                && (journeyManager.getSelectedJourney().getEndPosition() != null)) {
            journeyManager.saveJourney();
        } else {
            if (journeyManager.getSelectedJourney().getStartPosition() == null) {
                errors.add("No start location added");
            }
            if (journeyManager.getSelectedJourney().getEndPosition() == null) {
                errors.add("No end location added");
            }
            if (distanceError) {
                errors.add("Some of your locations are out of range of eachother");
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
        
        MenuItem custom = new MenuItem("Custom");
        custom.setOnAction(this::configureVehicleItem);
        vehicles.getItems().add(custom);

        vehicles.setText("Custom");

        for (Vehicle vehicle : garageManager.getData()) {
            String title = vehicle.getMake() + ' ' + vehicle.getModel();
            MenuItem item = new MenuItem(title);
            item.setId(Integer.toString(vehicle.getVehicleId()));
            item.setOnAction(this::configureVehicleItem);
            vehicles.getItems().add(item);
        }

        //if (vehicles.getText() == "") { //Only runs first time
        //vehicles.setText(garageManager.getData().get(0).getMake()
        //+ " " + garageManager.getData().get(0).getModel());
        //journeyManager.selectVehicle(garageManager.getData().get(0));
        //}
    }

    /**
     * Configures the vehicles MenuItem
     * Adds text to vehicles MenuButton and selects vehicle
     * @param e the event of menu item being selected
     */
    public void configureVehicleItem(ActionEvent e) {
        MenuItem item = ((MenuItem) e.getSource());
        vehicles.setText(item.getText());
        int i = 0;
        if (item.getText() == "Custom") {
            rangeSlider.setDisable(false);
            journeyManager.selectVehicle(null);
        } else {
            for (Vehicle vehicle : garageManager.getData()) {
                if (vehicle.getVehicleId() == Integer.parseInt(item.getId())) {
                    journeyManager.selectVehicle(vehicle);
                    rangeSlider.setValue(vehicle.getMaxRange());
                    rangeSlider.setDisable(true);
                    sliderUpdated();
                }
                i++;
            }
        }
    }

    /**
     * Uses {@link JourneyManager JourneyManager} function to toggle
     * error test based on calculations
     */
    public void errorTextCheck() {
        distanceError = journeyManager.checkDistanceBetweenChargers();
        errorText.setVisible(false);
        if (distanceError) {
            errorText.setVisible(true);
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
            errorPopup.initModality(Modality.WINDOW_MODAL);
            ErrorController controller = error.getController();
            controller.init();
            controller.setErrors(errors);
            controller.setPromptType("error");
            controller.displayErrors();
            errorPopup.setAlwaysOnTop(true);
            errorPopup.showAndWait();
            //TODO stop errors piling up when clicking multiple times
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles updates of vehicle range slider
     */
    public void sliderUpdated() {
        journeyManager.getSelectedJourney().setVehicleRange(rangeSlider.getValue());
        if (journeyManager.getSelectedJourney().getStartPosition() != null) {
            List<Charger> chargers = journeyManager.getSelectedJourney().getChargers();
            if (chargers.size() == 0) {
                mapController.addChargersAroundPoint(journeyManager.getSelectedJourney()
                    .getStartPosition());
            } else {
                mapController.addChargersAroundPoint(
                    chargers.get(chargers.size() - 1).getLocation());
            }
        }
    }
}