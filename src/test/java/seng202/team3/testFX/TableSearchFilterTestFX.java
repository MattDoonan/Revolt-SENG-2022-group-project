package seng202.team3.testFX;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobotException;
import org.testfx.framework.junit5.ApplicationTest;
import seng202.team3.data.entity.Charger;
import seng202.team3.gui.MainWindow;
import seng202.team3.gui.TableController;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class TableSearchFilterTestFX extends TestFXBase{

    private TableController controller;


    @Override
    public void setUp() throws Exception {
        ApplicationTest.launch(MainWindow.class);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main_table.fxml"));
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
        controller = loader.getController();
        controller.init(stage);
    }

    @Test
    public void timeLimitFilterWorks() {
        boolean isValid = true;
        clickOn("#filters");
        clickOn("#toggleTimeLimit");
        clickOn("#update");
        for (Charger charger : controller.getManager().getData()) {
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
        clickOn("#update");
        for (Charger charger : controller.getManager().getData()) {
            if (charger.getAvailableParks() < 5 ){
                isValid = false;
            }
        }
        assertTrue(isValid);
    }

    @Test
    public void filtersMultipleTicks() {
        boolean isValid = true;
        clickOn("#filters");
        clickOn("#cpMenu");
        clickOn("#withoutCarparkCost");
        clickOn("#openMenu");
        clickOn("#openAllButton");
        clickOn("#update");
        ObservableList<Charger> chargers = controller.getManager().getData();
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
    public void columnShowID() {
        boolean isValid = true;
        clickOn("#columnEdit");
        clickOn("#showId");
        clickOn("#update");
        try {
            clickOn("#idCol");
            assertTrue(isValid);
        } catch (FxRobotException e) {
            fail("No column found");
        }
    }

    @Test
    public void noIdColumn() {
        boolean isValid = true;
        try {
            clickOn("#idCol");
            fail("Column found");
        } catch (FxRobotException e) {
            assertTrue(isValid);
        }
    }

    @Test
    public void editMultipleColumns() {
        boolean isValid = true;
        clickOn("#columnEdit");
        clickOn("#showXpos");
        clickOn("#showYpos");
        clickOn("#showAttraction");
        clickOn("#showLon");
        clickOn("#update");
        try {
            clickOn("#attractionCol");
            clickOn("#longitudeCol");
            isValid = false;
        } catch (FxRobotException e) {}
        try {
            clickOn("#xposCol");
            clickOn("#yposCol");
        } catch (FxRobotException e) {
            isValid = false;
        }
        assertTrue(isValid);
    }
}
