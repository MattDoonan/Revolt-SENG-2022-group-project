package seng202.team3.gui;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team3.data.entity.PermissionLevel;
import seng202.team3.data.entity.User;
import seng202.team3.logic.UserManager;

/**
 * Class which loads the menu and runs the main controller (default)
 *
 * @author Michelle Hsieh
 * @version 1.0.0, Sep 22
 */
public class MenuController {
    /**
     * Logger
     */
    private static final Logger logManager = LogManager.getLogger();

    /**
     * BorderPane of the menu window
     */
    @FXML
    private BorderPane menuWindow;

    /**
     * Login signout button
     */
    @FXML
    private MenuItem loginSignout;

    /**
     * Home button box
     */
    @FXML
    private AnchorPane homeBox;

    /**
     * Garage button box
     */
    @FXML
    private AnchorPane garageBox;

    /**
     * Account button box
     */
    @FXML
    private AnchorPane accountBox;

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
     * The JourneyController of the application; static as there is only
     * one instance at a time
     */
    private static JourneyController journeyController;


    /**
     * Login Source path
     */
    private static final String LOGIN_PATH = "/fxml/login.fxml";

    /**
     * Login title text
     */
    private static final String LOGIN_TITLE = "Login";

    /**
     * Normal style
     */
    private static final String NORMAL_COLOUR = "-fx-background-color: #c2313b;";

    /**
     * Active style
     */
    private static final String ACTIVE_COLOUR = "-fx-background-color: linear-gradient"
            + "(from 50% 0% to 50% 100%, #c2313b, #9d3d43);";

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
     * Initialises the main screen with only one version of the maincontroller
     */
    public void initHome() {
        try {
            FXMLLoader mainScene = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            Parent mainNode = mainScene.load();
            setController(mainScene.getController());
            menuWindow.setCenter(mainNode);
            controller.init(stage, menuWindow);
            MainWindow.setController(controller);
            logManager.info("Switched to Home Screen");
        } catch (IOException e) {
            logManager.error(e.getMessage());
        }
    }

    /**
     * Gets the static Main Controller
     *
     * @return {@link seng202.team3.gui.MainController} the main controller of this
     *         run
     */
    public static MainController getController() {
        return controller;
    }

    /**
     * Gets the static Journey Controller
     *
     * @return {@link JourneyController} the journeycontroller of this run
     */
    public static JourneyController getJourneyController() {
        return journeyController;
    }

    /**
     * Sets the static Main Controller
     *
     * @param c controller to set static
     */
    private static void setController(MainController c) {
        controller = c;
    }

    /**
     * Sets the static Journey Controller
     *
     * @param controller journeyController to set static
     */
    private static void setJourneyController(JourneyController controller) {
        journeyController = controller;
    }

    /**
     * Loads the home upon clicking
     */
    @FXML
    public void loadHome() {
        if (Boolean.TRUE.equals(MapHandler.getLocationAccepted())) {
            MenuController.getController().getMapController().getLocation();
        }
        initHome();

        accountBox.setStyle(NORMAL_COLOUR);
        garageBox.setStyle(NORMAL_COLOUR);
        homeBox.setStyle(ACTIVE_COLOUR);
    }

    /**
     * Loads the vehicle screen upon click
     */
    public void loadVehicleScreen() {
        try {
            if (UserManager.getUser() == UserManager.getGuest()) {
                createLoginWindow(LOGIN_PATH, LOGIN_TITLE, null, null);
                logManager.warn("Must be logged in to access this feature");
            }
            if (UserManager.getUser() != UserManager.getGuest()) {
                FXMLLoader garageLoader = new FXMLLoader(getClass()
                        .getResource("/fxml/garage.fxml"));
                Parent garageViewParent = garageLoader.load();
                GarageController garageController = garageLoader.getController();
                garageController.init();
                menuWindow.setCenter(garageViewParent);
                MainWindow.setController(garageController);
                logManager.info("Switched to Garage screen");
                homeBox.setStyle(NORMAL_COLOUR);
                accountBox.setStyle(NORMAL_COLOUR);
                garageBox.setStyle(ACTIVE_COLOUR);
            }
        } catch (IOException e) {
            logManager.error(e.getMessage());
        }
    }

    /**
     * Checks if the user is logged in or out and does action
     */
    @FXML
    public void loginOut() {
        if (UserManager.getUser() == UserManager.getGuest()) {
            createLoginWindow(LOGIN_PATH, LOGIN_TITLE, null, null);
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
     * @param stage    the Stage of the window
     */
    public void createLoginWindow(String resource, String title, BorderPane pane, Stage stage) {
        if (pane == null) {
            try {
                stage = new Stage();
                FXMLLoader login = new FXMLLoader(getClass().getResource(
                        resource));
                BorderPane base = login.load();
                Scene modalScene = new Scene(base);
                stage.setScene(modalScene);
                stage.setResizable(false);
                stage.setTitle(title);
                stage.initModality(Modality.APPLICATION_MODAL);
                LoginSignupController loginController = login.getController();
                loginController.setStage(stage);
                loginController.init(this);
                loginController.setPane(base);
                MainWindow.setController(loginController);
                stage.showAndWait();
            } catch (IOException e) {
                logManager.error(e.getMessage());
            }

        } else {
            try {
                FXMLLoader login = new FXMLLoader(getClass().getResource(
                        resource));
                Parent base = login.load();
                LoginSignupController loginController = login.getController();
                pane.setCenter(base);
                stage.setTitle(title);
                loginController.init(this);
                loginController.setPane(pane);
                loginController.setStage(stage);
            } catch (IOException e) {
                logManager.error(e.getMessage());
            }
        }
        if (UserManager.getUser().getLevel() != PermissionLevel.GUEST) {
            initHome();
        }
    }

    /**
     * Loads the account page if the user is signed in
     */
    @FXML
    public void loadAccountScreen() {
        try {
            if (UserManager.getUser() == UserManager.getGuest()) {
                createLoginWindow(LOGIN_PATH, LOGIN_TITLE, null, null);
                logManager.warn("Must be logged in to access this feature");
            }
            if (UserManager.getUser() != UserManager.getGuest()) {
                FXMLLoader accountLoader = new FXMLLoader(getClass()
                        .getResource("/fxml/account.fxml"));
                Parent accountViewParent = accountLoader.load();
                AccountController accController = accountLoader.getController();
                accController.init(this);
                menuWindow.setCenter(accountViewParent);
                MainWindow.setController(accController);
                logManager.info("Switched to Account screen");
                homeBox.setStyle(NORMAL_COLOUR);
                garageBox.setStyle(NORMAL_COLOUR);
                accountBox.setStyle(ACTIVE_COLOUR);

            }
        } catch (IOException e) {
            logManager.error(e.getMessage());
        }
    }

    /**
     * Sets the current logged in user
     *
     * @param u the logged in user
     */
    public void setUser(User u) {
        UserManager.setUser(u);
        loginSignout.setText("LOGOUT");
        logManager.info("User has been successfully signed in");
    }

    /**
     * Sets loggedIn to be Guest
     */
    public void signOut() {
        UserManager.setUser(UserManager.getGuest());
        loginSignout.setText("LOGIN");
        initHome();
        logManager.info("The user has been successfully logged out");
    }

    /**
     * Deletes the set user
     */
    public void deleteUser() {
        loadHome();
        UserManager.deleteCurrentUser();
        loginSignout.setText(LOGIN_TITLE);
        logManager.info("The user has been successfully deleted");
    }

    /**
     * Loads the Journey Screen
     */
    public void loadJourneyScreen() {
        try {
            if (UserManager.getUser() == UserManager.getGuest()) {
                createLoginWindow(LOGIN_PATH, LOGIN_TITLE, null, null);
                logManager.warn("Must be logged in to access this feature");
            }
            FXMLLoader journeyLoader = new FXMLLoader(getClass().getResource("/fxml/journey.fxml"));
            Parent journeyViewParent = journeyLoader.load();
            menuWindow.setCenter(journeyViewParent);
            setJourneyController(journeyLoader.getController());
            journeyController.init(stage);
            MainWindow.setController(journeyLoader.getController());
            journeyController.setBorderPane(menuWindow);
            menuWindow.setCenter(journeyViewParent);
            logManager.info("Switched to Journeys Screen");
        } catch (IOException e) {
            logManager.error(e.getMessage());
        }
    }

}
