package seng202.team3.testfx;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.testfx.api.FxAssert.verifyThat;

import java.io.IOException;
import java.security.Key;
import java.util.stream.Stream;

import javax.management.InstanceAlreadyExistsException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.testfx.api.FxRobotException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.gui.GarageController;

/**
 * Runs the vehicle testFX
 *
 * @author Matthew Doonan
 * @version 1.0.0, Sep 22
 */
public class VehiclesPageFx extends TestFxBase {

    private GarageController controller;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/garage.fxml"));
        Parent page = loader.load();
        initState(loader, stage);
        Scene scene = new Scene(page);
        stage.setScene(scene);
        stage.show();
    }

    @BeforeEach
    public void reset() throws IOException, InstanceAlreadyExistsException {
        SqlInterpreter.removeInstance();
        SqlInterpreter.initialiseInstanceWithUrl(
                "jdbc:sqlite:./target/test-classes/test_database.db");
        SqlInterpreter.getInstance().defaultDatabase();
        SqlInterpreter.getInstance().deleteData("user", 0);
        controller.refresh();
    }

    private void initState(FXMLLoader loader, Stage stage) {
        controller = loader.getController();
        controller.init();
    }

    private static Stream<Arguments> nodesToCheck() {
        return Stream.of(
                Arguments.of("#deleteCarOne"),
                Arguments.of("#deleteCarTwo"),
                Arguments.of("#deleteCarThree"),
                Arguments.of("#editCarOne"),
                Arguments.of("editCarTwo"),
                Arguments.of("editCarThree"));
    }

    @ParameterizedTest
    @MethodSource("nodesToCheck")
    public void checkDelete(String nodes) {
        try {
            clickOn(nodes);
            fail(nodes + " Shouldn't be visible");
        } catch (FxRobotException e) {
            assertTrue(true);
        }
    }

    @Test
    public void addNewButtonCheck() {
        clickOn("#openUpdate");
        verifyThat("#makeText", Node::isVisible);
    }

    private static Stream<Arguments> importantInputs() {
        return Stream.of(
                Arguments.of("#makeText", "Tesla"),
                Arguments.of("#modelText", "Y"),
                Arguments.of("#maxRangeText", "500"));
    }

    @ParameterizedTest
    @MethodSource("importantInputs")
    public void checkOnlyOneReqInputs(String node, String text) {
        clickOn("#openUpdate");
        clickOn(node);
        write(text);
        clickOn("#saveChanges");
        // TODO: check for tooltip popup (see SignupTestFX#testDifferentPassword)
    }

    @Test
    public void connectorOnlyInput() {
        clickOn("#openUpdate");
        clickOn("#connectorType");
        moveBy(0, 40);
        clickOn();
        clickOn("#addConnectionBtn");
        clickOn("#saveChanges");
        // TODO: check for tooltip popup (see SignupTestFX#testDifferentPassword)
    }

    @Test
    public void minimalValidVeh() {
        clickOn("#openUpdate");
        clickOn("#makeText");
        write("Tesla");
        clickOn("#modelText");
        write("Y");
        clickOn("#maxRangeText");
        write("500");
        clickOn("#connectorType");
        moveBy(0, 40);
        clickOn();
        clickOn("#addConnectionBtn");
        clickOn("#saveChanges");
        verifyThat("#editCarOne", Node::isVisible);
        verifyThat("#deleteCarOne", Node::isVisible);
    }

    @Test
    public void extraValidVeh() {
        clickOn("#openUpdate");
        clickOn("#makeText");
        write("Tesla");
        clickOn("#modelText");
        write("Y");
        clickOn("#maxRangeText");
        write("500");
        clickOn("#connectorType");
        clickOn("CHAdeMO");
        clickOn("#addConnectionBtn");
        clickOn("#saveChanges");
        verifyThat("#editCarOne", Node::isVisible);
        verifyThat("#deleteCarOne", Node::isVisible);
    }

    @Test
    public void morethanThreeVehicles() {
        for (int i = 0; i < 4; i++) {
            clickOn("#openUpdate");
            clickOn("#makeText");
            write("Tesla");
            clickOn("#modelText");
            write("Y");
            clickOn("#maxRangeText");
            write("500");
            clickOn("#selectImgBtn");
            clickOn();
            moveBy(20, 220);
            clickOn();
            clickOn("#connectorType");
            moveBy(0, 40);
            clickOn();
            clickOn("#addConnectionBtn");
            clickOn("#saveChanges");
        }
        verifyThat("#nextBtn", Node::isVisible);
        verifyThat("#prevBtn", Node::isVisible);
    }

}
