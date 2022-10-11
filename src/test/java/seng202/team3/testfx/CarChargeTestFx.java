package seng202.team3.testfx;

import static org.testfx.api.FxAssert.verifyThat;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import seng202.team3.data.database.CsvInterpreter;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.PermissionLevel;
import seng202.team3.data.entity.User;
import seng202.team3.data.entity.Vehicle;
import seng202.team3.gui.MainController;
import seng202.team3.gui.MapHandler;
import seng202.team3.logic.UserManager;

import javax.management.InstanceAlreadyExistsException;

public class CarChargeTestFx extends TestFxBase {

    private MainController controller;
    static SqlInterpreter db;

    static User testUser;

    static Stage stage;

    static Vehicle testV;

    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;
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

    @BeforeAll
    public static void setup() throws InstanceAlreadyExistsException, IOException, SQLException {
        SqlInterpreter.removeInstance();
        db = SqlInterpreter.initialiseInstanceWithUrl(
                "jdbc:sqlite:./target/test-classes/test_database.db");
        new CsvInterpreter().importChargersToDatabase("/csvtest/filtering.csv");
        testUser = new User("admin@admin.com", "admin",
                PermissionLevel.ADMIN);
        db.writeUser(testUser);
        ArrayList<String> connectors = new ArrayList<String>();
        connectors.add("Test");
        testV = new Vehicle("Tesla", "S", 300, connectors);
        testV.setOwner(testUser.getId());
        testV.setcurrVehicle(true);
        db.writeVehicle(testV);
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
