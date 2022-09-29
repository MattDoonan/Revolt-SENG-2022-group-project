package seng202.team3.gui;

import java.io.IOException;
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
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import seng202.team3.data.database.ComparisonType;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.PermissionLevel;
import seng202.team3.logic.JavaScriptBridge;
import seng202.team3.logic.TableManager;
import seng202.team3.logic.UserManager;

/**
 * A TableController class that deals with the display of the table objects
 *
 * @author Michelle Hsieh, Matthew Doonan
 * @version 1.0.1, Sep 22
 */
public class TableController {

    /**
     * Manager for the table screen
     */
    private TableManager manage;

    /**
     * CheckBox for toggling dc filter
     */
    @FXML
    protected CheckBox dcButton;

    /**
     * CheckBox for toggling ac filter
     */
    @FXML
    protected CheckBox acButton;

    /**
     * CheckBox for toggling has attraction filter
     */
    @FXML
    protected CheckBox attractionButton;

    /**
     * CheckBox for enabling filter by chargingCost
     */
    @FXML
    protected CheckBox chargingCost;

    /**
     * CheckBox for toggling chargingcost filter (true/false)
     */
    @FXML
    protected CheckBox hasChargingCost;

    /**
     * Checkbox for enabling time limit filter
     */
    @FXML
    protected CheckBox toggleTimeLimit;

    /**
     * Slider to set maximum limit
     */
    @FXML
    protected Slider timeLimit;

    /**
     * CheckBox for enabling number of parks filter
     */
    @FXML
    protected CheckBox onParkingFilter;

    /**
     * Slider to set number of parks
     */
    @FXML
    protected Slider parkingLot;

    /**
     * Checkbox to set filter without park cost
     */
    @FXML
    protected CheckBox withoutCarparkCost;

    /**
     * Checkbox to set filter with park cost
     */
    @FXML
    protected CheckBox withCarparkCost;

    /**
     * Checkbox for toggling always open
     */
    @FXML
    protected CheckBox openAllButton;

    /**
     * Checkbox for toggling not always open
     */
    @FXML
    protected CheckBox notOpenAllButton;

    /**
     * CheckBox for toggling no nearby attractions filter
     */
    @FXML
    protected CheckBox noNearbyAttraction;

    /**
     * Toggle visibility of id field
     */
    @FXML
    protected CheckBox showId;

    /**
     * Toggle visibility of x field
     */
    @FXML
    protected CheckBox showXpos;

    /**
     * Toggle visibility of y field
     */
    @FXML
    protected CheckBox showYpos;

    /**
     * Toggle visibility of operator field
     */
    @FXML
    protected CheckBox showOperator;

    /**
     * Toggle visibility of address field
     */
    @FXML
    protected CheckBox showAddress;

    /**
     * Toggle visibility of owner field
     */
    @FXML
    protected CheckBox showOwner;

    /**
     * Toggle visibility of is24hrs field
     */
    @FXML
    protected CheckBox showHoursOpen;

    /**
     * Toggle visibility of num parks field
     */
    @FXML
    protected CheckBox showCarparks;

    /**
     * Toggle visibility of carpark cost field
     */
    @FXML
    protected CheckBox showCarparkCost;

    /**
     * Toggle visibility of time limit field
     */
    @FXML
    protected CheckBox showTimeLimit;

    /**
     * Toggle visibility of hasAttraction field
     */
    @FXML
    protected CheckBox showAttraction;

    /**
     * Toggle visibility of latitude field
     */
    @FXML
    protected CheckBox showLat;

    /**
     * Toggle visibility of longitude field
     */
    @FXML
    protected CheckBox showLon;

    /**
     * Toggle visibility of date opened field
     */
    @FXML
    protected CheckBox showOpening;

    /**
     * Toggle visibility of charging cost field
     */
    @FXML
    protected CheckBox showChargingCost;

    /**
     * Toggle visibility of connector current field
     */
    @FXML
    protected CheckBox showCurrent;

    /**
     * Toggle visibility of the chargers views
     */
    @FXML
    protected CheckBox showViews;

    /**
     * Search bar to search for addresses
     */
    @FXML
    protected TextField searchCharger;

    /**
     * Table to contain all charger information
     */
    @FXML
    protected TableView<Charger> mainTable;

    /**
     * Maps charger to the id
     */
    @FXML
    protected final TableColumn<Charger, Integer> idCol = new TableColumn<>("Charger ID");

    /**
     * Maps charger to the x coord
     */
    @FXML
    protected final TableColumn<Charger, Double> xposCol = new TableColumn<>("X coordinate");

    /**
     * Maps charger to the y coord
     */
    @FXML
    protected final TableColumn<Charger, Double> yposCol = new TableColumn<>("Y coordinate");

    /**
     * Maps charger to the operator
     */
    @FXML
    protected final TableColumn<Charger, String> operatorCol = new TableColumn<>("Operator");

    /**
     * Maps charger to the address
     */
    @FXML
    protected final TableColumn<Charger, String> addressCol = new TableColumn<>("Address");

    /**
     * Maps charger to the owner
     */
    @FXML
    protected final TableColumn<Charger, String> ownerCol = new TableColumn<>("Owner");

    /**
     * Maps charger to the hours open
     */
    @FXML
    protected final TableColumn<Charger, Boolean> hoursCol = new TableColumn<>("Hours Open");

    /**
     * Maps charger to the num parks
     */
    @FXML
    protected final TableColumn<Charger, Integer> carparkCol = new TableColumn<>("Carparks");

    /**
     * Maps charger to the parking cost
     */
    @FXML
    protected final TableColumn<Charger, Boolean>
            carparkCostCol = new TableColumn<>("Carpark Cost");

    /**
     * Maps charger to the time limit
     */
    @FXML
    protected final TableColumn<Charger, Double> timeLimitCol = new TableColumn<>("Time limit");

    /**
     * Maps charger to the has attraction
     */
    @FXML
    protected final TableColumn<Charger, Boolean>
            attractionCol = new TableColumn<>("Has Attraction");

    /**
     * Maps charger to the latitude
     */
    @FXML
    protected final TableColumn<Charger, Double> latitudeCol = new TableColumn<>(
            "Latitude coordinate");

    /**
     * Maps charger to the longitude
     */
    @FXML
    protected final TableColumn<Charger, Double> longitudeCol = new TableColumn<>(
            "Longitude coordinate");

    /**
     * Maps charger to the date opened
     */

    @FXML
    protected final TableColumn<Charger, String> openCol = new TableColumn<>(
            "Date open");

    /**
     * Maps charger to the charging cost
     */

    @FXML
    protected final TableColumn<Charger, Boolean> chargcostCol = new TableColumn<>(
            "Charging cost");

    /**
     * Maps charger to the connector currents
     */

    @FXML
    protected final TableColumn<Charger, String> currentsCol = new TableColumn<>(
            "Current types");


    /**
     * Unused constructor
     */
    public TableController() {
        // unused
    }

    /**
     * Initialize the window
     */
    public void init() {
        manage = new TableManager();
        manage.resetQuery();
        tableMaker();
        manage.makeAllChargers();
        addToDisplay(manage.getData());
        change();
        setIdForTesting();
    }

    /**
     * Sets the id of each column so they can all be fx tested
     */
    public void setIdForTesting() {
        idCol.setId("idCol");
        xposCol.setId("xposCol");
        yposCol.setId("yposCol");
        operatorCol.setId("operatorCol");
        addressCol.setId("addressCol");
        ownerCol.setId("ownerCol");
        hoursCol.setId("hoursCol");
        carparkCol.setId("carparkCol");
        carparkCostCol.setId("carparkCostCol");
        timeLimitCol.setId("timeLimitCol");
        attractionCol.setId("attractionCol");
        latitudeCol.setId("latitudeCol");
        longitudeCol.setId("longitudeCol");
        openCol.setId("openCol");
        chargcostCol.setId("chargcostCol");
        currentsCol.setId("currentsCol");
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

        // Checks if the user wants to view the column
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
     *
     * @param chargersToAdd Observable list of charger objects
     */
    public void addToDisplay(ObservableList<Charger> chargersToAdd) {
        mainTable.getItems().clear();
        mainTable.setItems(chargersToAdd);
        idCol.setCellValueFactory(charger -> new ReadOnlyIntegerWrapper(
                charger.getValue().getChargerId()).asObject());
        xposCol.setCellValueFactory(
                charger -> new ReadOnlyDoubleWrapper(
                        charger.getValue().getLocation().getXpos()).asObject());
        yposCol.setCellValueFactory(
                charger -> new ReadOnlyDoubleWrapper(
                        charger.getValue().getLocation().getYpos()).asObject());
        operatorCol.setCellValueFactory(
                charger -> new ReadOnlyStringWrapper(charger.getValue().getOperator()));
        addressCol.setCellValueFactory(
                charger -> new ReadOnlyStringWrapper(
                        charger.getValue().getLocation().getAddress()));
        ownerCol.setCellValueFactory(
                charger -> new ReadOnlyStringWrapper(charger.getValue().getOwner()));
        hoursCol.setCellValueFactory(
                charger -> new ReadOnlyBooleanWrapper(charger.getValue().getAvailable24Hrs()));
        carparkCol.setCellValueFactory(
                charger -> new ReadOnlyIntegerWrapper(
                        charger.getValue().getAvailableParks()).asObject());
        carparkCostCol.setCellValueFactory(
                charger -> new ReadOnlyBooleanWrapper(charger.getValue().getParkingCost()));
        timeLimitCol.setCellValueFactory(
                charger -> new ReadOnlyDoubleWrapper(charger.getValue().getTimeLimit()).asObject());
        attractionCol.setCellValueFactory(
                charger -> new ReadOnlyBooleanWrapper(charger.getValue().getHasAttraction()));
        latitudeCol.setCellValueFactory(
                charger -> new ReadOnlyDoubleWrapper(
                        charger.getValue().getLocation().getLat()).asObject());
        longitudeCol.setCellValueFactory(
                charger -> new ReadOnlyDoubleWrapper(
                        charger.getValue().getLocation().getLon()).asObject());
        openCol.setCellValueFactory(
                charger -> new ReadOnlyStringWrapper(charger.getValue().getDateOpened()));
        chargcostCol.setCellValueFactory(
                charger -> new ReadOnlyBooleanWrapper(charger.getValue().getChargeCost()));
        currentsCol.setCellValueFactory(
                charger -> new ReadOnlyStringWrapper(manage.getConnectors(charger.getValue())));
        mainTable.getSelectionModel().select(0);
        mainTable.getSortOrder().add(addressCol);
        mainTable.sort();
    }

    /**
     * Updates the table when a user clicks update table button
     * Checks filters and adds query accordingly and makes all chargers
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

    /**
     * Gets the manager of the table
     *
     * @return table manager class
     */
    public TableManager getManager() {
        return manage;
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
        manage.addCharger();
        if (manage.getPosition() != null) {
            new JavaScriptBridge().loadChargerEdit(null, manage.getPosition());
            updateTable();
        }
    }

    /**
     * Executes an edit on the selected charger
     */
    @FXML
    public void editCharger() {
        manage.editCharger();
        updateTable();
    }

    /**
     * Executes a delete on the selected charger
     */
    @FXML
    public void deleteCharger() {
        loadPromptScreen("Are you sure you'd like to \n"
                + "delete this charger?\n\n");
        updateTable();
    }

    /**
     * Loads a generic prompt screen pop-up {@link seng202.team3.gui.PopUpWindow}
     *
     * @param prompt a String of the instructions
     */
    public void loadPromptScreen(String prompt) {
        try {
            FXMLLoader popUp = new FXMLLoader(getClass().getResource(
                    "/fxml/generic_popup.fxml"));
            VBox root = popUp.load();
            Scene modalScene = new Scene(root);
            Stage modal = new Stage();
            modal.setScene(modalScene);
            modal.setResizable(false);
            modal.setTitle("Are you sure? ");
            modal.initModality(Modality.APPLICATION_MODAL);
            PopUpWindow popController = popUp.getController();
            popController.addPrompt(prompt);
            modal.showAndWait();
            if (popController.getClicked()) {
                manage.deleteCharger();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the selected charger upon clicking
     */
    @FXML
    public void setCharger() {
        manage.setSelectedCharger(mainTable.getSelectionModel().getSelectedItem());
    }

}
