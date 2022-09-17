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
    private ArrayList<String> errors = new ArrayList<>();
    private Stage stage;

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
