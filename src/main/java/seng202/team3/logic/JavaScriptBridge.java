package seng202.team3.logic;

import java.io.IOException;
import java.util.List;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import seng202.team3.data.database.ComparisonType;
import seng202.team3.data.database.QueryBuilder;
import seng202.team3.data.database.QueryBuilderImpl;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.Coordinate;
import seng202.team3.gui.ChargerController;
import seng202.team3.gui.MainController;
import seng202.team3.gui.MenuController;


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
        MenuController menu = new MenuController();
        MainController controller = menu.getController();
        controller.getManager().setPosition(parseCoordinate(latlng));
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
     * Refreshes the table
     */
    public void refreshTable() {
        MainController controller = new MenuController().getController();
        controller.refreshTable();
    }

    /**
     * Recalls a query with all the components at the new location
     */
    public void refreshQuery() {
        MainController controller = new MenuController().getController();
        controller.executeSearch();
    }

    /**
     * Displays the charger according to id and also zooms to it, if it is not a journey.
     * Sets the charger as the selected charger
     *
     * @param id the charger id selected
     */
    public void chargerHandler(int id) {
        MainController controller = new MenuController().getController();
        QueryBuilder query = new QueryBuilderImpl().withSource("charger")
                .withFilter("charger.chargerId", Integer.toString(id), ComparisonType.EQUAL);
        try {
            List<Object> object = SqlInterpreter.getInstance()
                    .readData(query.build(), Charger.class);
            if (object.size() == 1) {
                Charger charger = (Charger) object.get(0);
                controller.getManager().setSelectedCharger(charger);
                controller.viewChargers(charger);
                controller.getMapController().changePosition(charger.getLocation());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Zooms to a point
     */
    public void zoomToPoint(String latlng) {
        MainController controller = new MenuController().getController();
        controller.getMapController().changePosition(parseCoordinate(latlng));
    }

    /**
     * Adds a stop into the route
     *
     * @param latlng the String from the route.
     */
    public void addStopInRoute(String latlng) {
        MainController controller = new MenuController().getController();
        controller.getMapController().addStopInRoute(parseCoordinate(latlng));
    }


    /**
     * Loads the charger information on a separate pop-up
     */
    public void loadMoreInfo(int id) {
        chargerHandler(id);
        try {
            FXMLLoader chargerCont = new FXMLLoader(getClass().getResource(
                    "/fxml/charger_info.fxml"));
            AnchorPane root = chargerCont.load();
            Scene modalScene = new Scene(root);
            Stage modal = new Stage();
            modal.setScene(modalScene);
            modal.setWidth(400);
            modal.setHeight(550);
            modal.setResizable(false);
            modal.setTitle("Charger Information");
            modal.initModality(Modality.WINDOW_MODAL);
            ChargerController controller = chargerCont.getController();
            controller.displayChargerInfo();
            controller.init(modal);
            modal.setAlwaysOnTop(true);
            modal.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
