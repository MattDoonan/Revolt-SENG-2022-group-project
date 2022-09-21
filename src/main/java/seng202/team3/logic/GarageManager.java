package seng202.team3.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seng202.team3.data.database.QueryBuilder;
import seng202.team3.data.database.QueryBuilderImpl;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.Vehicle;

/**
 * Logic layer for the garage Controller
 *
 * @author Celia Allen
 * @version 1.0.0, Sep 13
 *
 */
public class GarageManager {

    /**
     * Query object to retrieve vehicles
     */
    private QueryBuilder vehicleDataQuery;

    /**
     * List of available vehicles
     */
    private ObservableList<Vehicle> vehicleData;

    /**
     * Initialize GarageManager
     */
    public GarageManager() {
        // resetQuery();
    }

    /**
     * Load the initial query
     */
    public void resetQuery() {
        vehicleDataQuery = new QueryBuilderImpl().withSource("vehicle");
    }

    /**
     * Create the vehicle list from the main Query
     *
     */
    public void getAllVehicles() {
        try {
            List<Vehicle> vehicleList = new ArrayList<>();
            for (Object o : SqlInterpreter.getInstance()
                    .readData(vehicleDataQuery.build(), Vehicle.class)) {
                vehicleList.add((Vehicle) o);
            }

            vehicleData = FXCollections
                    .observableList(vehicleList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the list of vehicles
     *
     * @return vehicleData of the required vehicle
     */
    public ObservableList<Vehicle> getData() {
        return vehicleData;
    }

}
