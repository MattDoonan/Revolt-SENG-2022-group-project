package seng202.team3.gui;

import java.io.IOException;
import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
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
     * Handler for error message tooltips
     */
    private ErrorHandler errors = new ErrorHandler();

    /**
     * Styling for invalid fields
     */
    private static final Border INVALID_STYLE = new Border(
        new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, 
            CornerRadii.EMPTY, BorderWidths.DEFAULT));

    /**
     * Styling for valid fields
     */
    private static final Border VALID_STYLE = new Border(
        new BorderStroke(Color.TRANSPARENT, BorderStrokeStyle.SOLID, 
            CornerRadii.EMPTY, BorderWidths.DEFAULT));

    /**
     * id for type node
     */
    private static final String TYPE_NODE = "typeField";

    /**
     * id for status node
     */
    private static final String STATUS_NODE = "statusField";

    /**
     * id for wattage node
     */
    private static final String WATT_NODE = "wattageField";

    /**
     * id for current node
     */
    private static final String CURR_NODE = "currentField";

    /**
     * id for charging points node
     */
    private static final String POINTS_NODE = "chargingPointsField";

    /**
     * Initialises the Controller editing
     */
    public ConnectorEditController() {
        // unused
    }

    /**
     * Initialises this controller
     *
     */
    public void init() {
        errors.add(POINTS_NODE,
                "Number of Charging Points needs to be an integer");
        errors.add(CURR_NODE, "Must have a current, e.g. AC or DC");
        errors.add(WATT_NODE, "Must have a wattage, e.g. 24 kW");
        errors.add(STATUS_NODE, "Must have a status, e.g. Operative");
        errors.add(TYPE_NODE, "Must have a charger type e.g. CHAdeMO");
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
        int points = 0;
        Boolean fail = false;
        errors.hideAll();
        chargingPointsField.setBorder(VALID_STYLE);
        currentField.setBorder(VALID_STYLE);
        wattageField.setBorder(VALID_STYLE);
        statusField.setBorder(VALID_STYLE);
        typeField.setBorder(VALID_STYLE);
        try {
            points = Integer.parseInt(chargingPointsField.getText());
        } catch (NumberFormatException e) {
            chargingPointsField.setBorder(INVALID_STYLE);
            errors.show(POINTS_NODE);
            fail = true;
        }
        String currentString = currentField.getText();
        if (currentString.length() == 0) {
            currentField.setBorder(INVALID_STYLE);
            errors.show(CURR_NODE);
            fail = true;
        }
        String powerString = wattageField.getText();
        if (powerString.length() == 0) {
            wattageField.setBorder(INVALID_STYLE);
            errors.show(WATT_NODE);
            fail = true;
        }
        String statusString = statusField.getText();
        if (statusString.length() == 0) {
            statusField.setBorder(INVALID_STYLE);
            errors.show(STATUS_NODE);
            fail = true;
        }
        String typeString = typeField.getText();
        if (typeString.length() == 0) {
            typeField.setBorder(INVALID_STYLE);
            errors.show(TYPE_NODE);
            fail = true;
        }
        if (Boolean.FALSE.equals(fail)) {
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
        }
    }

    /**
     * Cancels the edit connector portion
     */
    @FXML
    public void cancel() {
        controller.resetPage();
    }

}
