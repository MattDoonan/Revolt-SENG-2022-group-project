package seng202.team3.logic;

/**
 * An interface that deals with adding, deleting and editing chargers
 *
 * @author Michelle Hsieh
 * @version 1.0.0, Sep 22
 */
public interface ChargerInterface {
    /**
     * Adds a charger
     */
    void addCharger();

    /**
     * Removes the selected charger
     */
    void deleteCharger();

    /**
     * Edits the selected charger
     */
    void editCharger();

}
