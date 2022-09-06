package seng202.team3.cucumber;

import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import seng202.team3.gui.MainController;
import seng202.team3.gui.MainWindow;
import java.util.concurrent.TimeoutException;

import static io.cucumber.junit.platform.engine.Constants.*;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/main_features")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "seng202.team3.cucumber.mainPageSteps")
@ConfigurationParameter(key = SNIPPET_TYPE_PROPERTY_NAME, value = "camelcase")


/**
 * Cucumber Main Page tests to run
 *
 * @author Michelle Hsieh (based off Morgan English's code)
 * @version 1.0.0, Sep 22
 */
public class RunCucumberTestMainPage extends ApplicationTest {

    private MainController controller;

    /**
     * Implements the abstract method for this window
     *
     * @throws Exception if fail to launch
     */
    @Before
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
        controller = loader.getController();
        controller.init(stage);
    }

    /**
     * Cleans everything up after each Cucumber test
     *
     * @throws TimeoutException If it can't clean everything up
     */
    @After
    public void afterEach() throws TimeoutException {
        try {
            FxToolkit.cleanupStages();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    /**
     * Destroys everything at the end
     */
    @AfterAll
    public static void destroyWindows(){
        try {
            FxToolkit.cleanupStages();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    public <T extends Node> T find (final  String query){
        return (T) lookup(query).queryAll().iterator().next();
    }


}
