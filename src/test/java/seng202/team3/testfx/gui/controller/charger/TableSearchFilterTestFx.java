package seng202.team3.testfx.gui.controller.charger;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.testfx.api.FxAssert.verifyThat;

import java.util.function.Predicate;
import java.util.stream.Stream;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.testfx.api.FxRobotException;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.stage.Stage;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.database.csv.CsvInterpreter;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.User;
import seng202.team3.data.entity.util.PermissionLevel;
import seng202.team3.gui.controller.charger.TableController;
import seng202.team3.logic.manager.UserManager;
import seng202.team3.testfx.TestFxBase;

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

    @BeforeAll
    public static void initialize() throws Exception {
        SqlInterpreter.removeInstance();
        db = SqlInterpreter.initialiseInstanceWithUrl(
                "jdbc:sqlite:./target/test-classes/test_database.db");
    }

    @Override
    public void start(Stage stage) throws Exception {
        testUser = new User("admin@admin.com", "admin",
                PermissionLevel.ADMIN);
        testUser.setId(1);
        UserManager.setUser(testUser);

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

    private static Stream<Arguments> buttonsToCheck() {
        return Stream.of(
                Arguments.of("#cCostsMenu", "#chargingCost", "#hasChargingCost"),
                Arguments.of("#attractMenu", "#attractionButton", "#noNearbyAttraction"),
                Arguments.of("#openMenu", "#openAllButton", "#notOpenAllButton"),
                Arguments.of("#cpMenu", "#withoutCarparkCost", "#withCarparkCost"));
    }

    @ParameterizedTest
    @MethodSource("buttonsToCheck")
    public void checkSwapOnButtons(String menuButton, String firstButton, String secondButton) {
        clickOn("#filters");
        clickOn(menuButton);
        clickOn(firstButton);
        clickOn(secondButton);
        verifyThat(firstButton, Predicate.not(CheckBox::isSelected));
        clickOn("#update");
    }
}
