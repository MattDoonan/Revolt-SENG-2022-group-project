package seng202.team3.testfx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.bytebuddy.implementation.ExceptionMethod;
import org.apache.commons.collections.functors.NullIsExceptionPredicate;
import org.junit.jupiter.api.*;
import org.testfx.api.FxRobotException;
import org.testfx.framework.junit5.ApplicationTest;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.PermissionLevel;
import seng202.team3.data.entity.User;
import seng202.team3.gui.*;
import javax.management.InstanceAlreadyExistsException;
import java.io.IOException;

import static org.testfx.api.FxAssert.verifyThat;

public class LoginTextFX extends TestFxBase{


    private LoginSignupController controller;

    @BeforeAll
    static void initialize() throws InstanceAlreadyExistsException, IOException {
        SqlInterpreter.removeInstance();
        SqlInterpreter.initialiseInstanceWithUrl(
                "jdbc:sqlite:./target/test-classes/test_database.db");
    }

    @Override
    public void setUp() throws Exception {
        ApplicationTest.launch(MainWindow.class);

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
