package seng202.team3.unittest.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.management.InstanceAlreadyExistsException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seng202.team3.data.database.QueryBuilder;
import seng202.team3.data.database.QueryBuilderImpl;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.EntityType;
import seng202.team3.data.entity.PermissionLevel;
import seng202.team3.data.entity.User;
import seng202.team3.logic.AdminManager;

/**
 * Unit tests for the {@link seng202.team3.logic.AdminManager} class
 *
 * @author Michelle Hsieh
 * @version 1.0.0, Sep 22
 */
public class AdminManagerTest {

    /**
     * The {@link AdminManager} involved
     */
    private AdminManager manager;

    /**
     * Dummy users and chargerOwners
     */
    private User chargerOwnerOne;
    private User chargerOwnerTwo;
    private User userOne;
    private User userTwo;
    private User otherAdmin;

    /**
     * The instance of the database
     */
    private static SqlInterpreter database;

    /**
     * The instance of the {@link User} who is admin
     */
    private static User admin;

    /**
     * Creates a test database
     *
     * @throws IOException                    I/O exceptions
     * @throws InstanceAlreadyExistsException if database hasn't been cleaned off
     */
    @BeforeAll
    public static void initialise() throws IOException, InstanceAlreadyExistsException {
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
     * Sets up the test scenario with the default admin, and a few charger owners
     *
     * @throws IOException  if I/O faulty
     * @throws SQLException if SQL issue
     */
    @BeforeEach
    public void setUp() throws IOException, SQLException {
        manager = new AdminManager();
        manager.setAdmin(admin);
        chargerOwnerOne = new User("charger@test.com", "chargeOne", PermissionLevel.CHARGEROWNER);
        chargerOwnerOne.setId(2);
        chargerOwnerTwo = new User("charger2@test.com", "chargeTwo", PermissionLevel.CHARGEROWNER);
        chargerOwnerTwo.setId(3);
        userOne = new User("user@test.com", "userOne", PermissionLevel.USER);
        userOne.setId(4);
        userTwo = new User("user2@test.com", "userTwo", PermissionLevel.USER);
        userTwo.setId(5);
        otherAdmin = new User("admin@test.com", "admin2", PermissionLevel.ADMIN);
        otherAdmin.setId(6);
        database.writeUser(chargerOwnerOne, "null");
        database.writeUser(chargerOwnerTwo, "null");
        database.writeUser(userOne, "null");
        database.writeUser(userTwo, "null");
        database.writeUser(otherAdmin, "null");
        manager.makeUsers();
    }

    /**
     * Removes everything except the preset admin
     *
     * @throws IOException if IO error
     */
    @AfterEach
    public void tearDown() throws IOException {
        for (int i = 2; i < 7; i++) {
            database.deleteData(EntityType.USER, i);
        }
        manager = null;

        QueryBuilder allUsers = new QueryBuilderImpl().withSource(EntityType.USER);
        List<User> userList = new ArrayList<>();
        for (Object o : database
                .readData(allUsers.build())) {
            userList.add((User) o);
        }

        assertEquals(1, userList.size());
        assertEquals(1, userList.get(0).getId());
        assertNull(manager);
    }

    /**
     * Cleans up the database
     */
    @AfterAll
    public static void destroy() {
        admin = null;
        SqlInterpreter.removeInstance();
        database = null;
    }

    /**
     * Checks that the total number of users is correct
     */
    @Test
    public void correctNumberUsers() {
        assertEquals(6, manager.getUserList().size());
    }

    /**
     * Updates the user to chargerowner successfully
     *
     * @throws SQLException if database writing issues
     */
    @Test
    public void updateUserSuccess() throws SQLException {
        User user = manager.getUserList().get(3);
        manager.setSelectedUser(user);
        user.setLevel(PermissionLevel.CHARGEROWNER);
        manager.updateUser();
        manager.makeUsers();
        assertEquals(PermissionLevel.CHARGEROWNER, manager.getUserList().get(3).getLevel());
    }

    /**
     * Cannot update the currently selected admin
     *
     * @throws SQLException if database writing issues
     */
    @Test
    public void updateAdminFailure() throws SQLException {
        User admin = manager.getAdmin();
        manager.setSelectedUser(admin);
        admin.setLevel(PermissionLevel.CHARGEROWNER);
        manager.updateUser();
        manager.makeUsers();
        assertEquals(PermissionLevel.ADMIN, manager.getUserList().get(0).getLevel());
    }

    /**
     * Can update other admin
     *
     * @throws SQLException if database writing issues
     */
    @Test
    public void updateOtherAdminSuccess() throws SQLException {
        User admin = manager.getUserList().get(5);
        manager.setSelectedUser(admin);
        admin.setLevel(PermissionLevel.USER);
        manager.updateUser();
        manager.makeUsers();
        assertEquals(PermissionLevel.USER, manager.getUserList().get(5).getLevel());
    }

    /**
     * Checks if permissions strings look correct
     */
    @Test
    public void permissionStringCorrect() {
        String output = "";
        output += (manager.permissionString(manager.getUserList().get(0).getLevel()));
        output += (manager.permissionString(manager.getUserList().get(1).getLevel()));
        output += (manager.permissionString(manager.getUserList().get(3).getLevel()));
        assertEquals("AdministrationCharger OwnerUser", output);
    }

    /**
     * Checks if {@link PermissionLevel}s are correct if given strings
     */
    @Test
    public void permissionUser() {
        assertEquals(PermissionLevel.USER, manager.permissionLevel("User"));
    }

    /**
     * Checks if {@link PermissionLevel}s are correct if given strings
     */
    @Test
    public void permissionChargerOwner() {
        assertEquals(PermissionLevel.CHARGEROWNER, manager.permissionLevel("Charger Owner"));
    }

    /**
     * Checks if {@link PermissionLevel}s are correct if given strings
     */
    @Test
    public void permissionAdmin() {
        assertEquals(PermissionLevel.ADMIN, manager.permissionLevel("Administration"));
    }
}
