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

    /**
     * Controller for the displayed menu
     */
    private MenuController menu;

    /**
     * Unused constructor
     */
    public WelcomeController() {
        // unused
    }

    /**
     * Initialize the window
     *
     * @param menu  Controller for the menu to display
     * @param stage a {@link javafx.stage.Stage} object
     */
    public void init(Stage stage, MenuController menu) {
        this.menu = menu;
    }

    /**
     * Launches the home screen
     */
    @FXML
    public void launchHome() {
        menu.initHome();
    }
}
