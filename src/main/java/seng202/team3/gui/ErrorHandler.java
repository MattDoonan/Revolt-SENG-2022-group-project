package seng202.team3.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;

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
    private HashMap<String, Tooltip> errorTooltips = new HashMap<>();

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
     * @param node    node to install the tooltip on
     * @param message message of the tooltip
     */
    public void add(String id, Node node, String message) {
        Tooltip newTooltip = new Tooltip(message);
        newTooltip.setId(id);
        Tooltip.install(node, newTooltip);
        newTooltip.setShowDelay(Duration.millis(50.0));

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
     * Hides all of the error tooltips
     */
    public void hideAll() {
        for (Tooltip t : getAll()) {
            String id = t.getId();
            t.hide();
            errorTooltips.remove(id);
        }
    }

}
