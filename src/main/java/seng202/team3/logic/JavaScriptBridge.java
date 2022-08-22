package seng202.team3.logic;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import seng202.team3.data.entity.Coordinate;

/**
 * Converts JavaScript to Objects by hocus pocus
 *
 * @author Michelle Hsieh, based off code from Morgan English
 * @version 1.0.0, Aug 22
 */
public class JavaScriptBridge {

    /**
     * Makes a coordinate from a click and returns it.
     *
     * @param latlng the string created with latitude and longitude
     * @return {@link Coordinate} generated object
     */
    public Coordinate makeCoordinateFromClick(String latlng) {
        JSONParser parser = new JSONParser();
        Coordinate coord = new Coordinate();
        try {
            JSONObject latlng_json = (JSONObject) parser.parse(latlng);
            float lat = ((Double)latlng_json.get("lat")).floatValue();
            float lng = ((Double) latlng_json.get("lng")).floatValue();
            coord.setLat((double) lat);
            coord.setLon((double) lng);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return coord;
    }



}
