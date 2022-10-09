package seng202.team3.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team3.data.database.QueryBuilder;
import seng202.team3.data.database.QueryBuilderImpl;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.Journey;

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
     * Load the initial query
     */
    public void resetQuery() {
        journeyDataQuery = new QueryBuilderImpl().withSource("journey");
    }

    /**
     * Loads previous journeys from the database
     */
    private void loadJourneys() {
        try {
            List<Journey> journeyList = new ArrayList<>();
            for (Object o : SqlInterpreter.getInstance()
                    .readData(journeyDataQuery.build(), Journey.class)) {
                if (UserManager.getUser().getUserid() == ((Journey) o).getUser()) {
                    journeyList.add((Journey) o);
                }
            }
            previousJourneys = FXCollections.observableList(journeyList);
        } catch (IOException e) {
            logManager.warn(e.getMessage());
        }
    }

    /**
     * Gets previous journeys
     *
     * @return all previous journeys
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
            SqlInterpreter.getInstance().deleteData("journey",
                    journey.getJourneyId());
            logManager.info("Journey has been deleted");
        } catch (IOException e) {
            logManager.error(e.getMessage());
        }
    }
}
