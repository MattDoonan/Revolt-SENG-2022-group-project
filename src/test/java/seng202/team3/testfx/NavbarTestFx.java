package seng202.team3.testfx;

import static org.testfx.api.FxAssert.verifyThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import javax.management.InstanceAlreadyExistsException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.testfx.api.FxRobotException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import seng202.team3.data.database.CsvInterpreter;
import seng202.team3.data.database.QueryBuilderImpl;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.EntityType;
import seng202.team3.data.entity.PermissionLevel;
import seng202.team3.data.entity.Entity;
import seng202.team3.data.entity.User;
import seng202.team3.gui.MapHandler;
import seng202.team3.gui.MenuController;
import seng202.team3.logic.UserManager;

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
        SqlInterpreter.getInstance().deleteData(EntityType.USER, user.getId());
    }

    @Override
    public void start(Stage stage) throws Exception {
        MapHandler.resetPermission();
        MapHandler.setLocationAccepted(true);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/menu_bar.fxml"));
        Parent page = loader.load();
        initState(loader, stage);
        Scene scene = new Scene(page);
        stage.setScene(scene);
        stage.show();
    }

    private void initState(FXMLLoader loader, Stage stage) {
        controller = loader.getController();
        controller.init(stage);
    }

    public void login() {
        clickOn("#accountMenu");
        clickOn("#loginSignout");
        moveTo("#accountMenu");
        clickOn("#loginEmailField");
        write(user.getAccountName());
        clickOn("#loginPasswordField");
        write(password);
        clickOn("#loginBtn");
    }

    public void logout() {
        clickOn("#accountMenu");
        clickOn("#loginSignout");
    }

    @Test
    public void realLogin() {
        login();
        try {
            clickOn("#invalidLogin");
            Assertions.fail("Should be a valid login");
        } catch (FxRobotException e) {
            Assertions.assertTrue(true);
        }
        logout();
    }

    @Test
    public void realSignUp() throws IOException {
        clickOn("#accountMenu");
        clickOn("#loginSignout");
        moveTo("HOME");
        clickOn("#signUpBtn");
        clickOn("#signupUsernameField");
        write("Test User");
        clickOn("#signupEmailField");
        write("tester@gmail.com");
        clickOn("#signupPasswordField");
        write("1234");
        clickOn("#confPassField");
        write("1234");
        clickOn("#signUpBtn");
        try {
            clickOn("#invalidSignup");
            Assertions.fail("Should be a valid login");
        } catch (FxRobotException e) {
            Assertions.assertTrue(true);
        }
        logout();
        SqlInterpreter.getInstance()
                .deleteData(EntityType.USER, UserManager.getUser().getId());
    }

    @Test
    public void loginLogout() {
        login();
        logout();
        clickOn("#accountMenu");
        clickOn("#loginSignout");
        verifyThat("#loginEmailField", Node::isVisible);
    }

    private static Stream<Arguments> loadScreens() {
        return Stream.of(
                Arguments.of("#accountPage", "#editAccountButton"),
                Arguments.of("GARAGE", "#openUpdate"));
    }

    @ParameterizedTest
    @MethodSource("loadScreens")
    public void loadScreensTests(String page, String toCheck) {
        login();
        clickOn("#accountMenu");
        clickOn(page);
        verifyThat(toCheck, Node::isVisible);
        logout();
    }

    @Test
    public void screenThenBackToMain() {
        login();
        clickOn("#accountMenu");
        clickOn("#accountPage");
        clickOn("HOME");
        verifyThat("#displayInfo", Node::isVisible);
        logout();
    }
}
