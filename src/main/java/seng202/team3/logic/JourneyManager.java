package seng202.team3.logic;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.Coordinate;
import seng202.team3.data.entity.Journey;
import seng202.team3.data.entity.Stop;
import seng202.team3.data.entity.Vehicle;

/**
 * Manages creation and storage of journeys
 *
 * @author Angus Kirtlan, James Billows and Michelle Hsieh
 * @version 1.0.4, Sep 22
 */
public class JourneyManager extends ChargerHandler {

    /**
     * The log manager
     */
    private static final Logger logManager = LogManager.getLogger();

    /** {@link Journey Journey} which is the currently selected journey */
    private Journey selectedJourney;

    /**
     * The desired range of chargers to be found
     */
    private double desiredRange;

    /**
     * A list of all the chargers in range
     */
    private ArrayList<Charger> rangeChargers;

    /**
     * Current coordinate to get chargers in range
     */
    private Coordinate currentCoordinate;

    /**
     * Initialises the JourneyManager Class
     */
    public JourneyManager() {
        resetQuery();
        selectedJourney = new Journey();
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
     * Get the start location from entity
     * 
     * @return Coordinate start location
     */
    public Coordinate getStart() {
        return selectedJourney.getStartPosition();
    }

    /**
     * Sets the start location in entity
     * 
     * @param start start location
     */
    public void setStart(Coordinate start) {
        selectedJourney.setStartPosition(start);
    }

    /**
     * Gets end location from entity
     *
     * @return Coordinate end location
     */
    public Coordinate getEnd() {
        return selectedJourney.getEndPosition();
    }

    /**
     * Sets end location in entity
     *
     * @param end end location
     */
    public void setEnd(Coordinate end) {
        selectedJourney.setEndPosition(end);
    }

    /**
     * Sets the current coordinate
     *
     * @param coordinate the current coordinate
     */
    public void setCurrentCoordinate(Coordinate coordinate) {
        currentCoordinate = coordinate;
    }

    /**
     * Gets the current coordinate
     *
     * @return a {@link Coordinate} of the current coordinate for distance
     *         calculations
     */
    public Coordinate getCurrentCoordinate() {
        return currentCoordinate;
    }

    /**
     * Gets charger stops in from journey entity
     *
     * @return a list of {@link Stop}s in the journey
     */
    public List<Stop> getStops() {
        return selectedJourney.getStops();
    }

    /**
     * Clears charger stops in journey entity
     */
    public void clearChargers() {
        selectedJourney.getStops().clear();
    }

    /**
     * Gets the desired range
     *
     * @return a double of the desired range
     */
    public double getDesiredRange() {
        return desiredRange;
    }

    /**
     * Set the desired range
     *
     * @param desiredRange the desired range by the slider
     */
    public void setDesiredRange(Double desiredRange) {
        this.desiredRange = desiredRange;
    }

    /**
     * Gets a list of all the stops in range
     *
     * @return a list of {@link Stop}s in range
     */
    public List<Charger> getRangeChargers() {
        return rangeChargers;
    }

    /**
     * Makes a list of all stops in range
     */
    public void makeRangeChargers() {
        makeAllChargers();
        ArrayList<Charger> chargers = new ArrayList<>(getData());
        rangeChargers = chargers;
        if (currentCoordinate != null) {
            rangeChargers = new ChargerManager().getNearbyChargers(chargers,
                    currentCoordinate, desiredRange);
        }
    }

    /**
     * Gives the current coordinate a name
     */
    public void makeCoordinateName() {
        Coordinate position = GeoLocationHandler.getCoordinate();
        if (position != null) {
            GeoLocationHandler.setCoordinate(position, new JavaScriptBridge().makeLocationName());
            currentCoordinate = position;
        }
    }

    /**
     * Adds a stop without a charger
     *
     * @param coordinate the coordinate of the stop
     */
    public void addNoChargerStop(Coordinate coordinate) {
        Stop stop = new Stop(coordinate);
        selectedJourney.addStop(stop);
    }

    /**
     * Removes the last stop
     */
    public void removeLastStop() {
        if (!selectedJourney.getStops().isEmpty()) {
            Stop stop = selectedJourney.getStops()
                    .get(selectedJourney.getStops().size() - 1);
            selectedJourney.removeStop(stop);
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
     * Adds stop to journey
     *
     * @param stop charger to add to journey
     */
    public void addStop(Stop stop) {
        selectedJourney.addStop(stop);
    }

    /**
     * Removes stop from journey
     *
     * @param stop stop to remove from journey
     */
    public void removeStop(Stop stop) {
        selectedJourney.removeStop(stop);
    }

    /**
     * Clears the current journey
     */
    public void clearJourney() {
        selectedJourney = new Journey();
    }

    /**
     * Sets the selected Journey
     *
     * @param journey journey to set manager to have selected
     */
    public void setSelectedJourney(Journey journey) {
        selectedJourney = journey;
    }

    /**
     * Saves Journey
     */
    public void saveJourney() {
        try {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDateTime now = LocalDateTime.now();
            selectedJourney.setStartDate(dtf.format(now));
            selectedJourney.setUser(UserManager.getUser().getId());
            SqlInterpreter.getInstance().writeJourney(selectedJourney);
            logManager.info("Saved journey successfully");
            // TODO now successfully saves and to throw confirmation prompt up
        } catch (IOException e) {
            logManager.warn(e.getMessage());
        }
    }

    /**
     * Runs a check between all chargers and makes sure that there is no error
     * in range
     *
     * @return boolean true if there is an error
     */
    public boolean checkDistanceBetweenChargers() { // TODO possibly needs fixing
        boolean error = false;

        ArrayList<Coordinate> coordinates = new ArrayList<>();
        coordinates.add(selectedJourney.getStartPosition());
        for (Stop stop : this.getSelectedJourney().getStops()) {
            coordinates.add(stop.getLocation());
        }
        if (selectedJourney.getEndPosition() != null) {
            coordinates.add(selectedJourney.getEndPosition());
        }
        if (coordinates.size() > 1) {
            error = Calculations.calculateDistance(
                    coordinates.get(0), coordinates.get(1)) >= selectedJourney
                            .getVehicle().getMaxRange();
        }

        for (int i = 1; i < coordinates.size() - 1; i++) {
            if (Calculations.calculateDistance(
                    coordinates.get(i), coordinates.get(i + 1)) >= selectedJourney
                            .getVehicle().getMaxRange()) {
                error = true;
            }
        }
        return error;
    }

}