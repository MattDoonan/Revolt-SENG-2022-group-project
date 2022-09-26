package seng202.team3.cucumber.mainpagesteps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.management.InstanceAlreadyExistsException;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import javafx.collections.ObservableList;
import seng202.team3.data.database.ComparisonType;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.Charger;
import seng202.team3.logic.MainManager;

/**
 * Cucumber Tests designed to check acceptance tests of searching and filtering
 *
 * @author Michelle Hsieh
 * @version 1.0.1, Sep 22
 */
public class SearchFilterStepDefsTest {

    private MainManager manager;
    static SqlInterpreter db;

    /**
     * Initialises the Cucumber Test Base
     */
    public SearchFilterStepDefsTest() {
        SqlInterpreter.removeInstance();
        try {
            db = SqlInterpreter.initialiseInstanceWithUrl(
                    "jdbc:sqlite:./target/test-classes/test_database.db");
            db.addChargerCsvToData("csvtest/filtering");
        } catch (IOException | InstanceAlreadyExistsException e) {
            e.printStackTrace();
        }
    }

    /**
     * Implements a manager
     */
    @Before
    public void setUpManager() {
        manager = new MainManager();
    }

    @Given("There is no current input given")
    public void noInputGiven() {
        manager.resetQuery();
        manager.makeAllChargers();
    }

    /**
     * AT_2 when condition
     */
    @When("The user inputs a valid query {string}")
    public void userTriesValidQuery(String query) {
        manager.adjustQuery("address", query, ComparisonType.CONTAINS);
        manager.makeAllChargers();
    }

    /**
     * AT_2 then condition
     * 
     * @param query query being filtered
     */
    @Then("The user gets the chargers that best match their query {string}")
    public void chargersFilteredCorrectly(String query) {
        boolean isValid = true;
        ObservableList<Charger> chargers = manager.getCloseChargerData();
        for (Charger charger : chargers) {
            String lower = charger.getLocation().getAddress().toLowerCase();
            query = query.toLowerCase();
            if (!lower.contains(query)) {
                isValid = false;
            }
        }
        assertTrue(isValid);
    }

    /**
     * AT_3 when condition
     */
    @When("The user inputs an invalid query {string}")
    public void badInputGiven(String query) {
        manager.adjustQuery("address", query, ComparisonType.CONTAINS);
        manager.makeAllChargers();
    }

    /**
     * AT_3 then condition
     */
    @Then("No results are listed")
    public void noChargers() {
        assertEquals(0, manager.getCloseChargerData().size());
    }

    /**
     * AT_4
     */
    @When("The user filters for no charging cost")
    public void filterChargerCost() {
        manager.adjustQuery("haschargingcost", "False", ComparisonType.EQUAL);
        manager.makeAllChargers();
    }

    /**
     * AT_4
     */
    @Then("The list of chargers found have no charging cost")
    public void noPaidChargers() {
        boolean isValid = true;
        ObservableList<Charger> chargers = manager.getCloseChargerData();
        for (Charger charger : chargers) {
            if (charger.getChargeCost()) {
                isValid = false;
            }
        }
        assertTrue(isValid);
    }

    /**
     * AT_5 no favourite implemented
     */
    public void func4() {
        // IMPLEMENT ME
    }

    /**
     * AT_6 need testFX to sort as front end table handles the sorting
     */
    public void func5() {
        // IMPLEMENT ME
    }

    /**
     * AT_7 need testFX unless you can test FXML if field is shown?
     */
    public void func6() {
        // IMPLEMENT ME
    }

    /**
     * AT_8 need testFX
     */
    public void func7() {
        // IMPLEMENT ME
    }

    /**
     * AT_9 testFX
     */
    public void func8() {
        // IMPLEMENT ME
    }
}
