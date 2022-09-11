package seng202.team3.gui;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Class which loads the menu and runs the main controller (default)
 *
 * @author Michelle Hsieh
 * @version 1.0.0, Sep 22
 */
public class MenuController {

    private Stage stage;

    @FXML
    private BorderPane mainWindow;

    /**
     * Initialize the window
     *
     * @param stage Top level container for this window
     */
    public void init(Stage stage) {
        this.stage = stage;
        initHome();

    }

    /**
     * Initialises the main screen with only one version of the maincontroller
     */
    public void initHome() {
        try {
            FXMLLoader mainScene = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            Parent mainNode = mainScene.load();
            MainController controller = mainScene.getController();
            controller.init(stage);
            mainWindow.setCenter(mainNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the home upon clicking
     */
    @FXML
    public void loadHome() {
        initHome();
    }

    /**
     * Loads the vehicle screen upon click
     */
    public void loadVehicleScreen() {
        try {
            FXMLLoader vehicleLoader = new FXMLLoader(getClass().getResource(
                    "/fxml/vehicle.fxml"));
            AnchorPane root = vehicleLoader.load();
            Scene modalScene = new Scene(root);
            Stage modal = new Stage();
            modal.setScene(modalScene);
            modal.setResizable(false);
            modal.setTitle("Vehicle Screen");
            modal.initModality(Modality.WINDOW_MODAL);
            modal.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
