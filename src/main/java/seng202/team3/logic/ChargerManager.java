package seng202.team3.logic;

import seng202.team3.data.entity.Charger;
import java.lang.Math;

import static java.lang.Math.cos;

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
    public ChargerManager(Charger selectedCharger) {
        setSelectedCharger(selectedCharger);
    }

    /**
     * Sets selectedCharger
     *
     * @param selectedCharger {@link Charger} the Charger which is being selected.
     */
    public void setSelectedCharger(Charger selectedCharger) { this.selectedCharger = selectedCharger; }

    /**
     * Gets the selectedCharger
     *
     * @return {@link Charger} selectedCharger which is the currently selected charger
     */
    public Charger getSelectedCharger() {
        return selectedCharger;
    }

    /**
     * Calculates the distance between two {@link Charger} Chargers (selectedCharger and param input charger).
     *
     * @param toCharger {@link Charger} Charger, the charger which selectedCharger is calculating from.
     * @return double; the distance between chargers in kilometres.
     */
    public double distanceBetweenChargers(Charger toCharger) {
        double lat1 = selectedCharger.getLocation().getLat()/(180/Math.PI);
        double lon1 = selectedCharger.getLocation().getLon()/(180/Math.PI);
        double lat2 = toCharger.getLocation().getLat()/(180/Math.PI);
        double lon2 = toCharger.getLocation().getLon()/(180/Math.PI);

        //The distance between the selectedCharger and another charger. Is this because the world is round.
        return 6378.8 * Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2)
                * cos(lon2-lon1));

    }

}
