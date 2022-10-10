package seng202.team3.testfx;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobotException;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;
import javafx.stage.Window;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.Coordinate;
import seng202.team3.data.entity.PermissionLevel;
import seng202.team3.data.entity.User;
import seng202.team3.gui.ChargerController;
import seng202.team3.logic.GeoLocationHandler;
import seng202.team3.logic.UserManager;

/**
 * TestFx to test adding a charger
 *
 * @author Michelle Hsieh
 * @version 1.0.0, Sep 22
 */
public class AddChargerPageTestFx extends TestFxBase {
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

    @BeforeEach
    public void reset() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                List<Window> ws = Stage.getWindows();
                while (ws.size() > 1) {
                    ws.get(ws.size() - 1).hide();
                }
            }
        });
    }

    /**
     * Checks if adding a charger without filling out throws errors
     */
    @Test
    public void addChargerFail() {
        GeoLocationHandler.setCoordinate(new Coordinate(1.1, 2.3, -43.60, 172.572),
                "Trial location");
        clickOn("#saveButton");
        // clickOn("#okay");
        // TODO: assert error

        // At least one tooltip visible
        // for (Tooltip t : controller.getErrors().getAll()) {
        //     assertTrue(t.isShowing());
        //     return;
        // }
        // fail("No error tooltips shown");
    }


    /**
     * Checks if adding a charger works
     */
    @Test
    public void addChargerSuccess() {
        GeoLocationHandler.setCoordinate(new Coordinate(1.1, 2.3, -43.60, 172.572),
                "Trial location");
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
        ((TextField) this.find("#lat")).clear();
        ((TextField) this.find("#lon")).clear();
        doubleClickOn("#lat");
        write("-53.00");
        doubleClickOn("#lon");
        write("123.0");
        clickOn("#saveButton");

        // TODO: switch to new errors
        assertThrows(FxRobotException.class, () -> {
            clickOn("#prompt");
        });
    }

}
