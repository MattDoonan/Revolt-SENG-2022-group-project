package seng202.team3.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

/**
 * A prompt for clicking on the map or the charger list for information to happen.
 *
 * @author Michelle Hsieh
 * @version 1.0.0, Sep 22
 */
public class PromptPopUp extends PopUpWindow {

    protected String type;

    /**
     * The basic initialiser
     */
    public PromptPopUp() {
    }

    /**
     * Sets the type for the switch
     *
     * @param type A String of the type for the popup
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Executes the correct action as needed as per controller
     *
     */
    @Override
    @FXML
    public void confirm() {
        MainController controller = new MenuController().getController();
        stage.setAlwaysOnTop(false);
        switch (type) {
            case "edit" -> {
                controller.getManager().editCharger();
                cancel();
            }
            case "delete" -> {
                controller.getManager().deleteCharger();
                cancel();
            }
            case "add" -> {
                controller.getManager().addCharger();
                cancel();
            }
            default -> throw new IllegalStateException("Unexpected value: " + type);
        }
    }


}
