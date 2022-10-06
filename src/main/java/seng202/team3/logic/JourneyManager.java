package seng202.team3.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seng202.team3.data.database.SqlInterpreter;
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

    ///** Coordinate*/
    //private Coordinate tempPosition;

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

    ///**
    // * Gets temporary position
    // *
    // * @return temporary position
    // */
    //public Coordinate getPosition() {
    //    return tempPosition;
    //}

    ///**
    // * Sets temporary position
    // *
    // * @param tempPosition temporary position
    // */
    //public void setPosition(Coordinate tempPosition) {
    //    this.tempPosition = tempPosition;
    //}

    /**
     * Initialises a new Journeymv
     */
    public void startNewJourney() {
        if ((selectedJourney.getStartPosition() != null) 
            && (selectedJourney.getEndPosition() != null)) {
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
    }

    /**
     * Saves Journey
     */
    public void saveJourney() {
        try {
            SqlInterpreter.getInstance().writeJourney(selectedJourney);
            //TODO give feedback to user, reset journey so no accidental duplicate?
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets boolean for if there is an error or not
     *
     * @return boolean of error
     */
    public boolean checkDistanceBetweenChargers() { //TODO possibly needs fixing
        ArrayList<Coordinate> coordinates = new ArrayList<>();
        coordinates.add(selectedJourney.getStartPosition());
        for (Charger charger : this.getSelectedJourney().getChargers()) {
            coordinates.add(charger.getLocation());
        }
        coordinates.add(selectedJourney.getEndPosition());
        boolean error = Calculations.calculateDistance(coordinates.get(0), coordinates.get(1))
                >= selectedJourney.getVehicleRange();

        for (int i = 1; i < coordinates.size() - 1; i++) {
            if (Calculations.calculateDistance(coordinates.get(i), coordinates.get(i + 1))
                    >= selectedJourney.getVehicleRange()) {
                error = true;
            }
        }
        return error;
    }

}