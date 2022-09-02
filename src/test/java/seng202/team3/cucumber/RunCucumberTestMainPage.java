package seng202.team3.cucumber;

import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.Before;
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
@SelectClasspathResource("features/main_page_features")
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

    /**
     * Sets up the main screen with some data
     */
    @Before
    public void setUp() throws Exception {
        ApplicationTest.launch(MainWindow.class);
    }

    /**
     * Overridden load
     *
     * @param primaryStage A stage
     * @throws Exception The exception to be thrown if it doesn't work
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader baseLoader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        Parent root = baseLoader.load();
        MainController baseController = baseLoader.getController();
        baseController.init(primaryStage);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
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
