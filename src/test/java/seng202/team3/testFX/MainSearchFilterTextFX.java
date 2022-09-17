package seng202.team3.testFX;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.Connector;
import seng202.team3.gui.MainController;
import seng202.team3.gui.MainWindow;
import seng202.team3.logic.Calculations;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Code designed to test the searching and filtering of the Main Window
 * Heavily inspired by Morgan English
 *
 * @author Michelle Hsieh
 * @version 1.0.0, Sep 22
 */
public class MainSearchFilterTextFX extends TestFXBase {

    private MainController controller;

    /**
     * Implements the abstract method for this window
     *
     * @throws Exception if fail to launch
     */
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
        BorderPane b = new BorderPane();
        controller = loader.getController();
        controller.init(stage, b);
    }

    /**
     * Test the searching functionality
     */
    @Test
    public void lessWhenSearchAddress() {
        clickOn("#executeSearch");
        int total = controller.getManager().getCloseChargerData().size();
        clickOn("#searchCharger");
        write("auck");
        clickOn("#executeSearch");
        int newTotal = controller.getManager().getCloseChargerData().size();
        assertTrue(total > newTotal);
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
                boolean hasAC = false;
                for (Connector connector : charger.getConnectors()) {
                    if (connector.getPower().equalsIgnoreCase("AC")) {
                        hasAC = true;
                    }
                    if (!hasAC) {
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
        boolean isValid = false;
        clickOn("#filters");
        clickOn("#distanceDisplay");
        clickOn("#executeSearch");
        for (Charger charger : controller.getManager().getCloseChargerData()) {
            if (Calculations.calculateDistance(charger.getLocation(),
                    controller.getManager().getPosition()) > controller.getManager().getDistance()) {
                isValid = true;
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
            if (charger.getTimeLimit() < 60 ){
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
            if (charger.getAvailableParks() < 5 ){
                isValid = false;
            }
        }
        assertTrue(isValid);
    }

    @Test
    public void distanceFilterDisables() {
        boolean isValid = true;
        clickOn("#executeSearch");
        int total = controller.getManager().getCloseChargerData().size();
        controller.getManager().resetQuery();
        controller.getManager().makeAllChargers();
        if (total != controller.getManager().getCloseChargerData().size()) {
            isValid = false;
        }
        assertTrue(isValid);
    }
}
