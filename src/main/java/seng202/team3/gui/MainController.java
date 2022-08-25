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
import javafx.scene.text.Text;
import javafx.stage.Stage;
import seng202.team3.data.database.ComparisonType;
import seng202.team3.data.database.CsvInterpreter;
import seng202.team3.data.database.Query;
import seng202.team3.data.database.QueryBuilder;
import seng202.team3.data.database.QueryBuilderImpl;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.Coordinate;
import seng202.team3.logic.Calculations;
import seng202.team3.logic.ChargerManager;

/**
 * Controller for the main.fxml window
 * 
 * @author seng202 teaching team
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
    private TableView chargerTable;

    @FXML
    private CheckBox attractionButton;

    @FXML
    private CheckBox chargingCost;

    @FXML
    private TableColumn<Integer, String> AddressCol = new TableColumn<>("Address");

    @FXML
    private TableColumn<Integer, String> operatorCol = new TableColumn<>("Operator");

    @FXML
    private TableColumn<Integer, Double> DistanceCol = new TableColumn<>("km");

    public QueryBuilder mainDataQuery;

    private ObservableList<Charger> chargerData;

    private ChargerManager chargerManager = new ChargerManager();

    private Coordinate dummyPosition = new Coordinate(1574161.4056, 5173542.4743, -43.5097, 172.5452);

    /**
     * Initialize the window
     *
     * @param stage Top level container for this window
     */
    public void init(Stage stage) {
        mainDataQuery = new QueryBuilderImpl().withSource("charger");
        makeAllChargers();
        tableMaker();
        viewChargers(chargerData.get(0));
        insetText();
        selectToView();
        onSliderChanged();

    }

    public void makeAllChargers() {
        try {
            chargerData = FXCollections.observableList(
                    (List<Charger>) (List<?>) new CsvInterpreter().readData(mainDataQuery.build(), Charger.class));
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
                new ArrayList<>(chargerData), dummyPosition, changeDistance.getValue());
        ObservableList<Charger> closerChargers = FXCollections.observableList(arrayCloserChargers);
        addChargersToDisplay(closerChargers);
    }

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
        displayInfo.appendText("Operator: " + c.getOperator() + "\n" + "Location: " + c.getLocation().getAddress()
                + "\n" + "Number of parks: " + c.getAvailableParks() + "\nTime Limit " + c.getTimeLimit()
                + "\nHas Attraction = " + c.getHasAttraction() + "\nHas cost " + c.getChargeCost() + "\nCharger Type:"
                + word + "");
    }

    public void selectToView() {
        chargerTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object o, Object t1) {
                viewChargers(chargerData.get(chargerTable.getSelectionModel().getSelectedIndex()));

            }
        });

    }

    public void tableMaker() {
        chargerTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        AddressCol.setMaxWidth(1f * Integer.MAX_VALUE * 30);
        operatorCol.setMaxWidth(1f * Integer.MAX_VALUE * 15);
        DistanceCol.setMaxWidth(1f * Integer.MAX_VALUE * 5);

        chargerTable.getColumns().add(AddressCol);
        chargerTable.getColumns().add(operatorCol);
        chargerTable.getColumns().add(DistanceCol);
        chargerTable.requestFocus();
        chargerTable.getSelectionModel().select(0);
    }

    public void addChargersToDisplay(ObservableList<Charger> wantedChargers) {

        chargerTable.getItems().clear();

        for (int i = 0; i < wantedChargers.size(); i++) {
            chargerTable.getItems().add(i);
        }

        AddressCol.setCellValueFactory(cellData -> {
            Integer rowIndex = cellData.getValue();
            return new ReadOnlyStringWrapper(wantedChargers.get(rowIndex).getLocation().getAddress());
        });

        operatorCol.setCellValueFactory(cellData -> {
            Integer rowIndex = cellData.getValue();
            return new ReadOnlyStringWrapper(wantedChargers.get(rowIndex).getOperator());
        });

        DistanceCol.setCellValueFactory(cellData -> {
            Integer rowIndex = cellData.getValue();
            return new ReadOnlyDoubleWrapper(Math.round(
                    (Calculations.calculateDistance(dummyPosition, wantedChargers.get(rowIndex).getLocation())) * 10.0)
                    / 10.0).asObject();
        });

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

    @FXML
    public void onSliderChanged() {

        changeDistance.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                distanceDisplay.textProperty().setValue("Distance (" + Math.round(changeDistance.getValue()) + " km)");
                if (!changeDistance.isValueChanging()) {
                    compareDistance();
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
            mainDataQuery.withFilter("current", "AC", ComparisonType.CONTAINS);
        } else {
            mainDataQuery.withoutFilter("current");
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
            mainDataQuery.withFilter("current", "DC", ComparisonType.CONTAINS);
        } else {
            mainDataQuery.withoutFilter("current");
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
            mainDataQuery.withFilter("hasTouristAttraction", "True", ComparisonType.CONTAINS);
        } else {
            mainDataQuery.withoutFilter("hasTouristAttraction");
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
            mainDataQuery.withFilter("hasChargingCost", "False", ComparisonType.CONTAINS);
        } else {
            mainDataQuery.withoutFilter("hasChargingCost");
        }
        makeAllChargers();
    }

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

}
