package seng202.team3.gui;

import java.io.IOException;
import java.text.SimpleDateFormat;
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
    private ObservableList<Connector> connectors;

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
     * Adds a coordinate with a specified name and closes the box
     */
    @FXML
    public void displayChargerInfo() {
        charger = new MenuController().getController().getManager().getSelectedCharger();
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
                open24.isSelected();
            }
            if (charger.getChargeCost()) {
                cost.isSelected();
            }
            if (charger.getHasAttraction()) {
                attractions.isSelected();
            }
            if (charger.getParkingCost()) {
                costParks.isSelected();
            }
        }
    }

    /**
     * Sets the stage for this controller
     *
     * @param stage the stage this controller is on
     */
    public void setStage(Stage stage) {
        this.stage = stage;
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
            Coordinate location = new MenuController().getController().getManager().getPosition();
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
        newCharger.setTimeLimit(Double.parseDouble(time.getText()));
        newCharger.setLocation(coordinate);
        newCharger.setOperator(operator.getText());
        newCharger.setOwner(owner.getText());
        newCharger.setName(name.getText());
        newCharger.setAvailable24Hrs(open24.isSelected());
        newCharger.setChargeCost(cost.isSelected());
        newCharger.setAvailableParks(Integer.parseInt(parks.getText()));
        for (Connector connector : connectors) {
            newCharger.addConnector(connector);
        }

        try {
            SqlInterpreter.getInstance().writeCharger(newCharger);
        } catch (IOException e) {
            e.printStackTrace();
        }

        MainController controller = new MenuController().getController();
        controller.getMapController().addChargersOnMap();
        controller.viewChargers(newCharger);
        stage.close();
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
            controller.displayConnectorInfo();
            modal.setAlwaysOnTop(true);
            modal.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
