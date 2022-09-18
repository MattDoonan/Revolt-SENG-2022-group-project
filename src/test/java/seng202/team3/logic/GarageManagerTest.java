package seng202.team3.logic;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.Vehicle;

/**
 * Unit tests for {@link GarageManager GarageManager} class in Logic
 *
 * @author Celia Allen
 * @version 1.0.0, Aug 22
 */
public class GarageManagerTest {

    /**
     * Creates a {@link GarageManager GarageManager} to test
     * and three vehicles.
     */
    private GarageManager manager;
    private Vehicle testVehicle;
    private Vehicle testVehicleTwo;
    private Vehicle testVehicleDelete;
    private ObservableList<Vehicle> vehicleList = FXCollections.observableList(
        new ArrayList<Vehicle>());
    private ObservableList<Vehicle> initialVehicleList;

    /**
     * BeforeEach create a vehicle to add to garage
     *
     */
    @BeforeEach
    public void setUp() {
        testVehicle = new Vehicle("TestMake", "TestModel", 
            1234, new ArrayList<String>(Arrays.asList("Type 2 Socketed")));
        testVehicle.setImgPath("null");
        testVehicleTwo = new Vehicle("TestMakeTwo", "TestModelTwo", 
            555, new ArrayList<String>(Arrays.asList("Type 1 Tethered")));
        testVehicleTwo.setImgPath("null");
        testVehicleDelete = new Vehicle("TestMakeDelete", "TestModelDelete", 
            1000, new ArrayList<String>(Arrays.asList("Other")));
        testVehicleDelete.setImgPath("null");
        vehicleList.add(testVehicle);
        vehicleList.add(testVehicleTwo);
        manager = new GarageManager();
        manager.resetQuery();
        manager.getAllVehicles();
        initialVehicleList = manager.getData();
    }

    /**
     * AfterEach, sets everything to null.
     */
    @AfterEach
    public void tearDown() {
        for (Vehicle vehicle : vehicleList) {
            try {
                SqlInterpreter.getInstance().deleteData("vehicle", 
                    vehicle.getVehicleId());
                vehicle = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        manager.resetQuery();
        manager.getAllVehicles();
        ObservableList<Vehicle> vehicles = manager.getData();
        assertTrue(vehicles.equals(initialVehicleList));
        manager = null;
        testVehicle = null;
        testVehicleTwo = null;
        testVehicleDelete = null;
        vehicleList = null;
        assertNull(manager);
        assertNull(testVehicle);
        assertNull(testVehicleTwo);
        assertNull(testVehicleDelete);
        assertNull(vehicleList);
    }

    /**
     * Test getting vehicles from database
     */
    @Test
    public void testGetVehicles() {

        try {
            SqlInterpreter.getInstance().writeVehicle(testVehicle);
            SqlInterpreter.getInstance().writeVehicle(testVehicleTwo);
        } catch (IOException e) {
            e.printStackTrace();
        }
        manager.resetQuery();
        manager.getAllVehicles();
        ObservableList<Vehicle> vehicles = manager.getData();
        assertTrue(testVehicleTwo.equals(vehicles.get(vehicles.size() - 1)));
        assertTrue(testVehicle.equals(vehicles.get(vehicles.size() - 2)));
    }


    /**
     * Test getAllVehicles() works after deleting vehicles
     */
    @Test
    public void testGetVehicleAfterDelete() {

        try {
            SqlInterpreter.getInstance().writeVehicle(testVehicleDelete);
        } catch (IOException e) {
            e.printStackTrace();
        }
        manager.resetQuery();
        manager.getAllVehicles();
        ObservableList<Vehicle> vehicles = manager.getData();
        assertTrue(testVehicleDelete.equals(vehicles.get(vehicles.size() - 1)));

        try {
            SqlInterpreter.getInstance().deleteData("vehicle", 
                testVehicleDelete.getVehicleId());
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        manager.resetQuery();
        manager.getAllVehicles();
        vehicles = manager.getData();
        assertTrue(!testVehicleDelete.equals(vehicles.get(vehicles.size() - 1)));
        
    }


}