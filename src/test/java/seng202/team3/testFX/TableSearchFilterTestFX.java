package seng202.team3.testFX;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import seng202.team3.data.entity.Charger;
import seng202.team3.gui.MainController;
import seng202.team3.gui.MainWindow;
import seng202.team3.gui.TableController;

import static org.junit.jupiter.api.Assertions.assertTrue;

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
}
