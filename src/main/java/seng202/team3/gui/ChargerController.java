package seng202.team3.gui;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team3.data.database.ComparisonType;
import seng202.team3.data.database.QueryBuilder;
import seng202.team3.data.database.QueryBuilderImpl;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.Connector;
import seng202.team3.data.entity.Coordinate;
import seng202.team3.logic.GeoLocationHandler;
import seng202.team3.logic.UserManager;

/**
 * Allows you to edit a charger
 *
 * @author Michelle Hsieh
 * @version 1.0.0, Sep 22
 */
public class ChargerController {
    /**
     * Logger
     */
    private static final Logger logManager = LogManager.getLogger();

    /**
     * True if is displaying
     */
    private boolean isDisplay = false;

    /**
     * Active Charger
     */
    private Charger charger;

    /**
     * Screen object
     */
    private Stage stage;

    /**
     * Previous coordinate
     */
    private Coordinate prevCoordinate;

    /**
     * Connectors attached to the charger
     */
    private ObservableList<Connector> connectors;

    /**
     * Handler for error message tooltips
     */
    private ErrorHandler errors = new ErrorHandler();

    /**
     * The AnchorPane for the connector edits
     */
    @FXML
    private BorderPane connectorEdit;

    /**
     * Field for the name of the charger
     */
    @FXML
    private TextField name;

    /**
     * Field for the number of parks
     */
    @FXML
    private TextField parks;

    /**
     * Field for the time limit of the charger
     */
    @FXML
    private TextField time;

    /**
     * Field for the owner of the charger
     */
    @FXML
    private Label owner;

    /**
     * Field for the operator of the charger
     */
    @FXML
    private TextField operator;

    /**
     * Field for the address of the charger
     */
    @FXML
    private TextField address;

    /**
     * Field for the 24hr status of the charger
     */
    @FXML
    private CheckBox open24;

    /**
     * Field for the parking cost of the charger
     */
    @FXML
    private CheckBox costParks;

    /**
     * Field for the charger cost of the charger
     */
    @FXML
    private CheckBox cost;

    /**
     * Field for the nearby tourist attractions of the charger
     */
    @FXML
    private CheckBox attractions;

    /**
     * The latitude of the coordinate
     */
    @FXML
    private TextField lat;

    /**
     * The longitude of the coordinate
     */
    @FXML
    private TextField lon;

    /**
     * The lable for the connector section of the window
     */
    @FXML
    private Label connectorPageLabel;

    /**
     * Invalid vehicle lable
     */
    @FXML
    private Label invalidChargerLabel;

    /**
     * Column mapping connectors to their currents
     */
    @FXML
    private TableColumn<Connector, String> current;

    /**
     * Column mapping connectors to their power draw
     */
    @FXML
    private TableColumn<Connector, String> wattage;

    /**
     * Column mapping connectors to their charging points
     */
    @FXML
    private TableColumn<Connector, Integer> chargingPoints;

    /**
     * Column mapping connectors to their types
     */
    @FXML
    private TableColumn<Connector, String> connectorTypes;

    /**
     * Column mapping connectors to their operative status
     */
    @FXML
    private TableColumn<Connector, String> status;

    /**
     * Table view of connectors
     */
    @FXML
    private TableView<Connector> connectorTable;

    /**
     * The add connector button
     */
    @FXML
    private Button addConnectorButton;

    /**
     * The edit connector button
     */
    @FXML
    private Button editConnectorButton;

    /**
     * The delete connector button
     */
    @FXML
    private Button deleteConnectorButton;

    /**
     * The delete charger button
     */
    @FXML
    private Button deleteButton;

    /**
     * Styling for invalid fields
     */
    private static final String INVALID_STYLE = "-fx-border-color: #ff0000;";

    /**
     * Styling for valid fields
     */
    private static final String VALID_STYLE = "-fx-border-color: default;";

    /**
     * Initialises the ChargerController, loading in the charger info
     */
    public ChargerController() {
        // Unused
    }

    /**
     * Initialises this controller
     *
     * @param stage the stage this controller is on
     */
    public void init(Stage stage) {
        this.stage = stage;
        makeConnectors();
        connectorTable.setItems(connectors);
        prevCoordinate = GeoLocationHandler.getCoordinate();
        displayChargerInfo();
    }

    /**
     * Sets the current coordinate
     *
     * @param coordinate the current coordinate selected
     */
    public void setCoordinate(Coordinate coordinate) {
        prevCoordinate = coordinate;
    }

    /**
     * Sets the Charger being edited
     *
     * @param charger {@link seng202.team3.data.entity.Charger} the charger being
     *                edited
     */
    public void setCharger(Charger charger) {
        this.charger = charger;
    }

    /**
     * Gets the Charger being returned
     *
     * @return {@link Charger} the charger that is returned
     */
    public Charger getCharger() {
        return this.charger;
    }

    /**
     * Adds a coordinate with a specified name and closes the box
     */
    @FXML
    public void displayChargerInfo() {
        if (charger != null) {
            name.setText(charger.getName());
            parks.setText(Integer.toString(charger.getAvailableParks()));
            address.setText(charger.getLocation().getAddress());
            time.setText(Double.toString(charger.getTimeLimit()));
            operator.setText(charger.getOperator());
            owner.setText(charger.getOwner());
            lat.setText(Double.toString(charger.getLocation().getLat()));
            lon.setText(Double.toString(charger.getLocation().getLon()));
            if (charger.getAvailable24Hrs()) {
                open24.setSelected(true);
            }
            if (charger.getChargeCost()) {
                cost.setSelected(true);
            }
            if (charger.getHasAttraction()) {
                attractions.setSelected(true);
            }
            if (charger.getParkingCost()) {
                costParks.setSelected(true);
            }
        } else {
            address.setText(prevCoordinate.getAddress());
            if (GeoLocationHandler.getCoordinate() != GeoLocationHandler.DEFAULT_COORDINATE) {
                lat.setText(Double.toString(GeoLocationHandler.getCoordinate().getLat()));
                lon.setText(Double.toString(GeoLocationHandler.getCoordinate().getLon()));
            }
            owner.setText(UserManager.getUser().getAccountName());
            deleteButton.setOpacity(0.0);
        }
        displayConnectorInfo();
    }

    /**
     * Saves the changes; if new, will use the coordinates to make an entry
     */
    @FXML
    public void saveChanges() {
        Charger newCharger = new Charger();
        Coordinate coordinate = new Coordinate();
        coordinate.setXpos(0.0);
        coordinate.setYpos(0.0);

        errors.hideAll();
        lat.setStyle(VALID_STYLE);
        lon.setStyle(VALID_STYLE);
        name.setStyle(VALID_STYLE);
        parks.setStyle(VALID_STYLE);
        address.setStyle(VALID_STYLE);
        time.setStyle(VALID_STYLE);

        Boolean fail = false;

        try {
            coordinate.setLat(Double.parseDouble(lat.getText()));
            coordinate.setLon(Double.parseDouble(lon.getText()));
        } catch (NumberFormatException | NullPointerException e) {
            errors.add("chargerCoordFormatLat", lat, "Coordinate is not valid number");
            errors.add("chargerCoordFormatLon", lon, "Coordinate is not valid number");
            lat.setStyle(INVALID_STYLE);
            lon.setStyle(INVALID_STYLE);
            fail = true;
        }

        if (charger == null) {
            newCharger.setOwnerId(UserManager.getUser().getUserid());
            newCharger.setDateOpened(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
                    .format(Date.from(Instant.now())));
        } else {
            Coordinate location = charger.getLocation();
            coordinate.setLat(location.getLat());
            coordinate.setLon(location.getLon());
            newCharger.setOwnerId(charger.getOwnerId());
            newCharger.setDateOpened(charger.getDateOpened());
            newCharger.setChargerId(charger.getChargerId());
        }
        coordinate.setAddress(address.getText());
        if (address.getText().length() == 0) {
            errors.add("chargerAddressRequired", address,
                "Needs an address, e.g. 132 Science Road, Christchurch");
            address.setStyle(INVALID_STYLE);
            fail = true;
        }
        newCharger.setLocation(coordinate);
        newCharger.setOperator(operator.getText());
        newCharger.setName(name.getText());
        if (name.getText().length() == 0) {
            errors.add("chargerNameRequired", name, "Needs a name, e.g. Home");
            name.setStyle(INVALID_STYLE);
            fail = true;
        }
        newCharger.setAvailable24Hrs(open24.isSelected());
        newCharger.setChargeCost(cost.isSelected());
        newCharger.setParkingCost(costParks.isSelected());
        newCharger.setHasAttraction(attractions.isSelected());

        try {
            newCharger.setTimeLimit(Double.parseDouble(time.getText()));
        } catch (NullPointerException e) {
            time.setStyle(INVALID_STYLE);
            errors.add("chargerTimeRequired", time, "Needs a Time Limit");
            fail = true;
        } catch (NumberFormatException e) {
            time.setStyle(INVALID_STYLE);
            errors.add("chargerTimeInvalid", time, "Time Limit is not a valid number");
            fail = true;
        }

        try {
            newCharger.setAvailableParks(Integer.parseInt(parks.getText()));
        } catch (NumberFormatException e) {
            errors.add("chargerParksFormat", parks, "A Charger must have a whole number of parks");
            parks.setStyle(INVALID_STYLE);
            fail = true;
        }

        for (Connector connector : connectors) {
            newCharger.addConnector(connector);
        }

        if (connectors.isEmpty()) {
            errors.add("chargerConnectorRequired", addConnectorButton,
                "A Charger must have at least one Connector!");
            addConnectorButton.setStyle(INVALID_STYLE);
            fail = true;
        }

        if (Boolean.FALSE.equals(fail)) {
            try {
                SqlInterpreter.getInstance().writeCharger(newCharger);
                logManager.info("Added new Charger");
                stage.close();
            } catch (IOException e) {
                logManager.error(e.getMessage());
            }
        } else {
            invalidChargerLabel.setVisible(true);
        }
    }

    /**
     * Makes a list of observable chargers
     *
     */
    public void makeConnectors() {
        ArrayList<Connector> connectArray = new ArrayList<>();
        if (charger != null) {
            QueryBuilder query = new QueryBuilderImpl().withSource("connector")
                    .withFilter("chargerid", Integer.toString(charger.getChargerId()),
                            ComparisonType.EQUAL);
            try {
                for (Object object : SqlInterpreter.getInstance()
                        .readData(query.build(), Connector.class)) {
                    connectArray.add((Connector) object);
                }
            } catch (IOException e) {
                logManager.error(e.getMessage());
            }
        }
        connectors = (FXCollections.observableList(connectArray));
    }

    /**
     * Sets the connectors appropriately
     *
     * @param connectors an ObservableList of Connectors
     */
    public void setConnectors(ObservableList<Connector> connectors) {
        this.connectors = connectors;
    }

    /**
     * Gets the connectors
     *
     * @return a list of {@link Connector}
     */
    public ObservableList<Connector> getConnectors() {
        return connectors;
    }

    /**
     * Displays all the connector information in the table
     */
    public void displayConnectorInfo() {

        current.setCellValueFactory(new PropertyValueFactory<>("current"));

        wattage.setCellValueFactory(new PropertyValueFactory<>("power"));

        chargingPoints.setCellValueFactory(new PropertyValueFactory<>("count"));

        connectorTypes.setCellValueFactory(new PropertyValueFactory<>("type"));

        status.setCellValueFactory(new PropertyValueFactory<>("status"));

    }

    /**
     * Opens a new add section
     */
    @FXML
    public void addConnector() {
        if (!isDisplay) {
            hideButtons();
            isDisplay = true;
            launchEditable(null);
        }
    }

    /**
     * Deletes a connector
     */
    @FXML
    public void deleteConnector() {
        if (!isDisplay) {
            int deletedValue = -1;
            for (int i = 0; i < connectors.size(); i++) {
                if (connectors.get(i) == connectorTable.getSelectionModel().getSelectedItem()) {
                    deletedValue = i;
                }
            }
            if (deletedValue != -1) {
                connectors.remove(deletedValue);
            }
            connectorTable.refresh();
            displayConnectorInfo();
        }
    }

    /**
     * Edits a connector
     */
    @FXML
    public void editConnector() {

        Connector connector = connectorTable.getSelectionModel().getSelectedItem();
        if (!isDisplay && connector != null) {
            hideButtons();
            isDisplay = true;
            launchEditable(connector);
        }
    }

    /**
     * Visually hides all the buttons
     */
    public void hideButtons() {
        editConnectorButton.setOpacity(0.0);
        deleteConnectorButton.setOpacity(0.0);
        addConnectorButton.setOpacity(0.0);
    }

    /**
     * Launches the editable portion
     *
     * @param connector the {@link seng202.team3.data.entity.Connector} for the
     *                  connector info. Null if
     *                  adding.
     */
    public void launchEditable(Connector connector) {
        try {
            FXMLLoader connectorEditRoot = new FXMLLoader(getClass().getResource(
                    "/fxml/connector_info.fxml"));
            BorderPane root = connectorEditRoot.load();
            ConnectorEditController controller = connectorEditRoot.getController();
            controller.setController(this);
            controller.addConnector(connector);
            connectorEdit.setCenter(root);
            controller.init();
            controller.displayInfo();
        } catch (IOException e) {
            logManager.error(e.getMessage());
        }
    }

    /**
     * Resets the page
     */
    public void resetPage() {
        editConnectorButton.setOpacity(100);
        deleteConnectorButton.setOpacity(100);
        addConnectorButton.setOpacity(100);
        connectorEdit.setCenter(new BorderPane());
        isDisplay = false;
    }

    /**
     * Launches the confirmation screen
     */
    @FXML
    public void launchConfirmation() {
        if (charger != null) {
            stage.setAlwaysOnTop(false);
            try {
                FXMLLoader popUp = new FXMLLoader(getClass().getResource(
                        "/fxml/prompt_popup.fxml"));
                VBox root = popUp.load();
                Scene modalScene = new Scene(root);
                Stage modal = new Stage();
                modal.setScene(modalScene);
                modal.setResizable(false);
                modal.setTitle("Click or cancel:");
                modal.initModality(Modality.APPLICATION_MODAL);
                PromptPopUp popController = popUp.getController();
                popController.setType("delete");
                popController.addPrompt("Are you sure you want to DELETE this "
                        + "\n charger? \n\n");
                modal.showAndWait();
            } catch (IOException e) {
                logManager.error(e.getMessage());
            } finally {
                stage.close();
            }
        }
    }

    /**
     * Gets error handling object for tooltip messages
     * 
     * @return errorhandler with entry field tooltip alerts
     */
    public ErrorHandler getErrors() {
        return errors;
    }
}
