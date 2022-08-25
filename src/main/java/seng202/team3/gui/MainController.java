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
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import seng202.team3.data.database.ComparisonType;
import seng202.team3.data.database.CsvInterpreter;
import seng202.team3.data.database.Query;
import seng202.team3.data.database.QueryBuilderImpl;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.Coordinate;
import seng202.team3.logic.Calculations;
import seng202.team3.logic.MapManager;
import seng202.team3.logic.TempData;

/**
 * Controller for the main.fxml window
 * 
 * @author seng202 teaching team
 */
public class MainController {

    // private static final Logger log = LogManager.getLogger();

    // @FXML
    // private Label defaultLabel;

    // @FXML
    // private ListView listOfChargers;
    @FXML
    private Slider changeDistance;

    @FXML
    private TextField searchField;

    @FXML
    private TextField searchCharger;

    @FXML
    private TextArea displayInfo;

    @FXML
    private TableView<Charger> chargerTable;

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

    private ObservableList<Charger> chargerData;

    private MapViewController mapController;

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
        addChargersToDisplay();
        viewChargers(chargerData.get(0));
        insetText();
        selectToView();
        onSliderChanged();

    }

    /**
     * Sets chargerList to an example list of data
     * for testing
     */
    public void makeTestChargers() {
        Query myq = new QueryBuilderImpl().withSource("charger")
                .withFilter("operator", "MERIDIAN", ComparisonType.CONTAINS).build();
        try {
            List<Charger> chargerList = new ArrayList<>();
            for (Object o : new CsvInterpreter().readData(myq, Charger.class)) {
                chargerList.add((Charger) o);
            }

            chargerData = FXCollections
                    .observableList(chargerList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Display charger info on panel
     * 
     * @param c charger to display information about
     */
    public void viewChargers(Charger c) {
        displayInfo.clear();
        displayInfo.appendText("Operator: " + c.getOperator() + "\n"
                + "Location: " + c.getLocation().getAddress() + "\n"
                + "Number of parks: " + c.getAvailableParks()
                + "\nTime Limit " + c.getTimeLimit()
                + "\nHas Attraction = " + c.getHasAttraction() + "\n");

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
                        viewChargers(selectedCharger);

                        // Added functionality to move screen to charger
                        if (mapController != null) {
                            mapController.changePosition(selectedCharger.getLocation());
                        }

                    }
                });

    }

    /**
     * Adds every charger in charger list to the table
     */
    public void addChargersToDisplay() {

        chargerTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        addressCol.setMaxWidth(1f * Integer.MAX_VALUE * 30);
        operatorCol.setMaxWidth(1f * Integer.MAX_VALUE * 15);
        distanceCol.setMaxWidth(1f * Integer.MAX_VALUE * 5);

        chargerTable.setItems(chargerData);

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

        chargerTable.getColumns().add(addressCol);
        chargerTable.getColumns().add(operatorCol);
        chargerTable.getColumns().add(distanceCol);

        chargerTable.requestFocus();
    }

    /**
     * Update for chargers when user searches
     *
     */
    public void insetText() {
        searchCharger.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println(searchCharger.getText());
        });
    }

    /**
     * Method to call when our counter button is clicked
     *
     */
    @FXML
    public void onButtonClicked() {

        // ObservableList<String> data = FXCollections.observableArrayList();
        // IntStream.range(0, 1000).mapToObj(Integer::toString).forEach(data::add);

        FilteredList<String> filteredData = new FilteredList<>(
                FXCollections.observableArrayList(), s -> true);

        // TextField filterInput = new TextField();
        // filterInput.textProperty().addListener(obs->{
        String filter = searchField.getText();
        if (filter == null || filter.length() == 0) {
            filteredData.setPredicate(s -> true);
        } else {
            filteredData.setPredicate(s -> s.contains(filter));
        }
        // });

        // chargers.setItems(FXCollections.observableList(filteredData));

        // BorderPane content = new BorderPane(new ListView<>(filteredData));
        // content.setBottom(filterInput);

        // Scene scene = new Scene(content, 500, 500);
        // primaryStage.setScene(scene);
        // primaryStage.show();
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
     * Update chargers when slider moved
     */
    @FXML
    public void onSliderChanged() {
        // TODO
    }

    /**
     * Gets the charger list of chargers {@link Charger}
     *
     * @return ObservableList of chargers
     */
    public ObservableList<Charger> getChargerData() {
        return chargerData;
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
