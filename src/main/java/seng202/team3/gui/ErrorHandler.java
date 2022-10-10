package seng202.team3.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javafx.geometry.Point2D;
import javafx.scene.control.Control;
import javafx.scene.control.Tooltip;

/**
 * Manages the display, formatting and control of error tooltips
 * 
 * @author Harrison Tyson
 * @version 1.0.0, Oct 22
 */
public class ErrorHandler {

    /**
     * Hashmap to hold the tooltips referenced by the ids
     */
    private HashMap<String, Tooltip> errorTooltips = new HashMap<String, Tooltip>();

    /**
     * Unused constructor
     */
    public ErrorHandler() {

        // Unused
    }

    /**
     * Add a tooltip to the handler
     * 
     * @param id      id of the tooltip
     * @param message message of the tooltip
     */
    public void add(String id, String message) {
        Tooltip newTooltip = new Tooltip(message);
        newTooltip.setId(id);

        if (newTooltip.getId() == null) { // Id required
            throw new IllegalArgumentException("Tooltip id cannot be null");
        }

        if (errorTooltips.keySet().contains(newTooltip.getId())) { // Enforce unique id
            throw new IllegalArgumentException("Tooltip id already exists");
        }

        errorTooltips.put(newTooltip.getId(), newTooltip);
    }

    /**
     * Remove a tooltip from the handler by id
     * 
     * @param id id of tooltip to remove
     */
    public void remove(String id) {
        errorTooltips.remove(id);
    }

    /**
     * Get tooltip by id
     * 
     * @param id id of tooltip to get
     * @return tooltip with id
     */
    public Tooltip get(String id) {
        return errorTooltips.get(id);
    }

    /**
     * Get visibility status of tooltip
     * 
     * @param id tooltip id to check
     * @return visible status of tooltip
     */
    public Boolean isShowing(String id) {
        return get(id).isShowing();
    }

    /**
     * Get all error tooltips in the handler
     * 
     * @return list of all error tooltips
     */
    public List<Tooltip> getAll() {
        return new ArrayList<>(errorTooltips.values());
    }

    /**
     * Changes the message of a tooltip
     * 
     * @param id      id of tooltip to change
     * @param message new message
     */
    public void changeMessage(String id, String message) {
        errorTooltips.get(id).setText(message);
    }


    /**
     * Displays the error tooltip on the given textfield
     * 
     * @param textField the textfield to show the error for
     * @param id the id of the error
     */
    public void displayError(Control textField, String id, double addX, double addY) {
        Point2D point = textField.localToScene(0.0, 0.0);

        get(id).show(textField,
            point.getX() + textField.getScene().getX()
                + textField.getScene().getWindow().getX()
                + textField.getWidth() + addX,
            point.getY() + textField.getScene().getY()
                + textField.getScene().getWindow().getY() + addY);
    }

    /**
     * Hides all of the error tooltips
     */
    public void hideAll() {
        for (Tooltip t : getAll()) {
            t.hide();
        }
    }

}
