package seng202.team3.cucumber.mainPageSteps;

import io.cucumber.java.After;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.BeforeStep;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.testfx.framework.junit5.ApplicationTest;
import seng202.team3.data.database.ComparisonType;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.Connector;
import seng202.team3.gui.MainController;
import seng202.team3.gui.MainWindow;
import seng202.team3.logic.MainManager;
import seng202.team3.testFX.TestFXBase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;

/**
 * Cucumber Tests designed to check acceptance tests of searching and filtering
 *
 * @author Michelle Hsieh
 * @version 1.0.1, Sep 22
 */
public class SearchFilterStepDefs {

    private MainManager manager;

    /**
     * Initialises the Cucumber Test Base
     */
    public SearchFilterStepDefs() {
    }

    /**
     * Implements a manager
     */
    @Given("There is a valid instance")
    public void setUpManager() {
        manager = new MainManager();
    }


    @Given("There is no current input given")
    public void noInputGiven() {
        manager.resetQuery();
        manager.makeAllChargers();
    }

    /**
     * AT_1
     */
    @When("The user inputs a valid query {string}")
    public void userTriesValidQuery(String query) {
        manager.adjustQuery("address", query, ComparisonType.CONTAINS);
        manager.makeAllChargers();
    }

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
     * AT_2
     */
    @When("The user inputs an invalid query {string}")
    public void badInputGiven(String query) {
        manager.adjustQuery("address", query, ComparisonType.CONTAINS);
        manager.makeAllChargers();
    }

    @Then("No results are listed")
    public void noChargers() {
        assertEquals(0, manager.getCloseChargerData().size());
    }

}
