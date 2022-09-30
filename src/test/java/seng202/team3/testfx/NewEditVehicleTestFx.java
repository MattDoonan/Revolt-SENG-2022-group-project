package seng202.team3.testfx;

import static org.junit.Assert.assertThat;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.matcher.control.TextInputControlMatchers;
import seng202.team3.data.database.SqlInterpreter;

/**
 * Code designed to test the searching and filtering of the Main Window
 * Heavily inspired by Morgan English
 *
 * @author Celia Allen
 * @version 1.0.0, Sep 22
 */
public class NewEditVehicleTestFx extends TestFxBase {

    @FXML
    private TextField makeText;

    @FXML
    private TextField modelText;

    @FXML
    private TextField maxRangeText;

    @FXML
    private TextField currChargeText;

    static SqlInterpreter db;

    /**
     * Implements the abstract method for this window
     *
     * @throws Exception if fail to launch
     */
    @BeforeEach
    public void setUp() throws Exception {
        db.defaultDatabase();
        db.addChargerCsvToData("csvtest/filtering");
    }

    /**
     * Starts the main for testing
     *
     * @param stage the stage of the application
     * @throws Exception if fail to launch
     */
    @Override
    public void start(Stage stage) throws Exception {
        SqlInterpreter.removeInstance();
        db = SqlInterpreter.initialiseInstanceWithUrl(
                "jdbc:sqlite:./target/test-classes/test_database.db");
        db.addChargerCsvToData("csvtest/filtering");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/vehicle_update.fxml"));
        Parent page = loader.load();
        initState();
        Scene scene = new Scene(page);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Initialises the state of the current application
     * 
     */
    public void initState() {
        // Empty
    }

    /**
     * Tests textfields in update_vehicle.
     */
    @Test
    public void makeVehicle() {
        TextField makeText = find("#makeText");
        makeText.setText("TestMake");
        assertThat(makeText, TextInputControlMatchers.hasText("TestMake"));
        TextField modelText = find("#modelText");
        modelText.setText("TestModel");
        assertThat(modelText, TextInputControlMatchers.hasText("TestModel"));
        TextField maxRangeText = find("#maxRangeText");
        maxRangeText.setText("1234");
        assertThat(maxRangeText, TextInputControlMatchers.hasText("1234"));
        TextField currChargeText = find("#currChargeText");
        currChargeText.setText("87.6");
        assertThat(currChargeText, TextInputControlMatchers.hasText("87.6"));
    }

}
