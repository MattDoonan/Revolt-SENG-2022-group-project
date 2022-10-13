package seng202.team3.cucumber.journeysteps;

import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team3.cucumber.CucumberFxBase;
import seng202.team3.data.database.ComparisonType;
import seng202.team3.data.database.CsvInterpreter;
import seng202.team3.data.database.QueryBuilderImpl;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.*;
import seng202.team3.gui.*;
import seng202.team3.logic.UserManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class JourneyPageStepDefs extends CucumberFxBase{

    static SqlInterpreter db;

    private static Connector testConnector1;

    private static Charger testCharger;

    private static Vehicle testVehicle;

    private static Journey testJourneyOne;

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

    public void setUpDatabase() throws IOException {
        db.defaultDatabase();
        testConnector1 = new Connector("ChardaMo", "AC", "Available", "123", 3);
        testCharger = new Charger(new ArrayList<Connector>(
                Arrays.asList(testConnector1)),
                "Test2",
                new Coordinate(4.321, -23.323),
                1,
                0.3,
                "Meridian",
                "2020/05/01 00:00:00+00",
                false,
                false,
                true,
                false);
        testCharger.setOwner("admin");
        SqlInterpreter.getInstance().writeCharger(testCharger);

        testVehicle = new Vehicle("TestMake", "TestModel",
                300, new ArrayList<String>(Arrays.asList("Type 1 Tethered")));
        testVehicle.setOwner(1);
        SqlInterpreter.getInstance().writeVehicle(testVehicle);

        testJourneyOne = new Journey(testVehicle, new Coordinate(4.399, -24.122),
                new Coordinate(4.301, -22.992), "10/10/2002", "Name");
        testJourneyOne.addStop(new Stop(testCharger));
        testJourneyOne.setUser(1);
    }

    /**
     * {@inheritDoc}
     */
    @Before
    @Override
    public void init() throws Exception {
        db = SqlInterpreter.getInstance();
        db.defaultDatabase();
        new CsvInterpreter().importChargersToDatabase("/csvtest/filtering.csv");
    }

    @Given("I have the app open")
    public void iHaveTheAppOpen() {
        Assertions.assertTrue(Stage.getWindows().size() > 0);
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
        clickOn("#journeyButton");
    }

    @Given("The user has journeys to view")
    public void journeyToView() throws IOException {
        setUpDatabase();
        SqlInterpreter.getInstance().writeJourney(testJourneyOne);
    }

    @When("The user is on the accountPage")
    public void onAccountPage() {
        clickOn("#journeyButton");
    }

    @Then("The user can view a list of previous journeys")
    public void viewPrevJourneys() {
        TableView table = (TableView) find("#previousJourneyTable");
        assertTrue(table.getItems().size() == 1);
    }

    @Given("The user has no previous journeys")
    public void noPreviousJourneys() throws IOException {
        List<Entity> journeys = SqlInterpreter.getInstance().readData(new QueryBuilderImpl().withSource(EntityType.JOURNEY)
                .withFilter("userid", "1", ComparisonType.EQUAL).build());
        assertTrue(journeys.size() == 0);
    }

    @Then("The user cant view any journeys")
    public void hasNoJourneys() {
        TableView table = (TableView) find("#previousJourneyTable");
        assertTrue(table.getItems().size() == 0);
    }
}
