package seng202.team3.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seng202.team3.data.database.QueryBuilder;
import seng202.team3.data.database.QueryBuilderImpl;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.Connector;
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

    /**
     * Get the start location from entity
     * @return Coordinate start location
     */
    public Coordinate getStart() {
        return selectedJourney.getStartPosition();
    }

    /**
     * Sets the start location in entity
     * @param start start location
     */
    public void setStart(Coordinate start) {
        selectedJourney.setStartPosition(start);
    }

    /**
     * Gets end location from entity
     * @return Coordinate end location
     */
    public Coordinate getEnd() {
        return selectedJourney.getEndPosition();
    }

    /**
     * Sets end location in entity
     * @param end end location
     */
    public void setEnd(Coordinate end) {
        selectedJourney.setEndPosition(end);
    }

    /**
     * Gets charger stops in from journey entity
     * @return List<Charger> chargers
     */
    public List<Charger> getChargers() {
        return selectedJourney.getChargers();
    }

    /**
     * Clears charger stops in journey entity
     */
    public void clearChargers() {
        selectedJourney.getChargers().clear();
    }

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
     * TODO Docstring
     */
    public void makeDefaultVehicle() {
        ArrayList<String> connectorArray = new ArrayList<>();
        QueryBuilder query = new QueryBuilderImpl().withSource("connector");
        try {
            List<Connector> connectorList = new ArrayList<>();
            for (Object o : SqlInterpreter.getInstance()
                    .readData(query.build(), Connector.class)) {
                connectorList.add((Connector) o);
            }

            List<String> connectorStrings = connectorList.stream()
                    .map(Connector::getType)
                    .distinct().toList();

            connectorArray.addAll(connectorStrings);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Vehicle vehicle = new Vehicle("Default", "Default", 0, connectorArray);
        vehicle.setBatteryPercent(100.0);

        getSelectedJourney().setVehicle(vehicle);
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
        if (selectedJourney.getEndPosition() != null) {
            coordinates.add(selectedJourney.getEndPosition());
        }
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