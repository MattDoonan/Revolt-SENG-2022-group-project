package seng202.team3.logic;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import seng202.team3.data.entity.Coordinate;
import seng202.team3.gui.MainController;

/**
 * Converts JavaScript to Objects by hocus pocus
 *
 * @author Michelle Hsieh, based off code from Morgan English
 * @version 1.0.0, Aug 22
 */
public class JavaScriptBridge {

    /**
     * Makes a coordinate from a click and sends it to MapManager's MainController
     *
     * @param latlng the string created with latitude and longitude
     */
    public void addCoordinateFromClick(String latlng) {

        // Temporary setting code to make sure setting works
        TempData.setCoordinate(parseCoordinate(latlng));
        MainController controller = TempData.getController();
        controller.getManager().setPosition(TempData.getCoordinate());
    }

    /**
     * Parses a string into a series of coordinates
     *
     * @param latlng the string to be parsed
     * @return {@link Coordinate}, the coordinate end product
     */
    private Coordinate parseCoordinate(String latlng) {
        JSONParser parser = new JSONParser();
        Coordinate coord = new Coordinate();
        try {
            JSONObject latlngJson = (JSONObject) parser.parse(latlng);
            float lat = ((Double) latlngJson.get("lat")).floatValue();
            float lng = ((Double) latlngJson.get("lng")).floatValue();
            coord.setLat((double) lat);
            coord.setLon((double) lng);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return coord;
    }

    /**
     * Refreshes the table with new data
     */
    public void refreshTable() {
        MainController controller = TempData.getController();
        controller.refreshTable();

    }

    /**
     * Zooms to a point
     */
    public void zoomToPoint(String latlng) {
        MainController controller = TempData.getController();
        controller.getMapController().changePosition(parseCoordinate(latlng));
    }

}
