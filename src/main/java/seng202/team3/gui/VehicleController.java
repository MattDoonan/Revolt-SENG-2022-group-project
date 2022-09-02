package seng202.team3.gui;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
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


    @FXML
    private ListView<String> vehicleList;

    @FXML
    private TextArea vehicleDisplayOne;

    @FXML
    private TextArea vehicleDisplayTwo;

    @FXML
    private TextArea vehicleDisplayThree;

    @FXML
    private ImageView vehicleImageOne;

    @FXML
    private ImageView vehicleImageTwo;

    @FXML
    private ImageView vehicleImageThree;

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

    private ObservableList<Vehicle> vehicleData = FXCollections.observableArrayList();

    private ArrayList<String> connections = new ArrayList<String>();

    private Stage popup = new Stage();


    /**
     * Initialize the window
     *
     * @param stage Top level container for this window
     */
    public void init( ) {
        makeTestVehicles();
        setData(vehicleDisplayOne, vehicleImageOne, 0);

        if (vehicleData.size() > 1) {
            setData(vehicleDisplayTwo, vehicleImageTwo, 1);
            setData(vehicleDisplayThree, vehicleImageThree, 2);
        }

        addVehicle.setOnAction(e -> displayPopup());
    }

    /**
     * Displays pop-up window to add a new vehicle to the garage
     */
    public void displayPopup() {
        try {

            Parent root = FXMLLoader.load(getClass().getResource("/fxml/new_vehicle.fxml"));
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
        System.out.println(vehicleData.toString());
        System.out.println(vehicle);
        Stage popupStage = (Stage) addVehicleBtn.getScene().getWindow();
        popupStage.close();
    }

    /**
     * Populate display with index-th vehicle
     *
     * @param display location to put the text
     * @param index   index of the vehicle to display
     */
    public void setData(TextArea display, ImageView imageview, int index) {
        // System.out.println(vehicleData.toString());

        display.setText(vehicleData.get(index).getMake() + " " 
                + vehicleData.get(index).getModel() + "\n\n"
                + "License Plate: " + vehicleData.get(index).getLicensePlate() + "\n"
                + "Current Charge: " + vehicleData.get(index).getBatteryPercent() + "\n"
                + "Max. Range: " + vehicleData.get(index).getMaxRange() + "\n"
                + "Connections: " + vehicleData.get(index).getConnectors().toString());

        
        Image image;
        try {
            if (vehicleData.get(index).getImgPath() != null) {
                image = new Image(new FileInputStream(vehicleData.get(index).getImgPath()));
                imageview.setImage(image);
            } else {
                imageview.setImage(null);
            }
             
            // display.add(vehicleImageThree);

            // Text title = new Text(vehicleData.get(index).getMake() + "\u00A0" + vehicleData.get(index).getModel());
            
            // Text text = new Text("License Plate: " + vehicleData.get(index).getLicensePlate() + "\n"
            // + "Current Charge: " + vehicleData.get(index).getBatteryPercent() + "\n"
            // + "Max. Range: " + vehicleData.get(index).getMaxRange() + "\n"
            // + "Connections: " + vehicleData.get(index).getConnectors().toString());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }  

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
        vehicleData.get(0).setImgPath("src\\main\\resources\\images\\car_one.png");
        vehicleData.get(2).setImgPath("src\\main\\resources\\images\\car_two.png");
        vehicleData.get(3).setImgPath("src\\main\\resources\\images\\car_one.png");
        vehicleData.get(5).setImgPath("src\\main\\resources\\images\\car_three.png");
        vehicleData.get(6).setImgPath("src\\main\\resources\\images\\car_two.png");
    }

    /**
     * Method to call when next button is clicked
     *
     */
    @FXML
    public void nextBtnClicked() {
        if (vehicleData.size() > 0) {
            Vehicle vehicle = vehicleData.get(0);
            vehicleData.remove(vehicle);
            vehicleData.add(vehicle);
            setData(vehicleDisplayOne, vehicleImageOne, 0);
            setData(vehicleDisplayTwo, vehicleImageTwo, 1);
            setData(vehicleDisplayThree, vehicleImageThree, 2);
        }

        // if (vehicleData.size() > 0) {
        //     if (indexThree == vehicleData.size() - 1) {
        //         indexOne++;
        //         indexTwo++;
        //         indexThree = 0;
        //     } else if (indexTwo == vehicleData.size() - 1) {
        //         indexOne++;
        //         indexThree++;
        //         indexTwo = 0;
        //     } else if (indexOne == vehicleData.size() - 1) {
        //         indexTwo++;
        //         indexThree++;
        //         indexOne = 0;
        //     } else {
        //         indexOne++;
        //         indexTwo++;
        //         indexThree++;
        //     }

        //     setData(vehicleDisplayOne, indexOne);
        //     setData(vehicleDisplayTwo, indexTwo);
        //     setData(vehicleDisplayThree, indexThree);
        // }

    }


    /**
     * Method to call when prev button is clicked
     *
     */
    @FXML
    public void prevBtnClicked() {

        if (vehicleData.size() > 0) {
            Vehicle vehicle = vehicleData.get(vehicleData.size()-1);
            vehicleData.remove(vehicle);
            vehicleData.add(0, vehicle);
            setData(vehicleDisplayOne, vehicleImageOne, 0);
            setData(vehicleDisplayTwo, vehicleImageTwo, 1);
            setData(vehicleDisplayThree, vehicleImageThree, 2);
        }

        // if (vehicleData.size() > 0) {

        //     if (indexOne == 0) {
        //         indexOne = vehicleData.size() - 1;
        //         indexTwo--;
        //         indexThree--;
        //     } else if (indexTwo == 0) {
        //         indexTwo = vehicleData.size() - 1;
        //         indexOne--;
        //         indexThree--;
        //     } else if (indexThree == 0) {
        //         indexThree = vehicleData.size() - 1;
        //         indexOne--;
        //         indexTwo--;
        //     } else {
        //         indexOne--;
        //         indexTwo--;
        //         indexThree--;
        //     }
        //     setData(vehicleDisplayOne, indexOne);
        //     setData(vehicleDisplayTwo, indexTwo);
        //     setData(vehicleDisplayThree, indexThree);
        // }
    }
}
