package seng202.team3.data.entity;

import java.util.ArrayList;

/**
 * Representation of a past or current Journey
 * 
 * @author Angus Kirtlan
 * @version 1.0.0, Aug 22
 */
public class Journey {
    /** {@link Charger Chargers} used on journey */
    private ArrayList<Charger> chargers = new ArrayList<Charger>();

    /** {@link Vehicle Vehicle} used for journey*/
    private Vehicle vehicle;

    /** {@link Coordinate Coordinate} of start position */
    private Coordinate startPosition;

    /** {@link Coordinate Coordinate} of end position */
    private Coordinate endPosition;

    /** Starting date and time */
    private String startDate;

    /** Ending date and time */
    private String endDate;

    /** User has favourited */
    private boolean isFavourite;

    /** 
     * Constructor for the Journey 
     */
    public Journey(Coordinate startPosition, Coordinate endPosition) {
        ArrayList<String> connectors = new ArrayList<>();
        connectors.add("temp"); //change default connectors to all for veh?
        this.vehicle = new Vehicle("temp","temp","temp",100,20000,connectors);
        this.startPosition = startPosition;
        this.endPosition = endPosition;
    }

    /**
     * Get array of chargers used on journey
     * 
     * @return chargers in journey
     */
    public ArrayList<Charger> getChargers() {
        return chargers;
    }

    /** 
     * Add new {@link Charger charger} to journey
     */
    public void addCharger(Charger charger) {
        chargers.add(charger);
    }

    /**
     * Remove a {@link Charger charger} from the journey
     */
    public void removeCharger(Charger charger) {
        chargers.remove(charger);
    }

    /**
     * Gets the {@link Vehicle vehicle} used for the journey
     * 
     * @return vehicle used for the journey
     */
    public Vehicle getVehicle() {
        return vehicle;
    }

    /**
     * Sets the {@link Vehicle vehicle} used for the journey
     * 
     * @param vehicle vehicle used for the journey
     */
    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    /**
     * Gets the {@link Coordinate coordinate} of the start of the journey
     * 
     * @return start position of journey
     */
    public Coordinate getStartPosition() {
        return startPosition;
    }
    
    /**
     * Gets the {@link Coordinate coordinate} of the end of the journey
     * 
     * @return end position of journey
     */
    public Coordinate getEndPosition() {
        return endPosition;
    }

    /**
     * Get start date of journey
     * 
     * @return start date of journey
     */
    public String getStartDate() {
        return startDate;
    }

    /**
     * Set start date of journey
     * 
     * @param startDate start date of journey
     */
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    /**
     * Get end date of journey
     * 
     * @return end date of journey
     */
    public String getEndDate() {
        return endDate;
    }

    /**
     * Set end date of journey
     * 
     * @param endDate end date of journey
     */
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    /**
     * Get favourited value of journey
     * 
     * @return favourite boolean value
     */
    public boolean getFavourite() {
        return isFavourite;
    }

    /**
     * Add or remove journey from favourites
     * 
     * @param isFavourite boolean of what to change favourite to
     */
    public void setFavourite(boolean isFavourite) {
        this.isFavourite = isFavourite;
    }

}
