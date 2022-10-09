package seng202.team3.data.entity;

import java.util.List;
import seng202.team3.logic.UserManager;

/**
 * Stores information about vehicles
 *
 * @author James Billows
 * @version 1.0.0, Aug 2022
 */
public class Vehicle implements Storable {

    /** Vehicle id */
    private int vehicleId;

    /** Make of the vehicle */
    private String make;

    /** Model of the vehicle */
    private String model;

    /** Range of vehicle with full battery capacity, in kms */
    private int maxRange;

    /** List of connector types */
    private List<String> connectors;

    /** Path to vehicle image */
    private String imgPath;

    /**
     * userid of the vehicle owner
     */
    private int owner;

    /** Flag for if this vehicle is the user's currently-selected vehicle */
    private boolean currVehicle;

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
            List<String> connectors) {
        this.make = make;
        this.model = model;
        this.maxRange = maxRange;
        this.connectors = connectors;
        this.imgPath = DEFAULTIMGPATH;
        this.currVehicle = false;
        setOwner(UserManager.getUser().getUserid());
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
    public List<String> getConnectors() {
        return connectors;
    }

    /**
     * Set connectors
     *
     * @param connectors list of connecter types
     */
    public void setConnectors(List<String> connectors) {
        this.connectors = connectors;
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
     * Set whether this vehicle is the user's currently selected one
     *
     * @param flag true or false
     */
    public void setcurrVehicle(Boolean flag) {
        this.currVehicle = flag;
    }

    /**
     * returns whether this vehicle is the user's currently selected one
     *
     * @return the vehicle status
     */
    public boolean getCurrVehicle() {
        return currVehicle;
    }

    /**
     * Set the user id number
     *
     * @param number integer for the id
     */
    public void setOwner(int number) {
        this.owner = number;
    }

    /**
     * returns the user id number
     *
     * @return the id integer
     */
    public int getOwner() {
        return owner;
    }

    /**
     * toString function (mostly for debugging purposes)
     * 
     * @return string representation of vehicle
     */
    public String toString() {
        return "MAKE: " + make + "  MODEL: " + model + "  MAXRANGE: " + maxRange
                + "  ID: " + vehicleId + "  OWNER: " + owner + "  CURR: " + currVehicle;
    }

    /**
     * {@inheritDoc}
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
                && v.getMaxRange() == this.getMaxRange()
                && v.getConnectors().equals(this.getConnectors())
                && v.getImgPath().equals(this.getImgPath())
                && v.getVehicleId() == this.getVehicleId()
                && v.getOwner() == this.getOwner();
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        int result = (vehicleId ^ (vehicleId >>> 32));
        result = 31 * result + make.hashCode();
        result = 31 * result + model.hashCode();
        result = 31 * result + connectors.hashCode();
        result = 31 * result + imgPath.hashCode();

        return result;
    }
}
