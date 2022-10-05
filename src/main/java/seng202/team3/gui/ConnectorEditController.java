package seng202.team3.gui;

import java.io.IOException;
import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team3.data.entity.Connector;

/**
 * A connector editing controller
 *
 * @author Michelle Hsieh
 * @version 1.0.0, Sep 22
 */
public class ConnectorEditController {
    /**
     * Logger
     */
    private static final Logger logManager = LogManager.getLogger();

    /**
     * Field to contain the current of the connector
     */
    @FXML
    private TextField currentField;

    /**
     * Field to contain the power draw
     */
    @FXML
    private TextField wattageField;

    /**
     * Field to contain the number of connections
     */
    @FXML
    private TextField chargingPointsField;

    /**
     * Field to contain the type of connector
     */
    @FXML
    private TextField typeField;

    /**
     * Field to contain the operative status
     */
    @FXML
    private TextField statusField;

    /**
     * Active connector being edited
     */
    private Connector connector;

    /**
     * Controller managing the chargers
     */
    private ChargerController controller;

    /**
     * List of errors to display
     */
    private ArrayList<String> errors = new ArrayList<>();

    /**
     * Initialises the Controller editing
     */
    public ConnectorEditController() {
        // unused
    }

    /**
     * Initialises the ConnectorEditController with the selected connector
     *
     * @param connector a {@link seng202.team3.data.entity.Connector} if it's
     *                  preexisting
     */
    public void addConnector(Connector connector) {
        this.connector = connector;
    }

    /**
     * Displays all the info of the connector, if there is a connector
     */
    public void displayInfo() {
        if (connector != null) {
            currentField.setText(connector.getCurrent());
            wattageField.setText(connector.getPower());
            chargingPointsField.setText(Integer.toString(connector.getCount()));
            typeField.setText(connector.getType());
            statusField.setText(connector.getStatus());
        }
    }

    /**
     * Sets the ChargerController associated with this
     *
     * @param controller ChargerController controller
     */
    public void setController(ChargerController controller) {
        this.controller = controller;
    }

    /**
     * Saves the changes and closes this window if necessary
     */
    @FXML
    public void saveConnection() {
        Connector changedConnector;
        String powerString = wattageField.getText();
        String currentString = currentField.getText();
        int points = 0;
        try {
            points = Integer.parseInt(chargingPointsField.getText());
        } catch (NumberFormatException e) {
            errors.add("Number of Charging Points needs to be an integer");
        }
        if (currentString.length() == 0) {
            errors.add("Must have a current, e.g. AC or DC");
        }
        if (powerString.length() == 0) {
            errors.add("Must have a wattage, e.g. 24 kW");
        }
        String statusString = statusField.getText();
        if (statusString.length() == 0) {
            errors.add("Must have a status, e.g. Operative");
        }
        String typeString = typeField.getText();
        if (typeString.length() == 0) {
            errors.add("Must have a charger type e.g. CHAdeMO");
        }
        if (errors.isEmpty()) {
            if (connector == null) {
                changedConnector = new Connector(typeString, powerString,
                        statusString, currentString, points);
                controller.getConnectors().add(changedConnector);
            } else {
                changedConnector = new Connector(typeString,
                        powerString, statusString, currentString, points, connector.getId());
            }

            for (int i = 0; i < controller.getConnectors().size(); i++) {
                if (controller.getConnectors().get(i) == connector) {
                    controller.getConnectors().set(i, changedConnector);
                }
            }

            controller.resetPage();
        } else {
            launchErrorPopUps();
            errors.clear();
        }
    }

    /**
     * Cancels the edit connector portion
     */
    @FXML
    public void cancel() {
        controller.resetPage();
    }

    /**
     * Launches an error popup when trying to do illegal things
     */
    public void launchErrorPopUps() {
        Stage stage = (Stage) statusField.getScene().getWindow();
        try {
            stage.setAlwaysOnTop(false);
            FXMLLoader error = new FXMLLoader(getClass().getResource(
                    "/fxml/error_popup.fxml"));
            AnchorPane base = error.load();
            Scene modalScene = new Scene(base);
            Stage modal = new Stage();
            modal.setScene(modalScene);
            modal.setResizable(false);
            modal.setTitle("Error With Connectors:");
            modal.initModality(Modality.APPLICATION_MODAL);
            ErrorController errController = error.getController();
            errController.init();
            errController.setErrors(errors);
            errController.setPromptType("error");
            errController.displayErrors();
            modal.setAlwaysOnTop(true);
            modal.showAndWait();

            for (String e : errors) {
                logManager.warn(e);
            }
        } catch (IOException e) {
            logManager.error(e.getMessage());
        }
    }

}
