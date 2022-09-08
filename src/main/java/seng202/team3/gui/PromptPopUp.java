package seng202.team3.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import seng202.team3.logic.JavaScriptBridge;
import seng202.team3.logic.TempData;

/**
 * A prompt for clicking on the map or the charger list for information to happen.
 *
 * @author Michelle Hsieh
 * @version 1.0.0, Sep 22
 */
public class PromptPopUp {

    @FXML
    private Label inputBox;

    private String prompt;
    private String type;
    private Stage stage;

    /**
     * The basic initialiser
     */
    public PromptPopUp() {

    }

    /**
     * Initialise and display the prompt
     */
    public void addPrompt(String prompt, String type) {
        this.type = type;
        this.prompt = prompt;
        inputBox.setText(prompt);
        stage = (Stage) inputBox.getScene().getWindow();
        stage.setAlwaysOnTop(true);
    }

    /**
     * Executes the correct action as needed
     * TODO add the stubs functionality for the chargers to be added/deleted once DB up
     */
    @FXML
    public void confirm() {
        stage.setAlwaysOnTop(false);
        if (type.equals("edit")) {
            TempData.getController().editCharger();
            cancel();
        } else if (type.equals("delete")) {
            TempData.getController().getManager().deleteCharger();
            cancel();
        } else if (type.equals("add")) {
            TempData.getController().getManager().addCharger();
            cancel();
        }
    }

    /**
     * Cancels and closes the window
     */
    @FXML
    public void cancel() {
        stage.close();
    }

}
