package seng202.team3.gui;

import java.io.IOException;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team3.data.database.ComparisonType;
import seng202.team3.data.entity.Charger;
import seng202.team3.logic.GarageManager;
import seng202.team3.logic.GeoLocationHandler;
import seng202.team3.logic.MainManager;
import seng202.team3.logic.MapManager;
import seng202.team3.logic.UserManager;

/**
 * Controller for the main.fxml window (the home)
 *
 * @author Matthew Doonan, Michelle Hsieh
 * @version 1.0.2, Sep 22
 */
public class MainController {
    /**
     * Logger
     */
    private static final Logger logManager = LogManager.getLogger();

    /**
     * An AC button checkbox
     */
    @FXML
    private CheckBox acButton;

    /**
     * A Slider for distance
     */
    @FXML
    private Slider changeDistance;

    /**
     * A TextField to search charger by address
     */
    @FXML
    private TextField searchCharger;

    /**
     * A CheckBox for DC
     */
    @FXML
    private CheckBox dcButton;

    /**
     * A Checkbox for distance display
     */
    @FXML
    private CheckBox distanceDisplay;

    /**
     * A CheckBox for attraction
     */
    @FXML
    private CheckBox attractionButton;

    /**
     * A CheckBox for no charging cost
     */
    @FXML
    private CheckBox noChargingCost;

    /**
     * A CheckBox for charging cost
     */
    @FXML
    private CheckBox hasChargingCost;

    /**
     * BorderPane for the main window
     */
    @FXML
    private BorderPane mainWindow;

    /**
     * CheckBox for time limit on or off
     */
    @FXML
    private CheckBox toggleTimeLimit;

    /**
     * Slider for time limit
     */
    @FXML
    private Slider timeLimit;

    /**
     * CheckBox for on parking filter
     */
    @FXML
    private CheckBox onParkingFilter;

    /**
     * Slider for number of parks
     */
    @FXML
    private Slider parkingLot;

    /**
     * CheckBox for no carpark cost
     */
    @FXML
    private CheckBox withoutCarparkCost;

    /**
     * CheckBox for carpark cost
     */
    @FXML
    private CheckBox withCarparkCost;

    /**
     * CheckBox for open 24 hours
     */
    @FXML
    private CheckBox openAllButton;

    /**
     * CheckBox for not open 24 hours
     */
    @FXML
    private CheckBox notOpenAllButton;

    /**
     * CheckBox for no nearby attraction
     */
    @FXML
    private CheckBox noNearbyAttraction;

    /** The battery percentage box */
    @FXML
    private TextField batteryPercent;

    /**
     * The border plane to inherit charger list
     */
    @FXML
    private BorderPane chargerListPane;

    /**
     * A reflection of the routing state of mapcontroller
     */
    private Boolean routing;

    /**
     * The map controller
     */
    private MapViewController mapController;

    /**
     * The charger list controller
     */
    private ChargerListController listController;

    /**
     * The map manager
     */
    private MainManager manage;

    /** the garage manager */
    private GarageManager garageManager;

    /** Buffer for range */
    private Double buffer = 0.85;

    /**
     * unused constructor
     */
    public MainController() {
        // unused
    }

    /**
     * Initialize the window
     *
     * @param stage      Top level container for this window
     * @param menuWindow a {@link BorderPane} object
     */
    public void init(Stage stage, BorderPane menuWindow) {
        manage = new MainManager();
        createListController();
        loadMapView(stage);
        manage.resetQuery();
        manage.makeAllChargers();
        manage.setPosition();
        initialRange();
        if (MapHandler.getLocationAccepted() == null) {
            getMapController().getLocation();
        }

        if (GeoLocationHandler
                .getCoordinate() == GeoLocationHandler.DEFAULT_COORDINATE) {
            manage.setDistance(0);
            distanceDisplay.setSelected(false);
        } else {
            manage.setDistance(changeDistance.getValue() * buffer);
            distanceDisplay.setSelected(true);
        }
        updateChargerDisplay();
        change();

    }

    /**
     * Checks the battery percentage text field on input for non Integers
     * then changes the max range
     */
    public void checkForNumber() {
        if (!batteryPercent.getText().isEmpty()) {
            if (!batteryPercent.getText().matches("\\d*")) {
                batteryPercent.setText(batteryPercent.getText().replaceAll("[^\\d]", ""));
            } else if (Double.parseDouble(batteryPercent.getText()) > 100) {
                batteryPercent.setText(batteryPercent.getText()
                        .substring(0, batteryPercent.getText().length() - 1));
            } else {
                changeDistance.setValue(garageManager.getData().get(0).getMaxRange()
                        * (Double.parseDouble(batteryPercent.getText()) / 100));
            }
        } else {
            changeDistance.setValue(garageManager.getData().get(0).getMaxRange());
        }
    }

    /**
     * Sets the initial range of chargers in view to the vehicles range
     */
    public void initialRange() {
        if (UserManager.getUser() == UserManager.getGuest()) {
            batteryPercent.setVisible(false);
        } else {
            garageManager = new GarageManager();
            garageManager.resetQuery();
            if (garageManager.getSelectedVehicle() != null) {
                batteryPercent.setVisible(true);
                changeDistance.setValue(garageManager.getSelectedVehicle().getMaxRange());
            } else {
                batteryPercent.setVisible(false);
            }
        }
    }

    /**
     * Loads the charger list into the border pane
     */
    public void createListController() {
        try {
            FXMLLoader webViewLoader =
                    new FXMLLoader(getClass().getResource("/fxml/chargerListView.fxml"));
            Parent mapViewParent = webViewLoader.load();
            listController = webViewLoader.getController();
            listController.chargerListView(chargerListPane, this);
            chargerListPane.setCenter(mapViewParent);
            MainWindow.setController(listController);
            logManager.info("The list view has opened");
        } catch (IOException e) {
            logManager.error(e.getMessage());
        }
        try {
            // For reloading the datalist
            updateChargerDisplay();
        } catch (NullPointerException e) {
            logManager.info("Loading the main manager for the first time");
            return;
        }
    }

    /**
     * Button call that swaps the views
     */
    public void createLargeChargerView() {
        try {
            FXMLLoader webViewLoader =
                    new FXMLLoader(getClass().getResource("/fxml/moreChargerInfoView.fxml"));
            Parent chargerList = webViewLoader.load();
            listController = webViewLoader.getController();
            listController.largerView(chargerListPane, this);
            chargerListPane.setCenter(chargerList);
            logManager.info("The more charger info view has opened");
        } catch (IOException e) {
            logManager.error(e.getMessage());
        }
    }

    /**
     * Sets the Original text and updates value for the slider filter
     * names so the user can see what they have input
     */
    public void change() {
        distanceDisplay.textProperty()
                .setValue("Minimum distance (" + Math.round(changeDistance.getValue()) + " km)");
        onParkingFilter.textProperty()
                .setValue("Minimum number of spaces ("
                        + Math.round(parkingLot.getValue()) + ")");
        toggleTimeLimit.textProperty()
                .setValue("Minimum time limit of ("
                        + Math.round(timeLimit.getValue()) + " minutes)");

        changeDistance.valueProperty().addListener(
                (observableValue, number, t1) -> distanceDisplay.textProperty()
                        .setValue("Minimum distance ("
                                + Math.round(changeDistance.getValue()) + " km)"));
        parkingLot.valueProperty().addListener(
                (observableValue, number, t1) -> onParkingFilter.textProperty()
                        .setValue("Minimum number of spaces ("
                                + Math.round(parkingLot.getValue()) + ")"));
        timeLimit.valueProperty().addListener(
                (observableValue, number, t1) -> toggleTimeLimit.textProperty()
                        .setValue("Minimum time limit of ("
                                + Math.round(timeLimit.getValue()) + " minutes)"));
    }

    /**
     * Adds queries onto the query builder according to the current buttons selected
     */
    public void executeSearch() {
        manage.resetQuery();
        if (toggleTimeLimit.isSelected()) {
            manage.adjustQuery("maxtimelimit",
                    Double.toString(timeLimit.getValue()), ComparisonType.GREATER_THAN_EQUAL);
        }
        if (onParkingFilter.isSelected()) {
            manage.adjustQuery("carparkcount",
                    Double.toString(parkingLot.getValue()), ComparisonType.GREATER_THAN_EQUAL);
        }
        if (withoutCarparkCost.isSelected()) {
            manage.adjustQuery("hascarparkcost", "False", ComparisonType.EQUAL);
        }
        if (withCarparkCost.isSelected()) {
            manage.adjustQuery("hascarparkcost", "True", ComparisonType.EQUAL);
        }
        if (acButton.isSelected()) {
            manage.adjustQuery("currenttype", "AC", ComparisonType.CONTAINS);
        }
        if (dcButton.isSelected()) {
            manage.adjustQuery("currenttype", "DC", ComparisonType.CONTAINS);
        }
        if (openAllButton.isSelected()) {
            manage.adjustQuery("is24hours", "True", ComparisonType.EQUAL);
        }
        if (notOpenAllButton.isSelected()) {
            manage.adjustQuery("is24hours", "False", ComparisonType.EQUAL);
        }
        if (attractionButton.isSelected()) {
            manage.adjustQuery("hastouristattraction", "True", ComparisonType.EQUAL);
        }
        if (noNearbyAttraction.isSelected()) {
            manage.adjustQuery("hastouristattraction", "False", ComparisonType.EQUAL);
        }
        if (noChargingCost.isSelected()) {
            manage.adjustQuery("haschargingcost", "False", ComparisonType.EQUAL);
        }
        if (hasChargingCost.isSelected()) {
            manage.adjustQuery("haschargingcost", "True", ComparisonType.EQUAL);
        }
        if (searchCharger.getText().length() != 0) {
            manage.adjustQuery("address", searchCharger.getText(), ComparisonType.CONTAINS);
        }

        manage.makeAllChargers();
        if (distanceDisplay.isSelected()) {
            manage.setDistance(changeDistance.getValue() * buffer);
        } else {
            manage.setDistance(0);
        }
        ObservableList<Charger> chargers = manage.getCloseChargerData();
        updateChargerDisplay();
        if (!chargers.isEmpty() && Boolean.TRUE.equals(MapHandler.isMapRequested())) {
            mapController.changePosition(chargers.get(0).getLocation());
        }

    }

    /**
     * Gets the Map View Controller
     *
     * @return MapViewController of map viewer
     */
    public MapViewController getMapController() {
        return mapController;
    }

    /**
     * Loads the map view into the main part of the main window
     *
     * @param stage stage to load with
     */
    private void loadMapView(Stage stage) {

        try {
            FXMLLoader webViewLoader = new FXMLLoader(getClass().getResource("/fxml/map.fxml"));
            Parent mapViewParent = webViewLoader.load();

            mapController = webViewLoader.getController();
            MapManager mapManager = new MapManager(manage);
            mapController.init(stage, mapManager);

            mainWindow.setCenter(mapViewParent);
            MainWindow.setController(mapController);
        } catch (IOException e) {
            logManager.error(e.getMessage());
        }
    }

    /**
     * Gets the MainManager created by the MainController
     *
     * @return {@link MainManager} the manager of this
     *         controller
     */
    public MainManager getManager() {
        return manage;
    }

    /**
     * Updates the display of chargers
     */
    private void updateChargerDisplay() {
        if (listController.getChargerTable()) {
            listController.addChargersToDisplay(manage.getCloseChargerData());
        }
        if (Boolean.TRUE.equals(MapHandler.isMapRequested())) {
            getMapController().addChargersOnMap();
        }
    }

    /**
     * Gets the list view controller
     *
     * @return {@link ChargerListController} the controller
     */
    public ChargerListController getListController() {
        return listController;
    }

    /**
     * Gets the manager to edit the charger
     */
    public void editCharger() {
        checkRouting();
        if (Boolean.FALSE.equals(routing)) {
            manage.editCharger();
        }
    }

    /**
     * Checks if routing. If routing, sets editable to
     */
    public void checkRouting() {
        routing = mapController.isRouteDisplayed();
    }

    /**
     * Searches on enter
     *
     * @param e the event handler
     */
    @FXML
    public void onEnter(ActionEvent e) {
        executeSearch();
    }

}
