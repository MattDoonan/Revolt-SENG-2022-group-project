package seng202.team3.gui;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import seng202.team3.data.entity.Charger;

/**
 * Allows you to edit a charger
 *
 * @author Michelle Hsieh
 * @version 1.0.0, Sep 22
 */
public class ChargerController {

    @FXML
    private TextField name;
    @FXML
    private TextField parks;
    @FXML
    private TextField time;
    @FXML
    private TextField owner;
    @FXML
    private TextField operator;

    /**
     * Initialises the ChargerController, loading in the charger info
     */
    public ChargerController() {
    }

    /**
     * Adds a coordinate with a specified name and closes the box
     */
    @FXML
    public void displayChargerInfo() {
        Charger charger = new MenuController().getController().getManager().getSelectedCharger();
        if (charger != null) {
            name.setText(charger.getName());
            parks.setText(Integer.toString(charger.getAvailableParks()));
            time.setText(Double.toString(charger.getTimeLimit()));
            owner.setText(charger.getOwner());
            operator.setText(charger.getOperator());
        }
    }



}
