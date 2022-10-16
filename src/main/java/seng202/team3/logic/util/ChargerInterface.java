package seng202.team3.logic.util;

/**
 * A ChargerInterface that relates all objects that can add, delete or edit
 * chargers
 *
 * @author Michelle Hsieh
 * @version 1.0.0, Sep 22
 */
public interface ChargerInterface {

    /**
     * Adds a charger
     */
    public void addCharger();

    /**
     * Removes the selected charger
     */
    public void deleteCharger();

    /**
     * Edits the selected charger
     */
    public void editCharger();
}
