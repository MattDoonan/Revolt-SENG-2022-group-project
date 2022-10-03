package seng202.team3.data.entity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seng202.team3.data.database.ComparisonType;
import seng202.team3.data.database.QueryBuilder;
import seng202.team3.data.database.QueryBuilderImpl;
import seng202.team3.data.database.SqlInterpreter;

/**
 * Stores information about Users
 *
 * @author Matthew Doonan
 * @version 1.0.0, Sep 2022
 */
public class User {

    /** users id in database */
    private int userid;

    /** String for the email of the user */
    private String email;

    /** String of the account name */
    private String accountName;

    /** double for the carbon saved */
    private double carbonSaved;

    /** Permissions level as int */
    private PermissionLevel level;

    /** The user's currently-selected vehicle */
    private Vehicle vehicle;

    /**
     * Constructor for user class
     * 
     * @param email       String for the email
     * @param accountName String of the account name
     * @param level       the permission level
     */
    public User(String email, String accountName, PermissionLevel level) {
        this.email = email;
        this.accountName = accountName;
        this.level = level;
        this.carbonSaved = 0;
        setVehicle();
        // System.out.println(vehicle.toString());
    }

    /**
     * Default constructor
     */
    public User() {
        // Unused
    }

    /**
     * sets the user id to an int
     * 
     * @param id the user if to be set to
     */
    public void setUserid(int id) {
        userid = id;
    }

    /**
     * Gets the userid
     * 
     * @return integer for the user id
     */
    public int getUserid() {
        return userid;
    }

    /**
     * Edits the current email of the user
     * 
     * @param update a string representing the email
     */
    public void setEmail(String update) {
        email = update;
    }

    /**
     * Returns the email
     * 
     * @return String of the users email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set the account name
     * 
     * @param newName the new account name
     */
    public void setAccountName(String newName) {
        accountName = newName;
    }

    /**
     * Returns the account name
     * 
     * @return String of the account name
     */
    public String getAccountName() {
        return accountName;
    }

    /**
     * Adds to the users carbon saved stat
     * 
     * @param add double for the carbon saved
     */
    public void setCarbonSaved(double add) {
        carbonSaved += add;
    }

    /**
     * returns the users carbon savings
     * 
     * @return the carbon saved double
     */
    public double getCarbonSaved() {
        return carbonSaved;
    }

    /**
     * Sets the level of the users permissions
     * 
     * @param update the new user permissions
     */
    public void setLevel(PermissionLevel update) {
        this.level = update;
    }

    /**
     * Gets the level of the user permissions
     * 
     * @return integer of the user permissions
     */
    public PermissionLevel getLevel() {
        return level;
    }

    /**
     * Gets the user's current vehicle from the database and sets it
     */
    public void setVehicle() {
        QueryBuilder vehicleDataQuery = new QueryBuilderImpl().withSource("vehicle")
                .withFilter("owner", Integer.toString(getUserid()), ComparisonType.EQUAL);
        try {
            List<Vehicle> vehicleList = new ArrayList<>();
            for (Object o : SqlInterpreter.getInstance()
                    .readData(vehicleDataQuery.build(), Vehicle.class)) {
                vehicleList.add((Vehicle) o);
            }
            ObservableList<Vehicle> vehicleData = FXCollections.observableList(vehicleList);
            for (Vehicle vehicle : vehicleData) {
                if (vehicle.getCurrVehicle() == true) {
                    this.vehicle = vehicle;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the user's current vehicle
     * 
     * @return the user's current vehicle
     */
    public Vehicle getVehicle() {
        return vehicle;
    }

    @Override
    public boolean equals(Object o) {
        User u;
        if (o instanceof User) {
            u = (User) o;
        } else {
            return false;
        }

        return u.getUserid() == this.getUserid()
                && u.getEmail().equals(this.getEmail())
                && u.getAccountName().equals(this.getAccountName())
                && u.getCarbonSaved() == this.getCarbonSaved()
                && u.getLevel().equals(this.getLevel());
    }
}
