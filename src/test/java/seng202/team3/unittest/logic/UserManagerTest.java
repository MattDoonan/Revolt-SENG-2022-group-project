package seng202.team3.unittest.logic;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import seng202.team3.data.database.ComparisonType;
import seng202.team3.data.database.QueryBuilderImpl;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.PermissionLevel;
import seng202.team3.data.entity.User;
import seng202.team3.logic.UserManager;

import javax.management.InstanceAlreadyExistsException;

public class UserManagerTest {

    private UserManager manager;

    private User user;

    @BeforeAll
    static void initialize() throws InstanceAlreadyExistsException, IOException {
        SqlInterpreter.removeInstance();
        SqlInterpreter.initialiseInstanceWithUrl(
                "jdbc:sqlite:./target/test-classes/test_database.db");
    }

    @BeforeEach
    public void reset() {
        manager = new UserManager();
        user = new User("real@gmail.com", "testAccount", PermissionLevel.USER);
    }

    @AfterEach
    public void deleteUser() throws IOException {
        SqlInterpreter.getInstance().deleteData("user", user.getUserid());
    }

    /**
     * Checks if the set user works
     */
    @Test
    public void getSetUser() {
        manager.setUser(user);
        Assertions.assertEquals(user, manager.getUser());
    }

    /**
     * Checks if the save user function works
     */
    @Test
    public void saveUserTest() throws IOException {
        try {
            manager.saveUser(user, "test");
        } catch (IOException e) {
            Assertions.fail("Database failed");
        }
        List<Object> res = SqlInterpreter.getInstance().readData(new QueryBuilderImpl()
                .withSource("user").withFilter("username", "testAccount",
                        ComparisonType.EQUAL)
                .build(), User.class);

        Assertions.assertEquals((User) res.get(0), user);
    }

    /**
     * Checks if the login works
     */
    @Test
    public void loginTest() throws IOException {
        manager.saveUser(user, "test");
        List<Object> res = SqlInterpreter.getInstance().readData(new QueryBuilderImpl()
                .withSource("user").withFilter("username", "testAccount",
                        ComparisonType.EQUAL)
                .build(), User.class);
        try {
            Assertions.assertEquals((User) res.get(0),
                    manager.login("testAccount", "test"));
        } catch (SQLException | IOException e) {
            Assertions.fail("Sql Failed");
        }
    }

    /**
     * Checks if the user updates
     */
    @Test
    public void updateUser() throws IOException {
        manager.saveUser(user, "test");
        user.setEmail("newEmail@gmail.com");
        user.setCarbonSaved(50);
        try {
            manager.updateUser(user);
            List<Object> res = SqlInterpreter.getInstance().readData(new QueryBuilderImpl()
                    .withSource("user").withFilter("username", "testAccount",
                            ComparisonType.EQUAL)
                    .build(), User.class);
            Assertions.assertEquals((User) res.get(0), user);
        } catch (SQLException e) {
            Assertions.fail("Database failed");
        }
    }

    private static Stream<Arguments> positiveEmailTests() {
        return Stream.of(
                Arguments.of("abc-d@mail.com"),
                Arguments.of("abc.def@mail.com"),
                Arguments.of("abc_def@mail.com"),
                Arguments.of("abc@mail.com"),
                Arguments.of("abc.def@mail.cc"),
                Arguments.of("abc.def.ghi@mail.cc"),
                Arguments.of("abc.def@mail-archive.com"),
                Arguments.of("abc.def@mail.org"),
                Arguments.of("abc.def@mail.com"));
    }

    /**
     * Tests some valid email addresses
     * 
     * @param email the email string
     */
    @ParameterizedTest
    @MethodSource("positiveEmailTests")
    public void testValidEmails(String email) {
        Assertions.assertTrue(manager.checkEmail(email));
    }

    private static Stream<Arguments> negativeEmailTests() {
        return Stream.of(
                Arguments.of("abc.@mail.com"),
                Arguments.of("abc..def@mail.com"),
                Arguments.of(".abc@mail.com"),
                Arguments.of("abc#def@mail.com"),
                Arguments.of("abc.def@mail.c"),
                Arguments.of("abc.def@mail#archive.com"),
                Arguments.of("abc.def@mail"),
                Arguments.of("abc.def@mail..com"));
    }

    /**
     * Tests some invalid email addresses
     * 
     * @param email the email
     */
    @ParameterizedTest
    @MethodSource("negativeEmailTests")
    public void testInvalidEmails(String email) {
        Assertions.assertFalse(UserManager.checkEmail(email));
    }

    @Test
    public void checkEncryptThisString() {
        String original = "1234";
        String encrypted = manager.encryptThisString(original);
        Assertions.assertNotEquals(encrypted, original);
    }

    @Test
    public void checkEncryption() {
        String original = "1234";
        String encrypted = manager.encryptThisString(original);
        String toCheck = manager.encryptThisString(original);
        Assertions.assertEquals(encrypted, toCheck);
    }


    private static Stream<Arguments> passwords() {
        return Stream.of(
                Arguments.of("1234", "1 2 3 4"),
                Arguments.of("1234", " 1234"),
                Arguments.of("1234", "1234 "),
                Arguments.of("1234", "5678"),
                Arguments.of("1234", "123$"),
                Arguments.of("Supercalifragilisticexpialidocious", "Supercalifragilisticexpialidocious'"),
                Arguments.of("password", "Password"),
                Arguments.of("password", "PASSWORD"),
                Arguments.of("password", "pass word"),
                Arguments.of("''''", "''"),
                Arguments.of(" ", ""));
    }
    @ParameterizedTest
    @MethodSource("passwords")
    public void checkDifferentEncryption(String original, String different ) {
        String encrypted = manager.encryptThisString(original);
        String toCheck = manager.encryptThisString(different);
        Assertions.assertNotEquals(encrypted, toCheck);
    }
}

