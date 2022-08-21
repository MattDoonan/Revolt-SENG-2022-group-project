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
    Journey selectedJourney;

    /** Staring {@link Coordinate coordinate} of journey*/
    Coordinate start; 

    /** Ending {@link Coordinate coordinate} of journey */
    Coordinate end;

    /**
     * Sets start point of journey
     */
    public void setStart(Coordinate start) {
        this.start = start;
    }

    /**
     * Sets end point of journey
     */
    public void setEnd(Coordinate end) {
        this.end = end;
    }

    /**
     * Starts new journey from selected start and end point
     */
    public void startNewJourney () {
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
     * @param charger
     */
    public void addCharger(Charger charger) {
        selectedJourney.addCharger(charger);
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
