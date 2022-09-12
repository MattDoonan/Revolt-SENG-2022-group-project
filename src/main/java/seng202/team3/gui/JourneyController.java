package seng202.team3.gui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
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

  private Stage stage;

  /**
     * Initialize the window
     *
     * @param stage Top level container for this window
     */
  public void init(Stage stage, MenuController menu) {
      this.stage = stage;
  }

  private void loadMapView(Stage stage) {
    try {
        FXMLLoader webViewLoader = new FXMLLoader(getClass().getResource("/fxml/map.fxml"));
        Parent mapViewParent = webViewLoader.load();

        MapViewController mapController = webViewLoader.getController();
        MapManager mapManager = new MapManager(manage);
        mapController.init(stage, mapManager);

        mainWindow.setCenter(mapViewParent);
    } catch (IOException e) {
        e.printStackTrace();
    }
}


}
