package seng202.team3.testfx.gui.controller;

import static org.testfx.api.FxAssert.verifyThat;

import java.io.IOException;

import javax.management.InstanceAlreadyExistsException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.gui.controller.LoginSignupController;
import seng202.team3.gui.controller.MenuController;
import seng202.team3.testfx.TestFxBase;

/**
 * Runs the Login testFX
 *
 * @author Matthew Doonan
 * @version 1.0.0, Sep 22
 */
public class LoginTextFX extends TestFxBase {

    private LoginSignupController controller;

    @BeforeAll
    static void initialize() throws InstanceAlreadyExistsException, IOException {
        SqlInterpreter.removeInstance();
        SqlInterpreter.initialiseInstanceWithUrl(
                "jdbc:sqlite:./target/test-classes/test_database.db");
        SqlInterpreter.getInstance().defaultDatabase();
    }

    @BeforeEach
    public void init() throws IOException {
        SqlInterpreter.getInstance().defaultDatabase();
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
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
    public void testFailedLogin() {
        clickOn("#loginBtn");
        verifyThat("#invalidLogin", Node::isVisible);
    }

    @Test
    public void testInvalidUsername() {
        clickOn("#loginEmailField");
        write("This should fail");
        clickOn("#loginBtn");
        verifyThat("#invalidLogin", Node::isVisible);
    }

    @Test
    public void testInvalidPassword() {
        clickOn("#loginEmailField");
        write("admin");
        clickOn("#loginPasswordField");
        write("fake");
        clickOn("#loginBtn");
        verifyThat("#invalidLogin", Node::isVisible);
    }
}
