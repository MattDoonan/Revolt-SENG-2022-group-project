package seng202.team3.gui;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import seng202.team3.data.entity.Coordinate;
import seng202.team3.logic.TempData;

/**
 * Allows you to save a Coordinate into a list of coordinates
 *
 * @author Michelle Hsieh
 * @version 1.0.0, Aug 22
 */
public class SaveCoordController {

    @FXML
    TextField coordTextField;

    /**
     * Initialises the SaveCoordController
     */
    public SaveCoordController() {
    }


    /**
     * Adds a coordinate with a specified name and closes the box
     */
    @FXML
    public void addCoordinate() {
        Coordinate coord = TempData.getController().getManager().getPosition();
        coord.setAddress(coordTextField.getText());
        if (!coordTextField.getText().isEmpty()) {
            TempData.getController().getManager().addCoordinate(coord);
        }
        Stage stage = (Stage) coordTextField.getScene().getWindow();
        stage.close();
    }



}
