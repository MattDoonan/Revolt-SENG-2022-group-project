package seng202.team3.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
import seng202.team3.data.database.ComparisonType;
import seng202.team3.data.database.CsvInterpreter;
import seng202.team3.data.database.QueryBuilder;
import seng202.team3.data.database.QueryBuilderImpl;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.Coordinate;
import seng202.team3.logic.Calculations;
import seng202.team3.logic.ChargerManager;
import seng202.team3.logic.MapManager;
import seng202.team3.logic.TempData;

/**
 * Controller for the main.fxml window
 * 
 * @author Matthew Doonan
 */
public class MainController {

    @FXML
    private Slider changeDistance;

    @FXML
    private TextField searchField;

    @FXML
    private TextField searchCharger;

    @FXML
    private TextArea displayInfo;

    @FXML
    private Text distanceDisplay;

    @FXML
    private CheckBox acButton;

    @FXML
    private CheckBox dcButton;

    @FXML
    private TableView<Charger> chargerTable;

    @FXML
    private CheckBox attractionButton;

    @FXML
    private CheckBox chargingCost;

    @FXML
    private TableColumn<Charger, String> addressCol = new TableColumn<>("Address");

    @FXML
    private TableColumn<Charger, String> operatorCol = new TableColumn<>("Operator");

    @FXML
    private TableColumn<Charger, Double> distanceCol = new TableColumn<>("km");

    @FXML
    private BorderPane mainWindow;

    private Stage stage;

    private Charger selectedCharger;

    public QueryBuilder mainDataQuery;

    private ObservableList<Charger> chargerData;

    private MapViewController mapController;

    private ChargerManager chargerManager = new ChargerManager();

    private Coordinate position = new Coordinate(1574161.4056, 5173542.4743, -43.5097, 172.5452);

    /**
     * Initialize the window
     *
     * @param stage Top level container for this window
     */
    public void init(Stage stage) {
        this.stage = stage;
        TempData.makeTempData();
        chargerData = TempData.getChargers();
        TempData.setController(this);
        mainDataQuery = new QueryBuilderImpl().withSource("charger");
        autoMapView();
        makeAllChargers();
        tableMaker();
        viewChargers(chargerData.get(0));
        insetText();
        selectToView();
        onSliderChanged();

    }

    /**
     * Populate table view with chargers matching data query
     */
    public void makeAllChargers() {
        try {
            List<Charger> chargerList = new ArrayList<>();
            for (Object o : new CsvInterpreter().readData(mainDataQuery.build(), Charger.class)) {
                chargerList.add((Charger) o);
            }

            chargerData = FXCollections
                    .observableList(chargerList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        compareDistance();
    }

    /**
     * Gets chargers within range of the selected location
     */
    public void compareDistance() {
        ArrayList<Charger> arrayCloserChargers = chargerManager.getNearbyChargers(
                new ArrayList<>(chargerData), position, changeDistance.getValue());
        ObservableList<Charger> closerChargers = FXCollections.observableList(arrayCloserChargers);
        addChargersToDisplay(closerChargers);
    }

    /**
     * Display charger info on panel
     * 
     * @param c charger to display information about
     */
    public void viewChargers(Charger c) {
        displayInfo.clear();
        String word = "";
        ArrayList<String> check = new ArrayList<String>();
        for (int i = 0; i < c.getConnectors().size(); i++) {
            if (!check.contains(c.getConnectors().get(i).getCurrent())) {
                word = word + " " + c.getConnectors().get(i).getCurrent();
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
                .addListener(new ChangeListener<Charger>() {

                    @Override
                    public void changed(ObservableValue<? extends Charger> observable,
                            Charger oldValue, Charger newValue) {
                        selectedCharger = (Charger) chargerTable.getSelectionModel()
                                .getSelectedItem();
                        if (selectedCharger != null) {
                            viewChargers(selectedCharger);
                            // Added functionality to move screen to charger
                            if (mapController != null) {
                                mapController.changePosition(selectedCharger.getLocation());
                            }
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
                        Math.round((Calculations.calculateDistance(position,
                                charger.getValue().getLocation())) * 10.0) / 10.0)
                        .asObject());
        chargerTable.getSelectionModel().select(0);

        if (getMapController().getConnectorStatus()) {
            getMapController().addChargersOnMap();
        }
    }

    /**
     * Update for chargers when user searches
     *
     */
    public void insetText() {
        searchCharger.setOnMouseClicked(e -> {
            if (searchCharger.getText().contains("Search Charger")) {
                searchCharger.clear();
            }
        });
        searchCharger.textProperty().addListener((observable, oldValue, newValue) -> {
            mainDataQuery.withoutFilter("address");
            mainDataQuery.withFilter("address", searchCharger.getText(), ComparisonType.CONTAINS);
            makeAllChargers();
        });
    }

    /**
     * Update distance filter for chargers on slider
     */
    @FXML
    public void onSliderChanged() {

        changeDistance.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue,
                    Number number, Number t1) {
                distanceDisplay.textProperty()
                        .setValue("Distance (" + Math.round(changeDistance.getValue()) + " km)");

                if (!changeDistance.isValueChanging()) {
                    makeAllChargers();
                }

            }
        });
    }

    /**
     * Applies AC connector filter to charger table
     * 
     * @param actionEvent action that triggers filter
     */
    public void acChargersOnly(ActionEvent actionEvent) {
        if (acButton.isSelected()) {
            mainDataQuery.withFilter("connectorcurrent", "AC", ComparisonType.CONTAINS);
        } else {
            mainDataQuery.withoutFilter("connectorcurrent");
        }
        makeAllChargers();
    }

    /**
     * Applies DC connector filter to charger table
     * 
     * @param actionEvent action that triggers filter
     */
    public void dcChargersOnly(ActionEvent actionEvent) {
        if (dcButton.isSelected()) {
            mainDataQuery.withFilter("connectorcurrent", "DC", ComparisonType.CONTAINS);
        } else {
            mainDataQuery.withoutFilter("connectorcurrent");
        }
        makeAllChargers();
    }

    /**
     * Applies nearby attraction filter to charger table
     * 
     * @param actionEvent action that triggers filter
     */
    public void attractionNeeded(ActionEvent actionEvent) {
        if (attractionButton.isSelected()) {
            mainDataQuery.withFilter("hastouristattraction", "True", ComparisonType.CONTAINS);
        } else {
            mainDataQuery.withoutFilter("hastouristattraction");
        }
        makeAllChargers();
    }

    /**
     * Applies chargin cost filter to charger table
     * 
     * @param actionEvent action that triggers filter
     */
    public void noChargingCostNeeded(ActionEvent actionEvent) {
        if (chargingCost.isSelected()) {
            mainDataQuery.withFilter("haschargingcost", "False", ComparisonType.CONTAINS);
        } else {
            mainDataQuery.withoutFilter("haschargingcost");
        }
        makeAllChargers();
    }

    /**
     * Loads in vehicle screen
     */
    @FXML
    public void loadVehicleScreen() {

        try {
            // FXMLLoader fxmlLoader = new FXMLLoader();
            // fxmlLoader.setLocation(getClass().getResource("/fxml/vehicle.fxml"));
            FXMLLoader baseLoader = new FXMLLoader(getClass().getResource("/fxml/vehicle.fxml"));
            Parent root = baseLoader.load();
            /*
             * if "fx:controller" is not set in fxml
             * fxmlLoader.setController(NewWindowController);
             */
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
     * Gets the charger list of chargers {@link Charger}
     *
     * @return ObservableList of chargers
     */
    public ObservableList<Charger> getChargerData() {
        return chargerTable.getItems();
    }

    /**
     * Gets the position of the {@link Coordinate}
     *
     * @return Coordinate of position
     */
    public Coordinate getPosition() {
        return position;
    }

    /**
     * Sets the position using a {@link Coordinate}
     *
     * @param position a Coordinate of the selected position
     */
    public void setPosition(Coordinate position) {
        this.position = position;
        compareDistance();
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
            MapManager mapManager = new MapManager(this);
            mapController.init(stage, mapManager);

            mainWindow.setCenter(mapViewParent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
