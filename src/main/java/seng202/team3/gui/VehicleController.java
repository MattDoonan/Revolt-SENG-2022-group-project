package seng202.team3.gui;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import seng202.team3.data.entity.Vehicle;


/**
 * Controller for the vehicle.fxml window
 * 
 * @author Team 3
 * @version 1.0.3, Aug 22
 */
public class VehicleController {

    // private static final Logger log = LogManager.getLogger();

    // @FXML
    // private Label defaultLabel;

    @FXML
    private ListView<String> vehicleList;

    @FXML
    private TextArea vehicleDisplayOne;

    @FXML
    private TextArea vehicleDisplayTwo;

    @FXML
    private TextArea vehicleDisplayThree;

    @FXML
    private Button addVehicle;

    @FXML
    private Button nextBtn;

    @FXML
    private Button prevBtn;

    @FXML
    private Button addConnection;

    @FXML
    private Button addVehicleBtn;

    @FXML
    private Label addedConnections;

    @FXML
    private TextField licenseText;

    @FXML
    private TextField makeText;

    @FXML
    private TextField modelText;

    @FXML
    private TextField curChargeText;

    @FXML
    private TextField maxRangeText;

    @FXML
    private TextField connectionsText;

    private int indexOne = 0;

    private int indexTwo = 1;

    private int indexThree = 2;

    private ObservableList<Vehicle> vehicleData = FXCollections.observableArrayList();

    private ArrayList<String> connections = new ArrayList<String>();

    private Stage popup = new Stage();


    /**
     * Initialize the window
     *
     * @param stage Top level container for this window
     */
    public void init(Stage stage) {
        makeTestVehicles();
        System.out.println(vehicleData.toString());

        setData(vehicleDisplayOne, indexOne);

        if (vehicleData.size() > 1) {
            setData(vehicleDisplayTwo, indexTwo);
            setData(vehicleDisplayThree, indexThree);
        }

        addVehicle.setOnAction(e -> displayPopup());
    }

    /**
     * Displays pop-up window to add a new vehicle to the garage
     */
    public void displayPopup() {
        try {

            Parent root = FXMLLoader.load(getClass().getResource("/fxml/newVehicle.fxml"));
            // This will cause the login window to always be infront of the main window
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.setResizable(false);
            popup.setTitle("Add Vehicle");
            popup.setScene(new Scene(root, 600, 400));
            popup.showAndWait();


        } catch (IOException e) {
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.SEVERE, "Failed to create new Window.", e);
        }

    }


    /**
     * Adds a new connection (for an EV) when the button is clicked
     */
    @FXML
    public void addConnection() {
        connections.add(connectionsText.getText());
        addedConnections.setText("Connection: "
                + connectionsText.getText() + "\n" + addedConnections.getText());
    }

    /**
     * Adds the new vehicle to the list of vehicles (doesn't work quite yet)
     */
    @FXML
    public void addVehicle() {
        Vehicle vehicle = new Vehicle(licenseText.getText(), makeText.getText(),
                modelText.getText(), Float.parseFloat(curChargeText.getText()),
                Integer.parseInt(maxRangeText.getText()), connections);
        vehicleData.add(vehicle);
        Stage popupStage = (Stage) addVehicleBtn.getScene().getWindow();
        popupStage.close();
    }

    /**
     * Populate display with index-th vehicle
     *
     * @param display location to put the text
     * @param index   index of the vehicle to display
     */
    public void setData(TextArea display, int index) {
        System.out.println(vehicleData.toString());

        display.setText("License Plate: " + vehicleData.get(index).getLicensePlate() + "\n"
                + "Make: " + vehicleData.get(index).getMake() + "\n"
                + "Model: " + vehicleData.get(index).getModel() + "\n"
                + "Current Charge: " + vehicleData.get(index).getBatteryPercent() + "\n"
                + "Max. Range: " + vehicleData.get(index).getMaxRange() + "\n"
                + "Connections: " + vehicleData.get(index).getConnectors().toString() + "\n"
                + "\n\n\n" + "Index: " + vehicleData.indexOf(vehicleData.get(index)));
    }

    /**
     * Creates test vehicles
     * TODO: Remove once data can be properly pulled from the database
     */
    public void makeTestVehicles() {

        vehicleData.add(
                new Vehicle("ABC123", "Nissan", "Leaf", (float) 60.5, 270,
                        new ArrayList<>()));
        vehicleData.add(
                new Vehicle("AAA111", "Nissan", "Leaf E+", (float) 87, 385,
                        new ArrayList<>()));
        vehicleData.add(
                new Vehicle("XYZ789", "Tesla", "X", (float) 100.5, 536,
                        new ArrayList<>()));
        vehicleData.add(
                new Vehicle("QWE768", "Jaguar", "I-Pace", (float) 40, 470,
                        new ArrayList<>()));
        vehicleData.add(
                new Vehicle("FJG788", "Audi", "E-Tron", (float) 63, 441,
                        new ArrayList<>()));
        vehicleData.add(
                new Vehicle("WWW333", "Tesla", "5", (float) 43, 637,
                        new ArrayList<>()));
        vehicleData.add(
                new Vehicle("HGJ449", "Mercedes Benz", "EQC", (float) 77, 430,
                        new ArrayList<>()));
        vehicleData.add(
                new Vehicle("IUH909", "Porsche", "Taycan", (float) 98, 480,
                        new ArrayList<>()));
    }

    /**
     * Method to call when next button is clicked
     *
     */
    @FXML
    public void nextBtnClicked() {
        if (vehicleData.size() > 0) {
            if (indexThree == vehicleData.size() - 1) {
                indexOne++;
                indexTwo++;
                indexThree = 0;
            } else if (indexTwo == vehicleData.size() - 1) {
                indexOne++;
                indexThree++;
                indexTwo = 0;
            } else if (indexOne == vehicleData.size() - 1) {
                indexTwo++;
                indexThree++;
                indexOne = 0;
            } else {
                indexOne++;
                indexTwo++;
                indexThree++;
            }

            setData(vehicleDisplayOne, indexOne);
            setData(vehicleDisplayTwo, indexTwo);
            setData(vehicleDisplayThree, indexThree);
        }

    }

    public void selectToView() {
        // chargerTable.getSelectionModel().selectedItemProperty().addListener(new
        // ChangeListener() {
        // @Override
        // public void changed(ObservableValue observableValue, Object o, Object t1) {
        // viewChargers(chargerData.get(chargerTable.getSelectionModel().getSelectedIndex()));

        // }
        // });

    }

    /**
     * Method to call when prev button is clicked
     *
     */
    @FXML
    public void prevBtnClicked() {
        if (vehicleData.size() > 0) {

            if (indexOne == 0) {
                indexOne = vehicleData.size() - 1;
                indexTwo--;
                indexThree--;
            } else if (indexTwo == 0) {
                indexTwo = vehicleData.size() - 1;
                indexOne--;
                indexThree--;
            } else if (indexThree == 0) {
                indexThree = vehicleData.size() - 1;
                indexOne--;
                indexTwo--;
            } else {
                indexOne--;
                indexTwo--;
                indexThree--;
            }
            setData(vehicleDisplayOne, indexOne);
            setData(vehicleDisplayTwo, indexTwo);
            setData(vehicleDisplayThree, indexThree);
        }
    }
}
