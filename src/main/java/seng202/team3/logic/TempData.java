package seng202.team3.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seng202.team3.data.database.CsvInterpreter;
import seng202.team3.data.database.Query;
import seng202.team3.data.database.QueryBuilderImpl;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.Coordinate;
import seng202.team3.gui.MainController;

/**
 * A temporary DataManager class to store information and show data
 *
 * @author Michelle Hsieh
 * @version 1.0.0, Aug 22
 */
public class TempData {
    /**
     * This should contain the MainController, current coord and all the chargers
     */
    private static ObservableList<Charger> chargers;
    private static Coordinate coord;
    private static MainController controller;

    /**
     * Initiates all the data
     */
    public static void makeTempData() {
        Query myq = new QueryBuilderImpl().withSource("charger").build();
        try {
            List<Charger> chargerList = new ArrayList<>();
            for (Object o : new CsvInterpreter().readData(myq, Charger.class)) {
                chargerList.add((Charger) o);
            }
            chargers = FXCollections
                    .observableList(chargerList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the chargers
     *
     * @return ObservableList of Chargers which have been parsed
     */
    public static ObservableList<Charger> getChargers() {
        return chargers;
    }

    /**
     * Sets a coordinate
     *
     * @param coordinate the coordinate to set
     */
    public static void setCoordinate(Coordinate coordinate) {
        coord = coordinate;
    }

    /**
     * Gets a coordinate
     *
     * @return Coordinate stored in TempData
     */
    public static Coordinate getCoordinate() {
        return coord;
    }

    /**
     * Sets the Main Controller
     *
     * @param control the main controller of the app
     */
    public static void setController(MainController control) {
        controller = control;
    }

    /**
     * Gets the controller
     *
     * @return MainController, the main controller
     */
    public static MainController getController() {
        return controller;
    }

}