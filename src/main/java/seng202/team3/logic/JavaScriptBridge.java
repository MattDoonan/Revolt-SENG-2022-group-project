package seng202.team3.logic;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import seng202.team3.data.database.ComparisonType;
import seng202.team3.data.database.QueryBuilder;
import seng202.team3.data.database.QueryBuilderImpl;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.Coordinate;
import seng202.team3.data.entity.Entity;
import seng202.team3.data.entity.EntityType;
import seng202.team3.gui.ChargerController;
import seng202.team3.gui.JourneyController;
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
     * Logger
     */
    private static final Logger logManager = LogManager.getLogger();

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
        GeoLocationHandler.setCoordinate(parseCoordinate(latlng));
        refreshCoordinates();
    }

    /**
     * Refreshes the coordinates for journey manager and main manager
     */
    private void refreshCoordinates() {
        if (MenuController.getController() != null) {
            MenuController.getController().getManager().setPosition();
        }
        if (MenuController.getJourneyController() != null) {
            MenuController.getJourneyController().getManager().setPosition();
        }
    }

    /**
     * Parses a string into a series of coordinates
     *
     * @param latlng the string to be parsed
     * @return {@link seng202.team3.data.entity.Coordinate}, the coordinate end
     *         product
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
            coord.setAddress("Coordinate");
        } catch (ParseException e) {
            logManager.error(e.getMessage());
            return null;
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
        GeoLocationHandler.getCoordinate().setAddress(address);
        refreshCoordinates();
    }

    /**
     * Makes the location name; returns a string of it
     *
     * @return a string of the name
     */
    public String makeLocationName() {
        String address = "";
        JSONParser parser = new JSONParser();
        Coordinate coord = GeoLocationHandler.getCoordinate();
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder(
                    URI.create("https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat="
                            + Double.toString(coord.getLat()) + "&lon="
                            + Double.toString(coord.getLon())))
                    .build();
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
            JSONObject result = (JSONObject) parser.parse(response.body());
            address += (String) result.get("display_name");
            addLocationName(address);
        } catch (IOException e) {
            logManager.error(e.getMessage());
        } catch (ParseException | InterruptedException e) {
            Thread.currentThread().interrupt();
            logManager.error(e.getMessage());
        }

        return address;
    }

    /**
     * Refresh the menu table
     */
    public void refreshTable() {
        MainController controller = MenuController.getController();
        controller.getListController().refreshTable();
    }

    /**
     * Recalls a query with all the components at the new location
     */
    public void refreshQuery() {
        MainController controller = MenuController.getController();
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
        QueryBuilder query = new QueryBuilderImpl().withSource(EntityType.CHARGER)
                .withFilter("charger.chargerId", Integer.toString(id), ComparisonType.EQUAL);
        try {
            List<Entity> object = SqlInterpreter.getInstance()
                    .readData(query.build());
            if (object.size() == 1) {
                Charger charger = (Charger) object.get(0);
                MenuController.getController().getManager().setSelectedCharger(charger);
                MenuController.getController().getListController().viewChargers(charger);
                MenuController.getController().getMapController()
                        .changePosition(charger.getLocation());
            }
        } catch (IOException e) {
            logManager.error(e.getMessage());
        }
    }

    /**
     * Loads the charger information on a separate pop-up
     *
     * @param id id of the charger to get more information about
     */
    public void loadMoreInfo(int id) {
        chargerHandler(id);
        MainManager main = MenuController.getController().getManager();
        loadChargerEdit(main.getSelectedCharger());
    }

    /**
     * Adds a {@link Charger Charger} to Journey
     *
     * @param id unique identifier
     */
    public void addChargerToJourney(int id) {

        JourneyController journeyController = MenuController.getJourneyController();

        List<Charger> chargers = journeyController.getManager().getRangeChargers();

        List<Charger> chargerList = chargers.stream()
                .filter(charger -> charger.getId() == id).toList();

        if (!chargerList.isEmpty()) {
            Charger selectedCharger = chargerList.get(0);
            journeyController.addCharger(selectedCharger);
        } else {
            logManager.error("Charger is not in range, should not have been selected: %s", id);
        }
    }

    /**
     * Adds a stop into the journey of the geolocation point
     *
     */
    public void addStopInJourney() {

        makeLocationName();
        MenuController.getJourneyController().addStop(GeoLocationHandler.getCoordinate());

    }

    /**
     * Sets the singleton ChargerManager Coordinate to the latlng
     *
     * @param latlng the string created with latitude and longitude
     * @param name   the address of the coordinate to set
     */
    public void setCoordinate(String latlng, String name) {
        Coordinate point = parseCoordinate(latlng);
        point.setAddress(name);
        GeoLocationHandler.setCoordinate(point);
        MainManager main = MenuController.getController().getManager();
        loadChargerEdit(main.getSelectedCharger());
    }

    /**
     * Creates the charger adding/editing screen when necessary
     *
     * @param charger the {@link seng202.team3.data.entity.Charger} that is being
     *                selected
     */
    public void loadChargerEdit(Charger charger) {
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
            controller.setCharger(charger);
            controller.init(modal);
            modal.setAlwaysOnTop(true);
            modal.showAndWait();
            MenuController.getController().getManager().makeAllChargers();
            MenuController.getController().getListController().refreshTable();
        } catch (IOException e) {
            logManager.error(e.getMessage());
        }

    }

}
