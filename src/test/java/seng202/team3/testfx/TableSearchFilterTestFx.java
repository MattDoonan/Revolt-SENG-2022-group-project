package seng202.team3.testfx;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobotException;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import seng202.team3.data.database.CsvInterpreter;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.PermissionLevel;
import seng202.team3.data.entity.User;
import seng202.team3.gui.TableController;
import seng202.team3.logic.UserManager;

/**
 * Tests for charger searching from table GUI view
 *
 * @author Matthew Doonan
 * @version 1.0.0, Sep 22
 */
public class TableSearchFilterTestFx extends TestFxBase {

    private TableController controller;

    static SqlInterpreter db;
    static User testUser;

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

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main_table.fxml"));
        Parent page = loader.load();
        initState(loader, stage);
        Scene scene = new Scene(page);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Initialises the state of the current application
     *
     * @param loader the FXML loader after loading
     * @param stage  the stage of the application
     */
    public void initState(FXMLLoader loader, Stage stage) {
        UserManager.setUser(testUser);
        controller = loader.getController();
        controller.init();
        controller.setUser(UserManager.getUser());
        controller.populateTable();
    }

    @Test
    public void containsAddress() {
        UserManager.setUser(testUser);
        boolean isValid = true;
        clickOn("#searchCharger");
        write("christ");
        clickOn("#update");
        ObservableList<Charger> chargers = controller.getManager().getData();
        for (Charger charger : chargers) {
            String lowerAddress = charger.getLocation().getAddress().toLowerCase();
            if (!lowerAddress.contains("christ")) {
                isValid = false;
            }
        }
        assertTrue(isValid);
    }

    @Test
    public void timeLimitFilterWorks() {
        boolean isValid = true;
        clickOn("#filters");
        clickOn("#toggleTimeLimit");
        clickOn("#update");
        for (Charger charger : controller.getManager().getData()) {
            if (charger.getTimeLimit() < 60) {
                isValid = false;
            }
        }
        assertTrue(isValid);
    }

    @Test
    public void parkingFilterWorks() {
        boolean isValid = true;
        clickOn("#filters");
        clickOn("#onParkingFilter");
        clickOn("#update");
        for (Charger charger : controller.getManager().getData()) {
            if (charger.getAvailableParks() < 5) {
                isValid = false;
            }
        }
        assertTrue(isValid);
    }

    @Test
    public void filtersMultipleTicks() {
        boolean isValid = true;
        clickOn("#filters");
        clickOn("#cpMenu");
        clickOn("#withoutCarparkCost");
        clickOn("#openMenu");
        clickOn("#openAllButton");
        clickOn("#update");
        ObservableList<Charger> chargers = controller.getManager().getData();
        for (Charger charger : chargers) {
            if (charger.getParkingCost()) {
                isValid = false;
            } else if (!charger.getAvailable24Hrs()) {
                isValid = false;
            }
        }
        assertTrue(isValid);
    }

    @Test
    public void columnShowId() {
        boolean isValid = true;
        clickOn("#columnEdit");
        clickOn("#showId");
        clickOn("#searchCharger");
        clickOn("#update");
        try {
            clickOn("#idCol");
            assertTrue(isValid);
        } catch (FxRobotException e) {
            fail("No column found");
        }
    }

    @Test
    public void noIdColumn() {
        boolean isValid = true;
        try {
            clickOn("#idCol");
            fail("Column found");
        } catch (FxRobotException e) {
            assertTrue(isValid);
        }
    }

    @Test
    public void editMultipleColumns() {
        boolean isValid = true;
        clickOn("#columnEdit");
        clickOn("#showId");
        clickOn("#showAttraction");
        clickOn("#showLon");
        clickOn("#searchCharger");
        clickOn("#update");
        try {
            clickOn("#attractionCol");
            clickOn("#longitudeCol");
            isValid = false;
        } catch (FxRobotException e) {
            // Do Nothing
        }
        try {
            clickOn("#idCol");
        } catch (FxRobotException e) {
            isValid = false;
        }
        assertTrue(isValid);
    }
}
