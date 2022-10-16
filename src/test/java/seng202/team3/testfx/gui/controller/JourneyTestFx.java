package seng202.team3.testfx.gui.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.testfx.api.FxAssert.verifyThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.database.csv.CsvInterpreter;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.Connector;
import seng202.team3.data.entity.Coordinate;
import seng202.team3.data.entity.Journey;
import seng202.team3.data.entity.Stop;
import seng202.team3.data.entity.User;
import seng202.team3.data.entity.Vehicle;
import seng202.team3.data.entity.util.PermissionLevel;
import seng202.team3.gui.controller.JourneyController;
import seng202.team3.logic.manager.UserManager;
import seng202.team3.testfx.TestFxBase;

/**
 * TestFx for JourneyController
 *
 * @author Angus Kirtlan
 * @version 1.0.0, Oct 22
 */
public class JourneyTestFx extends TestFxBase {

    private JourneyController journeyController;
    static SqlInterpreter db;
    private static Connector testConnector1;

    private static Charger testCharger;

    private static Vehicle testVehicle;

    private static User testUser;

    private static Journey testJourneyOne;

    @BeforeAll
    public static void setUp() throws Exception {
        SqlInterpreter.removeInstance();
        db = SqlInterpreter.initialiseInstanceWithUrl(
                "jdbc:sqlite:./target/test-classes/test_database.db");
    }

    /**
     * Starts the JourneyController for testing
     *
     * @param stage the stage of the application
     * @throws Exception if fail to launch
     */
    @Override
    public void start(Stage stage) throws Exception {
        db.defaultDatabase();
        testUser = new User("admin@admin.com", "adminNew",
                PermissionLevel.USER);
        testUser.setId(1);
        UserManager.setUser(testUser);

        testVehicle = new Vehicle("TestMake", "TestModel",
                300, new ArrayList<String>(Arrays.asList("Type 1 Tethered")));
        testVehicle.setOwner(1);
        SqlInterpreter.getInstance().writeVehicle(testVehicle);

        new CsvInterpreter().importChargersToDatabase("/csvtest/filtering.csv");

        FXMLLoader journeyLoader = new FXMLLoader(getClass().getResource("/fxml/journey.fxml"));
        Parent page = journeyLoader.load();
        initState(journeyLoader, stage);
        Scene scene = new Scene(page);
        stage.setScene(scene);
        stage.show();
    }

    public void setUpJourney() throws IOException {

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
        testJourneyOne = new Journey(testVehicle, new Coordinate(4.399, -24.122),
                new Coordinate(4.301, -22.992), "10/10/2002", "Name");
        testJourneyOne.addStop(new Stop(testCharger));
        testJourneyOne.setUser(1);
    }

    /**
     * Initialises the state of the current application
     *
     * @param journeyLoader the FXML loader after loading
     * @param stage         the stage of the application
     */
    public void initState(FXMLLoader journeyLoader, Stage stage) {
        journeyController = journeyLoader.getController();
        journeyController.init(stage);
    }

    /**
     * Tests the start shows an error prompt when clicked without a vehicle created
     * / selected
     */
    @Test
    public void startJourneyError() {
        journeyController.getManager().getSelectedJourney().setVehicle(null);
        clickOn("#makeStart");
        double left_border = ((Button) find("#makeStart")).getBorder().getStrokes().get(0).getWidths().getLeft();
        assertEquals(1, left_border);
    }

    /**
     * Tests the end shows an error prompt when clicked without a start selected
     */
    @Test
    public void endJourneyError() {
        clickOn("#makeEnd");
        double left_border = ((Button) find("#makeStart")).getBorder().getStrokes().get(0).getWidths().getLeft();
        assertEquals(1, left_border);
    }

    /**
     * Tests that save shows an error prompt when click without a correct journey
     */
    @Test
    public void saveJourneyError() {
        clickOn("#saveJourney");
        double left_border = ((Button) find("#makeStart")).getBorder().getStrokes().get(0).getWidths().getLeft();
        assertEquals(1, left_border);
    }

    /**
     * Tests that load journey shows an error prompt when click without a correct
     * journey
     */
    @Test
    public void loadJourneyError() {
        clickOn("#loadJourney");
        double left_border = ((Button) find("#loadJourney")).getBorder().getStrokes().get(0).getWidths().getLeft();
        assertEquals(1, left_border);
    }

    /**
     * Tests that delete journey shows an error prompt when click without a correct
     * journey
     */
    @Test
    public void deleteJourneyError() {
        clickOn("#deleteJourney");
        double left_border = ((Button) find("#deleteJourney")).getBorder().getStrokes().get(0).getWidths().getLeft();
        assertEquals(1, left_border);
    }

    /**
     * Vehicle dropdown tests
     */
    @Test
    public void vehicleAddTest() throws IOException {
        setUpJourney();
        clickOn("#vehicles");
        press(KeyCode.DOWN);
        release(KeyCode.DOWN);
        press(KeyCode.DOWN);
        release(KeyCode.DOWN);
        press(KeyCode.ENTER);
        release(KeyCode.ENTER);
        verifyThat("#addedConnections", Node::isVisible);
    }

    /**
     * Vehicle dropdown tests
     */
    @Test
    public void vehicleSelectTest() throws IOException {
        setUpJourney();
        clickOn("#vehicles");
        press(KeyCode.DOWN);
        release(KeyCode.DOWN);
        press(KeyCode.ENTER);
        release(KeyCode.ENTER);
        assertEquals(testVehicle, journeyController.getManager().getSelectedJourney().getVehicle());
    }

    /**
     * Tests the text box
     */
    @Test
    public void textBoxTest() throws IOException {
        setUpJourney();
        clickOn("#tripName");
        write("Test trip");
        journeyController.getManager().setSelectedJourney(testJourneyOne);
        clickOn("#saveJourney");
        TableView table = (TableView) find("#previousJourneyTable");
        assertEquals("Test trip", ((Journey) table.getItems().get(0)).getTitle());
    }

    /**
     * Tests that reset journey successfully resets the journey
     */
    @Test
    public void resetJourneyTest() throws IOException {
        loadJourneyTest();
        clickOn("#resetJourney");
        assertEquals("", ((TextField) find("#tripName")).getText());
    }

    /**
     * Tests that load journey successfully loads a journey from the database
     */
    @Test
    public void loadJourneyTest() throws IOException {
        setUpJourney();
        clickOn("#tripName");
        write("Test journey");
        journeyController.getManager().setSelectedJourney(testJourneyOne);
        clickOn("#saveJourney");
        ((TextField) this.find("#tripName")).clear();
        clickOn("Test journey");
        clickOn("#resetJourney");
        clickOn("#loadJourney");
        assertEquals(1, journeyController.getManager().getSelectedJourney().getStops().size());
    }

}
