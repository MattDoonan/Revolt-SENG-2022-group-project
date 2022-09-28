package seng202.team3.gui;

import java.util.ArrayList;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

/**
 * A class that pops up error messages can be hijacked for warnings
 *
 * @author Michelle Hsieh
 * @version 1.0.0, Sep 22
 */
public class ErrorController {

    /**
     * Active screen
     */
    private Stage stage;

    /**
     * List of errors to be displayed
     */
    private ObservableList<String> errorList;

    /**
     * Table column mapping error names to messages
     */
    @FXML
    private TableColumn<String, String> errors;

    /**
     * Prompt description of where error occured
     */
    @FXML
    private Label prompt;

    /**
     * Table to display error messages
     */
    @FXML
    private TableView<String> table;

    /**
     * Blank initialiser
     */
    public ErrorController() {
        // Unused
    }

    /**
     * Initialises the ErrorController
     */
    public void init() {
        stage = (Stage) prompt.getScene().getWindow();
    }

    /**
     * Displays the errors and solutions as an observable list
     */
    public void displayErrors() {
        stage.setAlwaysOnTop(true);
        table.setItems(errorList);
        errors.setCellValueFactory(error -> new ReadOnlyStringWrapper(error.getValue()));
    }

    /**
     * Sets and displays the errors
     *
     * @param errorsList sets the list of errors (String)
     */
    public void setErrors(ArrayList<String> errorsList) {
        errorList = FXCollections.observableList(errorsList);
    }

    /**
     * Sets the text label
     *
     * @param type a String of either "error" or "warning" which will display the
     *             correct texts.
     */
    public void setPromptType(String type) {
        switch (type) {
            case ("error") -> {
                prompt.setText("Please fix the following errors: ");
                errors.setText("Errors:");
            }
            case ("warning") -> {
                prompt.setText("The following warnings are on these chargers: ");
                errors.setText("Warnings:");
            }
            default -> throw new IllegalStateException("Unexpected value: " + type);
        }

    }

    /**
     * Closes the stage
     */
    @FXML
    public void close() {
        stage.close();
    }

}
