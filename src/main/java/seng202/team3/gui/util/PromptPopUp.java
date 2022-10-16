package seng202.team3.gui.util;

import javafx.fxml.FXML;
import seng202.team3.gui.controller.MainController;
import seng202.team3.gui.controller.MenuController;
import seng202.team3.logic.util.GeoLocationHandler;
import seng202.team3.logic.util.JavaScriptBridge;

/**
 * A prompt for clicking on the map or the charger list for information to
 * happen.
 *
 * @author Michelle Hsieh
 * @version 1.0.0, Sep 22
 */
public class PromptPopUp extends PopUpWindow {

    /**
     * String of a type of popup window to display
     */
    protected String type;

    /**
     * The basic initialiser
     */
    public PromptPopUp() {
        // Unused
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
     * {@inheritDoc}
     * Executes the correct action as needed as per controller
     */
    @Override
    @FXML
    public void confirm() {
        MainController controller = MenuController.getController();
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
                stage.setOpacity(0.0);
                JavaScriptBridge bridge = new JavaScriptBridge();
                GeoLocationHandler.getCoordinate().setAddress(bridge.makeLocationName());
                GeoLocationHandler.setCoordinate(
                        GeoLocationHandler.getCoordinate());
                controller.getManager().addCharger();
                cancel();
            }
            default -> throw new IllegalStateException("Unexpected value: " + type);
        }
    }

}
