package seng202.team3.testfx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.Connector;
import seng202.team3.data.entity.Coordinate;
import seng202.team3.gui.ChargerController;

/**
 * Runs the editcharger testFX
 *
 * @author Michelle Hsieh
 * @version 1.0.0, Sep 22
 */
public class EditChargerPageTestFx extends TestFxBase {

    private Charger charger;
    private static SqlInterpreter database;
    private ChargerController controller;

    /**
     * Creates a test database
     *
     * @throws Exception execeptions
     */
    @BeforeAll
    public static void initialise() throws Exception {
        SqlInterpreter.removeInstance();
        database = SqlInterpreter.initialiseInstanceWithUrl(
                "jdbc:sqlite:./target/test-classes/test_database.db");
        database.defaultDatabase();
    }

    /**
     * Starts everything
     *
     * @param stage The Stage which this application is on
     * @throws Exception if stage issues
     */
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader editor = new FXMLLoader(getClass()
                .getResource("/fxml/charger_info.fxml"));
        Parent editorParent = editor.load();
        Scene scene = new Scene(editorParent);
        controller = editor.getController();
        addChargers();
        controller.setCharger(charger);
        stage.setScene(scene);
        controller.init(stage);
        stage.show();
    }

    /**
     * Adds just one charger to database
     *
     * @throws IOException IO error handling
     */
    public void addChargers() throws IOException {
        Coordinate coord1 = new Coordinate(1.1, 2.3, -43.53418, 172.627572);
        Connector connector = new Connector("ChardaMo", "AC", "Available", "123", 3);

        charger = new Charger(
                new ArrayList<>(List.of(connector)), "Hosp", coord1, 1, 0.3, "Meridian",
                "2020/1/1 00:00:00", true,
                false, false, false);
        database.writeCharger(charger);
    }

    /**
     * Checks if editing a charger after deleting a connector fails
     */
    @Test
    public void editChargerFail() {
        clickOn("#connectorTable");
        press(KeyCode.DOWN);
        clickOn("#deleteConnectorButton");
        clickOn("#saveButton");
        clickOn("#okay");
    }

    /**
     * Checks if editing a charger after a success is successful
     */
    @Test
    public void editChargerSuccess() {
        clickOn("#open24");
        clickOn("#saveButton");
        clickOn("#okay");
    }

}
