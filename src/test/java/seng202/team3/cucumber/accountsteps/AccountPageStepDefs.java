package seng202.team3.cucumber.accountsteps;

import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;
import seng202.team3.cucumber.CucumberFxBase;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.gui.AccountController;
import seng202.team3.gui.LoginSignupController;
import seng202.team3.gui.MainWindow;
import seng202.team3.gui.MapHandler;
import seng202.team3.logic.UserManager;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Cucumber Tests designed to check acceptance tests for the account page
 *
 * @author Matthew
 * @version 1.0.0 Oct 22
 */
public class AccountPageStepDefs extends CucumberFxBase {

    private static AccountController controller;

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


    @Before
    @Override
    public void init() throws Exception {
        db = SqlInterpreter.getInstance();
        db.defaultDatabase();
    }

    @Given("I have the app open")
    public void iHaveTheAppOpen() {
        assertTrue(Stage.getWindows().size() > 0);
    }

    @And("I am logged out")
    public void iAmLoggedOut() {
        if (UserManager.getUser() != UserManager.getGuest()) {
            clickOn("#loginSignout");
        }
        assertEquals(UserManager.getGuest(), UserManager.getUser());
    }

    @And("I log in with username: {string} password: {string}")
    public void iAmLoggedInWithUsernamePassword(String username, String password) {
        clickOn("#loginSignout");
        clickOn("#loginEmailField");
        write(username);
        clickOn("#loginPasswordField");
        write(password);
        clickOn("#loginBtn");
    }

    @When("I navigate to the account screen")
    public void iNavigateToTheAccountScreen() {
        clickOn("#accountPage");
    }

    @Given("I want to edit my information")
    public void clickEditButton() {
        clickOn("#editAccountButton");
    }

    @When("I want to change my account name to {string}")
    public void changeAccountName(String newName) {
        doubleClickOn("#accountName");
        press(KeyCode.BACK_SPACE).release(KeyCode.BACK_SPACE);
        clickOn("#accountName");
        write(newName);
    }

    @And("I want to save my information")
    public void saveAccountInfo() {
        clickOn("#confirm");
    }

    @Then("My account name has changed to {string}")
    public void differentAccountName(String actual) {
        Assertions.assertEquals(actual, UserManager.getUser().getAccountName());
    }

    @When("I want to change my account email to {string}")
    public void changeAccountEmail(String newEmail) {
        doubleClickOn("#accountEmail");
        press(KeyCode.BACK_SPACE).release(KeyCode.BACK_SPACE);
        clickOn("#accountEmail");
        write(newEmail);
    }

    @Then("My account email has changed to {string}")
    public void differentAccountEmail(String actual) {
        Assertions.assertEquals(actual, UserManager.getUser().getEmail());
    }

    @When("I want to change my password to {string}")
    public void changeAccountPassword(String password) {
        clickOn("#accountPassword");
        write(password);
    }

    @Then("I logout of the app")
    public void logout() {
        clickOn("#loginSignout");
    }

    @Then("I am successfully logged in")
    public void iSuccessfullyLoggedIn() {
        assertNotEquals(UserManager.getGuest(), UserManager.getUser());
        assertNotNull(UserManager.getUser());
    }

}
