package seng202.team3.gui;


import javafx.fxml.FXML;
import javafx.stage.Stage;

/**
 * A welcome page to initiate components correctly
 *
 * @author Michelle Hsieh
 * @version 1.0.0, Sep 22
 */
public class WelcomeController {

    private Stage stage;
    private MenuController menu;

    /**
     * Initialize the window
     *
     * @param stage Top level container for this window
     */
    public void init(Stage stage, MenuController menu) {
        this.stage = stage;
        this.menu = menu;
    }

    /**
     * Launches the home screen
     */
    @FXML
    public void launchHome() {
        menu.initHome();
    }

    /**
     * TODO Add in the launch journeys component
     */
    public void launchJourneys() {

    }
}

