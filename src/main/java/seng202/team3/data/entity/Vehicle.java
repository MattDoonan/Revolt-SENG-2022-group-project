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
    private Double batteryPercent;

    /** Range of vehicle with full battery capacity, in kms */
    private int maxRange;

    /** List of connector types */
    private ArrayList<String> connectors;

    /** Path to vehicle image */
    private String imgPath;

    /**
     * Default filepath for missing images
     */
    public static final String DEFAULTIMGPATH = "";

    /**
     * Constructor for a vehicle
     * 
     * @param make       make of the vehicle
     * @param model      model of the vehicle
     * @param maxRange   maximum range of vehicle with full battery capacity, in kms
     * @param connectors list of connectors supported by the vehicle
     */
    public Vehicle(String make, String model, int maxRange,
            ArrayList<String> connectors) {
        this.make = make;
        this.model = model;
        this.maxRange = maxRange;
        this.connectors = connectors;
        this.imgPath = DEFAULTIMGPATH;
        this.batteryPercent = 100.0;
    }

    /**
     * Default constructor to support future csv parsing
     */
    public Vehicle() {
        // Unused
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
    public Double getBatteryPercent() {
        return batteryPercent;
    }

    /**
     * Set current battery percentage
     *
     * @param batteryPercent current battery percentage
     */
    public void setBatteryPercent(Double batteryPercent) {
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
    public Double getCurrentRange() {
        return (double) (maxRange * (batteryPercent / 100));
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

    /**
     * Gets the effective range of the Vehicle
     *
     * @return the effective range vaule
     */
    public double getEffectiveRange() {
        return this.getCurrentRange() * this.getBatteryPercent() / 100.0;
    }

    /**
     * {@inheritDoc}}
     */
    @Override
    public boolean equals(Object o) {
        Vehicle v;
        if (o instanceof Vehicle) {
            v = (Vehicle) o;
        } else {
            return false;
        }

        return v.getMake().equals(this.getMake())
                && v.getModel().equals(this.getModel())
                && v.getBatteryPercent().equals(this.getBatteryPercent())
                && v.getMaxRange() == this.getMaxRange()
                && v.getConnectors().equals(this.getConnectors())
                && v.getCurrentRange().equals(this.getCurrentRange())
                && v.getImgPath().equals(this.getImgPath())
                && v.getVehicleId() == this.getVehicleId();
    }
}
