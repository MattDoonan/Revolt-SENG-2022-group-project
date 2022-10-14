package seng202.team3.cucumber.mainpagesteps;

import com.maxmind.geoip2.exception.GeoIp2Exception;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import javafx.collections.FXCollections;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.junit.jupiter.api.Assertions;
import seng202.team3.cucumber.CucumberFxBase;
import seng202.team3.data.database.CsvInterpreter;
import seng202.team3.data.database.QueryBuilderImpl;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.Coordinate;
import seng202.team3.data.entity.EntityType;
import seng202.team3.data.entity.Entity;
import seng202.team3.gui.MainController;
import seng202.team3.gui.MainWindow;
import seng202.team3.gui.MapHandler;
import seng202.team3.logic.ChargerManager;
import seng202.team3.logic.GeoLocationHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChargerListMainStepDefs extends CucumberFxBase {

    private static MainController controller;
    static SqlInterpreter db;

    private static List<Entity> chargerObject;

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
        new CsvInterpreter().importChargersToDatabase("/csvtest/filtering.csv");
    }

    @Given("No charger has been selected")
    public void refreshPage() {
        clickOn("HOME");
        controller = (MainController) MainWindow.getController();
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
        Assertions.assertEquals(controller.getManager().getSelectedCharger()
                .getLocation().getAddress(),
                addy.getText());
        Text name = (Text) information.getChildren().get(0);
        Assertions.assertEquals(controller.getManager().getSelectedCharger().getName(),
                name.getText());
    }

    @Given("The user has the correct tab open")
    public void correctTab() throws IOException, GeoIp2Exception {
        chargerObject = db.readData(new QueryBuilderImpl().withSource(EntityType.CHARGER)
                .build());

        for (Entity o : chargerObject) {
            ((Charger) o).setOwnerId(1); // Set owner to admin
        }
        db.writeCharger(new ArrayList<>(chargerObject));

        clickOn("HOME");
        controller = (MainController) MainWindow.getController();
        GeoLocationHandler.setCoordinate(new Coordinate(
                -43.52246856689453, 172.5812225341797),
                "University of Canterbury Waimairi Road, ChristChurch 8041, New Zealand Aotearoa");
        controller.getManager().setPosition();
        controller.getManager().setDistance(100.0);
    }

    @Given("The user has a location tracking on")
    public void locationTracking() {
        Assertions.assertFalse(GeoLocationHandler.getCoordinate() == GeoLocationHandler.DEFAULT_COORDINATE);
    }

    @Then("The user is told the distance (in km) between the given location and closest chargers")
    public void checkClosestChargers() throws IOException {
        List<Entity> o = db.readData(new QueryBuilderImpl().withSource(EntityType.CHARGER)
                .build());
        ArrayList<Charger> chargers = new ArrayList<>();
        for (Entity c : o) {
            chargers.add((Charger) c);
        }
        ChargerManager chargerManager = new ChargerManager();
        chargers = chargerManager.getNearbyChargers(chargers,
                GeoLocationHandler.getCoordinate(), 100.0);
        Assertions.assertEquals(FXCollections.observableList(chargers),
                controller.getManager().getCloseChargerData());
    }

    @When("There are possible chargers to list")
    public void chargersInRange() {
        Assertions.assertTrue(controller.getManager()
                .getCloseChargerData().size() > 0);
    }

    @Then("The chargers are shown on the list")
    public void chargersOnList() {
        VBox chargerList = (VBox) find("#chargerTable");
        Assertions.assertEquals(controller.getManager().getCloseChargerData().size(),
                chargerList.getChildren().size());
    }
}
