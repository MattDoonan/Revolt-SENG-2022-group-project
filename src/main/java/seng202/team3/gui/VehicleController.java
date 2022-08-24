package seng202.team3.gui;


import javafx.collections.ObservableList;
import javafx.scene.control.*;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.stage.Stage;


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


    private ObservableList<String> vehicleData = FXCollections.observableArrayList();  


   

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
        display.setText("Car: " + vehicleData.get(index) + "\n" + "Here's some more text");
    }


    /**
     * Creates test vehicles
     * TODO: Remove once data can be properly pulled from the database
     */
    public void makeTestVehicles() {

        vehicleData.add("BMW");
        vehicleData.add("Volkswagen");
        vehicleData.add("Toyota");
        vehicleData.add("Another car brand");
        vehicleData.add("????????");
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
