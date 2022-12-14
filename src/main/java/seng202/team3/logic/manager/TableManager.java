package seng202.team3.logic.manager;

import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.database.util.ComparisonType;
import seng202.team3.data.database.util.QueryBuilderImpl;
import seng202.team3.data.entity.Coordinate;
import seng202.team3.data.entity.User;
import seng202.team3.data.entity.util.EntityType;
import seng202.team3.data.entity.util.PermissionLevel;
import seng202.team3.logic.util.ChargerHandler;
import seng202.team3.logic.util.ChargerInterface;
import seng202.team3.logic.util.GeoLocationHandler;
import seng202.team3.logic.util.JavaScriptBridge;

/**
 * A Table Manager that implements the adding, deleting and editing
 * functionality of a charger
 *
 * @author Michelle Hsieh
 * @version 1.0.0, Sep 22
 */
public class TableManager extends ChargerHandler implements ChargerInterface {
    /**
     * Logger
     */
    private static final Logger logManager = LogManager.getLogger();

    /**
     * The current user of this table
     */
    private User user;

    /**
     * Checks to see if it's adding or not
     */
    private boolean adding = false;

    /**
     * A boolean where if getting owner, is true, else false
     */
    private boolean getOwner = false;

    /**
     * unused constructor
     */
    public TableManager() {
        // unused
    }

    /**
     * The user being queried
     *
     * @param user the user being queries
     */
    public void setUser(User user) {
        this.user = user;
        if ((UserManager.getUser().getLevel() == PermissionLevel.ADMIN)
                && (user.getLevel() != PermissionLevel.ADMIN)) {
            getOwner = true;
        }
    }

    /**
     * {@inheritDoc}
     * Adds a charger at a specified coordinate
     */
    @Override
    public void addCharger() {
        Coordinate coordinate = GeoLocationHandler.getCoordinate();
        if (coordinate != null) {
            setPosition();
        }
    }

    /**
     * {@inheritDoc}
     * Removes the selected charger and replaces it with null
     */
    @Override
    public void deleteCharger() {
        if (getSelectedCharger() != null) {
            try {
                SqlInterpreter.getInstance().deleteData(EntityType.CHARGER,
                        getSelectedCharger().getId());
                setSelectedCharger(null);
                logManager.info("Charger has been deleted");
            } catch (IOException e) {
                logManager.error(e.getMessage());
            }
        }

    }

    /**
     * Uses the JavaScript Bridge to load the charger edit functionality of the
     * selected charger
     */
    public void editCharger() {
        if (getSelectedCharger() != null) {
            JavaScriptBridge bridge = new JavaScriptBridge();
            bridge.loadChargerEdit(selectedCharger);
        }
    }

    /**
     * Load the initial query; overwritten to have a filter according to permission
     * level
     */
    @Override
    public void resetQuery() {
        mainDataQuery = new QueryBuilderImpl().withSource(EntityType.CHARGER);
        if (user.getLevel() == PermissionLevel.CHARGEROWNER || getOwner) {
            mainDataQuery.withFilter("owner",
                    Integer.toString(user.getId()), ComparisonType.EQUAL);
        }
    }

    /**
     * Gets it if it's adding
     *
     * @return boolean of adding, true if adding
     */
    public boolean isAdding() {
        return adding;
    }

    /**
     * Sets the adding parameter
     *
     * @param adding a boolean of adding or not
     */
    public void setAdding(boolean adding) {
        this.adding = adding;
    }
}
