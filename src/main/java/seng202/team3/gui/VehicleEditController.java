package seng202.team3.gui;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import seng202.team3.data.entity.Vehicle;

/**
 * A vehicle editing controller
 *
 * @author Celia Allen
 * @version 1.0.0, Sep 22
 */
public class VehicleEditController {

    @FXML
    private TextField licenseText;

    @FXML
    private TextField makeText;

    @FXML
    private TextField modelText;

    @FXML
    private TextField maxRangeText;

    @FXML
    private Label imgName;

    @FXML
    private Label addedConnections;

    @FXML
    private ComboBox<String> connectorType;

    private Vehicle vehicle;

    private VehicleController controller;

    /**
     * Initialises the Vehicle editing
     */
    public VehicleEditController() {
    }

    /**
     * Initialises the VehicleEditController with the selected connector
     *
     * @param vehicle a {@link Vehicle} if it's preexisting
     */
    public void addConnector(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    /**
     * Displays all the info of the connector, if there is a connector
     */
    public void displayInfo() {
        if (vehicle != null) {
            licenseText.setText("null");
            makeText.setText(vehicle.getMake());
            modelText.setText(vehicle.getModel());
            maxRangeText.setText(Integer.toString(vehicle.getMaxRange()));
            addedConnections.setText(vehicle.getConnectors().toString());
            imgName.setText(vehicle.getImgPath());
        }
    }

    /**
     * Sets the ConnectorController holding all the controllers
     *
     * @param controller ConnectorController controller
     */
    public void setController(VehicleController controller) {
        this.controller = controller;
    }

    // /**
    //  * Saves the changes and closes this window if necessary
    //  */
    // @FXML
    // public void saveChanges() {
    //     Vehicle changedVehicle;
    //     String typeString = type.getText();
    //     String powerString = wattage.getText();
    //     String statusString = status.getText();
    //     String currentString = current.getText();
    //     int points = Integer.parseInt(chargingPoints.getText());

    //     if (vehicle == null) {

    //         Vehicle vehicle = new Vehicle(makeText.getText(),
    //             modelText.getText(),
    //             Integer.parseInt(maxRangeText.getText()), connections);
    //         vehicle.setImgPath("src/main/resources/images/" + selectedImg);

    //         changedVehicle = new Vehicle(typeString, powerString,
    //                 statusString, currentString, points);
    //         controller.getVehicleList().add(changedVehicle);
    //     } else {
    //         changedvehicle = new vehicle(typeString,
    //                 powerString, statusString, currentString, points, vehicle.getId());
    //     }
    //     for (int i = 0; i < controller.getvehicleList().size(); i++) {
    //         if (controller.getvehicleList().get(i) == vehicle) {
    //             controller.getvehicleList().set(i, changedvehicle);
    //         }
    //     }
    //     Stage stage = (Stage) status.getScene().getWindow();
    //     stage.close();
    // }

}
