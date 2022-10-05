package seng202.team3.cucumber.mainpagesteps;

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
import javafx.stage.Stage;
import seng202.team3.cucumber.CucumberFxBase;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.Charger;
import seng202.team3.gui.MainController;
import seng202.team3.gui.MainWindow;
import seng202.team3.gui.MapHandler;

/**
 * Cucumber Tests designed to check acceptance tests of searching and filtering
 *
 * @author Michelle Hsieh
 * @version 1.0.1, Sep 22
 */
public class SearchFilterMainStepDefs extends CucumberFxBase {

    private static MainController controller;
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

        db.addChargerCsvToData("csvtest/filtering");
    }

    @Given("I have the app open")
    public void iHaveTheAppOpen() {
        assertTrue(Stage.getWindows().size() > 0);
    }

    @When("I navigate to the home view")
    public void iNavigateToTheHomeView() {
        clickOn("#menuButton");
    }

    @Then("chargers are available")
    public void chargersAreAvailable() {
        controller = (MainController) MainWindow.getController();
        assertTrue(controller.getManager().getData().size() > 0);
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
        clickOn("#executeSearch");

    }

    @When("The user inputs an invalid query {string}")
    public void userTriesInvalidQuery(String query) {
        userTriesValidQuery(query);

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
        assertTrue(chargers.isEmpty());
    }

    @When("The user filters for charging cost")
    public void userFiltersForNoChargingCost() {
        clickOn("#filters");
        clickOn("#chargingCost");
        clickOn("#hasChargingCost");
        clickOn("#executeSearch");
    }

    @Then("The list of chargers found have charging cost")
    public void noChargersFound() {
        ObservableList<Charger> chargers = controller.getManager().getData();
        for (Charger charger : chargers) {
            if (!charger.getChargeCost()) {
                fail();
            }
        }
    }
}
