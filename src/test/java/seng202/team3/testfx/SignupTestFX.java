package seng202.team3.testfx;

import static org.testfx.api.FxAssert.verifyThat;

import java.io.IOException;

import javax.management.InstanceAlreadyExistsException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.gui.LoginSignupController;
import seng202.team3.gui.MenuController;

/**
 * Runs the Signup testFX
 *
 * @author Matthew Doonan
 * @version 1.0.0, Sep 22
 */
public class SignupTestFX extends TestFxBase {

    private LoginSignupController controller;

    @BeforeAll
    static void initialize() throws InstanceAlreadyExistsException, IOException {
        SqlInterpreter.removeInstance();
        SqlInterpreter.initialiseInstanceWithUrl(
                "jdbc:sqlite:./target/test-classes/test_database.db");
        SqlInterpreter.getInstance().defaultDatabase();
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/signup.fxml"));
        Parent page = loader.load();
        initState(loader, stage);
        Scene scene = new Scene(page);
        stage.setScene(scene);
        stage.show();
    }

    private void initState(FXMLLoader loader, Stage stage) {
        controller = loader.getController();
        controller.init(new MenuController());
    }

    @Test
    public void testBlankLogin() {
        clickOn("#signUpBtn");
        verifyThat("#invalidSignup", Node::isVisible);
    }

    @Test
    public void testOnlyUserName() {
        clickOn("#signupUsernameField");
        write("Test User");
        clickOn("#signUpBtn");
        verifyThat("#invalidSignup", Node::isVisible);
    }

    @Test
    public void testOnlyEmail() {
        clickOn("#signupEmailField");
        write("tester@gmail.com");
        clickOn("#signUpBtn");
        verifyThat("#invalidSignup", Node::isVisible);
    }

    @Test
    public void testOnlyPassword() {
        clickOn("#signupPasswordField");
        write("1234");
        clickOn("#signUpBtn");
        verifyThat("#invalidSignup", Node::isVisible);
    }

    @Test
    public void testOnlyPasswords() {
        clickOn("#signupPasswordField");
        write("1234");
        clickOn("#confPassField");
        write("1234");
        clickOn("#signUpBtn");
        verifyThat("#invalidSignup", Node::isVisible);
    }

    @Test
    public void testNoPassword() {
        clickOn("#signupUsernameField");
        write("Test User");
        clickOn("#signupEmailField");
        write("tester@gmail.com");
        clickOn("#signUpBtn");
        verifyThat("#invalidSignup", Node::isVisible);
    }

    @Test
    public void testNoEmail() {
        clickOn("#signupUsernameField");
        write("Test User");
        clickOn("#signupPasswordField");
        write("1234");
        clickOn("#confPassField");
        write("1234");
        clickOn("#signUpBtn");
        verifyThat("#invalidSignup", Node::isVisible);
    }

    @Test
    public void testNoAccount() {
        clickOn("#signupEmailField");
        write("tester@gmail.com");
        clickOn("#signupPasswordField");
        write("1234");
        clickOn("#confPassField");
        write("1234");
        clickOn("#signUpBtn");
        verifyThat("#invalidSignup", Node::isVisible);
    }

    @Test
    public void testNoConfirmPassword() {
        clickOn("#signupUsernameField");
        write("Test User");
        clickOn("#signupEmailField");
        write("tester@gmail.com");
        clickOn("#signupPasswordField");
        write("1234");
        clickOn("#signUpBtn");
        verifyThat("#invalidSignup", Node::isVisible);
    }

    @Test
    public void testNoRealPassword() {
        clickOn("#signupUsernameField");
        write("Test User");
        clickOn("#signupEmailField");
        write("tester@gmail.com");
        clickOn("#confPassField");
        write("1234");
        clickOn("#signUpBtn");
        verifyThat("#invalidSignup", Node::isVisible);
    }

    @Test
    public void testExistingUserName() {
        clickOn("#signupUsernameField");
        write("admin");
        clickOn("#signupEmailField");
        write("tester@gmail.com");
        clickOn("#signupPasswordField");
        write("1234");
        clickOn("#confPassField");
        write("1234");
        clickOn("#signUpBtn");
        verifyThat("#invalidSignup", Node::isVisible);
    }

    @Test
    public void testInvalidEmail() {
        clickOn("#signupUsernameField");
        write("Test User");
        clickOn("#signupEmailField");
        write("fakeEmail");
        clickOn("#signupPasswordField");
        write("1234");
        clickOn("#confPassField");
        write("1234");
        clickOn("#signUpBtn");
        verifyThat("#invalidSignup", Node::isVisible);
    }

    @Test
    public void testDifferentPassword() {
        clickOn("#signupUsernameField");
        write("Test User");
        clickOn("#signupEmailField");
        write("tester@gmail.com");
        clickOn("#signupPasswordField");
        write("1234");
        clickOn("#confPassField");
        write("5678");
        clickOn("#signUpBtn");
        verifyThat("#invalidSignup", Node::isVisible);
    }
}
