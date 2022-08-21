package seng202.team3.logic;

import java.util.ArrayList;

import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.Coordinate;
import seng202.team3.data.entity.Journey;
import seng202.team3.data.entity.Vehicle;

/**
 * 
 * 
 * @author Angus Kirtlan
 * @version 1.0.0, Aug 22
 */
public class JourneyManager {
    /** {@link Journey Journey} which is the currently selected journey */
    private Journey selectedJourney;


    /** Staring {@link Coordinate coordinate} of journey*/
    private Coordinate start; 

    /** Ending {@link Coordinate coordinate} of journey */
    private Coordinate end;

    /**
     * Gets selected journey
     * 
     * @return selected journey
     */
    public Journey getSelectedJourney() {
        return selectedJourney;
    }

    /**
     * Sets start point of journey
     * 
     * @param start start of journey
     */
    public void setStart(Coordinate start) {
        this.start = start;
    }

    /**
     * Sets end point of journey
     * 
     * @param end end of journey
     */
    public void setEnd(Coordinate end) {
        this.end = end;
    }

    /**
     * Starts new journey from selected start and end point
     */
    public void startNewJourney () {
        if ((start != null) && (end != null));
            selectedJourney = new Journey(start, end);
    }
    
    /**
     * Selects vehicle to use for selected journey
     * 
     * @param vehicle vehicle to use for journey
     */
    public void selectVehicle(Vehicle vehicle) {
        selectedJourney.setVehicle(vehicle);
    }

    /**
     * Adds charger to journey
     * 
     * @param charger charger to add to journey
     */
    public void addCharger(Charger charger) {
        selectedJourney.addCharger(charger);
    }
    
    /**
     * Removes charger from journey
     * 
     * @param charger charger to remove from journey
     */
    public void removeCharger(Charger charger) {
        selectedJourney.removeCharger(charger);
    }


    /**
     * Calculates all candidate chargers for the journey
     * Calculates rectangle of width 2*(car range) and length end-start
     * Perhaps doesnt return and just adds to candidate chargers
     * 
     * @return Array of candidate chargers
     */
    public ArrayList<Charger> callCalculations() {
        return null;
    }
}
