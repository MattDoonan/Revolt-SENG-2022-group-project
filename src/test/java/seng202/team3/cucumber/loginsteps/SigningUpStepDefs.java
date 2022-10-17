package seng202.team3.cucumber.loginsteps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.testfx.api.FxAssert.verifyThat;

import java.io.IOException;
import java.util.List;

import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import javafx.application.Platform;
import javafx.scene.Node;
import seng202.team3.cucumber.CucumberFxBase;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.database.util.ComparisonType;
import seng202.team3.data.database.util.QueryBuilderImpl;
import seng202.team3.data.entity.Entity;
import seng202.team3.data.entity.User;
import seng202.team3.data.entity.util.EntityType;
import seng202.team3.data.entity.util.PermissionLevel;
import seng202.team3.gui.MainWindow;
import seng202.team3.gui.controller.LoginSignupController;
import seng202.team3.gui.controller.map.MapHandler;
import seng202.team3.logic.manager.UserManager;

/**
 * Cucumber Tests designed to check acceptance tests of searching and filtering
 * COMMON BACKGROUND STEPS ARE DEFINED IN {@link LoggingInStepDefs}
 *
 * @author Harrison Tyson
 * @version 1.0.0, Oct 22
 */
public class SigningUpStepDefs extends CucumberFxBase {

    private static LoginSignupController controller;
    static SqlInterpreter db;

    /**
     * {@inheritDoc}
     *
     * @throws Exception if the setup cannot be initialized
     */
    @BeforeAll
    public static void setup() throws Exception {
        MapHandler.setLocationAccepted(false);
        CucumberFxBase.setup();
    }

    /**
     * Calls Clean Up from static FxBase
     * {@inheritDoc}
     */
    @AfterAll
    public static void cleanUp() {
        CucumberFxBase.cleanUp();
    }

    /**
     * {@inheritDoc}
     */
    @Before
    @Override
    public void init() throws Exception {
        db = SqlInterpreter.getInstance();
        db.defaultDatabase();
    }

    @And("the login screen is not open")
    public void closeLoginScreen() {
        if (MainWindow.getController() instanceof LoginSignupController) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    ((LoginSignupController) MainWindow.getController()).close();
                }
            });

        }
    }

    @And("there is an option to sign up")
    public void optionToSignUp() {
        verifyThat("#signUpBtn", Node::isVisible);
    }

    @Given("account with username: {string} does not exist")
    public void accountWithUsernameDoesNotExist(String username) throws IOException {
        List<Entity> users = db.readData(new QueryBuilderImpl()
                .withSource(EntityType.USER)
                .withFilter("username", username, ComparisonType.EQUAL).build());
        assertTrue(users.isEmpty());
    }

    @When("I enter my details username: {string} email: {string} password: {string}")
    public void iEnterMyDetailsUsername(String username, String email, String password) {
        clickOn("#signupUsernameField");
        write(username);
        clickOn("#signupEmailField");
        write(email);
        clickOn("#signupPasswordField");
        write(password);
        clickOn("#confPassField");
        write(password);
    }

    @And("I click the sign up button")
    public void iClickTheSignUpButton() {
        clickOn("#signUpBtn");
    }

    @Then("an account with username: {string} and email: {string} is created")
    public void anAccountWithUsernameAndEmail(String username, String email) throws IOException {
        List<Entity> users = db.readData(new QueryBuilderImpl()
                .withSource(EntityType.USER)
                .withFilter("username", username, ComparisonType.EQUAL).build());
        assertEquals(1, users.size());

        if (users.get(0) instanceof User) {
            User user = (User) users.get(0);
            assertEquals(username, user.getAccountName());
            assertEquals(email, user.getEmail());
        } else {
            fail();
        }
    }

    @And("email: {string} is invalid")
    public void emailIsInvalid(String email) {
        assertFalse(UserManager.checkEmail(email));
    }

    @Then("I am informed my email is invalid")
    public void iAmInformedMyEmailIsInvalid() {
        verifyThat("#invalidSignup", Node::isVisible);
    }

    @And("an account with username: {string} is not created")
    public void anAcccountWithUsernameIsNotCreated(String username) throws IOException {
        accountWithUsernameDoesNotExist(username);
    }

    @Given("account with username: {string} email: {string} password: {string} exists")
    public void accountWithUsernameEmailPassword(String username, String email, String password)
            throws IOException {
        User user = new User();
        user.setAccountName(username);
        user.setEmail(email);
        user.setLevel(PermissionLevel.USER);
        UserManager accountManager = new UserManager();
        accountManager.saveUser(user, UserManager.encryptThisString(password));
    }

    @Then("I am informed that my account exists")
    public void iAmInformedThatMyAccountExists() {
        verifyThat("#invalidSignup", Node::isVisible);
    }

}
