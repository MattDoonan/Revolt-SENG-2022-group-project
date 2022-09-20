package seng202.team3.gui;

import java.io.IOException;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import seng202.team3.data.entity.Connector;

/**
 * The connector controller class which operates the connector changes
 *
 * @author Michelle Hsieh
 * @version 1.0.0, Sep 22
 */
public class ConnectorController {

    /**
     * List of editable connectors
     */
    private ObservableList<Connector> connectorList;

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
     * Screen for displaying connector editing
     */
    private Stage stage;

    /**
     * Unused constructor
     */
    public ConnectorController() {
        // Unused
    }

    /**
     * Displays all the connector information in the table
     */
    public void displayConnectorInfo() {
        connectorTable.setItems(connectorList);

        current.setCellValueFactory(new PropertyValueFactory<>("current"));

        wattage.setCellValueFactory(new PropertyValueFactory<>("power"));

        chargingPoints.setCellValueFactory(new PropertyValueFactory<>("count"));

        connectorTypes.setCellValueFactory(new PropertyValueFactory<>("type"));

        status.setCellValueFactory(new PropertyValueFactory<>("status"));

    }

    /**
     * Sets the list of connectors currently being edited.
     *
     * @param connectorList an observablelist of Connectors
     */
    public void setConnectorList(ObservableList<Connector> connectorList) {
        this.connectorList = connectorList;
    }

    /**
     * Gets the connectorlist
     *
     * @return and observable list of {@link seng202.team3.data.entity.Connector}s
     */
    public ObservableList<Connector> getConnectorList() {
        return connectorList;
    }

    /**
     * Opens a new add window
     */
    @FXML
    public void addConnector() {
        launchEditable(null);
    }

    /**
     * Deletes a connector
     */
    @FXML
    public void deleteConnector() {
        int deletedValue = -1;
        for (int i = 0; i < connectorList.size(); i++) {
            if (connectorList.get(i) == connectorTable.getSelectionModel().getSelectedItem()) {
                deletedValue = i;
            }
        }
        if (deletedValue != -1) {
            connectorList.remove(deletedValue);
        }
        connectorTable.refresh();
        displayConnectorInfo();
    }

    /**
     * Edits a connector
     */
    @FXML
    public void editConnector() {

        Connector connector = connectorTable.getSelectionModel().getSelectedItem();
        if (connector != null) {
            launchEditable(connector);
        }
    }

    /**
     * Launches the editable portion
     *
     * @param connector the {@link seng202.team3.data.entity.Connector} for the
     *                  connector info. Null if
     *                  adding.
     */
    public void launchEditable(Connector connector) {
        stage = (Stage) connectorTable.getScene().getWindow();
        try {
            stage.setAlwaysOnTop(false);
            FXMLLoader connectorEdit = new FXMLLoader(getClass().getResource(
                    "/fxml/connector_info.fxml"));
            AnchorPane root = connectorEdit.load();
            Scene modalScene = new Scene(root);
            Stage modal = new Stage();
            modal.setScene(modalScene);
            modal.setResizable(false);
            modal.setTitle("Connector Information");
            modal.initModality(Modality.WINDOW_MODAL);
            ConnectorEditController controller = connectorEdit.getController();
            controller.setController(this);
            controller.addConnector(connector);
            controller.displayInfo();
            modal.setAlwaysOnTop(true);
            modal.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            connectorTable.refresh();
            displayConnectorInfo();
        }
    }

}
