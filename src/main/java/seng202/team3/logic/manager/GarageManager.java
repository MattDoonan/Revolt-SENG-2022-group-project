package seng202.team3.logic.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.database.util.ComparisonType;
import seng202.team3.data.database.util.QueryBuilder;
import seng202.team3.data.database.util.QueryBuilderImpl;
import seng202.team3.data.entity.Entity;
import seng202.team3.data.entity.User;
import seng202.team3.data.entity.Vehicle;
import seng202.team3.data.entity.util.EntityType;

/**
 * Logic layer for the garage Controller
 *
 * @author Celia Allen
 * @version 1.0.0, Sep 13
 *
 */
public class GarageManager {
    /**
     * Logger
     */
    private static final Logger logManager = LogManager.getLogger();

    /**
     * Query object to retrieve vehicles
     */
    private QueryBuilder vehicleDataQuery;

    /**
     * List of available vehicles
     */
    private ObservableList<Vehicle> vehicleData;

    /**
     * The current user
     */
    private User user;

    /**
     * Initialize GarageManager
     */
    public GarageManager() {
        user = UserManager.getUser();
    }

    /**
     * Load the initial query
     */
    public void resetQuery() {
        vehicleDataQuery = new QueryBuilderImpl().withSource(EntityType.VEHICLE)
                .withFilter("owner", Integer.toString(user.getId()), ComparisonType.EQUAL);
    }

    /**
     * Edits query so it gets the selected vehicle
     *
     * @return the selected vehicle
     */
    public Vehicle getSelectedVehicle() {
        vehicleDataQuery.withFilter("currVehicle", "true", ComparisonType.EQUAL);
        getAllVehicles();
        resetQuery();
        if (vehicleData.size() == 1) {
            return vehicleData.get(0);
        }
        return null;
    }

    /**
     * Create the vehicle list from the main Query
     *
     */
    public void getAllVehicles() {
        try {
            List<Vehicle> vehicleList = new ArrayList<>();
            for (Entity o : SqlInterpreter.getInstance()
                    .readData(vehicleDataQuery.build())) {
                vehicleList.add((Vehicle) o);
            }

            vehicleData = FXCollections
                    .observableList(vehicleList);
        } catch (IOException e) {
            logManager.error(e.getMessage());
        }
    }

    /**
     * Saves the changes to the given vehicle to the database
     * 
     * @param vehicle the vehicle to be saved
     */
    public void saveCurrVehicle(Vehicle vehicle) {
        try {
            SqlInterpreter.getInstance().writeVehicle(vehicle);
        } catch (IOException e) {
            logManager.error(e.getMessage());
        }
    }

    /**
     * Returns the list of vehicles
     *
     * @return vehicleData of the required vehicle
     */
    public ObservableList<Vehicle> getData() {
        getAllVehicles();
        return vehicleData;
    }

}
