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
import javafx.scene.layout.AnchorPane;
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
import seng202.team3.data.entity.Entity;
import seng202.team3.data.entity.EntityType;
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
     * String list of errors to display
     */
    private ArrayList<String> errors = new ArrayList<>();

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
     * Invisible label to store views for the new charger object
     */
    @FXML
    private Label views;

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
            views.setText("" + charger.getViews());
        } else {
            address.setText(prevCoordinate.getAddress());
            if (GeoLocationHandler.getCoordinate() != GeoLocationHandler.DEFAULT_COORDINATE) {
                lat.setText(Double.toString(GeoLocationHandler.getCoordinate().getLat()));
                lon.setText(Double.toString(GeoLocationHandler.getCoordinate().getLon()));
            }
            owner.setText(UserManager.getUser().getAccountName());
            deleteButton.setOpacity(0.0);
            views.setText("0");
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

        try {
            coordinate.setLat(Double.parseDouble(lat.getText()));
            coordinate.setLon(Double.parseDouble(lon.getText()));
        } catch (NumberFormatException | NullPointerException e) {
            errors.add("Coordinate is not valid number");
        }

        if (charger == null) {
            newCharger.setOwnerId(UserManager.getUser().getId());
            newCharger.setDateOpened(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
                    .format(Date.from(Instant.now())));
        } else {
            Coordinate location = charger.getLocation();
            coordinate.setLat(location.getLat());
            coordinate.setLon(location.getLon());
            newCharger.setOwnerId(charger.getOwnerId());
            newCharger.setDateOpened(charger.getDateOpened());
            newCharger.setId(charger.getId());
        }
        coordinate.setAddress(address.getText());
        if (address.getText().length() == 0) {
            errors.add("Needs an address, e.g. 132 Science Road, Christchurch");
        }
        newCharger.setLocation(coordinate);
        newCharger.setOperator(operator.getText());
        newCharger.setName(name.getText());
        if (name.getText().length() == 0) {
            errors.add("Needs a name, e.g. Home");
        }
        newCharger.setAvailable24Hrs(open24.isSelected());
        newCharger.setChargeCost(cost.isSelected());
        newCharger.setParkingCost(costParks.isSelected());
        newCharger.setHasAttraction(attractions.isSelected());
        newCharger.setViews(Integer.parseInt(views.getText()));

        try {
            newCharger.setTimeLimit(Double.parseDouble(time.getText()));
        } catch (NullPointerException e) {
            errors.add("Needs a Time Limit");
        } catch (NumberFormatException e) {
            errors.add("Time Limit is not a valid number");
        }

        try {
            newCharger.setAvailableParks(Integer.parseInt(parks.getText()));
        } catch (NumberFormatException e) {
            errors.add("A Charger must have a whole number of parks");
        }

        for (Connector connector : connectors) {
            newCharger.addConnector(connector);
        }

        if (connectors.isEmpty()) {
            errors.add("A Charger must have at least one Connector!");
        }

        if (errors.isEmpty()) {
            try {
                SqlInterpreter.getInstance().writeCharger(newCharger);
                logManager.info("Added new Charger");
                stage.close();
            } catch (IOException e) {
                logManager.error(e.getMessage());
            }
        } else {
            launchErrorPopUps();
            errors.clear();
        }
    }

    /**
     * Makes a list of observable chargers
     *
     */
    public void makeConnectors() {
        ArrayList<Connector> connectArray = new ArrayList<>();
        if (charger != null) {
            QueryBuilder query = new QueryBuilderImpl().withSource(EntityType.CONNECTOR)
                    .withFilter("chargerid", Integer.toString(charger.getId()),
                            ComparisonType.EQUAL);
            try {
                for (Entity object : SqlInterpreter.getInstance()
                        .readData(query.build())) {
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
     * Launches an error popup when trying to do illegal things
     */
    public void launchErrorPopUps() {
        try {
            stage.setAlwaysOnTop(false);
            FXMLLoader error = new FXMLLoader(getClass().getResource(
                    "/fxml/error_popup.fxml"));
            AnchorPane base = error.load();
            Scene modalScene = new Scene(base);
            Stage modal = new Stage();
            modal.setScene(modalScene);
            modal.setResizable(false);
            modal.setTitle("Error With Charger:");
            modal.initModality(Modality.APPLICATION_MODAL);
            ErrorController controller = error.getController();
            controller.init();
            controller.setErrors(errors);
            controller.setPromptType("error");
            controller.displayErrors();
            modal.setAlwaysOnTop(true);
            modal.showAndWait();
            for (String e : errors) {
                logManager.warn(e);
            }
        } catch (IOException e) {
            logManager.error(e.getMessage());
        }
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
}
