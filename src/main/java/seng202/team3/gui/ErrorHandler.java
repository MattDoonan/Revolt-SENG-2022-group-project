package seng202.team3.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;
import javafx.stage.Window;
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
     * @param nodeId  nodeId to install the tooltip on
     * @param message message of the tooltip
     */
    public void add(String nodeId, String message) {
        Tooltip newTooltip = new Tooltip(message);

        if (errorTooltips.keySet().contains(nodeId)) { // Enforce unique id
            throw new IllegalArgumentException("Tooltip nodeId already exists");
        }

        errorTooltips.put(nodeId, newTooltip);
    }

    /**
     * Remove a tooltip from the handler by nodeId
     * 
     * @param nodeId nodeId of tooltip to remove
     */
    public void remove(String nodeId) {
        errorTooltips.remove(nodeId);
    }

    /**
     * Get tooltip by nodeId
     * 
     * @param nodeId nodeId of tooltip to get
     * @return tooltip with nodeId
     */
    public Tooltip get(String nodeId) {
        return errorTooltips.get(nodeId);
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
     * @param nodeId  nodeId of tooltip to change
     * @param message new message
     */
    public void changeMessage(String nodeId, String message) {
        errorTooltips.get(nodeId).setText(message);
    }

    /**
     * Hides all of the error tooltips
     */
    public void hideAll() {
        for (Entry<String, Tooltip> entry : errorTooltips.entrySet()) {
            Tooltip.uninstall(
                    ((Stage) Window.getWindows().get(Window.getWindows().size() - 1))
                            .getScene().lookup("#" + entry.getKey()),
                    entry.getValue());
        }
    }

    /**
     * Shows the tooltip for the given nodeId
     * 
     * @param nodeId the nodeId to show tooltip for
     */
    public void show(String nodeId) {
        Tooltip.install(((Stage) Window.getWindows().get(Window.getWindows().size() - 1))
                .getScene().lookup("#" + nodeId), get(nodeId));
        get(nodeId).setShowDelay(Duration.millis(50.0));
    }

}
