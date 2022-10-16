package seng202.team3.logic.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.database.util.QueryBuilder;
import seng202.team3.data.database.util.QueryBuilderImpl;
import seng202.team3.data.entity.Entity;
import seng202.team3.data.entity.Journey;
import seng202.team3.data.entity.util.EntityType;

/**
 * Manages the updating of previous journeys
 *
 * @author Angus Kirtlan
 * @version 1.0.0, Sep 22
 */
public class JourneyUpdateManager {

    /**
     * The log manager
     */
    private static final Logger logManager = LogManager.getLogger();

    /**
     * ObservableList of journeys
     */
    private ObservableList<Journey> previousJourneys;

    /**
     * Query object to retrieve vehicles
     */
    private QueryBuilder journeyDataQuery;

    /**
     * Constructor method
     */
    public JourneyUpdateManager() {
        resetQuery();
    }

    /**
     * Load the initial query
     */
    public void resetQuery() {
        journeyDataQuery = new QueryBuilderImpl().withSource(EntityType.JOURNEY);
    }

    /**
     * Loads previous journeys from the database that belong to the user
     */
    private void loadJourneys() {
        try {
            List<Journey> journeyList = new ArrayList<>();
            for (Entity o : SqlInterpreter.getInstance()
                    .readData(journeyDataQuery.build())) {
                if (UserManager.getUser().getId() == ((Journey) o).getUser()) {
                    journeyList.add((Journey) o);
                }
            }
            previousJourneys = FXCollections.observableList(journeyList);
        } catch (IOException e) {
            logManager.warn(e.getMessage());
        }
    }

    /**
     * Gets previous journeys that belong to the user
     *
     * @return all previous journeys for the user
     */
    public ObservableList<Journey> getData() {
        loadJourneys();
        return previousJourneys;
    }

    /**
     * Deletes the selected journey
     *
     * @param journey to delete from database
     */
    public void deleteJourney(Journey journey) {
        try {
            SqlInterpreter.getInstance().deleteData(EntityType.JOURNEY,
                    journey.getId());
            logManager.info("Journey has been deleted");
        } catch (IOException e) {
            logManager.error(e.getMessage());
        }
    }
}
