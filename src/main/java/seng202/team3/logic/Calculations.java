package seng202.team3.logic;

import static java.lang.Math.cos;

import seng202.team3.data.entity.Coordinate;

/**
 * A final class which has static methods to handle distance calculations
 * between two {@link Coordinate}
 *
 * @author Michelle Hsieh
 * @version 1.0.0, Aug 22
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
        double lat1 = location1.getLat() / (180 / Math.PI);
        double lon1 = location1.getLon() / (180 / Math.PI);
        double lat2 = location2.getLat() / (180 / Math.PI);
        double lon2 = location2.getLon() / (180 / Math.PI);

        //The distance between two locations. Is this because the world is round.
        return 6371 * Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2)
                * cos(lon2 - lon1));
    }
}
