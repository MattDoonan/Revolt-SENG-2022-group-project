package seng202.team3.gui;

import java.io.IOException;
import java.util.ArrayList;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import seng202.team3.data.database.ComparisonType;
import seng202.team3.data.database.QueryBuilder;
import seng202.team3.data.database.QueryBuilderImpl;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.Connector;



/**
 * The connector controller class which operates the connector changes
 *
 * @author Michelle Hsieh
 * @version 1.0.0, Sep 22
 */
public class ConnectorController {

    private Stage stage;
    private ObservableList<Connector> connectorList;

    @FXML
    private TableColumn<Connector, String> current;

    @FXML
    private TableColumn<Connector, String> wattage;

    @FXML
    private TableColumn<Connector, String> chargingPoints;

    @FXML
    private TableColumn<Connector, String> connectorTypes;

    @FXML
    private TableColumn<Connector, String> status;

    @FXML
    private TableView<Connector> connectors;

    /**
     * Initialises the connectorcontroller
     */
    public ConnectorController() {
    }

    /**
     * Displays all the connector information in the table
     */
    public void displayConnectorInfo() {
        ArrayList<Connector> connectArray =  new ArrayList<>();
        Charger selectedCharger = new MenuController().getController()
                .getManager().getSelectedCharger();
        QueryBuilder query = new QueryBuilderImpl().withSource("connector")
                .withFilter("chargerid", Integer.toString(selectedCharger.getChargerId()),
                        ComparisonType.EQUAL);
        try {
            for (Object object : SqlInterpreter.getInstance()
                    .readData(query.build(), Connector.class)) {
                connectArray.add((Connector) object);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        connectorList = FXCollections.observableList(connectArray);

        connectors.setItems(connectorList);

        current.setCellValueFactory(connector
                -> new ReadOnlyStringWrapper(connector.getValue().getCurrent()));

        wattage.setCellValueFactory(connector
                -> new ReadOnlyStringWrapper(connector.getValue().getPower()));

        chargingPoints.setCellValueFactory(connector
                -> new ReadOnlyStringWrapper(Integer.toString(connector.getValue().getCount())));

        connectorTypes.setCellValueFactory(connector
                -> new ReadOnlyStringWrapper(connector.getValue().getType()));

        status.setCellValueFactory(connector
                -> new ReadOnlyStringWrapper(connector.getValue().getStatus()));

    }

    /**
     * TODO Add this functionality with a new window
     */
    @FXML
    public void addConnector() {

    }

    /**
     * TODO Add this functionality
     */
    @FXML
    public void deleteConnector() {
    }

    /**
     * TODO Add this functionality
     */
    @FXML
    public void editConnector() {

    }
}
