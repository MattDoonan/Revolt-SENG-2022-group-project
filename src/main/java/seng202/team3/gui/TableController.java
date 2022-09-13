package seng202.team3.gui;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import seng202.team3.logic.TableManager;


/**
 * A TableController class that deals with the display of the table objects
 *
 * @author Michelle Hsieh
 * @version 1.0.0, Sep 22
 */
public class TableController {

    private Stage stage;
    private TableManager manager;

    /**
     * Initialize the window
     *
     * @param stage Top level container for this window
     */
    public void init(Stage stage) {
        this.stage = stage;
        manager = new TableManager();
        manager.resetQuery();
        manager.makeAllChargers();
    }


    /**
     * Executes an add, loading a small mapview screen
     */
    @FXML
    public void addCharger() {
        try {
            FXMLLoader miniMap = new FXMLLoader(getClass().getResource(
                    "/fxml/mini_map.fxml"));
            BorderPane root = miniMap.load();
            Scene modalScene = new Scene(root);
            Stage modal = new Stage();
            modal.setScene(modalScene);
            modal.setResizable(false);
            modal.setTitle("Charger Location");
            modal.initModality(Modality.WINDOW_MODAL);
            MiniMapController controller = miniMap.getController();
            controller.init(modal);
            controller.setManager(manager);
            modal.setAlwaysOnTop(true);
            modal.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }

        manager.addCharger();
    }

    /**
     * Executes an edit on the selected charger
     */
    @FXML
    public void editCharger() {
        manager.editCharger();
    }

    /**
     * Executes a delete on the selected charger
     */
    @FXML
    public void deleteCharger() {
        manager.deleteCharger();

    }

}
