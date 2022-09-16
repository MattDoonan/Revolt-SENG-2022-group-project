package seng202.team3.gui;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
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
    private static MainController controller;
    private static JourneyController journeyController;

    @FXML
    private BorderPane menuWindow;

    /**
     * Initialize the window
     *
     * @param stage Top level container for this window
     */
    public void init(Stage stage) {
        this.stage = stage;
    }

    /**
     * Initialises the main screen with only one version of the maincontroller
     */
    public void initHome() {
        try {
            FXMLLoader mainScene = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            Parent mainNode = mainScene.load();
            controller = mainScene.getController();
            controller.init(stage, menuWindow);
            menuWindow.setCenter(mainNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the static Main Controller
     *
     * @return {@link MainController} the main controller of this run
     */
    public MainController getController() {
        return controller;
    }

    /**
     * Gets the static Journey Controller
     *
     * @return {@link JourneyController} the journeycontroller of this run
     */
    public JourneyController getJourneyController() {
        return journeyController;
    }

    /**
     * Loads the home upon clicking
     */
    @FXML
    public void loadHome() {
        initHome();
    }


    /**
     * Initialises the welcome page;
     */
    public void launchWelcome() {
        try {
            FXMLLoader mainScene = new FXMLLoader(getClass()
                    .getResource("/fxml/welcome_page.fxml"));
            Parent mainNode = mainScene.load();
            WelcomeController controller = mainScene.getController();
            controller.init(stage, this);
            menuWindow.setCenter(mainNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Launches the Journey Screen
     */
    public void launchJourneyScreen() {
        try {
            FXMLLoader journeyLoader = new FXMLLoader(getClass().getResource("/fxml/journey.fxml"));
            Parent journeyViewParent = journeyLoader.load();
            journeyController = journeyLoader.getController();
            journeyController.init(stage, this);
            menuWindow.setCenter(journeyViewParent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Loads the vehicle screen upon click
     */
    public void loadVehicleScreen() {
        try {
            FXMLLoader vehicleLoader = new FXMLLoader(getClass().getResource(
                    "/fxml/vehicle.fxml"));
            AnchorPane root = vehicleLoader.load();
            VehicleController baseController = vehicleLoader.getController();
            baseController.init();
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
