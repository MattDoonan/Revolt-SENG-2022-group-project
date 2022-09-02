package seng202.team3.testFX;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import seng202.team3.gui.MainController;
import seng202.team3.gui.MainWindow;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Code designed to test the searching and filtering of the Main Window
 * Heavily inspired by Morgan English
 *
 * @author Michelle Hsieh
 * @version 1.0.0, Sep 22
 */
public class SearchFilterTextFX extends TestFXBase {

    private MainController controller;

    /**
     * Implements the abstract method for this window
     *
     * @throws Exception if fail to launch
     */
    @Override
    public void setUpClass() throws Exception {
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
        controller = loader.getController();
        controller.init(stage);
    }

    /**
     * Test the searching functionality
     */
    @Test
    public void lessWhenSearchAddress() {
        int total = controller.getManager().getCloseChargerData().size();
        clickOn("#searchCharger");
        write("christ");
        clickOn("#executeSearch");
        int newTotal = controller.getManager().getCloseChargerData().size();
        assertTrue(total > newTotal);
    }
}
