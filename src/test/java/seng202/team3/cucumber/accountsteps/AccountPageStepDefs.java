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
import io.cucumber.java.mk_latn.No;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.VerticalDirection;
import javafx.scene.Node;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import seng202.team3.cucumber.CucumberFxBase;
import seng202.team3.data.database.ComparisonType;
import seng202.team3.data.database.QueryBuilderImpl;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.*;
import seng202.team3.gui.AccountController;
import seng202.team3.gui.MainWindow;
import seng202.team3.gui.MapHandler;
import seng202.team3.logic.UserManager;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Cucumber Tests designed to check acceptance tests for the account page
 *
 * @author Matthew
 * @version 1.0.0 Oct 22
 */
public class AccountPageStepDefs extends CucumberFxBase {

    private static AccountController controller;

    static SqlInterpreter db;

    private static List<Object> chargerObject;

    private static List<Object> users;

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
        User chargerOwner = new User("chargerowner@gmail.com", "MrTestOwner",
                PermissionLevel.CHARGEROWNER);
        db.writeUser(chargerOwner, UserManager.encryptThisString("qwerty"));

        users = db.readData(new QueryBuilderImpl().withSource("user").build(), User.class);

        chargerObject = db.readData(new QueryBuilderImpl().withSource("charger")
                .build(),
                Charger.class);

        for (Object o : chargerObject) {
            ((Charger) o).setOwnerId(1); // Set owner to admin
        }
        db.writeCharger(new ArrayList<>(chargerObject));

        Connector dummyConnector1 = new Connector("ChardaMo", "AC", "Available", "123", 3);
        Coordinate coord1 = new Coordinate(1.1, 2.3, -43.53418, 172.627572, "TestCity");
        Charger testCharger = new Charger(
                new ArrayList<>(List.of(dummyConnector1)), "Hosp", coord1, 1, 0.3, "Meridian",
                "2020/1/1 00:00:00", true,
                false, false, false);
        testCharger.setOwnerId(chargerOwner.getUserid());
        testCharger.setOwner("MrTestOwner");
        db.writeCharger(testCharger);

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

    @Then("I am informed that my email is invalid")
    public void invalidAccountEmail() {
        verifyThat("#invalidUpdateAccount", Node::isVisible);
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

    @Then("I am informed that my password is invalid")
    public void invalidAccountPassword() {
        verifyThat("#invalidUpdateAccount", Node::isVisible);
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

    @Given("The user has a charger they would like to add to the app")
    public void goToAddCharger() throws InterruptedException {
        clickOn("#modChargers");
        clickOn("#addCharger");
    }

    @When("the user inputs the charger’s details, and clicks the ‘add charger’ button")
    public void inputChargerInfo() {
        clickOn("#name");
        write("Test Charger");
        clickOn("#operator");
        write("Test Op");
        clickOn("#address");
        write("11 Test addy");
        clickOn("#open24");
        clickOn("#parks");
        write("5");
        clickOn("#time");
        write("240");
        clickOn("#lat");
        write("1.1");
        clickOn("#lon");
        write("1.1");
        clickOn("#addConnectorButton");
        clickOn("#currentField");
        write("CURRENT");
        clickOn("#wattageField");
        write("TEEEEST");
        clickOn("#chargingPointsField");
        write("4");
        clickOn("#typeField");
        write("AC");
        clickOn("#statusField");
        write("operational");
        clickOn("#saveConnectors");
        clickOn("#saveButton");
    }

    @Then("The charger is added to the table")
    public void checkTableForCharger() throws IOException {
        List<Object> newCharger = db.readData(new QueryBuilderImpl().withSource("charger")
                .withFilter("name", "Test Charger", ComparisonType.EQUAL)
                .build(),
                Charger.class);
        assertFalse(chargerObject.contains(newCharger));
    }

    @Given("There is sufficient reason to change a user’s status")
    public void goToPermissions() {
        clickOn("#editAdmin");
        clickOn("#table");
        press(KeyCode.DOWN);
        release(KeyCode.DOWN);
    }

    @When("A user upgrades a user permission")
    public void upgradePermission() {
        clickOn("#menu");
        clickOn("#chargerOwner");
        // scroll(10, VerticalDirection.DOWN);
        clickOn("#updatePermissions");
    }

    @Then("The user now has access to different functionality of the app")
    public void checkPermission() throws IOException {
        List<Object> user = db.readData(new QueryBuilderImpl().withSource("user")
                .build(), User.class);
        assertEquals(PermissionLevel.CHARGEROWNER, ((User) user.get(1)).getLevel());
    }

    @When("The admin deletes an account")
    public void deleteAccount() {
        scroll(30, VerticalDirection.DOWN);
        clickOn("#delete");
        clickOn("#confirm");
    }

    @Then("The account is deleted")
    public void checkDeletedAccount() throws IOException {
        List<Object> change = db.readData(new QueryBuilderImpl().withSource("user").build(), User.class);
        assertEquals(users.size() - 1, change.size());
    }

    @Given("The user owns a charger")
    public void ownsCharger() throws IOException {
        List<Object> chargers = db.readData(new QueryBuilderImpl().withSource("charger")
                .withFilter("owner", "3", ComparisonType.EQUAL).build(), Charger.class);
        assertTrue(chargers.size() > 0);
        clickOn("#mainTable");
    }

    @When("The user edits the charger details")
    public void editCharger() {
        clickOn("#modChargers");
        clickOn("#editCharger");
        doubleClickOn("#name");
        press(KeyCode.BACK_SPACE).release(KeyCode.BACK_SPACE);
        write("NewName");
        doubleClickOn("#parks");
        press(KeyCode.BACK_SPACE).release(KeyCode.BACK_SPACE);
        write("10");
        clickOn("#saveButton");
    }

    @Then("The charger details are saved")
    public void checkChargerDetails() throws IOException {
        List<Object> chargers = db.readData(new QueryBuilderImpl().withSource("charger")
                .withFilter("name", "NewName", ComparisonType.EQUAL).build(), Charger.class);
        assertEquals(10, ((Charger) chargers.get(0)).getAvailableParks());
    }

    @When("The user clicks delete charger")
    public void deleteCharger() {
        clickOn("#modChargers");
        clickOn("#deleteCharger");
        clickOn("#confirm");
    }

    @Then("The charger details are deleted")
    public void noChargerExists() throws IOException {
        List<Object> chargers = db.readData(new QueryBuilderImpl().withSource("charger")
                .withFilter("owner", "2", ComparisonType.EQUAL).build(), Charger.class);
        assertEquals(0, chargers.size());
    }

    @Given("The user owns no chargers")
    public void noOwnedChargers() throws IOException {
        List<Object> chargers = db.readData(new QueryBuilderImpl().withSource("charger")
                .withFilter("owner", "3", ComparisonType.EQUAL).build(), Charger.class);
        for (Object o : chargers) {
            db.deleteData("charger", ((Charger) o).getChargerId());
        }
        clickOn("#menuButton");
        clickOn("#accountPage");
    }

    @Then("The table is empty")
    public void emptyTable() {
        TableView<Charger> table = (TableView<Charger>) find("#mainTable");
        assertTrue(table.getItems().isEmpty());
    }

}
