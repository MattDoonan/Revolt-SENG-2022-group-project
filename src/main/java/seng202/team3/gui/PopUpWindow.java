package seng202.team3.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Generic pop-up window class that has a confirm button.
 *
 * @author Michelle Hsieh
 * @version 1.0.0, Sep 22
 */
public class PopUpWindow {

    /**
     * Label input box
     */
    @FXML
    protected Label inputBox;

    /**
     * String prompt to be displayed
     */
    protected String prompt;

    /**
     * Stage of application
     */
    protected Stage stage;

    /**
     * Boolean, if is clicked, is true
     */
    protected Boolean isClicked = false;

    /**
     * unused constructor
     */
    public PopUpWindow() {
        // unused
    }

    /**
     * Initialise and display the prompt
     *
     * @param prompt a {@link java.lang.String} object
     */
    public void addPrompt(String prompt) {
        this.prompt = prompt;
        inputBox.setText(prompt);
        stage = (Stage) inputBox.getScene().getWindow();
        stage.setAlwaysOnTop(true);
    }

    /**
     * Executes the correct action and returns true if clicked
     */
    @FXML
    public void confirm() {
        isClicked = true;
        cancel();
    }

    /**
     * Cancels and closes the window
     */
    @FXML
    public void cancel() {
        stage.close();
    }

    /**
     * Gets the clicked boxes
     *
     * @return boolean if the box is clicked
     */
    public Boolean getClicked() {
        return isClicked;
    }
}
