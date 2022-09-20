package seng202.team3.logic;

import java.io.IOException;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.Coordinate;

/**
 * Logic layer for the main Controller
 *
 * @author Matthew Doonan, Michelle Hsieh
 * @version 1.0.1, Aug 22
 *
 */

public class MainManager extends ChargerHandler implements ChargerInterface {

    private double distance = 0;
    private final ChargerManager chargerManager = new ChargerManager();
    private ArrayList<Coordinate> savedCoordinates;

    /**
     * Saves the MainController to call later
     */
    public MainManager() {
        selectedCoordinate = new Coordinate(null, null, -43.522518157958984, 172.5811767578125);
        savedCoordinates = new ArrayList<>();
    }

    /**
     * Sets the distance
     *
     * @param distance a double of the set distance
     */
    public void setDistance(double distance) {
        this.distance = distance;
    }

    /**
     * Gets the distance
     *
     * @return the double distance of the distance
     */
    public double getDistance() {
        return distance;
    }

    /**
     * Adds a coordinate into the coordinate list
     *
     * @param coordinate {@link Coordinate} a coordinate to be added into the list
     *                   of coordinates
     */
    public void addCoordinate(Coordinate coordinate) {
        savedCoordinates.add(coordinate);
    }

    /**
     * Deletes a coordinate according to address
     *
     * @param address The string address of the coordinate to be deleted
     */
    public void deleteCoordinate(String address) {
        int deletedCoord = 0;
        for (int i = 0; i < savedCoordinates.size(); i++) {
            if (address.equals(savedCoordinates.get(i).getAddress())) {
                deletedCoord = i;
            }
        }
        savedCoordinates.remove(deletedCoord);
    }

    /**
     * Returns an arraylist of all the saved coordinates
     *
     * @return an ArrayList of {@link Coordinate} coordinates
     */
    public ArrayList<Coordinate> getSavedCoordinates() {
        return savedCoordinates;
    }

    /**
     * Send chargers within range of the selected location to the table and temp
     * data.
     *
     * @return an ObservableList of {@link Charger}s which are nearby
     */
    public ObservableList<Charger> compareDistance() {
        ArrayList<Charger> arrayChargers = new ArrayList<>(chargerData);
        if (distance != 0) {
            arrayChargers = chargerManager.getNearbyChargers(
                    arrayChargers, selectedCoordinate, distance);
        }
        ObservableList<Charger> closerChargers = FXCollections.observableList(arrayChargers);
        return closerChargers;
    }

    /**
     * Returns a list of the closest chargers according to set distance from
     * position
     *
     * @return an Observable list of chargers
     */
    public ObservableList<Charger> getCloseChargerData() {
        if (selectedCoordinate == null) {
            return getData();
        }
        return compareDistance();
    }

    /**
     * Adds a charger at the location of the coordinate.
     */
    @Override
    public void addCharger() {
        new JavaScriptBridge().loadChargerEdit(null);
    }

    /**
     * Removes the selected charger and replaces it with null
     *
     */
    @Override
    public void deleteCharger() {
        if (selectedCharger != null) {
            try {
                SqlInterpreter.getInstance().deleteData("charger", selectedCharger.getChargerId());
                selectedCharger = null;
                makeAllChargers();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Uses the JavaScript Bridge to load the charger edit functionality of the
     * selected charger
     */
    @Override
    public void editCharger() {
        if (getSelectedCharger() != null) {
            JavaScriptBridge bridge = new JavaScriptBridge();
            bridge.loadMoreInfo(getSelectedCharger().getChargerId());
        }
    }

}
