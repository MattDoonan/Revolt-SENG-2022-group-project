package seng202.team3.testfx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.testfx.api.FxRobotException;
import org.testfx.framework.junit5.ApplicationTest;
import seng202.team3.data.entity.Vehicle;
import seng202.team3.gui.GarageController;
import seng202.team3.gui.MainWindow;
import seng202.team3.gui.MenuController;

import java.util.stream.Stream;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.api.FxAssert.verifyThatIter;

public class VehiclesPageFx extends TestFxBase {

    private GarageController controller;


    @Override
    public void setUp() throws Exception {
        ApplicationTest.launch(MainWindow.class);

    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/garage.fxml"));
        Parent page = loader.load();
        initState(loader, stage);
        Scene scene = new Scene(page);
        stage.setScene(scene);
        stage.show();
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
            Assertions.fail("Shouldn't be visible");
        } catch (FxRobotException e) {
            Assertions.assertTrue(true);
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
    public void checkAllReqInputs(String node, String text) {
        clickOn("#openUpdate");
        clickOn(node);
        write(text);
        clickOn("#saveChanges");
        verifyThat("#prompt", Node::isVisible);
    }



}
