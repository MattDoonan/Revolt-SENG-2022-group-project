package seng202.team3.cucumber.journeysteps;

import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import javafx.geometry.VerticalDirection;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team3.cucumber.CucumberFxBase;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.database.csv.CsvInterpreter;
import seng202.team3.data.database.util.ComparisonType;
import seng202.team3.data.database.util.QueryBuilderImpl;
import seng202.team3.data.entity.*;
import seng202.team3.data.entity.util.EntityType;
import seng202.team3.gui.*;
import seng202.team3.gui.controller.JourneyController;
import seng202.team3.gui.controller.map.MapHandler;
import seng202.team3.logic.manager.UserManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class JourneyPageStepDefs extends CucumberFxBase {

    static SqlInterpreter db;

    private static Connector testConnector1;

    private static Charger testCharger;

    private static Vehicle testVehicle;

    private static Journey testJourneyOne;

    private JourneyController controller;

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
            clickOn("#accountMenu");
            clickOn("#loginSignout");
        }
        assertEquals(UserManager.getGuest(), UserManager.getUser());
    }

    @And("I log in with username: {string} password: {string}")
    public void iAmLoggedInWithUsernamePassword(String username, String password) {
        clickOn("#accountMenu");
        clickOn("#loginSignout");
        clickOn("#loginEmailField");
        write(username);
        clickOn("#loginPasswordField");
        write(password);
        clickOn("#loginBtn");
    }

    @When("I navigate to the account screen")
    public void iNavigateToTheAccountScreen() {
        clickOn("JOURNEY");
    }

    @Given("The user has journeys to view")
    public void journeyToView() throws IOException {
        setUpDatabase();
        SqlInterpreter.getInstance().writeJourney(testJourneyOne);
    }

    @When("The user is on the journey page")
    public void onJourneyPage() {
        clickOn("JOURNEY");
    }

    @Then("The user can view a list of previous journeys")
    public void viewPrevJourneys() {
        TableView table = (TableView) find("#previousJourneyTable");
        assertTrue(table.getItems().size() == 1);
    }

    @Given("The user has no previous journeys")
    public void noPreviousJourneys() throws IOException {
        List<Entity> journeys = SqlInterpreter.getInstance()
                .readData(new QueryBuilderImpl().withSource(EntityType.JOURNEY)
                        .withFilter("userid", "1", ComparisonType.EQUAL).build());
        assertTrue(journeys.size() == 0);
    }

    @Then("The user cant view any journeys")
    public void hasNoJourneys() {
        TableView table = (TableView) find("#previousJourneyTable");
        assertTrue(table.getItems().size() == 0);
    }

    @Given("The user has planned a trip")
    public void plannedTrip() throws IOException {
        setUpDatabase();
        controller = (JourneyController) MainWindow.getController();
        controller.getManager().setSelectedJourney(testJourneyOne);
    }

    @When("A user saves there planned Journey")
    public void saveJourney() {
        clickOn("#maxRange");
        scroll(30, VerticalDirection.DOWN);
        clickOn("#saveJourney");
    }

    @Then("The Journey is saved to the user's list of Journeys")
    public void checkIfSaved() throws IOException {
        TableView table = (TableView) find("#previousJourneyTable");
        assertEquals(table.getItems().get(0), testJourneyOne);
        List<Entity> journeys = SqlInterpreter.getInstance()
                .readData(new QueryBuilderImpl().withSource(EntityType.JOURNEY)
                        .withFilter("userid", "1", ComparisonType.EQUAL).build());
        assertEquals(1, journeys.size());

    }

    @Given("The user has taken a previous Journey")
    public void takenJourney() throws IOException {
        setUpDatabase();
        controller = (JourneyController) MainWindow.getController();
        controller.getManager().setSelectedJourney(testJourneyOne);
        controller.getManager().saveJourney();
        testJourneyOne.setId(0);
        clickOn("JOURNEY");
    }

    @When("The user makes the previous journey")
    public void makePreviousJourney() {
        controller = (JourneyController) MainWindow.getController();
        controller.getManager().setSelectedJourney(testJourneyOne);
        clickOn("#maxRange");
        scroll(30, VerticalDirection.DOWN);
        clickOn("#saveJourney");
        clickOn("#maxRange");

    }

    @Then("The previous journey is saved to the database")
    public void prevInDatabase() throws IOException {
        TableView table = (TableView) find("#previousJourneyTable");
        assertEquals(table.getItems().get(1), testJourneyOne);
        List<Entity> journeys = SqlInterpreter.getInstance()
                .readData(new QueryBuilderImpl().withSource(EntityType.JOURNEY)
                        .withFilter("userid", "1", ComparisonType.EQUAL).build());
        assertEquals(2, journeys.size());
    }

}
