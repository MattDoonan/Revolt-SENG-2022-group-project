package seng202.team3.cucumber.tablepagesteps;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import javafx.collections.ObservableList;
import javafx.scene.input.KeyCode;
import seng202.team3.cucumber.CucumberFxBase;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.Charger;
import seng202.team3.gui.MainWindow;
import seng202.team3.gui.TableController;

/**
 * Cucumber Tests designed to check acceptance tests of searching and filtering
 *
 * @author Michelle Hsieh
 * @version 1.0.1, Sep 22
 */
public class SearchFilterTableStepDefsTest extends CucumberFxBase {

    private static TableController controller;
    static SqlInterpreter db;

    /**
     * {@inheritDoc}
     *
     * @throws Exception if the setup cannot be initialized
     */
    @BeforeAll
    public static void setup() throws Exception {
        CucumberFxBase.setup();
    }

    /**
     * {@inheritDoc}
     * Cucumber won't allow inheritence with tags
     * This is a small work around
     */
    @Before
    @Override
    public void reset() {
        super.reset();
    }

    /**
     * {@inheritDoc}
     */
    @AfterAll
    public static void cleanUp() {
        CucumberFxBase.cleanUp();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() throws Exception {
        SqlInterpreter.removeInstance();
        db = SqlInterpreter.initialiseInstanceWithUrl(
                "jdbc:sqlite:./target/test-classes/test_database.db");

        db.addChargerCsvToData("csvtest/filtering");

        controller = (TableController) MainWindow.getController();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void navigateToScreen() {
        clickOn("#showTable");
    }

    @Given("There is no current input given")
    public void noInputGiven() {
        clickOn("#searchCharger");
        doubleClickOn("#searchCharger");
        press(KeyCode.BACK_SPACE).release(KeyCode.BACK_SPACE);

    }

    @When("The user inputs a valid query {string}")
    public void userTriesValidQuery(String query) {
        clickOn("#searchCharger");
        write(query);
        clickOn("#update");

    }

    @When("The user inputs an invalid query {string}")
    public void userTriesInValidQuery(String query) {
        clickOn("#searchCharger");
        write(query);
        clickOn("#update");

    }

    @Then("The user gets the chargers that best match their query {string}")
    public void chargersFilteredCorrectly(String query) {
        boolean isValid = true;
        ObservableList<Charger> chargers = controller.getManager().getData();
        for (Charger charger : chargers) {
            String lowerAddress = charger.getLocation().getAddress().toLowerCase();
            if (!lowerAddress.contains(query.toLowerCase())) {
                isValid = false;
            }
        }
        assertTrue(isValid);
    }

    @Then("No results are listed")
    public void noResults() {
        ObservableList<Charger> chargers = controller.getManager().getData();
        assertTrue(chargers.size() == 0);
    }

    @When("The user filters for no charging cost")
    public void userFiltersForNoChargingCost() {
        clickOn("#filters");
        clickOn("#cCostsMenu");
        clickOn("#chargingCost");
        clickOn("#update");
    }

    @Then("The list of chargers found have no charging cost")
    public void noChargersFound() {
        ObservableList<Charger> chargers = controller.getManager().getData();
        for (Charger charger : chargers) {
            if (charger.getChargeCost()) {
                fail();
            }
        }
    }

}
