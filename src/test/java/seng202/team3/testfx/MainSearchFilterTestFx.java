package seng202.team3.testfx;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobotException;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.Connector;
import seng202.team3.data.entity.PermissionLevel;
import seng202.team3.data.entity.User;
import seng202.team3.gui.MainController;
import seng202.team3.gui.MapHandler;
import seng202.team3.logic.Calculations;
import seng202.team3.logic.UserManager;

/**
 * Code designed to test the searching and filtering of the Main Window
 * Heavily inspired by Morgan English
 *
 * @author Michelle Hsieh
 * @version 1.0.0, Sep 22
 */
public class MainSearchFilterTestFx extends TestFxBase {

    private MainController controller;
    static SqlInterpreter db;
    static User testUser;

    /**
     * Starts the main for testing
     *
     * @param stage the stage of the application
     * @throws Exception if fail to launch
     */
    @Override
    public void start(Stage stage) throws Exception {
        testUser = new User("admin@admin.com", "admin",
                PermissionLevel.ADMIN);
        testUser.setUserid(1);

        UserManager.setUser(testUser);
        SqlInterpreter.removeInstance();
        db = SqlInterpreter.initialiseInstanceWithUrl(
                "jdbc:sqlite:./target/test-classes/test_database.db");
        db.defaultDatabase();

        db.addChargerCsvToData("csvtest/filtering");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        Parent page = loader.load();
        MapHandler.resetPermission();
        MapHandler.setLocationAccepted(true);
        initState(loader, stage);
        Scene scene = new Scene(page);
        stage.setScene(scene);
        stage.show();
        controller.getManager().resetQuery();
    }

    /**
     * Initialises the state of the current application
     *
     * @param loader the FXML loader after loading
     * @param stage  the stage of the application
     */
    public void initState(FXMLLoader loader, Stage stage) {

        controller = loader.getController();
        BorderPane b = new BorderPane();
        controller.init(stage, b);
    }

    @Test
    public void containsAddress() {
        boolean isValid = true;
        clickOn("#searchCharger");
        write("christ");
        clickOn("#executeSearch");
        ObservableList<Charger> chargers = controller.getManager().getCloseChargerData();
        for (Charger charger : chargers) {
            String lowerAddress = charger.getLocation().getAddress().toLowerCase();
            if (!lowerAddress.contains("christ")) {
                isValid = false;
            }
        }
        assertTrue(isValid);
    }

    @Test
    public void filtersMultipleTicks() {
        boolean isValid = true;
        clickOn("#filters");
        clickOn("#chargerTypes");
        clickOn("#acButton");
        clickOn("#chargingCost");
        clickOn("#hasChargingCost");
        clickOn("#executeSearch");
        ObservableList<Charger> chargers = controller.getManager().getCloseChargerData();
        for (Charger charger : chargers) {
            if (!charger.getChargeCost()) {
                boolean hasAc = false;
                for (Connector connector : charger.getConnectors()) {
                    if (connector.getPower().equalsIgnoreCase("AC")) {
                        hasAc = true;
                    }
                    if (!hasAc) {
                        isValid = false;
                    }
                }
                isValid = false;
            }
        }
        assertTrue(isValid);
    }

    @Test
    public void filtersMoreMultipleTicks() {
        boolean isValid = true;
        clickOn("#filters");
        clickOn("#carparkCost");
        clickOn("#withoutCarparkCost");
        clickOn("#hoursOpen");
        clickOn("#openAllButton");
        clickOn("#executeSearch");
        ObservableList<Charger> chargers = controller.getManager().getCloseChargerData();
        for (Charger charger : chargers) {
            if (charger.getParkingCost()) {
                isValid = false;
            } else if (!charger.getAvailable24Hrs()) {
                isValid = false;
            }
        }
        assertTrue(isValid);
    }

    @Test
    public void distanceFilterWorks() {
        boolean isValid = true;
        clickOn("#filters");
        if (!((CheckBox) this.find("#distanceDisplay")).isSelected()) {
            clickOn("#distanceDisplay");
        }
        clickOn("#executeSearch");
        for (Charger charger : controller.getManager().getCloseChargerData()) {
            if (Calculations.calculateDistance(charger.getLocation(),
                    controller.getManager()
                            .getPosition()) > controller.getManager().getDistance()) {
                isValid = false;
            }
        }
        assertTrue(isValid);
    }

    @Test
    public void timeLimitFilterWorks() {
        boolean isValid = true;
        clickOn("#filters");
        clickOn("#toggleTimeLimit");
        clickOn("#executeSearch");
        for (Charger charger : controller.getManager().getCloseChargerData()) {
            if (charger.getTimeLimit() < 60) {
                isValid = false;
            }
        }
        assertTrue(isValid);
    }

    @Test
    public void parkingFilterWorks() {
        boolean isValid = true;
        clickOn("#filters");
        clickOn("#onParkingFilter");
        clickOn("#executeSearch");
        for (Charger charger : controller.getManager().getCloseChargerData()) {
            if (charger.getAvailableParks() < 5) {
                isValid = false;
            }
        }
        assertTrue(isValid);
    }

    /**
     * Can't test on other file
     *
     */
    @Test
    public void carRangeNotExistOnGuest() throws Exception {
        UserManager.setUser(UserManager.getGuest());
        try {
            clickOn("#batteryPercent");
            fail("Should not exist");
        } catch (FxRobotException e) {
            Assertions.assertTrue(true);
        }
    }

    // TODO: fix test
    // @Test
    // public void distanceFilterDisables() {
    // boolean isValid = true;
    // int total;

    // clickOn("#executeSearch");
    // total = controller.getManager().getCloseChargerData().size();
    // controller.getManager().resetQuery();
    // controller.getManager().makeAllChargers();
    // if (total != controller.getManager().getCloseChargerData().size()) {
    // isValid = false;
    // }
    // assertTrue(isValid);
    // }
}
