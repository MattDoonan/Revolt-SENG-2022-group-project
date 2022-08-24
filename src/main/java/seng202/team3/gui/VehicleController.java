package seng202.team3.gui;


import javafx.collections.ObservableList;
import javafx.scene.control.*;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import seng202.team3.data.entity.Vehicle;


/**
 * Controller for the vehicle.fxml window
 * @author seng202 teaching team
 */
public class VehicleController {

    // private static final Logger log = LogManager.getLogger();

    // @FXML
    // private Label defaultLabel;


    @FXML
    private ListView vehicleList;

    @FXML
    private TextArea vehicleDisplayOne;

    @FXML
    private TextArea vehicleDisplayTwo;

    @FXML
    private TextArea vehicleDisplayThree;

    @FXML
    private Button nextBtn;

    @FXML
    private Button prevBtn;


    private int indexOne = 0;

    private int indexTwo = 1;

    private int indexThree = 2;


    private ObservableList<Vehicle> vehicleData = FXCollections.observableArrayList();  


   

    /**
     * Initialize the window
     *
     * @param stage Top level container for this window
     */
    public void init(Stage stage) {
        makeTestVehicles();

        setData(vehicleDisplayOne, indexOne);

        if (vehicleData.size() > 1) {
            setData(vehicleDisplayTwo, indexTwo);
            setData(vehicleDisplayThree, indexThree);
        }

    }

    public void setData(TextArea display, int index) {
        display.setText("License Plate: " + vehicleData.get(index).getLicensePlate() + "\n" + 
        "Make: " + vehicleData.get(index).getMake() + "\n" +
        "Model: " + vehicleData.get(index).getModel() + "\n" +
        "Current Charge: " + vehicleData.get(index).getBatteryPercent() + "\n" +
        "Max. Range: " + vehicleData.get(index).getMaxRange() + "\n" +
        "Connections: " + vehicleData.get(index).getConnectors().toString() + "\n" +
        "\n\n\n" +
        "Index: " + vehicleData.indexOf(vehicleData.get(index)));
    }


    /**
     * Creates test vehicles
     * TODO: Remove once data can be properly pulled from the database
     */
    public void makeTestVehicles() {

        vehicleData.add(new Vehicle("ABC123", "Nissan", "Leaf", (float)60.5, 270, new ArrayList<String>()));
        vehicleData.add(new Vehicle("AAA111", "Nissan", "Leaf E+", (float)87, 385, new ArrayList<String>()));
        vehicleData.add(new Vehicle("XYZ789", "Tesla", "X", (float)100.5, 536, new ArrayList<String>()));
        vehicleData.add(new Vehicle("QWE768", "Jaguar", "I-Pace", (float)40, 470, new ArrayList<String>()));
        vehicleData.add(new Vehicle("FJG788", "Audi", "E-Tron", (float)63, 441, new ArrayList<String>()));
        vehicleData.add(new Vehicle("WWW333", "Tesla", "5", (float)43, 637, new ArrayList<String>()));
        vehicleData.add(new Vehicle("HGJ449", "Mercedes Benz", "EQC", (float)77, 430, new ArrayList<String>()));
        vehicleData.add(new Vehicle("IUH909", "Porsche", "Taycan", (float)98, 480, new ArrayList<String>()));
    }


    /**
     * Method to call when next button is clicked
     *
     */
    @FXML
    public void nextBtnClicked() {

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


    /**
     * Method to call when prev button is clicked
     *
     */
    @FXML
    public void prevBtnClicked() {
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
