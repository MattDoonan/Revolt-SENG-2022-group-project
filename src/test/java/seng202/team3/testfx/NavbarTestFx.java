package seng202.team3.testfx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobotException;
import org.testfx.framework.junit5.ApplicationTest;
import seng202.team3.gui.GarageController;
import seng202.team3.gui.MainWindow;
import seng202.team3.gui.MenuController;

import java.lang.annotation.Target;

public class NavbarTestFx extends TestFxBase{

    private MenuController controller;


    @Override
    public void setUp() throws Exception {
        ApplicationTest.launch(MainWindow.class);

    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/menu_bar.fxml"));
        Parent page = loader.load();
        initState(loader, stage);
        Scene scene = new Scene(page);
        stage.setScene(scene);
        stage.show();
    }

    private void initState(FXMLLoader loader, Stage stage) {
        controller = loader.getController();
        controller.init(stage);
    }

    @Test
    public void realLogin() {
        clickOn("#loginSignout");
        clickOn("#loginEmailField");
        write("admin");
        clickOn("#loginPasswordField");
        write("admin");
        clickOn("#loginBtn");
        try {
            clickOn("#invalidLogin");
            Assertions.fail("Should be a valid login");
        } catch (FxRobotException e) {
            Assertions.assertTrue(true);
        }
    }
}
