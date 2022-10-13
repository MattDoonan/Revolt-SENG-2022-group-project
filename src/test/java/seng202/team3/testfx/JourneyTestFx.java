package seng202.team3.testfx;

import static org.testfx.api.FxAssert.verifyThat;

import org.junit.jupiter.api.Test;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import seng202.team3.data.database.CsvInterpreter;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.PermissionLevel;
import seng202.team3.data.entity.User;
import seng202.team3.gui.JourneyController;
import seng202.team3.logic.UserManager;

/**
 * TestFx for JourneyController
 *
 * @author Angus Kirtlan
 * @version 1.0.0, Oct 22
 */
public class JourneyTestFx extends TestFxBase {

    private JourneyController journeyController;
    static SqlInterpreter db;
    static User testUser;

    /**
     * Starts the JourneyController for testing
     *
     * @param stage the stage of the application
     * @throws Exception if fail to launch
     */
    @Override
    public void start(Stage stage) throws Exception {
        testUser = new User("admin@admin.com", "admin",
                PermissionLevel.ADMIN);
        testUser.setId(1);

        UserManager.setUser(testUser);
        SqlInterpreter.removeInstance();
        db = SqlInterpreter.initialiseInstanceWithUrl(
                "jdbc:sqlite:./target/test-classes/test_database.db");
        db.defaultDatabase();

        new CsvInterpreter().importChargersToDatabase("/csvtest/filtering.csv");


        FXMLLoader journeyLoader = new FXMLLoader(getClass().getResource("/fxml/journey.fxml"));
        Parent page = journeyLoader.load();
        initState(journeyLoader, stage);
        Scene scene = new Scene(page);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Initialises the state of the current application
     *
     * @param journeyLoader the FXML loader after loading
     * @param stage  the stage of the application
     */
    public void initState(FXMLLoader journeyLoader, Stage stage) {
        journeyController = journeyLoader.getController();
        journeyController.init(stage);
    }

    /**
     * Tests the start shows an error prompt when clicked without a vehicle created / selected
     */
    @Test
    public void startJourneyError() {
        clickOn("#makeStart");
        verifyThat("#prompt", Node::isVisible);
    }

    /**
     * Tests the end shows an error prompt when clicked without a start selected
     */
    @Test
    public void endJourneyError() {
        clickOn("#makeEnd");
        verifyThat("#prompt", Node::isVisible);
    }


}
