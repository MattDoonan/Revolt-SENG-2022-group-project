package seng202.team3.gui;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import seng202.team3.logic.JourneyManager;
import seng202.team3.logic.MapManager;

/**
 * Controller for journeySidebar.fxml
 * 
 * @author James Billows
 * @version 1.0.0, Aug 22
 */
public class JourneyController {

    @FXML
    private Button calculateRoute;

    @FXML
    private Button startMapFind;

    @FXML
    private Button destMapFind;

    @FXML
    private TextField startTextField;

    @FXML
    private TextField destTextField;

    @FXML
    private BorderPane mainWindow;

    private Stage stage;
    private JourneyMapController mapController;
    private JourneyManager manager;

    /**
     * Initialize the window
     *
     * @param stage Top level container for this window
     */
    public void init(Stage stage) {
        this.stage = stage;
        manager = new JourneyManager();
        loadMapView(stage);
    }


    /**
     * Loads the map view into the main part of the main window
     *
     * @param stage stage to load with
     */
    private void loadMapView(Stage stage) {
        try {
            FXMLLoader webViewLoader = new FXMLLoader(getClass()
                    .getResource("/fxml/map_journey.fxml"));
            Parent mapViewParent = webViewLoader.load();
            mapController = webViewLoader.getController();
            mapController.init(stage, manager);
            mainWindow.setCenter(mapViewParent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}