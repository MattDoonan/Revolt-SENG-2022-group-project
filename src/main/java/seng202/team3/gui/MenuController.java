package seng202.team3.gui;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import seng202.team3.data.entity.User;

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
     * Login signout button
     */
    @FXML
    private Button loginSignout;

    /**
     * Account button
     */
    @FXML
    private Button accountPage;


    /**
     * The stage the application runs on
     */
    private Stage stage;

    /**
     * The MainController of the application; static as there is only
     * one instance at a time
     */
    private static MainController controller;

    /** User that is logged in */
    private static User loggedIn;

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
     * accountHover.
     */
    public void accountHover() {
        accountPage.setStyle("-fx-text-fill:#000000;-fx-font-size: 26px; "
                + "-fx-background-color: #e06666;");
    }

    /**
     * accountExit.
     */
    public void accountExit() {
        accountPage.setStyle("-fx-text-fill:#ffffff;-fx-font-size: 24px; "
                + "-fx-background-color: #e06666;");
    }

    /**
     * loginHover.
     */
    public void loginHover() {
        loginSignout.setStyle("-fx-text-fill:#000000;-fx-font-size: 26px; "
                + "-fx-background-color: #e06666;");
    }

    /**
     * loginExit.
     */
    public void loginExit() {
        loginSignout.setStyle("-fx-text-fill:#ffffff;-fx-font-size: 24px; "
                + "-fx-background-color: #e06666;");
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

    /**
     * Checks if the user is logged in or out and does action
     */
    @FXML
    public void loginOut() {
        if (loggedIn == null) {
            createLoginWindow("/fxml/login.fxml", "Login", null);
        } else {
            signOut();
        }
    }

    /**
     * Creates a login window if not previously initialised
     *
     * @param resource the resource to be fetched
     * @param title    the title of the window
     * @param pane     the BorderPane of the window
     */
    public void createLoginWindow(String resource, String title, BorderPane pane) {
        Stage loginPopup = new Stage();
        if (pane == null) {
            try {
                FXMLLoader login = new FXMLLoader(getClass().getResource(
                        resource));
                BorderPane base = login.load();
                Scene modalScene = new Scene(base);
                loginPopup.setScene(modalScene);
                loginPopup.setResizable(false);
                loginPopup.setTitle(title);
                loginPopup.initModality(Modality.WINDOW_MODAL);
                LoginSignupController loginController = login.getController();
                loginController.setStage(loginPopup);
                loginController.init(this);
                loginController.setPane(base);
                loginPopup.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            try {
                FXMLLoader login = new FXMLLoader(getClass().getResource(
                        resource));
                Parent base = login.load();
                LoginSignupController loginController = login.getController();
                pane.setCenter(base);
                loginPopup.setTitle(title);
                loginController.init(this);
                loginController.setPane(pane);
                loginController.setStage(loginPopup);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Loads the account page if the user is signed in
     */
    @FXML
    public void loadAccountScreen() {
        try {
            if (loggedIn == null) {
                createLoginWindow("/fxml/login.fxml", "Login", null);
            } else {
                FXMLLoader accountLoader =
                        new FXMLLoader(getClass().getResource("/fxml/account.fxml"));
                Parent accountViewParent = accountLoader.load();
                AccountController controller = accountLoader.getController();
                controller.init();
                menuWindow.setCenter(accountViewParent);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the current logged in user
     * @param u the logged in user
     */
    public void setUser(User u) {
        this.loggedIn = u;
        loginSignout.setText("logout");
    }

    /**
     * Sets loggedIn to be null
     */
    public void signOut() {
        this.loggedIn = null;
        loginSignout.setText("Login");
    }

    /**
     * Gets the current logged in user
     * @return the logged in user
     */
    public User getUser() {
        return loggedIn;
    }

}
