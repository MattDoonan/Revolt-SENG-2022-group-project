package seng202.team3.cucumber.mainpagesteps;

import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.junit.jupiter.api.Assertions;
import seng202.team3.cucumber.CucumberFxBase;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.gui.MainController;
import seng202.team3.gui.MapHandler;

public class ChargerListMainStepDefs extends CucumberFxBase{


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
        db.importDemoData();
    }

    @Given("No charger has been selected")
    public void refreshPage() {
        clickOn("#menuButton");
    }

    @When("The user selects a charger")
    public void selectCharger() {
        clickOn("#chargerTable");
    }

    @Then("More info of charger displayed")
    public void checkInfoPanel() {
        HBox display = (HBox) find("#displayInfo");
        VBox information = (VBox) display.getChildren().get(1);
        Text addy = (Text) information.getChildren().get(1);
        Assertions.assertEquals("100 Port Road, Seaview, Lower Hutt 5010\n", addy.getText());
        Text name = (Text) information.getChildren().get(0);
        Assertions.assertEquals("SEAVIEW MARINA", name.getText());
    }
}
