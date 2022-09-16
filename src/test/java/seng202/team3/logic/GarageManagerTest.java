package seng202.team3.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
     * Creates a {@link JourneyManager JourneyManager} to test
     * and four chargers.
     */
    private GarageManager manager;
    private Vehicle testVehicle;

    /**
     * BeforeEach Journey, Charger and vehicle setup
     * coord 2 is between coord 1 and coord 3
     * TODO fix tests
     *
     */
    @BeforeEach
    public void setUp() {
        testVehicle = new Vehicle("TestMake", "TestModel", 
            1234, new ArrayList<String>(Arrays.asList("Type 2 Socketed")));
        ArrayList<Vehicle> vehicleList = new ArrayList<>(1);
        vehicleList.add(testVehicle);
        manager = new GarageManager();
    }

    /**
     * AfterEach, teras it all down.
     */
    @AfterEach
    public void tearDown() {
        manager = null;
        assertNull(manager);
    }

    /**
     * Test that vehicles are successfully added to journey
     * TODO fix
     */
    @Test
    public void testGetVehicles() {

        try {
            SqlInterpreter.getInstance().writeVehicle(testVehicle);
        } catch (IOException e) {
            e.printStackTrace();
        }
        manager.resetQuery();
        manager.getAllVehicles();
        ObservableList<Vehicle> vehicles = manager.getData();
        assertTrue(testVehicle.equals(vehicles.get(vehicles.size() - 1)));
    }


}