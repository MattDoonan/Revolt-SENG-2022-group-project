package seng202.team3.gui;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Class which loads the menu and runs the main controller (default)
 *
 * @author Michelle Hsieh
 * @version 1.0.0, Sep 22
 */
public class MenuController {

    /**
     * BorderPane of the menu window
     */
    @FXML
    private BorderPane menuWindow;

    /**
     * Button for the menu
     */
    @FXML
    private Button menuButton;

    /**
     * Button for the vehicle
     */
    @FXML
    private Button vehicleButton;

    /**
     * Button for the journey
     */
    @FXML
    private Button journeyButton;

    /**
     * Button for the accounts
     */
    @FXML
    private Button accountButton;

    /**
     * The stage the application runs on
     */
    private Stage stage;

    /**
     * The MainController of the application; static as there is only
     * one instance at a time
     */
    private static MainController controller;

    /**
     * unused constructor
     */
    public MenuController() {
        // unused
    }

    /**
     * Initialize the window
     *
     * @param stage Top level container for this window
     */
    public void init(Stage stage) {
        this.stage = stage;
    }

    /**
     * Following functions are the hover and hover exit effects for the nav bar
     * buttons
     */
    public void menuHover() {
        menuButton.setStyle("-fx-text-fill:#000000; -fx-font-size: 26px; "
                + "-fx-background-color: #e06666;");
    }

    /**
     * menuExit.
     */
    public void menuExit() {
        menuButton.setStyle("-fx-text-fill:#ffffff;-fx-font-size: 24px; "
                + "-fx-background-color: #e06666;");
    }

    /**
     * vehicleHover.
     */
    public void vehicleHover() {
        vehicleButton.setStyle("-fx-text-fill:#000000;-fx-font-size: 26px; "
                + "-fx-background-color: #e06666;");
    }

    /**
     * vehicleExit.
     */
    public void vehicleExit() {
        vehicleButton.setStyle("-fx-text-fill:#ffffff;-fx-font-size: 24px; "
                + "-fx-background-color: #e06666;");
    }

    /**
     * journeyHover.
     */
    public void journeyHover() {
        journeyButton.setStyle("-fx-text-fill:#000000;-fx-font-size: 26px; "
                + "-fx-background-color: #e06666;");
    }

    /**
     * journeyExit.
     */
    public void journeyExit() {
        journeyButton.setStyle("-fx-text-fill:#ffffff;-fx-font-size: 24px; "
                + "-fx-background-color: #e06666;");
    }

    /**
     * accountHover.
     */
    public void accountHover() {
        accountButton.setStyle("-fx-text-fill:#000000;-fx-font-size: 26px; "
                + "-fx-background-color: #e06666;");
    }

    /**
     * accountExit.
     */
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
     * @return {@link seng202.team3.gui.MainController} the main controller of this
     *         run
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
     * Loads the vehicle screen upon click
     */
    public void loadVehicleScreen() {
        try {
            FXMLLoader garageLoader = new FXMLLoader(getClass().getResource("/fxml/garage.fxml"));
            Parent garageViewParent = garageLoader.load();
            GarageController controller = garageLoader.getController();
            controller.init();
            menuWindow.setCenter(garageViewParent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
