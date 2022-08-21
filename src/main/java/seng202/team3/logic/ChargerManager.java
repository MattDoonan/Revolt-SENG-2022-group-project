package seng202.team3.logic;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.Coordinate;

/**
 * Manages charger-related functionality
 *
 * @author Michelle Hsieh
 * @version 1.0.0, Aug 22
 */
public class ChargerManager {
    /** {@link Charger Charger} Charger which is the currently selected charger */
    Charger selectedCharger;

    /** Constructor for ChargerManager */
    public ChargerManager() {
        setSelectedCharger(null);
    }

    /** Constructor for ChargerManager */
    public ChargerManager(Charger selectedCharger) {
        setSelectedCharger(selectedCharger);
    }

    /**
     * Sets selectedCharger
     *
     * @param selectedCharger {@link Charger} the Charger which is being selected.
     */
    public void setSelectedCharger(Charger selectedCharger) {
        this.selectedCharger = selectedCharger;
    }

    /**
     * Gets the selectedCharger
     *
     * @return {@link Charger} selectedCharger which is the currently selected charger
     */
    public Charger getSelectedCharger() {
        return selectedCharger;
    }

    /**
     * Calculates the distance between two {@link Charger} (selectedCharger, input charger).
     *
     * @param toCharger {@link Charger} Charger which selectedCharger is calculating from.
     * @return double; the distance between chargers in kilometres.
     */
    public double distanceBetweenChargers(Charger toCharger) {
        Coordinate location1 = selectedCharger.getLocation();
        Coordinate location2 = toCharger.getLocation();
        return (Calculations.calculateDistance(location1, location2));
    }

    /**
     * Returns the list of the closest chargers, in order from closest to furthest away.
     *
     * @param chargers {@link Charger} An ArrayList of Chargers
     * @param location {@link Coordinate} A coordinate of the location to calculate distance from
     * @param distance double, the maximum distance to filter chargers by
     * @return ArrayList<> of the {@link Charger} chargers sorted from closest to furthest
     */
    public ArrayList<Charger> getNearbyChargers(ArrayList<Charger> chargers, Coordinate location,
                                                double distance) {

        List<Charger> sortedChargers = chargers.stream()
                .filter(dist -> Calculations.calculateDistance(dist.getLocation(),
                        location) <= distance)
                .sorted(Comparator.comparingDouble(dist -> Calculations.calculateDistance(
                        dist.getLocation(), location)))
                .toList();

        return (new ArrayList<>(sortedChargers));
    }

    /**
     * Changes the warnings of the selected charger
     * 
     * @param warning the warning to change the state of
     * @param state boolean of state of warning to change to
     */
    public void toggleWarning(String warning, boolean state) {
        switch (warning) {
            case "high cost":
                selectedCharger.setWarningHighCost(state);
                break;
            case "long wait":
                selectedCharger.setWarningLongWait(state);
                break;
            case "low availability":
                selectedCharger.setWarningLowAvailability(state);
                break;
            default:
                throw new IllegalArgumentException("'high cost', 'long wait', 'low availability'");
        }
    }
}