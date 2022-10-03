package seng202.team3.testfx;

import io.cucumber.java.mk_latn.No;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.testfx.api.FxRobotException;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.PermissionLevel;
import seng202.team3.data.entity.User;
import seng202.team3.data.entity.Vehicle;
import seng202.team3.gui.MainController;
import seng202.team3.gui.MapHandler;
import seng202.team3.logic.UserManager;

import java.util.ArrayList;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.fail;
import static org.testfx.api.FxAssert.verifyThat;

public class CarChargeTestFx extends TestFxBase {

    private MainController controller;
    static SqlInterpreter db;

    static User testUser;

    static Stage stage;

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        SqlInterpreter.removeInstance();
        db = SqlInterpreter.initialiseInstanceWithUrl(
                "jdbc:sqlite:./target/test-classes/test_database.db");
        db.addChargerCsvToData("csvtest/filtering");
        testUser = new User("admin@admin.com", "admin",
                PermissionLevel.ADMIN);
        db.writeUser(testUser);
        ArrayList<String> connectors = new ArrayList<String>();
        connectors.add("Test");
        Vehicle testV = new Vehicle("Tesla", "S", 300, connectors);
        testV.setOwner(testUser.getUserid());
        db.writeVehicle(testV);
        UserManager.setUser(testUser);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));

        MapHandler.resetPermission();
        MapHandler.setLocationAccepted(true);
        Parent page = loader.load();
        initState(loader, stage);
        Scene scene = new Scene(page);
        stage.setScene(scene);
        stage.show();
    }

    private void initState(FXMLLoader loader, Stage stage) {
        controller = loader.getController();
        BorderPane b = new BorderPane();
        controller.init(stage, b);
    }

    @Test
    public void checkAppearOnUserWithCar() {
        verifyThat("#batteryPercent", Node::isVisible);
    }

    private static Stream<Arguments> percentageCharge() {
        return Stream.of(
                Arguments.of("50", 150),
                Arguments.of("20", 60),
                Arguments.of("99", 297),
                Arguments.of("100", 300),
                Arguments.of("0", 10),
                Arguments.of("1", 10));
    }

    @ParameterizedTest
    @MethodSource("percentageCharge")
    public void checkFilterUpdate(String want, int result) {
        clickOn("#batteryPercent");
        write(want);
        clickOn("#filters");
        Slider slide = (Slider) find("#changeDistance");
        Assertions.assertEquals(result, slide.getValue());
    }

    private static Stream<Arguments> invalidInput() {
        return Stream.of(
                Arguments.of("ab50", 150),
                Arguments.of("20df", 60),
                Arguments.of("999", 297),
                Arguments.of("-1", 10),
                Arguments.of("", 300));
    }

    @ParameterizedTest
    @MethodSource("invalidInput")
    public void invalidInputTests(String want, int result) {
        clickOn("#batteryPercent");
        write(want);
        clickOn("#filters");
        Slider slide = (Slider) find("#changeDistance");
        Assertions.assertEquals(result, slide.getValue());
    }

}
