package seng202.team3.gui;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
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

    @FXML
    private BorderPane mainWindow;

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
            controller.init(stage);
            mainWindow.setCenter(mainNode);
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
            mainWindow.setCenter(mainNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * TODO docstring
     */
    public void launchJourneyScreen() {
        try {
            FXMLLoader journeyLoader = new FXMLLoader(getClass().getResource("/fxml/journey.fxml"));
            Parent journeyViewParent = journeyLoader.load();
            JourneyController controller = journeyLoader.getController();
            controller.init(stage, this);
            mainWindow.setCenter(journeyViewParent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Loads the vehicle screen upon click
     */
    public void loadVehicleScreen() {
        try {
            FXMLLoader garageLoader = new FXMLLoader(getClass().getResource("/fxml/garage.fxml"));
            Parent garageViewParent = garageLoader.load();
            GarageController controller = garageLoader.getController();
            controller.init(stage, this);
            mainWindow.setCenter(garageViewParent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
