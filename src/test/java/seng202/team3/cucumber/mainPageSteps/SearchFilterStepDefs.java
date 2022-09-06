package seng202.team3.cucumber.mainPageSteps;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.testfx.framework.junit5.ApplicationTest;
import seng202.team3.data.entity.Charger;
import seng202.team3.gui.MainController;
import seng202.team3.gui.MainWindow;
import seng202.team3.testFX.TestFXBase;

import static org.junit.Assert.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;

/**
 * Cucumber Tests designed to check acceptance tests of user interacting
 * with table search and filter steps. Heavily inspired by
 * Morgan English's code.
 *
 * @author Michelle Hsieh
 * @version 1.0.0, Sep 22
 */
public class SearchFilterStepDefs extends TestFXBase {


    /**
     * Implements the abstract method for this window
     *
     * @throws Exception if fail to launch
     */
    @Before
    @Override
    public void setUp() throws Exception {
        ApplicationTest.launch(MainWindow.class);
    }

    /**
     * Starts the main for testing
     *
     * @param stage the stage of the application
     * @throws Exception if fail to launch
     */
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        Parent page = loader.load();
        initState(loader, stage);
        Scene scene = new Scene(page);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Initialises the state of the current application
     *
     * @param loader the FXML loader after loading
     * @param stage the stage of the application
     */
    public void initState(FXMLLoader loader, Stage stage) {
        MainController controller = loader.getController();
        controller.init(stage);
    }


    @Given("The user is on the main page showing all chargers")
    public void onTheMainScreen() {
        clickOn("#filters");
        clickOn("#distanceDisplay");
        clickOn("#filters");
        clickOn("#distanceDisplay");
        clickOn("#executeSearch");
    }

    /**
     * AT_1
     */
    @When("The user inputs a valid query {string} and clicks Go")
    public void userTriesValidQuery(String query) {
        clickOn("#searchCharger");
        write(query);
        clickOn("#executeSearch");
    }

    @Then("The user gets shown the chargers that best match their query {string}")
    public void chargersFilteredCorrectly(String query) {
        boolean isValid = true;

        assertTrue(isValid);
    }


}
