package seng202.team3.logic;

import java.util.ArrayList;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.Coordinate;
import seng202.team3.data.entity.Journey;
import seng202.team3.data.entity.Vehicle;

/**
 * Manages creation and storage of journeys
 *
 * @author Angus Kirtlan and James Billows
 * @version 1.0.0, Aug 22
 */
public class JourneyManager extends ChargerHandler {
    /** {@link Journey Journey} which is the currently selected journey */
    private Journey selectedJourney;

    /** Staring {@link Coordinate coordinate} of journey */
    private Coordinate start;

    /** Ending {@link Coordinate coordinate} of journey */
    private Coordinate end;


    private Coordinate tempPosition;

    /** List of {@link Charger chargers} that are candidates for journey*/
    private ObservableList<Charger> candidateChargers;


    /**
     * Initialises the JourneyManager Class
     */
    public JourneyManager() {
        selectedJourney = new Journey();
    }

    /**
     * Gets a list of candidate chargers
     *
     * @return an Observable list of all the candidate chargers
     */
    public ObservableList<Charger> getCandidateChargers() {
        return candidateChargers;
    }

    /**
     * Gets selected journey
     *
     * @return selected journey
     */
    public Journey getSelectedJourney() {
        return selectedJourney;
    }

    /**
     * Gets temporary position
     *
     * @return temporary position
     */
    public Coordinate getPosition() {
        return tempPosition;
    }

    /**
     * Sets temporary position
     *
     * @param tempPosition temporary position
     */
    public void setPosition(Coordinate tempPosition) {
        this.tempPosition = tempPosition;
    }

    /**
     * Gets start position
     *
     * @return start of journey
     */
    public Coordinate getStart() {
        return selectedJourney.getStartPosition();
    }

    /**
     * Sets start point of journey
     *
     * @param start start of journey
     */
    public void setStart(Coordinate start) {
        selectedJourney.setStartPosition(start);
    }

    /**
     * Gets end position
     *
     * @return end of journey
     */
    public Coordinate getEnd() {
        return selectedJourney.getEndPosition();
    }

    /**
     * Sets end point of journey
     *
     * @param end end of journey
     */
    public void setEnd(Coordinate end) {
        selectedJourney.setEndPosition(end);
    }

    /**
     * Initialises a new Journey
     */
    public void startNewJourney() {
        if ((start != null) && (end != null)) {
            selectedJourney = new Journey();
        }
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
     * Calculates ellipse where start and end are foci
     * Assumes full tank max range
     * Assigns the candidate chargers
     *
     * @param chargers an array of all chargers
     */
    public void makeCandidateChargers(ArrayList<Charger> chargers) {
        double distance = Calculations.calculateDistance(selectedJourney.getStartPosition(),
                selectedJourney.getEndPosition()) * 1.1;
        ArrayList<Charger> validChargers = chargers.stream()
                .filter(charger -> (Calculations.isWithinRange(charger.getLocation(),
                        selectedJourney.getStartPosition(),
                        selectedJourney.getEndPosition(), distance)))
                .collect(Collectors.toCollection(ArrayList::new));
        candidateChargers = FXCollections.observableList(validChargers);
    }

    /**
     * Clears the current journey
     */
    public void clearJourney() {
        selectedJourney = new Journey();
        setStart(null);
        setEnd(null);
    }

}