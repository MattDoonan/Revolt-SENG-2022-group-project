package seng202.team3.gui;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.Vehicle;

/**
 * A vehicle editing controller
 *
 * @author Celia Allen
 * @version 1.0.0, Sep 22
 */
public class VehicleUpdateController {

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

    @FXML
    private Button saveChanges;

    private Button saveImg = new Button("Select");

    private Vehicle vehicle;

    private Vehicle selectedVehicle;

    private String selectedImg;

    private ArrayList<String> connections = new ArrayList<String>();

    private ObservableList<Button> imgBtns = FXCollections.observableArrayList();

    private String[] imgNames = {"car_one.png", "car_two.png", "car_three.png",
        "truck_one.png", "truck_two.png"};

    private VehicleController controller;

    private Stage updatePopup = new Stage();

    private Stage connectorPopup = new Stage();

    /**
     * Initialises the Vehicle editing
     */
    public VehicleUpdateController() {
    }


    /**
     * Saves the changes; if new, will use the coordinates to make an entry
     */
    @FXML
    public void saveChanges() {
        // TODO: Vehicles are re-pulled from database, and displayed
        Vehicle vehicle;
        System.out.println("selectedVehicle: " + selectedVehicle);
        if (selectedVehicle != null) {
            selectedVehicle.setImgPath("src/main/resources/images/" + selectedImg);
            selectedVehicle.setConnectors(connections);
            selectedVehicle.setMaxRange(Integer.parseInt(maxRangeText.getText()));
            selectedVehicle.setModel(modelText.getText());
            selectedVehicle.setMake(makeText.getText());
            vehicle = selectedVehicle;
            System.out.println("VControl selectedVehicleID: " + selectedVehicle.getVehicleId());
        } else {
            vehicle = new Vehicle(makeText.getText(),
            modelText.getText(),
            Integer.parseInt(maxRangeText.getText()), connections);
            vehicle.setImgPath("src/main/resources/images/" + selectedImg);
            System.out.println("VControl vehicleid: " + vehicle.getVehicleId());
        }
        System.out.println("VControl VehicleID: " + vehicle.getVehicleId());
        try {
            SqlInterpreter.getInstance().writeVehicle(vehicle);
        } catch (IOException e) {
            e.printStackTrace();
        }
        selectedVehicle = null;
        Stage popupStage = (Stage) saveChanges.getScene().getWindow();
        popupStage.close();
    }


    /**
     * Adds a new connection (for an EV) when the button is clicked
     */
    @FXML
    public void addConnection() {
        if (connectorType.getValue().equals("Other...")) {
            GridPane root = new GridPane();
            Button save = new Button("Save");
            TextField connector = new TextField("Connector Type");
            root.addRow(0, connector);
            root.addRow(1, save);
            root.setStyle("-fx-padding: 20;");
            save.setOnMouseClicked((MouseEvent event) -> {
                connections.add(connector.getText());
                addedConnections.setText("Connection: "
                        + connector.getText() + "\n" + addedConnections.getText());
                Stage connectorStage = (Stage) save.getScene().getWindow();
                connectorStage.close();
            });
            connectorPopup.initModality(Modality.APPLICATION_MODAL);
            connectorPopup.setResizable(false);
            connectorPopup.setTitle("Other Connector");
            connectorPopup.setScene(new Scene(root, 300, 100));
            connectorPopup.showAndWait();
        } else {
            connections.add(connectorType.getValue());
            addedConnections.setText("Connection: "
                    + connectorType.getValue() + "\n" + addedConnections.getText());
        }
    }


    /**
     * Lets the user select an image (from a list) to represent their vehicle
     */
    @FXML
    public void selectImg() {
        try {
            saveImg.setOnAction(e -> save());
            saveImg.setLayoutX(538);
            saveImg.setLayoutY(360);

            for (int i = 0; i < imgNames.length; i++) {

                Image img = new Image(new FileInputStream("src/main/resources/images/"
                    + imgNames[i]));
                ImageView view = new ImageView(img);
                Button button = new Button();
                button.setGraphic(view);
                button.setId(imgNames[i]);
                imgBtns.add(button);
                button.setOnAction(e -> imgSelected(e));
            }
            VBox anchor = new VBox();
            anchor.getChildren().addAll(imgBtns);

            ScrollPane imgsDisplay = new ScrollPane();
            imgsDisplay.setPrefViewportWidth(570);
            imgsDisplay.setPrefViewportHeight(330);
            imgsDisplay.setContent(anchor);
            imgsDisplay.setPannable(true);
            AnchorPane root = new AnchorPane();
            root.getChildren().addAll(imgsDisplay, saveImg);
            updatePopup.initModality(Modality.APPLICATION_MODAL);
            updatePopup.setResizable(false);
            updatePopup.setTitle("Select Image");
            updatePopup.setScene(new Scene(root, 600, 400));
            updatePopup.showAndWait();
        } catch (IOException e) {
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.SEVERE, "Failed to create new Window.", e);
        }
    }


    /**
     * Updates selectedImg to the ID of the currently-selected image
     * @param e ActionEvent
     */
    public void imgSelected(ActionEvent e) {
        selectedImg = ((Node) e.getSource()).getId();
    }


    /**
     * Saves the currently selected image for the user's vehicle
     */
    public void save() {
        System.out.println(selectedImg);
        imgName.setText(selectedImg);
        Stage popupStage = (Stage) saveImg.getScene().getWindow();
        popupStage.close();
    }

    /**
     * Displays all the info of the vehicle, if there is a vehicle
     */
    public void displayInfo(Vehicle vehicle) {
        // System.out.println("vehicle: " + vehicle.toString());
        // System.out.println("vehicleID: " + Integer.toString(vehicle.getVehicleId()));
        selectedVehicle = vehicle;
        if (vehicle != null) {
            licenseText.setText("null");
            makeText.setText(vehicle.getMake());
            modelText.setText(vehicle.getModel());
            maxRangeText.setText(Integer.toString(vehicle.getMaxRange()));
            addedConnections.setText(vehicle.getConnectors().toString());
            imgName.setText(vehicle.getImgPath().replace("src/main/resources/images/", ""));
            connections = vehicle.getConnectors();
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