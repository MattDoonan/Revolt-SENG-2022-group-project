package seng202.team3.logic.manager;

import java.io.IOException;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.util.EntityType;
import seng202.team3.data.entity.util.PermissionLevel;
import seng202.team3.logic.util.ChargerHandler;
import seng202.team3.logic.util.ChargerInterface;
import seng202.team3.logic.util.GeoLocationHandler;
import seng202.team3.logic.util.JavaScriptBridge;

/**
 * Logic layer for the main Controller
 *
 * @author Matthew Doonan, Michelle Hsieh
 * @version 1.0.1, Aug 22
 */
public class MainManager extends ChargerHandler implements ChargerInterface {
    /**
     * Logger
     */
    private static final Logger logManager = LogManager.getLogger();

    /**
     * Distance between selected coordinate and specified charger
     */
    private double distance = 0;

    /**
     * ChargerManager class to handle chargers
     */
    private final ChargerManager chargerManager = new ChargerManager();

    /**
     * Saves the MainController to call later
     */
    public MainManager() {
        selectedCoordinate = GeoLocationHandler.getCoordinate();
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
     * Send chargers within range of the selected location to the table and temp
     * data.
     *
     * @return an ObservableList of {@link seng202.team3.data.entity.Charger}s which
     *         are nearby
     */
    public ObservableList<Charger> compareDistance() {
        ArrayList<Charger> arrayChargers = new ArrayList<>(chargerData);
        if (distance != 0) {
            arrayChargers = chargerManager.getNearbyChargers(
                    arrayChargers, selectedCoordinate, distance);
        } else {
            arrayChargers = chargerManager.getNearbyChargers(
                    arrayChargers, selectedCoordinate, 2000); // Enough to cover NZ
        }
        return FXCollections.observableList(arrayChargers);
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
     * {@inheritDoc}
     * Adds a charger at the location of the coordinate.
     */
    @Override
    public void addCharger() {
        new JavaScriptBridge().loadChargerEdit(null);
        makeAllChargers();
        logManager.info("Charger has been added");
    }

    /**
     * {@inheritDoc}
     * Removes the selected charger and replaces it with null
     */
    @Override
    public void deleteCharger() {
        if (selectedCharger != null) {
            try {
                SqlInterpreter.getInstance()
                        .deleteData(EntityType.CHARGER, selectedCharger.getId());
                selectedCharger = null;
                logManager.info("Charger has been deleted");
                makeAllChargers();
            } catch (IOException e) {
                logManager.error(e.getMessage());
            }
        }
    }

    /**
     * {@inheritDoc}
     * Uses the JavaScript Bridge to load the charger edit functionality of the
     * selected charger
     */
    @Override
    public void editCharger() {
        if (getSelectedCharger() == null) {
            // Do nothing
        } else if ((UserManager.getUser().getLevel() == PermissionLevel.ADMIN)
                || (getSelectedCharger().getOwnerId() == UserManager.getUser().getId())) {
            JavaScriptBridge bridge = new JavaScriptBridge();
            bridge.loadMoreInfo(getSelectedCharger().getId());
            makeAllChargers();
        }
    }

}
