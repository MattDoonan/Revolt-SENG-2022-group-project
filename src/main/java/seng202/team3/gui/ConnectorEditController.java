package seng202.team3.gui;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import seng202.team3.data.entity.Connector;

/**
 * A connector editing controller
 *
 * @author Michelle Hsieh
 * @version 1.0.0, Sep 22
 */
public class ConnectorEditController {

    @FXML
    private TextField current;

    @FXML
    private TextField wattage;

    @FXML
    private TextField chargingPoints;

    @FXML
    private TextField type;

    @FXML
    private TextField status;

    private Connector connector;
    private ConnectorController controller;

    /**
     * Initialises the Controller editing
     */
    public ConnectorEditController() {
    }

    /**
     * Initialises the ConnectorEditController with the selected connector
     *
     * @param connector a {@link Connector} if it's preexisting
     */
    public void addConnector(Connector connector) {
        this.connector = connector;
    }

    /**
     * Displays all the info of the connector, if there is a connector
     */
    public void displayInfo() {
        if (connector != null) {
            current.setText(connector.getCurrent());
            wattage.setText(connector.getPower());
            chargingPoints.setText(Integer.toString(connector.getCount()));
            type.setText(connector.getType());
            status.setText(connector.getStatus());
        }
    }

    /**
     * Sets the ConnectorController holding all the controllers
     *
     * @param controller ConnectorController controller
     */
    public void setController(ConnectorController controller) {
        this.controller = controller;
    }

    /**
     * Saves the changes and closes this window if necessary
     */
    @FXML
    public void saveChanges() {
        Connector changedConnector;
        String typeString = type.getText();
        String powerString = wattage.getText();
        String statusString = status.getText();
        String currentString = current.getText();
        int points = Integer.parseInt(chargingPoints.getText());

        if (connector == null) {
            changedConnector = new Connector(typeString, powerString,
                    statusString, currentString, points);
            controller.getConnectorList().add(changedConnector);
        } else {
            changedConnector = new Connector(typeString,
                    powerString, statusString, currentString, points, connector.getId());
        }
        for (int i = 0; i < controller.getConnectorList().size(); i++) {
            if (controller.getConnectorList().get(i) == connector) {
                controller.getConnectorList().set(i, changedConnector);
            }
        }
        Stage stage = (Stage) status.getScene().getWindow();
        stage.close();
    }

}
