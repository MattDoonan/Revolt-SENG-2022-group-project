package seng202.team3.testfx.gui.controller;

import static org.testfx.api.FxAssert.verifyThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import javax.management.InstanceAlreadyExistsException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.testfx.api.FxRobotException;
import org.testfx.api.FxRobotInterface;
import org.testfx.robot.SleepRobot;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.database.csv.CsvInterpreter;
import seng202.team3.data.database.util.QueryBuilderImpl;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.Entity;
import seng202.team3.data.entity.User;
import seng202.team3.data.entity.util.EntityType;
import seng202.team3.data.entity.util.PermissionLevel;
import seng202.team3.gui.controller.MenuController;
import seng202.team3.gui.controller.map.MapHandler;
import seng202.team3.logic.manager.UserManager;
import seng202.team3.testfx.TestFxBase;

/**
 * Runs the Nav bar testFX
 *
 * @author Matthew Doonan
 * @version 1.0.0, Sep 22
 */
public class NavbarTestFx extends TestFxBase {

    private MenuController controller;

    private static User user;

    private static String password;

    @BeforeAll
    static void initialize() throws InstanceAlreadyExistsException, IOException {
        SqlInterpreter.removeInstance();
        SqlInterpreter.initialiseInstanceWithUrl(
                "jdbc:sqlite:./target/test-classes/test_database.db");
        SqlInterpreter.getInstance().defaultDatabase();
        new CsvInterpreter().importChargersToDatabase("/csvtest/filtering.csv");
        user = new User("test@gmail.com", "MrTest", PermissionLevel.CHARGEROWNER);
        user.setId(2);
        password = "1234";
        SqlInterpreter.getInstance().writeUser(user, UserManager.encryptThisString(password));

        List<Entity> chargers = SqlInterpreter.getInstance()
                .readData(new QueryBuilderImpl()
                        .withSource(EntityType.CHARGER)
                        .build());
        for (Entity o : chargers) {
            ((Charger) o).setOwnerId(user.getId());
        }
        SqlInterpreter.getInstance().writeCharger(new ArrayList<>(chargers));
    }

    @AfterAll
    static void deleteUser() throws IOException {
        SqlInterpreter.getInstance().defaultDatabase();
    }

    @Override
    public void start(Stage stage) throws Exception {
        MapHandler.setLocationAccepted(false);
        MapHandler.setMapRequested(false);
        UserManager.setUser(user);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/menu_bar.fxml"));
        Parent page = loader.load();
        initState(loader, stage);
        Scene scene = new Scene(page);
        stage.setScene(scene);
        stage.setMinWidth(1500);
        stage.show();
    }

    private void initState(FXMLLoader loader, Stage stage) {
        controller = loader.getController();
        controller.init(stage);
    }

    private static Stream<Arguments> loadScreens() {
        return Stream.of(
                Arguments.of("#accountPage", "#editAccountButton"),
                Arguments.of("GARAGE", "#openUpdate"),
                Arguments.of("JOURNEY", "#previousJourneyTable"),
                Arguments.of("HOME", "#chargerListPane"));
    }

    @ParameterizedTest
    @MethodSource("loadScreens")
    public void loadScreensTests(String page, String toCheck) {
        clickOn("#accountMenu");
        clickOn(page);
        verifyThat(toCheck, Node::isVisible);
    }

    @Test
    public void screenThenBackToMain() {
        clickOn("#accountMenu");
        clickOn("#accountPage");
        clickOn("HOME");
        verifyThat("#displayInfo", Node::isVisible);
    }

}
