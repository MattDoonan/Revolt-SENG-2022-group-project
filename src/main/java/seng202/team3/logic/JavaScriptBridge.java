package seng202.team3.logic;

import java.io.IOException;
import java.util.List;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.Coordinate;
import seng202.team3.gui.MainController;


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
        TempData.setCoordinate(parseCoordinate(latlng));
        MainController controller = TempData.getController();
        controller.getManager().setPosition(TempData.getCoordinate());
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
     * Refreshes the table with new data and adds the coordinate
     */
    public void refreshTableSaveCoord() {
        MainController controller = TempData.getController();
        controller.refreshTable();
        try {
            FXMLLoader saveCoordLoader = new FXMLLoader(getClass().getResource(
                    "/fxml/save_coord.fxml"));
            VBox root = saveCoordLoader.load();
            Scene modalScene = new Scene(root);
            Stage modal = new Stage();
            modal.setScene(modalScene);
            modal.setWidth(300);
            modal.setHeight(150);
            modal.setResizable(false);
            modal.setTitle("Add a coordinate");
            modal.initModality(Modality.WINDOW_MODAL);
            modal.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Recalls a query with all the components at the new location
     */
    public void refreshQuery() {
        MainController controller = TempData.getController();
        controller.executeSearch();
    }

    /**
     * Displays the charger according to id and also zooms to it.
     * Hands the object over to TempData
     *
     * @param id the charger id selected
     */
    public void zoomDisplayAndEdit(int id) {
        MainController controller = TempData.getController();
        List<Charger> chargers = controller.getManager().getData().stream()
                .filter(c -> c.getChargerId() == id)
                .toList();
        if (chargers != null) {
            Charger charger = chargers.get(0);
            controller.getManager().setSelectedCharger(charger);
            controller.viewChargers(charger);
            controller.getMapController().changePosition(charger.getLocation());
        }
    }

    /**
     * Zooms to a point
     */
    public void zoomToPoint(String latlng) {
        MainController controller = TempData.getController();
        controller.getMapController().changePosition(parseCoordinate(latlng));
    }

}
