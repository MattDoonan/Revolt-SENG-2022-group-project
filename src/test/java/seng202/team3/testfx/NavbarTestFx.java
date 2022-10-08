package seng202.team3.testfx;

import static org.testfx.api.FxAssert.verifyThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import javax.management.InstanceAlreadyExistsException;

import javafx.geometry.HorizontalDirection;
import javafx.geometry.VerticalDirection;
import javafx.scene.control.Button;
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
import seng202.team3.data.database.ComparisonType;
import seng202.team3.data.database.QueryBuilderImpl;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.PermissionLevel;
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
        SqlInterpreter.getInstance().addChargerCsvToData("csvtest/filtering");
        user = new User("test@gmail.com", "MrTest", PermissionLevel.CHARGEROWNER);
        user.setUserid(2);
        password = "1234";
        SqlInterpreter.getInstance().writeUser(user, UserManager.encryptThisString(password));
        List<Object> chargers = SqlInterpreter.getInstance().readData(new QueryBuilderImpl()
                .withSource("charger")
                .build(), Charger.class);
        for (Object o : chargers) {
            ((Charger) o).setOwnerId(user.getUserid());
        }
        SqlInterpreter.getInstance().writeCharger(new ArrayList<>(chargers));
    }

    @AfterAll
    static void deleteUser() throws IOException {
        SqlInterpreter.getInstance().deleteData("user", user.getUserid());
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

    @Test
    public void realLogin() {
        clickOn("#loginSignout");
        clickOn("#loginSignout");
        clickOn("#loginEmailField");
        write(user.getAccountName());
        clickOn("#loginPasswordField");
        write(password);
        clickOn("#loginBtn");
        try {
            clickOn("#invalidLogin");
            Assertions.fail("Should be a valid login");
        } catch (FxRobotException e) {
            Assertions.assertTrue(true);
        }
    }

    @Test
    public void realSignUp() throws IOException {
        clickOn("#loginSignout");
        clickOn("#loginSignout");
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
        SqlInterpreter.getInstance()
                .deleteData("user", UserManager.getUser().getUserid());
    }

    @Test
    public void loginLogout() {
        clickOn("#loginSignout");
        clickOn("#loginSignout");
        clickOn("#loginEmailField");
        write(user.getAccountName());
        clickOn("#loginPasswordField");
        write(password);
        clickOn("#loginBtn");
        clickOn("#loginSignout");
        clickOn("#loginSignout");
        verifyThat("#loginBtn", Node::isVisible);
    }

    private static Stream<Arguments> loadScreens() {
        return Stream.of(
                Arguments.of("#accountPage", "#editAccountButton"),
                Arguments.of("#vehicleButton", "#openUpdate"));
    }

    @ParameterizedTest
    @MethodSource("loadScreens")
    public void loadScreensTests(String accountPage, String toCheck) {
        clickOn("#loginSignout");
        clickOn("#loginSignout");
        clickOn("#loginEmailField");
        write(user.getAccountName());
        clickOn("#loginPasswordField");
        write(password);
        clickOn("#loginBtn");
        clickOn(accountPage);
        verifyThat(toCheck, Node::isVisible);
    }

    @Test
    public void screenThenBackToMain() {
        clickOn("#loginSignout");
        clickOn("#loginSignout");
        clickOn("#loginEmailField");
        write(user.getAccountName());
        clickOn("#loginPasswordField");
        write(password);
        clickOn("#loginBtn");
        clickOn("#accountPage");
        clickOn("#menuButton");
    }

    @Test
    public void deleteUserTest() throws IOException {
        clickOn("#loginSignout");
        clickOn("#loginSignout");
        clickOn("#loginEmailField");
        write(user.getAccountName());
        clickOn("#loginPasswordField");
        write(password);
        clickOn("#loginBtn");
        clickOn("#accountPage");
        clickOn("#accountPage");
        clickOn("#accountName");
        clickOn("#editAccountButton");
        clickOn("#delete");
        clickOn("#confirm");
        Button b = (Button) find("#loginSignout");
        Assertions.assertTrue(b.getText() == "Login");
        List<Object> users =  SqlInterpreter.getInstance().readData(new QueryBuilderImpl().withSource("user")
                .withFilter("email", user.getEmail(), ComparisonType.EQUAL).build(), User.class);
        Assertions.assertTrue(users.size() == 0);
    }
}
