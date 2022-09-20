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
import seng202.team3.data.entity.Connector;

/**
 * A connector editing controller
 *
 * @author Michelle Hsieh
 * @version 1.0.0, Sep 22
 */
public class ConnectorEditController {

    /**
     * Field to contain the current of the connector
     */
    @FXML
    private TextField current;

    /**
     * Field to contain the power draw
     */
    @FXML
    private TextField wattage;

    /**
     * Field to contain the number of connections
     */
    @FXML
    private TextField chargingPoints;

    /**
     * Field to contain the type of connector
     */
    @FXML
    private TextField type;

    /**
     * Field to contain the operative status
     */
    @FXML
    private TextField status;

    /**
     * Active connector being edited
     */
    private Connector connector;

    /**
     * Controller managing the connectors
     */
    private ConnectorController controller;

    /**
     * List of errors to display
     */
    private ArrayList<String> errors = new ArrayList<>();

    /**
     * Active screen
     */
    private Stage stage;

    /**
     * Initialises the Controller editing
     */
    public ConnectorEditController() {
        // unused
    }

    /**
     * Initialises the ConnectorEditController with the selected connector
     *
     * @param connector a {@link seng202.team3.data.entity.Connector} if it's preexisting
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
        int points = 0;
        try {
            points = Integer.parseInt(chargingPoints.getText());
        } catch (NumberFormatException e) {
            errors.add("Number of Charging Points needs to be an integer");
        }

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
        if (errors.isEmpty()) {
            stage = (Stage) status.getScene().getWindow();
            stage.close();
        }
        launchErrorPopUps();
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
            modal.setTitle("Error With Connectors:");
            modal.initModality(Modality.WINDOW_MODAL);
            ErrorController errController = error.getController();
            errController.init();
            errController.setErrors(errors);
            errController.setPromptType("error");
            errController.displayErrors();
            modal.setAlwaysOnTop(true);
            modal.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
