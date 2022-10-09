package seng202.team3.logic;

import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.EntityType;
import seng202.team3.data.entity.Vehicle;

/**
 * Manages updates of vehicles
 *
 * @author Celia Allen
 * @version 1.0.0, Sep 18
 */
public class VehicleUpdateManager {
    /**
     * Logger
     */
    private static final Logger logManager = LogManager.getLogger();

    /**
     * Unused constructor
     */
    public VehicleUpdateManager() {
        // Unused
    }

    /**
     * Saves the given vehicle (or given vehicle changes) to the database
     * 
     * @param vehicle the vehicle to be saved
     */
    public void saveVehicle(Vehicle vehicle) {
        try {
            SqlInterpreter.getInstance().writeVehicle(vehicle);
            logManager.info("Vehicle has been saved");
        } catch (IOException e) {
            logManager.error(e.getMessage());
        }
    }

    /**
     * Deletes the given vehicle from the database
     * 
     * @param vehicle the vehicle to be deleted
     */
    public void deleteVehicle(Vehicle vehicle) {
        try {
            SqlInterpreter.getInstance().deleteData(EntityType.VEHICLE,
                    vehicle.getVehicleId());
            logManager.info("Vehicle has been deleted");
        } catch (IOException e) {
            logManager.error(e.getMessage());
        }
    }

}
