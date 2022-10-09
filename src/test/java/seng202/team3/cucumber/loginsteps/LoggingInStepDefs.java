package seng202.team3.cucumber.loginsteps;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.io.IOException;
import java.util.List;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import seng202.team3.cucumber.CucumberFxBase;
import seng202.team3.data.database.QueryBuilderImpl;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.EntityType;
import seng202.team3.data.entity.Entity;
import seng202.team3.gui.LoginSignupController;
import seng202.team3.gui.MainWindow;
import seng202.team3.gui.MapHandler;
import seng202.team3.logic.UserManager;

/**
 * Cucumber Tests designed to check acceptance tests of loggin in
 *
 * @author Harrison Tyson
 * @version 1.0.0, Oct 22
 */
public class LoggingInStepDefs extends CucumberFxBase {

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

    @When("I navigate to the login screen")
    public void iNavigateToLoginScreen() {
        clickOn("#loginSignout");
    }

    @Then("The login popup appears")
    public void theLoginPopupAppears() {
        controller = (LoginSignupController) MainWindow.getController();
        assertEquals("Login", controller.getStage().getTitle());
    }

    @Given("I have an account")
    public void iHaveAnAccount() throws IOException {
        // Check for default admin account
        List<Entity> users = db.readData(new QueryBuilderImpl()
                .withSource(EntityType.USER)
                .build());
        assertTrue(users.size() >= 1);
    }

    @When("I enter my username: {string} and password: {string}")
    public void iEnterMyUsernameAndPassword(String username, String password) {
        clickOn("#loginEmailField");
        write(username);
        clickOn("#loginPasswordField");
        write(password);
        clickOn("#loginBtn");
    }

    @Then("I am successfully logged in")
    public void iSuccessfullyLoggedIn() {
        assertNotEquals(UserManager.getGuest(), UserManager.getUser());
        assertNotNull(UserManager.getUser());
    }

    @Then("I am informed it was incorrect")
    public void iInformedItWasIncorrect() {
        Label info = this.find("#invalidLogin");
        assertTrue(info.isVisible());
    }

    @Given("I do not have an account")
    public void iDoNotHaveAnAccount() {
        UserManager.setUser(UserManager.getGuest());
        assertEquals(UserManager.getGuest(), UserManager.getUser());
    }

    @When("I choose to sign up")
    public void iChooseToSignUp() {
        clickOn("#signUpBtn");
    }

    @Then("I am redirected to the sign up page")
    public void iAmRedirectedToTheSignUpPage() {
        assertEquals("Signup", controller.getStage().getTitle());

    }

}
