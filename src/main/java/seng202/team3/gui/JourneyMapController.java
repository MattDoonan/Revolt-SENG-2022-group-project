package seng202.team3.gui;

import java.util.ArrayList;
import javafx.stage.Stage;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.Coordinate;
import seng202.team3.logic.ChargerManager;
import seng202.team3.logic.JavaScriptBridge;
import seng202.team3.logic.JourneyManager;

/**
 * A Journey Map Controller that extends the abstract MapHandler class
 *
 * @author Michelle Hsieh and James Billows
 * @version 1.0.0, Sep 22
 */
public class JourneyMapController extends MapHandler {

    /**
     * The logic manager for journeys
     */
    private JourneyManager journeyManager;

    /**
     * Initialise the map view
     *
     * @param stage top level container for this window
     * @param journeyManager Manager to interact with
     */
    public void init(Stage stage, JourneyManager journeyManager) {
        this.stage = stage;
        javaScriptBridge = new JavaScriptBridge();
        this.journeyManager = journeyManager;
        initMap();
        this.stage.sizeToScene();
    }

    /**
     * Implements adding a list of all eligible chargers on the map
     */
    @Override
    public void addChargersOnMap() {
        javaScriptConnector.call("setJourney");
        javaScriptConnector.call("clearMarkers");
        journeyManager.resetQuery();
        journeyManager.makeAllChargers();

        for (Charger charger : journeyManager.getData()) {
            javaScriptConnector.call("addMarker", charger.getLocation().getAddress(),
                    charger.getLocation().getLat(), charger.getLocation().getLon(),
                    charger.getChargerId());
        }
    }

    /**
     * Shows chargers on map around given coordinate with radius of vehicle range
     * @param coord centre of circle
     */
    public void addChargersAroundPoint(Coordinate coord) {
        javaScriptConnector.call("setJourney");
        javaScriptConnector.call("clearMarkers");
        journeyManager.resetQuery();
        journeyManager.makeAllChargers();
        //TODO move above into own function

        ChargerManager chargerManager = new ChargerManager();
        ArrayList<Charger> arrayChargers = new ArrayList<>(journeyManager.getData());
        ArrayList<Charger> nearbyChargers = chargerManager.getNearbyChargers(arrayChargers,
                    coord, journeyManager.getSelectedJourney().getVehicle().getCurrentRange());

        for (Charger charger : nearbyChargers) {
            javaScriptConnector.call("addMarker", charger.getLocation().getAddress(),
                    charger.getLocation().getLat(), charger.getLocation().getLon(),
                    charger.getChargerId());
        }

        addCircle(coord);
    }

    /**
     * Adds a marker at the start
     */
    public void addStartMarker() {
        javaScriptConnector.call("addCoordinate", "Start",
                journeyManager.getPosition().getLat(), journeyManager.getPosition().getLon());
    }

    /**
     * Adds a marker at the destination
     */
    public void addEndMarker() {
        javaScriptConnector.call("addCoordinate", "Destination",
                journeyManager.getPosition().getLat(), journeyManager.getPosition().getLon());
    }

    /**
     * Adds a circle around an inputted point on the map
     * with radius of vehicle range
     * @param coord coordinate for centre of circle
     */
    public void addCircle(Coordinate coord) {
        javaScriptConnector.call("addCircle", coord.getLat(), coord.getLon(),
                journeyManager.getSelectedJourney().getVehicle().getCurrentRange() * 1000);
    }


    /**
     * Adds route to map, calling the underlying js function, from the currently
     * selected start and end coordinates.
     */
    public void addRouteToScreen() { 

        javaScriptConnector.call("removeRoute");

        Coordinate start = journeyManager.getStart();
        Coordinate end = journeyManager.getEnd();

        if (start != null && end != null) {

            javaScriptConnector.call("addLocationToRoute", start.getLat(), start.getLon(),
                    start.getAddress(), "p", 0);

            int i = 1;
            for (Charger charger : journeyManager.getSelectedJourney().getChargers()) {
                javaScriptConnector.call("addLocationToRoute", charger.getLocation().getLat(),
                        charger.getLocation().getLon(), charger.getChargerId(), "c", i);
                i += 1;
            }

            javaScriptConnector.call("addLocationToRoute",
                    end.getLat(), end.getLon(), end.getAddress(), "p", i);

            javaScriptConnector.call("addRoute");
        }
        //TODO add pop up if missing either start or end
    }

}
