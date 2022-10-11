package seng202.team3.logic;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team3.data.entity.Coordinate;

/**
 * A Singleton Class to handle the geolocation parsing
 *
 * @author Michelle Hsieh
 * @version 1.0.0, Sep 22
 */
public class GeoLocationHandler {

    /**
     * Logger
     */
    private static final Logger logManager = LogManager.getLogger();

    /**
     * Coordinate to perform the geolocation with
     */
    private static Coordinate coordinate;

    // TODO remove set positions
    /**
     * Default coordinate all nz
     */
    public static final Coordinate DEFAULT_COORDINATE = new Coordinate(
            -40.9006, 174.8860, "New Zealand");

    /**
     * DatabaseReader for geolocation data
     */
    private static DatabaseReader dbReader;

    /**
     * Private initaliser for the geolocation handler
     */
    private GeoLocationHandler() {
    }

    /**
     * Clear current instance - USED FOR TESTING ONLY
     */
    public static void clearInstance() {
        clearCoordinate();
    }

    /**
     * Gets the coordinate
     *
     * @return {@link seng202.team3.data.entity.Coordinate} the selected coordinate
     */
    public static Coordinate getCoordinate() {

        if (coordinate == null) {
            coordinate = DEFAULT_COORDINATE;
            logManager.info("Location not set: set to default coordinate");
        }

        return coordinate;
    }

    /**
     * Sets the coordinate
     *
     * @param coordinate the {@link seng202.team3.data.entity.Coordinate} to set the
     *                   coordinate
     * @param name       the address of the coordinate
     */
    public static void setCoordinate(Coordinate coordinate, String name) {
        // TODO remove set positions
        GeoLocationHandler.coordinate = coordinate;
        String[] splitAddress = name.split(",", 10);
        if (splitAddress.length > 6) {
            name = "";
            name += splitAddress[0] + splitAddress[1] + ", "
                    + splitAddress[3] + splitAddress[splitAddress.length - 2] + ", "
                    + splitAddress[splitAddress.length - 1];
        }
        coordinate.setAddress(name);
    }

    /**
     * Clears the coordinate
     */
    public static void clearCoordinate() {
        GeoLocationHandler.coordinate = null;
    }

    /**
     * Finds the users city from their ip address
     * 
     * @throws IOException     if the database cannot be read
     * @throws GeoIp2Exception if a city cannot be found
     */
    public static void setCurrentLocation() throws IOException, GeoIp2Exception {
        loadGeoLocationData(); // load in database if not already loaded

        String ipStr = getIp();
        InetAddress userIp = InetAddress.getByName(ipStr);
        logManager.info("Retrieved users location using ip: {}", ipStr);
        Coordinate location = new Coordinate();
        CityResponse response = dbReader.city(userIp);

        location.setLat(response.getLocation().getLatitude());
        location.setLon(response.getLocation().getLongitude());
        location.setAddress("My Position");
        GeoLocationHandler.setCoordinate(location, location.getAddress());
        logManager.info("Current position set: {}, {}", location.getLat(), location.getLon());
    }

    /**
     * Loads in the GeoLocation database which maps ips to cities
     * 
     * @throws IOException if the database cannot be read
     */
    private static void loadGeoLocationData() throws IOException {
        if (dbReader == null) {
            File ipDb = File.createTempFile("geolocation", "db");
            FileUtils.copyInputStreamToFile(
                    GeoLocationHandler.class.getResourceAsStream("/GeoLite2-City.mmdb"), ipDb);

            dbReader = new DatabaseReader.Builder(ipDb).build();
            logManager.info("Loaded GeoLocation database from file");
        }
    }

    /**
     * Gets the external ip of the user through amazon web services
     * 
     * @return string ip of the user in dotted decimal
     * @throws IOException if the fetch fails or ip cannot be read
     */
    private static String getIp() throws IOException {
        try (InputStream ip = new URL("http://checkip.amazonaws.com").openStream();
                BufferedReader input = new BufferedReader(new InputStreamReader(ip))) {
            logManager.info("User ip retrieved from Amazon Web Services");
            return input.readLine(); // ip
        }

    }
}
