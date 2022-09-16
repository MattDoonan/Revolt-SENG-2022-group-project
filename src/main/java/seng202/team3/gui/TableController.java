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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import seng202.team3.data.entity.Charger;
import seng202.team3.logic.TableManager;





/**
 * A TableController class that deals with the display of the table objects
 *
 * @author Michelle Hsieh
 * @version 1.0.0, Sep 22
 */
public class TableController {

    private Stage stage;
    private TableManager manager;

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
    private CheckBox onParkingFiler;

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
        manager = new TableManager();
        manager.resetQuery();
        tableMaker();
        manager.makeAllChargers();
        addToDisplay(manager.getData());
    }

    /**
     * Initializes tha table
     */
    public void tableMaker() {
        mainTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        idCol.setMaxWidth(1f * Integer.MAX_VALUE * 30);
        xposCol.setMaxWidth(1f * Integer.MAX_VALUE * 30);
        yposCol.setMaxWidth(1f * Integer.MAX_VALUE * 30);
        operatorCol.setMaxWidth(1f * Integer.MAX_VALUE * 30);
        addressCol.setMaxWidth(1f * Integer.MAX_VALUE * 30);
        ownerCol.setMaxWidth(1f * Integer.MAX_VALUE * 30);
        hoursCol.setMaxWidth(1f * Integer.MAX_VALUE * 30);
        carparkCol.setMaxWidth(1f * Integer.MAX_VALUE * 30);
        carparkCostCol.setMaxWidth(1f * Integer.MAX_VALUE * 30);
        timeLimit.setMaxWidth(1f * Integer.MAX_VALUE * 30);
        attractionCol.setMaxWidth(1f * Integer.MAX_VALUE * 30);
        latitudeCol.setMaxWidth(1f * Integer.MAX_VALUE * 30);
        longitudeCol.setMaxWidth(1f * Integer.MAX_VALUE * 30);
        openCol.setMaxWidth(1f * Integer.MAX_VALUE * 30);
        chargcostCol.setMaxWidth(1f * Integer.MAX_VALUE * 30);
        currentsCol.setMaxWidth(1f * Integer.MAX_VALUE * 30);

        mainTable.getColumns().add(idCol);
        mainTable.getColumns().add(xposCol);
        mainTable.getColumns().add(yposCol);
        mainTable.getColumns().add(operatorCol);
        mainTable.getColumns().add(addressCol);
        mainTable.getColumns().add(ownerCol);
        mainTable.getColumns().add(hoursCol);
        mainTable.getColumns().add(carparkCol);
        mainTable.getColumns().add(carparkCostCol);
        mainTable.getColumns().add(timeLimitCol);
        mainTable.getColumns().add(attractionCol);
        mainTable.getColumns().add(latitudeCol);
        mainTable.getColumns().add(longitudeCol);
        mainTable.getColumns().add(openCol);
        mainTable.getColumns().add(chargcostCol);
        mainTable.getColumns().add(currentsCol);

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
                new ReadOnlyStringWrapper(manager.getConnectors(charger.getValue())));
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
            controller.setManager(manager);
            modal.setAlwaysOnTop(true);
            modal.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
        manager.addCharger();
        if (manager.getPosition() == null) {
            System.out.println("not added");
        } else {
            System.out.println(manager.getPosition().getAddress());
        }
    }

    /**
     * Executes an edit on the selected charger
     */
    @FXML
    public void editCharger() {
        manager.editCharger();
    }

    /**
     * Executes a delete on the selected charger
     */
    @FXML
    public void deleteCharger() {
        manager.deleteCharger();

    }

}
