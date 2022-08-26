package seng202.team3.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import seng202.team3.data.entity.Charger;
import seng202.team3.logic.*;

/**
 * Controller for the main.fxml window
 * 
 * @author Matthew Doonan
 */
public class MainController {

    @FXML
    private CheckBox acButton;

    @FXML
    private Slider changeDistance;

    @FXML
    private TextField searchCharger;

    @FXML
    private CheckBox dcButton;

    @FXML
    private TextArea displayInfo;

    @FXML
    private Text distanceDisplay;

    @FXML
    private TableView<Charger> chargerTable;

    @FXML
    private CheckBox attractionButton;

    @FXML
    private CheckBox chargingCost;

    @FXML
    private final TableColumn<Charger, String> addressCol = new TableColumn<>("Address");

    @FXML
    private final TableColumn<Charger, String> operatorCol = new TableColumn<>("Operator");

    @FXML
    private final TableColumn<Charger, Double> distanceCol = new TableColumn<>("km");

    @FXML
    private BorderPane mainWindow;

    private Stage stage;

    private Charger selectedCharger;

    private MapViewController mapController;

    private MainManager manage;

    /**
     * Initialize the window
     *
     * @param stage Top level container for this window
     */
    public void init(Stage stage) {
        this.stage = stage;
        manage = new MainManager(this, changeDistance.getValue());
        autoMapView();
        tableMaker();
        manage.createOriginalQuery();
        viewChargers(manage.getData().get(0));
        insetText();
        selectToView();
        change();

    }

    /**
     * Display charger info on panel
     * 
     * @param c charger to display information about
     */
    public void viewChargers(Charger c) {
        displayInfo.clear();
        StringBuilder word = new StringBuilder();
        ArrayList<String> check = new ArrayList<>();
        for (int i = 0; i < c.getConnectors().size(); i++) {
            if (!check.contains(c.getConnectors().get(i).getCurrent())) {
                word.append(" ").append(c.getConnectors().get(i).getCurrent());
                check.add(c.getConnectors().get(i).getCurrent());
            }
        }
        displayInfo.appendText("Operator: " + c.getOperator() + "\n"
                + "Location: " + c.getLocation().getAddress()
                + "\n" + "Number of parks: " + c.getAvailableParks()
                + "\nTime Limit " + c.getTimeLimit()
                + "\nHas Attraction = " + c.getHasAttraction()
                + "\nHas cost " + c.getChargeCost() + "\nCharger Type:"
                + word + "");
    }

    /**
     * Changes active charger on selected and moves the map
     */
    public void selectToView() {
        chargerTable.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    selectedCharger = chargerTable.getSelectionModel()
                            .getSelectedItem();
                    if (selectedCharger != null) {
                        viewChargers(selectedCharger);
                        // Added functionality to move screen to charger
                        if (mapController != null) {
                            mapController.changePosition(selectedCharger.getLocation());
                        }
                    }

                });

    }

    /**
     * Create charger table
     */
    public void tableMaker() {
        chargerTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        addressCol.setMaxWidth(1f * Integer.MAX_VALUE * 30);
        operatorCol.setMaxWidth(1f * Integer.MAX_VALUE * 15);
        distanceCol.setMaxWidth(1f * Integer.MAX_VALUE * 5);

        chargerTable.getColumns().add(addressCol);
        chargerTable.getColumns().add(operatorCol);
        chargerTable.getColumns().add(distanceCol);
        chargerTable.requestFocus();
        chargerTable.getSelectionModel().select(0);
    }

    /**
     * Adds every charger in charger list to the table
     */
    public void addChargersToDisplay(ObservableList<Charger> chargersToAdd) {
        chargerTable.getItems().clear();
        chargerTable.setItems(chargersToAdd);

        addressCol.setCellValueFactory(
                charger -> new ReadOnlyStringWrapper(
                        charger.getValue().getLocation().getAddress()));

        operatorCol.setCellValueFactory(
                charger -> new ReadOnlyStringWrapper(charger.getValue().getOperator()));

        distanceCol.setCellValueFactory(
                charger -> new ReadOnlyDoubleWrapper(
                        Math.round((Calculations.calculateDistance(manage.getPosition(),
                                charger.getValue().getLocation())) * 10.0) / 10.0)
                        .asObject());
        chargerTable.getSelectionModel().select(0);

        if (getMapController().getConnectorStatus()) {
            getMapController().addChargersOnMap();
        }
    }

    /**
     * Clears the search bar
     */
    public void clearSearchBar() {
        searchCharger.clear();
    }

    /**
     * Update for chargers when user searches
     *
     */
    public void insetText() {
        searchCharger.setOnMouseClicked(e -> manage.clearSearchCharger());
        searchCharger.textProperty().addListener((observable, oldValue, newValue) ->
                manage.addressQuery(searchCharger.getText()));
    }

    /**
     * Sets the Original text and updates distance filter for chargers on slider
     */
    public void change() {
        distanceDisplay.textProperty()
                .setValue("Distance (" + Math.round(changeDistance.getValue()) + " km)");
        changeDistance.valueProperty().addListener((observableValue, number, t1) -> {
            distanceDisplay.textProperty()
                    .setValue("Distance (" + Math.round(changeDistance.getValue()) + " km)");

            if (!changeDistance.isValueChanging()) {
                manage.sliderChange(changeDistance.getValue());
            }
        });
    }

    /**
     * Applies AC connector filter to charger table
     *
     */
    public void acChargersOnly() {
        manage.acQuery(acButton.isSelected());
    }

    /**
     * Applies DC connector filter to charger table
     *
     */
    public void dcChargersOnly() {
        manage.dcQuery(dcButton.isSelected());
    }

    /**
     * Applies nearby attraction filter to charger table
     *
     */
    public void attractionNeeded() {
        manage.attractionQuery(attractionButton.isSelected());
    }

    /**
     * Applies charging cost filter to charger table
     *
     */
    public void noChargingCostNeeded() {
        manage.noChargeQuery(chargingCost.isSelected());
    }

    /**
     * Loads in vehicle screen
     */
    @FXML
    public void loadVehicleScreen() {

        try {
            FXMLLoader baseLoader = new FXMLLoader(getClass().getResource("/fxml/vehicle.fxml"));
            Parent root = baseLoader.load();
            Scene scene = new Scene(root, 1080, 720);
            Stage stage = new Stage();

            VehicleController baseController = baseLoader.getController();
            baseController.init(stage);

            stage.setTitle("Revolt App");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.SEVERE, "Failed to create new Window.", e);
        }
    }

    /**
     * Loads the map view onto the Map pane automatically
     */
    @FXML
    public void autoMapView() {
        loadMapView(stage);
    }

    /**
     * Focuses the ChargerTable
     */
    public void refreshTable() {
        chargerTable.refresh();
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
