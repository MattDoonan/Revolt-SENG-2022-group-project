package seng202.team3.gui;

import java.io.IOException;
import java.util.ArrayList;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import seng202.team3.data.database.ComparisonType;
import seng202.team3.data.entity.Charger;
import seng202.team3.logic.MainManager;
import seng202.team3.logic.TableManager;





/**
 * A TableController class that deals with the display of the table objects
 *
 * @author Michelle Hsieh
 * @version 1.0.0, Sep 22
 */
public class TableController {

    private Stage stage;

    private TableManager manage;

    @FXML
    private CheckBox dcButton;

    @FXML
    private CheckBox acButton;

    @FXML
    private CheckBox attractionButton;

    @FXML
    private CheckBox chargingCost;

    @FXML
    private CheckBox hasChargingCost;

    @FXML
    private CheckBox toggleTimeLimit;

    @FXML
    private Slider timeLimit;

    @FXML
    private CheckBox onParkingFilter;

    @FXML
    private Slider parkingLot;

    @FXML
    private CheckBox withoutCarparkCost;

    @FXML
    private CheckBox withCarparkCost;

    @FXML
    private CheckBox openAllButton;

    @FXML
    private CheckBox notOpenAllButton;

    @FXML
    private CheckBox noNearbyAttraction;

    @FXML
    private CheckBox showId;

    @FXML
    private CheckBox showXpos;

    @FXML
    private CheckBox showYpos;

    @FXML
    private CheckBox showOperator;

    @FXML
    private CheckBox showAddress;

    @FXML
    private CheckBox showOwner;

    @FXML
    private CheckBox showHoursOpen;

    @FXML
    private CheckBox showCarparks;

    @FXML
    private CheckBox showCarparkCost;

    @FXML
    private CheckBox showTimeLimit;

    @FXML
    private CheckBox showAttraction;

    @FXML
    private CheckBox showLat;

    @FXML
    private CheckBox showLon;

    @FXML
    private CheckBox showOpening;

    @FXML
    private CheckBox showChargingCost;

    @FXML
    private CheckBox showCurrent;

    @FXML
    private TextField searchCharger;

    @FXML
    private TableView<Charger> mainTable;

    @FXML
    private final TableColumn<Charger, Integer> idCol =
            new TableColumn<>("Charger ID");
    @FXML
    private final TableColumn<Charger, Double> xposCol =
            new TableColumn<>("X coordinate");
    @FXML
    private final TableColumn<Charger, Double> yposCol =
            new TableColumn<>("Y coordinate");
    @FXML
    private final TableColumn<Charger, String> operatorCol =
            new TableColumn<>("Operator");
    @FXML
    private final TableColumn<Charger, String> addressCol =
            new TableColumn<>("Address");
    @FXML
    private final TableColumn<Charger, String> ownerCol =
            new TableColumn<>("Owner");
    @FXML
    private final TableColumn<Charger, Boolean> hoursCol =
            new TableColumn<>("Hours Open");
    @FXML
    private final TableColumn<Charger, Integer> carparkCol =
            new TableColumn<>("Carparks");
    @FXML
    private final TableColumn<Charger, Boolean> carparkCostCol =
            new TableColumn<>("Carpark Cost");
    @FXML
    private final TableColumn<Charger, Double> timeLimitCol =
            new TableColumn<>("Time limit");
    @FXML
    private final TableColumn<Charger, Boolean> attractionCol =
            new TableColumn<>("Has Attraction");
    @FXML
    private final TableColumn<Charger, Double> latitudeCol =
            new TableColumn<>("Latitude coordinate");
    @FXML
    private final TableColumn<Charger, Double> longitudeCol =
            new TableColumn<>("Longitude coordinate");
    @FXML
    private final TableColumn<Charger, String> openCol =
            new TableColumn<>("Date open");
    @FXML
    private final TableColumn<Charger, Boolean> chargcostCol =
            new TableColumn<>("Charging cost");
    @FXML
    private final TableColumn<Charger, String> currentsCol =
            new TableColumn<>("Current types");

    /**
     * Initialize the window
     *
     * @param stage Top level container for this window
     */
    public void init(Stage stage) {
        this.stage = stage;
        manage = new TableManager();
        manage.resetQuery();
        tableMaker();
        manage.makeAllChargers();
        addToDisplay(manage.getData());
        change();
    }

    /**
     * Initializes tha table and columns
     */
    public void tableMaker() {
        mainTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        idCol.setMaxWidth(1f * Integer.MAX_VALUE * 10);
        xposCol.setMaxWidth(1f * Integer.MAX_VALUE * 10);
        yposCol.setMaxWidth(1f * Integer.MAX_VALUE * 10);
        operatorCol.setMaxWidth(1f * Integer.MAX_VALUE * 10);
        addressCol.setMaxWidth(1f * Integer.MAX_VALUE * 10);
        ownerCol.setMaxWidth(1f * Integer.MAX_VALUE * 10);
        hoursCol.setMaxWidth(1f * Integer.MAX_VALUE * 10);
        carparkCol.setMaxWidth(1f * Integer.MAX_VALUE * 10);
        carparkCostCol.setMaxWidth(1f * Integer.MAX_VALUE * 10);
        timeLimitCol.setMaxWidth(1f * Integer.MAX_VALUE * 10);
        attractionCol.setMaxWidth(1f * Integer.MAX_VALUE * 10);
        latitudeCol.setMaxWidth(1f * Integer.MAX_VALUE * 10);
        longitudeCol.setMaxWidth(1f * Integer.MAX_VALUE * 10);
        openCol.setMaxWidth(1f * Integer.MAX_VALUE * 10);
        chargcostCol.setMaxWidth(1f * Integer.MAX_VALUE * 10);
        currentsCol.setMaxWidth(1f * Integer.MAX_VALUE * 10);
        mainTable.getColumns().removeAll(mainTable.getColumns());
        if (showId.isSelected()) {
            mainTable.getColumns().add(idCol);
        }
        if (showXpos.isSelected()) {
            mainTable.getColumns().add(xposCol);
        }
        if (showYpos.isSelected()) {
            mainTable.getColumns().add(yposCol);
        }
        if (showOperator.isSelected()) {
            mainTable.getColumns().add(operatorCol);
        }
        if (showAddress.isSelected()) {
            mainTable.getColumns().add(addressCol);
        }
        if (showOwner.isSelected()) {
            mainTable.getColumns().add(ownerCol);
        }
        if (showHoursOpen.isSelected()) {
            mainTable.getColumns().add(hoursCol);
        }
        if (showCarparks.isSelected()) {
            mainTable.getColumns().add(carparkCol);
        }
        if (showCarparkCost.isSelected()) {
            mainTable.getColumns().add(carparkCostCol);
        }
        if (showTimeLimit.isSelected()) {
            mainTable.getColumns().add(timeLimitCol);
        }
        if (showAttraction.isSelected()) {
            mainTable.getColumns().add(attractionCol);
        }
        if (showLat.isSelected()) {
            mainTable.getColumns().add(latitudeCol);
        }
        if (showLon.isSelected()) {
            mainTable.getColumns().add(longitudeCol);
        }
        if (showOpening.isSelected()) {
            mainTable.getColumns().add(openCol);
        }
        if (showChargingCost.isSelected()) {
            mainTable.getColumns().add(chargcostCol);
        }
        if (showCurrent.isSelected()) {
            mainTable.getColumns().add(currentsCol);
        }
        mainTable.requestFocus();
    }

    /**
     * adds chargers to the display
     * @param chargersToAdd Observable list of charger objects
     */
    public void addToDisplay(ObservableList<Charger> chargersToAdd) {
        mainTable.getItems().clear();
        mainTable.setItems(chargersToAdd);
        idCol.setCellValueFactory(charger ->
                new ReadOnlyIntegerWrapper(charger.getValue().getChargerId()).asObject());
        xposCol.setCellValueFactory(charger ->
                new ReadOnlyDoubleWrapper(charger.getValue().getLocation().getXpos()).asObject());
        yposCol.setCellValueFactory(charger ->
                new ReadOnlyDoubleWrapper(charger.getValue().getLocation().getYpos()).asObject());
        operatorCol.setCellValueFactory(charger ->
                new ReadOnlyStringWrapper(charger.getValue().getOperator()));
        addressCol.setCellValueFactory(charger ->
                new ReadOnlyStringWrapper(charger.getValue().getLocation().getAddress()));
        ownerCol.setCellValueFactory(charger ->
                new ReadOnlyStringWrapper(charger.getValue().getOwner()));
        hoursCol.setCellValueFactory(charger ->
                new ReadOnlyBooleanWrapper(charger.getValue().getAvailable24Hrs()));
        carparkCol.setCellValueFactory(charger ->
                new ReadOnlyIntegerWrapper(charger.getValue().getAvailableParks()).asObject());
        carparkCostCol.setCellValueFactory(charger ->
                new ReadOnlyBooleanWrapper(charger.getValue().getParkingCost()));
        timeLimitCol.setCellValueFactory(charger ->
                new ReadOnlyDoubleWrapper(charger.getValue().getTimeLimit()).asObject());
        attractionCol.setCellValueFactory(charger ->
                new ReadOnlyBooleanWrapper(charger.getValue().getHasAttraction()));
        latitudeCol.setCellValueFactory(charger ->
                new ReadOnlyDoubleWrapper(charger.getValue().getLocation().getLat()).asObject());
        longitudeCol.setCellValueFactory(charger ->
                new ReadOnlyDoubleWrapper(charger.getValue().getLocation().getLon()).asObject());
        openCol.setCellValueFactory(charger ->
                new ReadOnlyStringWrapper(charger.getValue().getDateOpened()));
        chargcostCol.setCellValueFactory(charger ->
                new ReadOnlyBooleanWrapper(charger.getValue().getChargeCost()));
        currentsCol.setCellValueFactory(charger ->
                new ReadOnlyStringWrapper(manage.getConnectors(charger.getValue())));
        mainTable.getSelectionModel().select(0);
        mainTable.getSortOrder().add(idCol);
        mainTable.sort();
    }

    /**
     * Executes an add, loading a small mapview screen
     */
    @FXML
    public void addCharger() {

        try {
            FXMLLoader miniMap = new FXMLLoader(getClass().getResource(
                    "/fxml/mini_map.fxml"));
            BorderPane root = miniMap.load();
            Scene modalScene = new Scene(root);
            Stage modal = new Stage();
            modal.setScene(modalScene);
            modal.setResizable(false);
            modal.setTitle("Charger Location");
            modal.initModality(Modality.WINDOW_MODAL);
            MiniMapController controller = miniMap.getController();
            controller.init(modal);
            controller.setManager(manage);
            modal.setAlwaysOnTop(true);
            modal.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (manage.getPosition() != null) {
            manage.addCharger();
        }
    }

    /**
     * Updates the table when a user clicks update table button
     */
    public void updateTable() {
        manage.resetQuery();
        tableMaker();
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
        if (chargingCost.isSelected()) {
            manage.adjustQuery("haschargingcost", "False", ComparisonType.EQUAL);
        }
        if (hasChargingCost.isSelected()) {
            manage.adjustQuery("haschargingcost", "True", ComparisonType.EQUAL);
        }
        if (searchCharger.getText().length() != 0) {
            manage.adjustQuery("address", searchCharger.getText(), ComparisonType.CONTAINS);
        }
        manage.makeAllChargers();
        addToDisplay(manage.getData());
    }


    /**
     * Sets the Original text and updates distance filter for chargers on slider
     */
    public void change() {
        onParkingFilter.textProperty()
                .setValue("Minimum number of spaces ("
                        + Math.round(parkingLot.getValue()) + ")");
        toggleTimeLimit.textProperty()
                .setValue("Minimum time limit of ("
                        + Math.round(timeLimit.getValue()) + " minutes)");
        parkingLot.valueProperty().addListener((observableValue, number, t1) -> {
            onParkingFilter.textProperty()
                    .setValue("Minimum number of spaces ("
                            + Math.round(parkingLot.getValue()) + ")");
        });
        timeLimit.valueProperty().addListener((observableValue, number, t1) -> {
            toggleTimeLimit.textProperty()
                    .setValue("Minimum time limit of ("
                            + Math.round(timeLimit.getValue()) + " minutes)");
        });
    }

    public TableManager getManager() {
        return manage;
    }

    /**
     * Executes an edit on the selected charger
     */
    @FXML
    public void editCharger() {
        manage.editCharger();
    }

    /**
     * Executes a delete on the selected charger
     */
    @FXML
    public void deleteCharger() {
        manage.deleteCharger();

    }

}
