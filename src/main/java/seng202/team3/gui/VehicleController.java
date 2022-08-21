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


    private ObservableList<String> vehicleData = FXCollections.observableArrayList();  


   

    /**
     * Initialize the window
     *
     * @param stage Top level container for this window
     */
    public void init(Stage stage) {
        makeTestVehicles();
        
        for (int i = 0; i < vehicleData.size(); i++) {
            vehicleList.getItems().add("Car: " + vehicleData.get(i) + "\n" + "Here's some more text");
        }


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
     * Displays the user's vehicles in the window
     *
     * @param c List of vehicles to display
     */
    public void viewChargers(ObservableList<String> c) {
        
        for (int i = 0; i < c.size(); i++) {
            System.out.println("Vehicle: " + c.get(i));
        }


    }



    public void selectToView(){
        // chargerTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
        //     @Override
        //     public void changed(ObservableValue observableValue, Object o, Object t1) {
        //         viewChargers(chargerData.get(chargerTable.getSelectionModel().getSelectedIndex()));

        //     }
        // });

    }




    /**
     * Method to call when our counter button is clicked
     *
     */
    @FXML
    public void onButtonClicked() {


    }
}
