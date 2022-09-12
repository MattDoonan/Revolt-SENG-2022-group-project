package seng202.team3.gui;

import java.util.ArrayList;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.stage.Stage;


/**
 * A class that pops up error messages can be hijacked for warnings
 *
 * @author Michelle Hsieh
 * @version 1.0.0, Sep 22
 */
public class ErrorController {

    private Stage stage;

    @FXML
    private TableColumn<String, String> errors;

    @FXML
    private Label prompt;


    /**
     * Blank initialiser
     */
    public ErrorController() {
    }

    /**
     * Initialises the ErrorController
     */
    public void init() {
        stage = (Stage) prompt.getScene().getWindow();
    }

    /**
     * Displays the errors and solutions as an observable list
     *
     * @param errorList a String list of all the errors
     */
    public void displayErrors(ArrayList<String> errorList) {
        stage.setAlwaysOnTop(true);
        ObservableList<String> errorsList = FXCollections.observableList(errorList);
        errors.setCellValueFactory(error -> new ReadOnlyStringWrapper(error.getValue()));
    }

    /**
     * Sets the text label
     *
     * @param type a String of either "error" or "warning" which will display the correct texts.
     */
    public void setPromptType(String type) {
        switch (type) {
            case ("error") -> {
                prompt.setText("Please fix the following errors: ");
                errors.setText("Errors:");
                break;
            }
            case ("warning") -> {
                prompt.setText("The following warnings are on these chargers: ");
                errors.setText("Warnings:");
                break;
            }
            default -> throw new IllegalStateException("Unexpected value: " + type);
        }

    }

    /**
     * Sets the header string
     *
     * @param header the title of the box
     */
    public void setHeader(String header) {
        stage.setTitle(header);
    }

    /**
     * Closes the stage
     */
    @FXML
    public void close() {
        stage.close();
    }

}
