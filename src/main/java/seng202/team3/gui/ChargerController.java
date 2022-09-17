package seng202.team3.gui;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import seng202.team3.data.database.ComparisonType;
import seng202.team3.data.database.QueryBuilder;
import seng202.team3.data.database.QueryBuilderImpl;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.Connector;
import seng202.team3.data.entity.Coordinate;

/**
 * Allows you to edit a charger
 *
 * @author Michelle Hsieh
 * @version 1.0.0, Sep 22
 */
public class ChargerController {

    private Charger charger;
    private Stage stage;
    private Coordinate prevCoordinate;
    private ObservableList<Connector> connectors;
    private ArrayList<String> errors = new ArrayList<>();

    @FXML
    private TextField name;
    @FXML
    private TextField parks;
    @FXML
    private TextField time;
    @FXML
    private TextField owner;
    @FXML
    private TextField operator;
    @FXML
    private TextField currents;
    @FXML
    private TextField address;
    @FXML
    private CheckBox open24;
    @FXML
    private CheckBox costParks;
    @FXML
    private CheckBox cost;
    @FXML
    private CheckBox attractions;


    /**
     * Initialises the ChargerController, loading in the charger info
     */
    public ChargerController() {
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
     * @param charger {@link Charger} the charger being edited
     */
    public void setCharger(Charger charger) {
        this.charger = charger;
    }

    /**
     * Adds a coordinate with a specified name and closes the box
     */
    @FXML
    public void displayChargerInfo() {
        if (charger != null) {
            connectors = FXCollections.observableList(charger.getConnectors());
            name.setText(charger.getName());
            parks.setText(Integer.toString(charger.getAvailableParks()));
            address.setText(charger.getLocation().getAddress());
            currents.setText(charger.getCurrentType());
            time.setText(Double.toString(charger.getTimeLimit()));
            owner.setText(charger.getOwner());
            operator.setText(charger.getOperator());
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
        }
    }

    /**
     * Initialises this controller
     *
     * @param stage the stage this controller is on
     */
    public void init(Stage stage) {
        this.stage = stage;
        setConnectors(getConnectors());
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
        if (charger == null) {
            Coordinate location = prevCoordinate;
            coordinate.setLon(location.getLon());
            coordinate.setLat(location.getLat());
            newCharger.setDateOpened(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").toString());
        } else {
            Coordinate location = charger.getLocation();
            coordinate.setLat(location.getLat());
            coordinate.setLon(location.getLon());
            newCharger.setDateOpened(charger.getDateOpened());
            newCharger.setChargerId(charger.getChargerId());
        }
        coordinate.setAddress(address.getText());
        newCharger.setLocation(coordinate);
        newCharger.setOperator(operator.getText());
        newCharger.setOwner(owner.getText());
        newCharger.setName(name.getText());
        newCharger.setAvailable24Hrs(open24.isSelected());
        newCharger.setChargeCost(cost.isSelected());
        newCharger.setParkingCost(costParks.isSelected());
        newCharger.setHasAttraction(attractions.isSelected());

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

        if (connectors.size() == 0) {
            errors.add("A Charger must have at least one Connector!");
        }

        if (errors.size() == 0) {
            try {
                SqlInterpreter.getInstance().writeCharger(newCharger);
                stage.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            launchErrorPopUps();
            errors.clear();
        }
    }

    /**
     * Gets the connectors from the charger
     *
     * @return an ObservableList of {@link Connector}s
     */
    public ObservableList<Connector> getConnectors() {
        ArrayList<Connector> connectArray =  new ArrayList<>();
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
                e.printStackTrace();
            }
        }
        return (FXCollections.observableList(connectArray));
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
     * Initialises the editConnectors screen
     */
    @FXML
    public void editConnectors() {
        try {
            stage.setAlwaysOnTop(false);
            FXMLLoader connectorCont = new FXMLLoader(getClass().getResource(
                    "/fxml/connectors.fxml"));
            AnchorPane root = connectorCont.load();
            Scene modalScene = new Scene(root);
            Stage modal = new Stage();
            modal.setScene(modalScene);
            modal.setResizable(false);
            modal.setTitle("Connector Information");
            modal.initModality(Modality.WINDOW_MODAL);
            ConnectorController controller = connectorCont.getController();
            controller.setConnectorList(connectors);
            controller.displayConnectorInfo();
            modal.setAlwaysOnTop(true);
            modal.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }

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
            modal.initModality(Modality.WINDOW_MODAL);
            ErrorController controller = error.getController();
            controller.init();
            controller.setErrors(errors);
            controller.setPromptType("error");
            controller.displayErrors();
            modal.setAlwaysOnTop(true);
            modal.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
