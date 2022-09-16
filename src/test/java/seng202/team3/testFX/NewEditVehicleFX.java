package seng202.team3.testFX;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.Vehicle;
import seng202.team3.gui.GarageController;
import seng202.team3.gui.MainWindow;
import seng202.team3.logic.GarageManager;


/**
 * Code designed to test the searching and filtering of the Main Window
 * Heavily inspired by Morgan English
 *
 * @author Celia Allen
 * @version 1.0.0, Sep 22
 */
public class NewEditVehicleFX extends TestFXBase {

    private GarageController controller;
    private GarageManager manager = new GarageManager();
    private ObservableList<Vehicle> vehicles;
    private Vehicle vehicle;

    /**
     * Implements the abstract method for this window
     *
     * @throws Exception if fail to launch
     */
    @Override
    public void setUp() throws Exception {
        ApplicationTest.launch(MainWindow.class);
    }

    /**
     * Starts the main for testing
     *
     * @param stage the stage of the application
     * @throws Exception if fail to launch
     */
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/garage.fxml"));
        Parent page = loader.load();
        initState(loader, stage);
        Scene scene = new Scene(page);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Initialises the state of the current application
     *
     * @param loader the FXML loader after loading
     * @param stage the stage of the application
     */
    public void initState(FXMLLoader loader, Stage stage) {
        controller = loader.getController();
        controller.init();
    }

    /**
     * Test the creating vehicle functionality
     */
    @Test
    public void makeVehicle() {
        try {
            SqlInterpreter.getInstance().writeVehicle(new Vehicle(
                "TestMake", "TestModel", 10, new ArrayList<String>(
                    Arrays.asList("Type 2 Socketed"))));
        } catch (IOException e) {
            e.printStackTrace();
        } 
        vehicles = manager.getData();
        vehicle = vehicles.get(vehicles.size() - 1);
        assertTrue(vehicle.getMake().equals("TestMake"));
        assertTrue(vehicle.getModel().equals("TestModel"));
        assertTrue(vehicle.getMaxRange() == 1234);
        assertTrue(vehicle.getConnectors().get(0).equals("Type 2 Socket"));
        assertTrue(vehicle.getBatteryPercent() == 100.0);
    }

    @Test
    public void editVehicle() {
        vehicles = manager.getData();
        if (vehicles.size() == 1) {
            clickOn("#editCarOne");
        } else if (vehicles.size() == 2) {
            clickOn("#editCarTwo");
        } else if () {
            clickOn("#editCarThree");
        } else {
            clickOn("#prevBtn");
            clickOn("#editCarOne");
        }
        clickOn("#makeText");
        write("Edit");
        clickOn("#saveChanges");
        
        vehicle = vehicles.get(vehicles.size() - 1);
        assertTrue(vehicle.getMake().equals("TestMakeEdit"));
        assertTrue(vehicle.getModel().equals("TestModel"));
        assertTrue(vehicle.getMaxRange() == 1234);
        assertTrue(vehicle.getConnectors().get(0).equals("Type 2 Socket"));
        assertTrue(vehicle.getBatteryPercent() == 100.0);
    }


    @Test
    public void deleteVehicle() {
        clickOn("#deleteCarOne");
        clickOn("#confirm");
        vehicles = manager.getData();
        assertTrue(vehicles.size() == 0);
    }


}
