package seng202.team3.gui;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
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

    @FXML
    private Button menuButton;

    @FXML
    private Button vehicleButton;

    @FXML
    private Button journeyButton;

    @FXML
    private Button accountButton;

    /**
     * Initialize the window
     *
     * @param stage Top level container for this window
     */
    public void init(Stage stage) {
        this.stage = stage;
    }


    /**
     * Following functions are the hover and hover exit effects for the nav bar buttons
     */
    public void menuHover() {
        menuButton.setStyle("-fx-text-fill:#000000; -fx-font-size: 26px; "
                + "-fx-background-color: #e06666;");
    }

    public void menuExit() {
        menuButton.setStyle("-fx-text-fill:#ffffff;-fx-font-size: 24px; "
                + "-fx-background-color: #e06666;");
    }

    public void vehicleHover() {
        vehicleButton.setStyle("-fx-text-fill:#000000;-fx-font-size: 26px; "
                + "-fx-background-color: #e06666;");
    }

    public void vehicleExit() {
        vehicleButton.setStyle("-fx-text-fill:#ffffff;-fx-font-size: 24px; "
                + "-fx-background-color: #e06666;");
    }

    public void journeyHover() {
        journeyButton.setStyle("-fx-text-fill:#000000;-fx-font-size: 26px; "
                + "-fx-background-color: #e06666;");
    }

    public void journeyExit() {
        journeyButton.setStyle("-fx-text-fill:#ffffff;-fx-font-size: 24px; "
                + "-fx-background-color: #e06666;");
    }

    public void accountHover() {
        accountButton.setStyle("-fx-text-fill:#000000;-fx-font-size: 26px; "
                + "-fx-background-color: #e06666;");
    }

    public void accountExit() {
        accountButton.setStyle("-fx-text-fill:#ffffff;-fx-font-size: 24px;"
                + " -fx-background-color: #e06666;");
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
            journeyController.init(stage);
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
            FXMLLoader vehicleLoader = new FXMLLoader(getClass()
                    .getResource("/fxml/vehicle.fxml"));
            Parent vehicle = vehicleLoader.load();
            VehicleController controller = vehicleLoader.getController();
            controller.init();
            menuWindow.setCenter(vehicle);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
