package seng202.team3.cucumber.accountsteps;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.testfx.api.FxRobotException;

import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import javafx.scene.Node;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import seng202.team3.cucumber.CucumberFxBase;
import seng202.team3.data.database.QueryBuilderImpl;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.PermissionLevel;
import seng202.team3.data.entity.User;
import seng202.team3.gui.AccountController;
import seng202.team3.gui.MainWindow;
import seng202.team3.gui.MapHandler;
import seng202.team3.logic.UserManager;

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
        db.addChargerCsvToData("csvtest/filtering");
        db.writeUser(new User("Tester@gmail.com",
                "MrTest", PermissionLevel.USER), "1234");

        List<Object> chargerObject = db.readData(new QueryBuilderImpl().withSource("charger")
                .build(),
                Charger.class);

        for (Object o : chargerObject) {
            ((Charger) o).setOwnerId(1); // Set owner to admin
        }
        db.writeCharger(new ArrayList<>(chargerObject));

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
        controller = (AccountController) MainWindow.getController();
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

    @Given("There are chargers in the presentation")
    public void noCurrentInput() {
        TableView<Charger> table = (TableView<Charger>) this.find("#mainTable");
        assertTrue(table.getItems().size() > 0);
    }

    @When("The user clicks on the carparks table header")
    public void clickCarParkHeader() {
        clickOn("#carparkCol");
    }

    @Then("The list of chargers is sorted by carparks")
    public void sortedTableCarParks() {
        TableView<Charger> table = (TableView<Charger>) find("#mainTable");
        List<Charger> chargers = table.getItems();
        Collections.sort(chargers,
                (o1, o2) -> Integer.compare(o1.getAvailableParks(), o2.getAvailableParks()));

        assertArrayEquals(chargers.toArray(), table.getItems().toArray());

    }

    @When("The user click select columns and deselects 'Show address' and 'Show owner'")
    public void deselectColumns() {
        clickOn("#columnEdit");
        clickOn("#showAddress");
        clickOn("#showOwner");
        clickOn("#update");
    }

    @Then("The fields disappear")
    public void fieldsNotThere() {
        Boolean pass = true;
        try {
            clickOn("#showAddress");
            pass = false;
        } catch (FxRobotException e) {
            // Do nothing
        }
        try {
            clickOn("#showOwner");
            pass = false;
        } catch (FxRobotException e) {
            // Do nothing
        }
        assertTrue(pass);
    }

    @Given("The time limit is hidden")
    public void hideTimeLimit() {
        clickOn("#columnEdit");
        clickOn("#showTimeLimit");
        clickOn("#update");
    }

    @When("The user click select columns and selects show time limit")
    public void selectTimeLimit() {
        clickOn("#columnEdit");
        clickOn("#showTimeLimit");
        clickOn("#update");
    }

    @Then("The time limit field appears")
    public void checkTimeLimitField() {
        verifyThat("#timeLimitCol", Node::isVisible);
    }

}
