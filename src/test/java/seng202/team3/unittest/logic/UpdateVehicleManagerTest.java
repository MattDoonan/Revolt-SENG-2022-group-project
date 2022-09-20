package seng202.team3.unittest.logic;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

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
import seng202.team3.logic.GarageManager;
import seng202.team3.logic.VehicleUpdateManager;

/**
 * Unit tests for {@link UpdateVehicleManagerTest UpdateVehicleManagerTest}
 * class in Logic
 *
 * @author Celia Allen
 * @version 1.0.0, Aug 22
 */
public class UpdateVehicleManagerTest {

    /**
     * Creates a {@link GarageManager GarageManager} to test
     * and three vehicles.
     */
    private GarageManager garageManager = new GarageManager();
    private VehicleUpdateManager manager = new VehicleUpdateManager();
    private Vehicle testVehicle;
    private Vehicle testVehicleTwo;
    private Vehicle testVehicleDelete;
    private ObservableList<Vehicle> vehicleList = FXCollections.observableList(
            new ArrayList<Vehicle>());
    private ObservableList<Vehicle> initialVehicleList;

    /**
     * BeforeEach create vehicles to add to garage
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
        garageManager.resetQuery();
        garageManager.getAllVehicles();
        initialVehicleList = garageManager.getData();
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

        ObservableList<Vehicle> vehicles = getVehicles();
        assertTrue(vehicles.equals(initialVehicleList));
        garageManager = null;
        manager = null;
        testVehicle = null;
        testVehicleTwo = null;
        testVehicleDelete = null;
        vehicleList = null;
        assertNull(garageManager);
        assertNull(manager);
        assertNull(testVehicle);
        assertNull(testVehicleTwo);
        assertNull(testVehicleDelete);
        assertNull(vehicleList);
    }

    /**
     * Test that vehicles are successfully added to database
     */
    @Test
    public void testAddEditVehicles() {
        // Test adding a vehicle
        manager.saveVehicle(testVehicle);
        manager.saveVehicle(testVehicleTwo);
        ObservableList<Vehicle> vehicles = getVehicles();
        assertTrue(testVehicleTwo.equals(vehicles.get(vehicles.size() - 1)));
        assertTrue(testVehicle.equals(vehicles.get(vehicles.size() - 2)));

        // Test editing a vehicle
        testVehicle.setMake("NewTestMake");
        testVehicleTwo.setMake("NewTestMakeTwo");
        manager.saveVehicle(testVehicle);
        manager.saveVehicle(testVehicleTwo);
        vehicles = getVehicles();
        assertTrue(testVehicleTwo.equals(vehicles.get(vehicles.size() - 1)));
        assertTrue(testVehicle.equals(vehicles.get(vehicles.size() - 2)));
    }

    /**
     * Test that vehicles are successfully deleted from database
     */
    @Test
    public void testGetVehicleAfterDelete() {

        manager.saveVehicle(testVehicleDelete);
        ObservableList<Vehicle> vehicles = getVehicles();
        assertTrue(testVehicleDelete.equals(vehicles.get(vehicles.size() - 1)));

        manager.deleteVehicle(testVehicleDelete);

        vehicles = getVehicles();
        assertFalse(vehicles.contains(testVehicleDelete));

    }

    /**
     * Gets the vehicles in the database
     * 
     * @return List of vehicles from database
     */
    public ObservableList<Vehicle> getVehicles() {
        garageManager.resetQuery();
        garageManager.getAllVehicles();
        ObservableList<Vehicle> vehicles = garageManager.getData();
        return vehicles;
    }

}
