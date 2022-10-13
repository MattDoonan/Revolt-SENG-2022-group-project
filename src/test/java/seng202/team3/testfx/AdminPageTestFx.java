package seng202.team3.testfx;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import javax.management.InstanceAlreadyExistsException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.PermissionLevel;
import seng202.team3.data.entity.User;
import seng202.team3.gui.AdminController;
import seng202.team3.logic.AdminManager;

/**
 * AdminPageTestFx for {@link seng202.team3.gui.AdminController}
 *
 * @author Michelle Hsieh
 * @version 1.0.0, Sep 22
 */
public class AdminPageTestFx extends TestFxBase {

    private static SqlInterpreter database;
    private static User admin;
    private AdminController controller;
    private AdminManager manager;
    private Stage stage;

    /**
     * Creates a test database, and sets an admin
     *
     * @throws IOException                    I/O exceptions
     * @throws InstanceAlreadyExistsException if database hasn't been cleaned off
     */
    @BeforeAll
    public static void initialise() throws Exception {
        SqlInterpreter.removeInstance();
        database = SqlInterpreter.initialiseInstanceWithUrl(
                "jdbc:sqlite:./target/test-classes/test_database.db");
        database.defaultDatabase();
        admin = new User("admin@admin.com", "admin",
                PermissionLevel.ADMIN);
        admin.setId(1);
        SqlInterpreter.getInstance().writeUser(admin, "admin");
    }

    /**
     * Starts everything
     *
     * @param stage The Stage which this application is on
     * @throws Exception if stage issues
     */
    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        FXMLLoader editor = new FXMLLoader(getClass()
                .getResource("/fxml/admin_page.fxml"));
        Parent editorParent = editor.load();
        Scene scene = new Scene(editorParent);
        controller = editor.getController();
        stage.setScene(scene);
        controller.init(new BorderPane());
        stage.show();
    }

    /**
     * Adds some data to use
     *
     * @throws IOException if writing issues
     */
    @BeforeEach
    public void addData() throws IOException {
        manager = controller.getManager();
        manager.setAdmin(admin);
        User chargerOwnerOne = new User("charger@test.com", "chargeOne", PermissionLevel.CHARGEROWNER);
        chargerOwnerOne.setId(2);
        User chargerOwnerTwo = new User("charger2@test.com", "chargeTwo", PermissionLevel.CHARGEROWNER);
        chargerOwnerTwo.setId(3);
        User userOne = new User("user@test.com", "userOne", PermissionLevel.USER);
        userOne.setId(4);
        User userTwo = new User("user2@test.com", "userTwo", PermissionLevel.USER);
        userTwo.setId(5);
        User otherAdmin = new User("admin@test.com", "admin2", PermissionLevel.ADMIN);
        otherAdmin.setId(6);
        database.writeUser(chargerOwnerOne, "null");
        database.writeUser(chargerOwnerTwo, "null");
        database.writeUser(userOne, "null");
        database.writeUser(userTwo, "null");
        database.writeUser(otherAdmin, "null");
        manager.makeUsers();
        controller.updateTable();
    }

    /**
     * Checks the select item and change permissions works
     */
    @Test
    public void changePermissions() {
        clickOn("#menu");
        clickOn("#admin");
        clickOn("#table");
        for (int i = 0; i < 3; i++) {
            press(KeyCode.DOWN);
            release(KeyCode.DOWN);
        }
        clickOn("#updatePermissions");
        assertEquals(PermissionLevel.ADMIN, manager.getUserList().get(3).getLevel());
    }

    /**
     * Checks to see if user is deleted accurately
     */
    @Test
    public void deleteUser() {
        clickOn("#table");
        for (int i = 0; i < 5; i++) {
            press(KeyCode.DOWN);
            release(KeyCode.DOWN);
        }
        clickOn("#delete");
        clickOn("#confirm");
        assertEquals(5, manager.getUserList().size());
    }

    /**
     * Checks the select item and change permission errors are thrown
     */
    @Test
    public void changePermissionsFail() {
        clickOn("#table");
        for (int i = 0; i < 2; i++) {
            press(KeyCode.DOWN);
            release(KeyCode.DOWN);
        }
        clickOn("#updatePermissions");
        clickOn("#okay");
    }

    /**
     * Checks the delete current admin fails and change errors are thrown
     */
    @Test
    public void deleteCurrentAdmin() {
        clickOn("#table");
        press(KeyCode.DOWN);
        release(KeyCode.DOWN);
        press(KeyCode.UP);
        release(KeyCode.UP);
        clickOn("#delete");
        clickOn("#okay");
    }

    /**
     * Checks the viewChargers can be pushed; testing for output as JUnit Tests
     */
    @Test
    public void viewChargers() {
        clickOn("#table");
        for (int i = 0; i < 2; i++) {
            press(KeyCode.DOWN);
            release(KeyCode.DOWN);
        }
        clickOn("#viewChargers");
    }
}
