package seng202.team3.cucumber.vehiclesteps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.stage.Window;
import seng202.team3.cucumber.CucumberFxBase;
import seng202.team3.data.database.ComparisonType;
import seng202.team3.data.database.Query;
import seng202.team3.data.database.QueryBuilderImpl;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.Vehicle;
import seng202.team3.gui.ErrorController;
import seng202.team3.gui.GarageController;
import seng202.team3.gui.MainWindow;
import seng202.team3.gui.MapHandler;
import seng202.team3.gui.VehicleUpdateController;
import seng202.team3.logic.UserManager;

/**
 * Cucumber Tests designed to check acceptance tests of vehicle management
 *
 * @author Harrison Tyson
 * @version 1.0.0, Oct 22
 */
public class VehicleManagementStepDefs extends CucumberFxBase {

    private static GarageController controller;
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

        // Remove all popups
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                List<Window> ws = Stage.getWindows();
                while (ws.size() > 1) {
                    ws.get(ws.size() - 1).hide();
                }
            }
        });
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

    @And("I have no vehicles")
    public void iHaveNoVehicles() throws IOException {
        int uid = UserManager.getUser().getUserid();
        Query q = new QueryBuilderImpl()
                .withSource("vehicle")
                .withFilter("owner", "" + uid, ComparisonType.EQUAL)
                .build();

        List<Object> vehicles = db.readData(q,
                Vehicle.class);

        if (vehicles.size() > 0) {
            for (Object o : vehicles) {
                db.deleteData("vehicle", ((Vehicle) o).getVehicleId());
            }
        }

        assertTrue(db.readData(q,
                Vehicle.class).isEmpty());
    }

    @When("I navigate to the vehicle screen")
    public void iNavigateToTheVehicleScreen() {
        clickOn("#vehicleButton");
    }

    @Then("I am shown an empty garage")
    public void iAmShownAnEmptyGarage() {
        controller = (GarageController) MainWindow.getController();
        assertTrue(controller.getManage().getData().isEmpty());
    }

    @Given("I click add new vehicle")
    public void iClickAddNewVehicle() {
        clickOn("#openUpdate");
    }

    @When("I provide vehicle make: {string} model: {string} maxrange: {string} connector type: {string}")
    public void iProvideVehicleMake(String vehicleMake, String vehicleModel, String maxRange, String connectorType) {
        clickOn("#makeText");
        write(vehicleMake);
        clickOn("#modelText");
        write(vehicleModel);
        clickOn("#maxRangeText");
        write(maxRange);
        clickOn("#connectorType");
        if (connectorType != "") {
            clickOn(connectorType);
            clickOn("#addConnectionBtn");
        }
        clickOn("#saveChanges");
    }

    @Then("I can see the vehicle in the garage")
    public void iCanSeeTheVehicleInTheGarage() {
        assertTrue(controller.getManage().getData().size() == 1);
    }

    @Then("I am informed my input is invalid")
    public void iIsInvalid() {
        // clickOn("#close");
        ErrorController err = (ErrorController) MainWindow.getController();
        assertTrue(err.getErrors().size() > 0);
    }

    @Given("I have a vehicle in the garage")
    public void iHaveAVehicleInTheGarage() throws IOException {
        Vehicle v = new Vehicle("Honda",
                "Civic",
                300,
                new ArrayList<>(Arrays.asList("CHAdeMO")));
        db.writeVehicle(v);

        controller.getManage().getAllVehicles();
        assertTrue(controller.getManage().getData().size() == 1);
        iNavigateToTheVehicleScreen();
    }

    @When("I click delete vehicle")
    public void iClickDeleteVehicle() {
        clickOn("#deleteCarOne");
    }

    @And("I confirm the action")
    public void iConfirmTheAction() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ((VehicleUpdateController) MainWindow.getController()).confirmDelete();
            }
        });

    }

    @Then("I can no longer see the vehicle")
    public void iCanNoLongerSeeTheVehicle() {
        iNavigateToTheVehicleScreen();
        controller = (GarageController) MainWindow.getController();
        assertTrue(controller.getManage().getData().isEmpty());
    }

    @When("I edit the vehicle")
    public void iEditTheVehicle() {
        clickOn("#editCarOne");
    }

    @And("change the make to {string}")
    public void changeTheMakeTo(String make) {
        doubleClickOn("#makeText");
        write(make);
        clickOn("#saveChanges");
    }

    @Then("the vehicle's make is now {string}")
    public void theVehiclesMakeIsNow(String make) {
        controller.getManage().getAllVehicles();
        Vehicle v = controller.getManage().getData().get(0);
        assertEquals(make, v.getMake());
    }
}
