package seng202.team3.gui;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team3.data.database.ComparisonType;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.User;
import seng202.team3.logic.JavaScriptBridge;
import seng202.team3.logic.TableManager;

/**
 * A TableController class that deals with the display of the table objects
 *
 * @author Michelle Hsieh, Matthew Doonan
 * @version 1.0.1, Sep 22
 */
public class TableController {
    /**
     * Logger
     */
    private static final Logger logManager = LogManager.getLogger();

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
    protected final TableColumn<Charger, Boolean> carparkCostCol = new TableColumn<>(
            "Carpark Cost");

    /**
     * Maps charger to the time limit
     */
    @FXML
    protected final TableColumn<Charger, Double> timeLimitCol = new TableColumn<>("Time limit");

    /**
     * Maps charger to the has attraction
     */
    @FXML
    protected final TableColumn<Charger, Boolean> attractionCol = new TableColumn<>(
            "Has Attraction");

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
     * Maps visibility checkboxes to the columns
     */
    private HashMap<CheckBox, TableColumn<Charger, ?>> colSelectionMap = null;

    /**
     * Sets mapping for columns to check boxes
     */
    public TableController() {
        // Unused
    }

    /**
     * Populates the hashmap with checkboxes -> columns
     * for visibility toggles
     */
    private void mapCheckBoxes() {
        colSelectionMap.put(showId, idCol);
        colSelectionMap.put(showOperator, operatorCol);
        colSelectionMap.put(showAddress, addressCol);
        colSelectionMap.put(showOwner, ownerCol);
        colSelectionMap.put(showHoursOpen, hoursCol);
        colSelectionMap.put(showCarparks, carparkCol);
        colSelectionMap.put(showTimeLimit, timeLimitCol);
        colSelectionMap.put(showAttraction, attractionCol);
        colSelectionMap.put(showLat, latitudeCol);
        colSelectionMap.put(showLon, longitudeCol);
        colSelectionMap.put(showOpening, openCol);
        colSelectionMap.put(showChargingCost, chargcostCol);
        colSelectionMap.put(showCurrent, currentsCol);
    }

    /**
     * Initialize the window
     */
    public void init() {
        manage = new TableManager();
    }

    /**
     * Populates the table
     */
    public void populateTable() {
        manage.resetQuery();
        tableMaker();
        manage.makeAllChargers();
        addToDisplay(manage.getData());
        change();
        setIdForTesting();
    }

    /**
     * The table from point-of-view of user
     *
     * @param user the user involved
     */
    public void setUser(User user) {
        manage.setUser(user);
    }

    /**
     * Sets the id of each column so they can all be fx tested
     */
    private void setIdForTesting() {
        idCol.setId("idCol");
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
    private void tableMaker() {
        mainTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        setColWidths(1f * Integer.MAX_VALUE * 10);
        mainTable.getColumns().removeAll(mainTable.getColumns());

        // Set up mapping between columns and checkboxes
        if (colSelectionMap == null) {
            colSelectionMap = new HashMap<>();
            mapCheckBoxes();
        }

        // Checks if the user wants to view the column
        for (Entry<CheckBox, TableColumn<Charger, ?>> check : colSelectionMap.entrySet()) {
            if (check.getKey().isSelected()) {
                mainTable.getColumns().add(check.getValue());
            }
        }

        mainTable.requestFocus();
    }

    /**
     * Sets max width of all columns
     * 
     * @param width width to set to
     */
    private void setColWidths(float width) {
        idCol.setMaxWidth(width);
        operatorCol.setMaxWidth(width);
        addressCol.setMaxWidth(width);
        ownerCol.setMaxWidth(width);
        hoursCol.setMaxWidth(width);
        carparkCol.setMaxWidth(width);
        carparkCostCol.setMaxWidth(width);
        timeLimitCol.setMaxWidth(width);
        attractionCol.setMaxWidth(width);
        latitudeCol.setMaxWidth(width);
        longitudeCol.setMaxWidth(width);
        openCol.setMaxWidth(width);
        chargcostCol.setMaxWidth(width);
        currentsCol.setMaxWidth(width);
    }

    /**
     * adds chargers to the display
     *
     * @param chargersToAdd Observable list of charger objects
     */
    private void addToDisplay(ObservableList<Charger> chargersToAdd) {
        mainTable.getItems().clear();
        mainTable.setItems(chargersToAdd);
        idCol.setCellValueFactory(charger -> new ReadOnlyIntegerWrapper(
                charger.getValue().getId()).asObject());
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
    private void change() {
        onParkingFilter.textProperty()
                .setValue("Minimum number of spaces ("
                        + Math.round(parkingLot.getValue()) + ")");
        toggleTimeLimit.textProperty()
                .setValue("Minimum time limit of ("
                        + Math.round(timeLimit.getValue()) + " minutes)");

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
    private void addCharger() {
        if (Boolean.TRUE.equals(MapHandler.isMapRequested())) {
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
                controller.init(modal, manage);
                controller.setManager(manage);
                modal.setAlwaysOnTop(true);
                modal.showAndWait();
            } catch (IOException e) {
                logManager.error(e.getMessage());
            }
        }
        manage.addCharger();

        if (manage.isAdding() || Boolean.TRUE.equals(!MapHandler.isMapRequested())) {
            new JavaScriptBridge().loadChargerEdit(null);
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
            if (Boolean.TRUE.equals(popController.getClicked())) {
                manage.deleteCharger();
            }
        } catch (IOException e) {
            logManager.error(e.getMessage());
        }
    }

    /**
     * Refreshes the table info
     */
    public void refreshTable() {
        populateTable();
    }

    /**
     * Sets the selected charger upon clicking
     */
    @FXML
    public void setCharger() {
        manage.setSelectedCharger(mainTable.getSelectionModel().getSelectedItem());
    }

}
