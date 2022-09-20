package seng202.team3.logic;

import java.io.IOException;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.Vehicle;

/**
 * Manages updates of vehicles
 *
 * @author Celia Allen
 * @version 1.0.0, Aug 22
 */
public class VehicleUpdateManager {

    /**
     * Saves the given vehicle (or given vehicle changes) to the database
     * @param vehicle the vehicle to be saved
     */
    public void saveVehicle(Vehicle vehicle) {
        System.out.println("vehicle: " + vehicle.getVehicleId());

        try {
            SqlInterpreter.getInstance().writeVehicle(vehicle);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Deletes the given vehicle from the database
     * @param vehicle the vehicle to be deleted
     */
    public void deleteVehicle(Vehicle vehicle) {
        try {
            SqlInterpreter.getInstance().deleteData("vehicle", 
                vehicle.getVehicleId());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
}
