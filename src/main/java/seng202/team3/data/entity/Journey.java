package seng202.team3.data.entity;

import java.util.ArrayList;
import java.util.List;
import seng202.team3.logic.UserManager;

/**
 * Representation of a past or current Journey
 *
 * @author Angus Kirtlan
 * @version 1.0.0, Aug 22
 */
public class Journey extends Entity {
    /** {@link Charger Chargers} used on journey */
    private ArrayList<Charger> chargers = new ArrayList<>();

    /** {@link Vehicle Vehicle} used for journey */
    private Vehicle vehicle;

    /** {@link Coordinate Coordinate} of start position */
    private Coordinate startPosition;

    /** {@link Coordinate Coordinate} of end position */
    private Coordinate endPosition;

    /** Starting date and time */
    private String startDate;

    /** Name of the journey */
    private String title;

    /**
     * id of user who took the journey
     */
    private int user;

    /**
     * Constructor for the Journey
     *
     * @param vehicle       vehicle partaking in the journey
     * @param startPosition start coordinate of the journey
     * @param endPosition   end coordinate of the journey
     * @param startDate     start date of the journey
     * @param title         name of the journey
     */
    public Journey(Vehicle vehicle, Coordinate startPosition,
            Coordinate endPosition, String startDate,
            String title) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.vehicle = vehicle;
        this.startDate = startDate;
        this.title = title;
        this.user = UserManager.getUser().getId();
    }

    /**
     * Unused constructor
     */
    public Journey() {
        // Unused
    }

    /**
     * Get array of chargers used on journey
     *
     * @return chargers in journey
     */
    public List<Charger> getChargers() {
        return chargers;
    }

    /**
     * Add new {@link Charger charger} to journey
     *
     * @param charger charger to add
     */
    public void addCharger(Charger charger) {
        chargers.add(charger);
    }

    /**
     * Remove a {@link Charger charger} from the journey
     *
     * @param charger charger to remove
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
     * Sets the start position of the journey
     *
     * @param coord starting position
     */
    public void setStartPosition(Coordinate coord) {
        startPosition = coord;
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
     * Sets the end position of the journey
     *
     * @param coord ending position
     */
    public void setEndPosition(Coordinate coord) {
        endPosition = coord;
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
    public String getTitle() {
        return title;
    }

    /**
     * Set end date of journey
     *
     * @param title name of journey
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * sets the user ID number
     *
     * @param number integer for the id
     */
    public void setUser(int number) {
        this.user = number;
    }

    /**
     * returns the user id number
     *
     * @return the integer id number
     */
    public int getUser() {
        return this.user;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object o) {
        Journey j;
        if (o instanceof Journey) {
            j = (Journey) o;
        } else {
            return false;
        }
        return j.getChargers().equals(this.getChargers())
                && j.getVehicle().equals(this.getVehicle())
                && j.getStartPosition().equals(this.getStartPosition())
                && j.getEndPosition().equals(this.getEndPosition())
                && j.getStartDate().equals(this.getStartDate())
                && j.getTitle().equals(this.getTitle())
                && j.getUser() == this.getUser();
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        int result = (getId() ^ (getId() >>> 32));
        result = 31 * result + chargers.hashCode();
        result = 31 * result + vehicle.hashCode();
        result = 31 * result + startPosition.hashCode();
        result = 31 * result + endPosition.hashCode();
        result = 31 * result + startDate.hashCode();
        result = 31 * result + title.hashCode();

        return result;
    }

}
