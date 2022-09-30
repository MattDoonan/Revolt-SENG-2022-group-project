package seng202.team3.testfx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobotException;
import org.testfx.framework.junit5.ApplicationTest;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.PermissionLevel;
import seng202.team3.data.entity.User;
import seng202.team3.gui.MainWindow;
import seng202.team3.gui.MenuController;
import seng202.team3.logic.UserManager;

import javax.management.InstanceAlreadyExistsException;
import java.io.IOException;
import java.sql.SQLException;

import static org.testfx.api.FxAssert.verifyThat;

public class NavbarTestFx extends TestFxBase{

    private MenuController controller;

    private static User user;

    private static String password;

    @BeforeAll
    static void initialize() throws InstanceAlreadyExistsException, IOException {
        SqlInterpreter.removeInstance();
        SqlInterpreter.initialiseInstanceWithUrl(
                "jdbc:sqlite:./target/test-classes/test_database.db");
        user = new User("test@gmail.com", "MrTest", PermissionLevel.ADMIN);
        password = "1234";
        SqlInterpreter.getInstance().writeUser(user, SqlInterpreter.encryptThisString(password));
    }

    @AfterAll
    static void deleteUser() throws IOException {
        SqlInterpreter.getInstance().deleteData("user", user.getUserid());
    }

    @Override
    public void setUp() throws Exception {
        ApplicationTest.launch(MainWindow.class);

    }
    @Override
    public void start(Stage stage) throws Exception {
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
}
