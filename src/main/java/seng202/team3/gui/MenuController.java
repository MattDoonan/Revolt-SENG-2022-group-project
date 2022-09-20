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
     * <p>menuExit.</p>
     */
    public void menuExit() {
        menuButton.setStyle("-fx-text-fill:#ffffff;-fx-font-size: 24px; "
                + "-fx-background-color: #e06666;");
    }

    /**
     * <p>vehicleHover.</p>
     */
    public void vehicleHover() {
        vehicleButton.setStyle("-fx-text-fill:#000000;-fx-font-size: 26px; "
                + "-fx-background-color: #e06666;");
    }

    /**
     * <p>vehicleExit.</p>
     */
    public void vehicleExit() {
        vehicleButton.setStyle("-fx-text-fill:#ffffff;-fx-font-size: 24px; "
                + "-fx-background-color: #e06666;");
    }

    /**
     * <p>journeyHover.</p>
     */
    public void journeyHover() {
        journeyButton.setStyle("-fx-text-fill:#000000;-fx-font-size: 26px; "
                + "-fx-background-color: #e06666;");
    }

    /**
     * <p>journeyExit.</p>
     */
    public void journeyExit() {
        journeyButton.setStyle("-fx-text-fill:#ffffff;-fx-font-size: 24px; "
                + "-fx-background-color: #e06666;");
    }

    /**
     * <p>accountHover.</p>
     */
    public void accountHover() {
        accountButton.setStyle("-fx-text-fill:#000000;-fx-font-size: 26px; "
                + "-fx-background-color: #e06666;");
    }

    /**
     * <p>accountExit.</p>
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
     * @return {@link seng202.team3.gui.MainController} the main controller of this run
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
            menuWindow.setCenter(mainNode);
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
