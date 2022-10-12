package seng202.team3.gui;

import javafx.stage.Stage;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.Coordinate;
import seng202.team3.data.entity.Stop;
import seng202.team3.logic.GeoLocationHandler;
import seng202.team3.logic.JavaScriptBridge;

/**
 * A Journey Map Controller that extends the abstract MapHandler class
 *
 * @author Michelle Hsieh, James Billows, Angus Kirtlan
 * @version 1.0.4, Sep 22
 */
public class JourneyMapController extends MapHandler {

    /**
     * Controller for journey sidebar
     */
    private JourneyController journeyController;

    /**
     * Constructor for this class
     */
    public JourneyMapController() {
        // unused
    }

    /**
     * Initialise the map view
     *
     * @param stage             top level container for this window
     * @param journeyController The journey controller for this object
     */
    public void init(Stage stage, JourneyController journeyController) {
        path = "html/map.html";
        this.stage = stage;
        javaScriptBridge = new JavaScriptBridge();
        this.journeyController = journeyController;
        initMap();
        this.stage.sizeToScene();
    }

    /**
     * Implements adding a list of all eligible chargers on the map around the
     * selected coordinate
     */
    @Override
    public void addChargersOnMap() {
        javaScriptConnector.call("setJourney");
        javaScriptConnector.call("clearMarkers");
        javaScriptConnector.call("removeRoute");
        journeyController.getManager().makeRangeChargers();

        Coordinate start = journeyController.getManager().getStart();
        Coordinate end = journeyController.getManager().getEnd();

        boolean isEditable = false;
        if (start != null) {
            javaScriptConnector.call("addPoint", start.getAddress(),
                    start.getLat(), start.getLon());
            isEditable = true;
        } else if (end != null) {
            javaScriptConnector.call("addPoint", end.getAddress(), end.getLat(), end.getLon());
        }
        for (Charger charger : journeyController.getManager().getRangeChargers()) {
            javaScriptConnector.call("addMarker", charger.getLocation().getAddress(),
                    charger.getLocation().getLat(), charger.getLocation().getLon(),
                    charger.getId(), isEditable);
        }
        for (Stop stop : journeyController.getManager().getStops()) {

            javaScriptConnector.call("addPoint", stop.getLocation().getAddress(),
                    stop.getLocation().getLat(), stop.getLocation().getLon());
        }
        if (journeyController.getManager().getCurrentCoordinate() != null) {
            addCircle();
        }

    }

    /**
     * Adds a marker either at the start or destination according to string input
     *
     * @param position a String of either "Start" or "Destination" or "Stop"
     */
    public void positionMarker(String position) {
        javaScriptConnector.call("addCoordinate", position + ":" + "\n"
                + GeoLocationHandler.getCoordinate().getAddress(),
                GeoLocationHandler.getCoordinate().getLat(),
                GeoLocationHandler.getCoordinate().getLon());
    }

    /**
     * Adds a circle around a point on the map
     * with radius of vehicle range
     */
    public void addCircle() {
        Coordinate coord = journeyController.getManager().getCurrentCoordinate();
        javaScriptConnector.call("addCircle", coord.getLat(), coord.getLon(),
                journeyController.getManager().getDesiredRange() * 1000);

    }

    /**
     * Adds route to map, calling the underlying js function, from the currently
     * selected start and end coordinates.
     */
    public void addRouteToScreen() {

        javaScriptConnector.call("removeRoute");

        Coordinate start = journeyController.getManager().getSelectedJourney().getStartPosition();
        Coordinate end = journeyController.getManager().getSelectedJourney().getEndPosition();

        if (start != null && end != null) {

            javaScriptConnector.call("addLocationToRoute", start.getLat(), start.getLon(),
                    start.getAddress(), "p", 0);

            int i = 1;
            for (Stop stop : journeyController.getManager().getStops()) {
                if (stop.getCharger() != null) {
                    javaScriptConnector.call("addLocationToRoute", stop.getLocation().getLat(),
                            stop.getLocation().getLon(), stop.getCharger().getId(), "c", i);
                } else {
                    javaScriptConnector.call("addLocationToRoute", stop.getLocation().getLat(),
                            stop.getLocation().getLon(), stop.getId(), "p", i);
                }
                i += 1;
            }

            javaScriptConnector.call("addLocationToRoute",
                    end.getLat(), end.getLon(), end.getAddress(), "p", i);

            javaScriptConnector.call("addRoute");
        }
        // TODO add pop up if missing either start or end
    }

    /**
     * Removes the route and circles
     */
    public void removeRoute() {
        javaScriptConnector.call("removeRoute");
        javaScriptConnector.call("removeCircle");
    }
}
