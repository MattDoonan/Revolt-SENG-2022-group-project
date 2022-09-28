package seng202.team3.logic;

import java.io.IOException;
import java.util.List;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
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
     * Unused constructor
     */
    public JavaScriptBridge() {
        // Unused
    }

    /**
     * Makes a coordinate from a click and sets the GeoLocationHandler
     *
     * @param latlng the string created with latitude and longitude
     */
    public void addCoordinateFromClick(String latlng) {
        GeoLocationHandler.getInstance().setCoordinate(parseCoordinate(latlng), "Coordinate");
        refreshCoordinates();
    }

    /**
     * Refreshes the coordinates for journey manager and main manager
     */
    private void refreshCoordinates() {
        MenuController menu = new MenuController();
        if (menu.getController() != null) {
            menu.getController().getManager().setPosition();
        }
    }

    /**
     * Parses a string into a series of coordinates
     *
     * @param latlng the string to be parsed
     * @return {@link seng202.team3.data.entity.Coordinate}, the coordinate end product
     */
    public Coordinate parseCoordinate(String latlng) {
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
     * Takes a string with the name of the string and adds it to the current
     * coordinate
     *
     * @param address String of the address
     */
    public void addLocationName(String address) {
        String[] splitAddress = address.split("[,]", 10);
        if (splitAddress.length > 6) {
            address = "";
            address += splitAddress[0] + splitAddress[1] + ", "
                    + splitAddress[2] + ", " + splitAddress[3] + ", "
                    + splitAddress[splitAddress.length - 2] + ", "
                    + splitAddress[splitAddress.length - 1];
        }
        GeoLocationHandler.getInstance().setCoordinate(GeoLocationHandler
                .getInstance().getCoordinate(), address);
        refreshCoordinates();
    }

    /**
     * Refresh the menu table
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
     * Displays the charger according to id and also zooms to it, if it is not a
     * journey.
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
     *
     * @param latlng string representation of a physical coordinate
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
     *
     * @param id id of the charger to get more information about
     */
    public void loadMoreInfo(int id) {
        chargerHandler(id);
        MainManager main = new MenuController().getController().getManager();
        loadChargerEdit(main.getSelectedCharger(), main.getPosition());
    }

    /**
     * Sets the singleton ChargerManager Coordinate to the latlng
     *
     * @param latlng the string created with latitude and longitude
     * @param name   the address of the coordinate to set
     */
    public void setCoordinate(String latlng, String name) {
        GeoLocationHandler.getInstance().setCoordinate(parseCoordinate(latlng), name);
    }

    /**
     * Creates the charger adding/editing screen when necessary
     *
     * @param charger    the {@link seng202.team3.data.entity.Charger} that is being selected
     * @param coordinate the {@link seng202.team3.data.entity.Coordinate} that is being selected
     */
    public void loadChargerEdit(Charger charger, Coordinate coordinate) {
        try {
            FXMLLoader chargerCont = new FXMLLoader(getClass().getResource(
                    "/fxml/charger_info.fxml"));
            BorderPane root = chargerCont.load();
            Scene modalScene = new Scene(root);
            Stage modal = new Stage();
            modal.setScene(modalScene);
            modal.setWidth(1085);
            modal.setHeight(630);
            modal.setResizable(false);
            modal.setTitle("Charger Information");
            modal.initModality(Modality.APPLICATION_MODAL);
            ChargerController controller = chargerCont.getController();
            controller.setCoordinate(coordinate);
            controller.setCharger(charger);
            controller.displayChargerInfo();
            controller.init(modal);
            modal.setAlwaysOnTop(true);
            modal.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            MenuController menu = new MenuController();
            menu.getController().getManager().makeAllChargers();
            menu.getController().refreshTable();
        }

    }

}
