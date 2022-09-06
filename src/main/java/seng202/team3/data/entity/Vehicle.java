package seng202.team3.data.entity;

import java.util.ArrayList;

/**
 * Stores information about vehicles
 * 
 * @author James Billows
 * @version 1.0.0, Aug 2022
 */
public class Vehicle {

    /** Vehicle id */
    private int vehicleId;

    /** Make of the vehicle */
    private String make;

    /** Model of the vehicle */
    private String model;

    /** Current battery percentage (0 - 100) */
    private float batteryPercent;

    /** Range of vehicle with full battery capacity, in kms */
    private int maxRange;

    /** List of connector types */
    private ArrayList<String> connectors;

    /** Path to vehicle image */
    private String imgPath;

    /** Constructor for Vehicle */
    public Vehicle(String make, String model, int maxRange,
            ArrayList<String> connectors) {
        this.make = make;
        this.model = model;
        this.maxRange = maxRange;
        this.connectors = connectors;
    }

    /**
     * Get vehicle make
     * 
     * @return make
     */
    public String getMake() {
        return make;
    }

    /**
     * Set vehicle make
     * 
     * @param make make of the vehicle
     */
    public void setMake(String make) {
        this.make = make;
    }

    /**
     * Get vehicle model
     * 
     * @return model of the vehicle
     */
    public String getModel() {
        return model;
    }

    /**
     * Set vehicle model
     * 
     * @param model model of the vehicle
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Get current battery percentage
     * 
     * @return current battery percentage
     */
    public float getBatteryPercent() {
        return batteryPercent;
    }

    /**
     * Set current battery percentage
     * 
     * @param batteryPercent current battery percentage
     */
    public void setBatteryPercent(float batteryPercent) {
        this.batteryPercent = batteryPercent;
    }

    /**
     * Get max range
     * 
     * @return range on full capacity
     */
    public int getMaxRange() {
        return maxRange;
    }

    /**
     * Set max range
     * 
     * @param maxRange range on full capacity
     */
    public void setMaxRange(int maxRange) {
        this.maxRange = maxRange;
    }

    /**
     * Get connectors
     * 
     * @return list of connecter types
     */
    public ArrayList<String> getConnectors() {
        return connectors;
    }

    /**
     * Set connectors
     * 
     * @param connectors list of connecter types
     */
    public void setConnectors(ArrayList<String> connectors) {
        this.connectors = connectors;
    }

    /**
     * Calculates the range of the vehicle based on maxRange and batteryPercent
     * 
     * @return current range of the vehicle
     */
    public double getCurrentRange() {
        return maxRange * (batteryPercent / 100);
    }

    /**
     * Get a vehicle's image path
     * 
     * @return string of image path
     */
    public String getImgPath() {
        return imgPath;
    }

    /**
     * Set vehicle image path
     * 
     * @param path string of image path
     */
    public void setImgPath(String path) {
        this.imgPath = path;
    }

    /**
     * Set the vehicle id number
     *
     * @param number integer for the id
     */
    public void setVehicleId(int number) {
        this.vehicleId = number;
    }

    /**
     * returns the vehicle id number
     *
     * @return the id integer
     */
    public int getVehicleId() {
        return vehicleId;
    }
}