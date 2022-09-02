package seng202.team3.logic;

import static java.lang.Math.cos;

import seng202.team3.data.entity.Coordinate;

/**
 * A final class which has static methods to handle distance calculations
 * between two {@link Coordinate}
 *
 * @author Michelle Hsieh
 * @version 1.0.1, Aug 22
 */
public final class Calculations {

    /**
     * Returns the difference (in km) between two locations
     *
     * @param location1 {@link Coordinate} The first location coordinates
     * @param location2 {@link Coordinate} The second location coordinates
     * @return double; the distance (in km) between the two coordinates
     */
    public static double calculateDistance(Coordinate location1, Coordinate location2) {
        if (location1 == location2) {
            return 0.0;
        }
        double lat1 = location1.getLat() / (180 / Math.PI);
        double lon1 = location1.getLon() / (180 / Math.PI);
        double lat2 = location2.getLat() / (180 / Math.PI);
        double lon2 = location2.getLon() / (180 / Math.PI);

        // The distance between two locations. Is this because the world is round.
        return 6371 * Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2)
                * cos(lon2 - lon1));
    }

    /**
     * Takes a {@link Coordinate} of the charger and a start and end coordinate,
     * with a total
     * distance (km) which a charger cannot exceed from the start and end point
     * added together. Returns true if in range.
     *
     * @param chargerCd {@link Coordinate} of the charger to query
     * @param startCd   {@link Coordinate} of the start point
     * @param endCd     {@link Coordinate} of the end point
     * @param distance  double; the maximum point reachable
     * @return boolean; true if the point is in range
     */
    public static boolean isWithinRange(Coordinate chargerCd, Coordinate startCd, Coordinate endCd,
            double distance) {
        return ((Calculations.calculateDistance(chargerCd, startCd) + Calculations
                .calculateDistance(chargerCd, endCd)) <= distance);
    }
}
