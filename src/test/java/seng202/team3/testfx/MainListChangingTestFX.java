package seng202.team3.testfx;

import javafx.fxml.FXMLLoader;
import javafx.geometry.VerticalDirection;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import seng202.team3.data.database.CsvInterpreter;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.PermissionLevel;
import seng202.team3.data.entity.User;
import seng202.team3.gui.MainController;
import seng202.team3.gui.MapHandler;
import seng202.team3.logic.UserManager;

import static org.testfx.api.FxAssert.verifyThat;

/**
 * Tests for changing the list view
 *
 * @author Matthew Doonan
 * @version 1.0.0
 */
public class MainListChangingTestFX extends TestFxBase {

    private MainController controller;
    static SqlInterpreter db;
    static User testUser;

    @Override
    public void start(Stage stage) throws Exception {
        testUser = new User("admin@admin.com", "admin",
                PermissionLevel.ADMIN);
        testUser.setId(1);

        UserManager.setUser(testUser);
        SqlInterpreter.removeInstance();
        db = SqlInterpreter.initialiseInstanceWithUrl(
                "jdbc:sqlite:./target/test-classes/test_database.db");
        db.defaultDatabase();

        new CsvInterpreter().importChargersToDatabase("/csvtest/filtering.csv");

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
    public void swappingToMoreInfoView() {
        clickOn("#Inherit");
        verifyThat("#largeDisplayInfo", Node::isVisible);
    }

    @Test
    public void swappingThereAndBack() {
        clickOn("#Inherit");
        clickOn("#Inherit");
        verifyThat("#chargerTable", Node::isVisible);
    }

    @Test
    public void checkCorrectChargerOnChange() {
        HBox smallerInfo = (HBox) find("#displayInfo");
        VBox information = (VBox) smallerInfo.getChildren().get(1);
        clickOn("#Inherit");
        VBox largerInfo = (VBox) find("#largeDisplayInfo");
        Assertions.assertEquals(((Text) information.getChildren().get(0)).getText(),
                ((Text) largerInfo.getChildren().get(2)).getText());
        Assertions.assertEquals(((Text) information.getChildren().get(1)).getText(),
                ((Text) largerInfo.getChildren().get(7)).getText());
    }

    @Test
    public void checkCorrectChargerOnReturn() {
        clickOn("#Inherit");
        VBox largerInfo = (VBox) find("#largeDisplayInfo");
        clickOn("#Inherit");
        HBox smallerInfo = (HBox) find("#displayInfo");
        VBox information = (VBox) smallerInfo.getChildren().get(1);
        Assertions.assertEquals(((Text) information.getChildren().get(0)).getText(),
                ((Text) largerInfo.getChildren().get(2)).getText());
        Assertions.assertEquals(((Text) information.getChildren().get(1)).getText(),
                ((Text) largerInfo.getChildren().get(7)).getText());
    }

}
