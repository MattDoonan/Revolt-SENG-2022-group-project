package seng202.team3.testfx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.testfx.util.WaitForAsyncUtils;
import seng202.team3.data.database.ComparisonType;
import seng202.team3.data.database.QueryBuilder;
import seng202.team3.data.database.QueryBuilderImpl;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.*;
import seng202.team3.gui.ChargerController;
import seng202.team3.gui.MenuController;
import seng202.team3.logic.GeoLocationHandler;
import seng202.team3.logic.UserManager;

/**
 * TestFx to test adding a charger
 *
 * @author Michelle Hsieh
 * @version 1.0.0, Sep 22
 */
public class AddChargerPageTestFx extends TestFxBase {
    /**
     * Logger
     */
    private static final Logger logManager = LogManager.getLogger();
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
        User user = new User("admin@something.co", "trial", PermissionLevel.ADMIN);
        user.setUserid(1);
        UserManager.setUser(user);
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
        stage.setScene(scene);
        controller.init(stage);
        stage.show();
    }

    /**
     * Checks if adding a charger without filling out throws errors
     */
    @Test
    public void addChargerFail() {
        GeoLocationHandler.setCoordinate(new Coordinate(1.1, 2.3, -43.60, 172.572), "Trial location");
        clickOn("#saveButton");
        clickOn("#okay");
    }

    /**
     * Checks if adding a charger works
     */
    @Test
    public void addChargerSuccess() {
        GeoLocationHandler.setCoordinate(new Coordinate(1.1, 2.3, -43.60, 172.572), "Trial location");
        clickOn("#name");
        write("Trial");
        clickOn("#operator");
        write("Me");
        clickOn("#parks");
        write("2");
        clickOn("#time");
        write("129");
        clickOn("#cost");
        clickOn("#addConnectorButton");
        clickOn("#currentField");
        write("something");
        clickOn("#wattageField");
        write("12");
        clickOn("#chargingPointsField");
        write("12");
        clickOn("#typeField");
        write("else");
        clickOn("#statusField");
        write("operative");
        clickOn("#saveConnectors");
        clickOn("#lat");
        write("-53.00");
        clickOn("#lon");
        write("123.0");
        clickOn("#saveButton");

        ArrayList<Object> returnedChargers = new ArrayList<>();
        try {
            List<Object> chargers = database.readData(new QueryBuilderImpl().withSource("charger")
                    .withFilter("name", "Trial", ComparisonType.EQUAL).build(), Charger.class);
            returnedChargers.addAll(chargers);

        } catch (IOException e) {
            e.printStackTrace();
        }

        //TODO find a way to assert across TestFX, Java Application and JUnit threads! -_-
    }

}
