package seng202.team3.cucumber;

import java.util.concurrent.TimeoutException;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import seng202.team3.gui.MainWindow;

/**
 * Allows TestFx to run through cucumber using static reference
 * to the main scene which stores the active controller
 *
 * @author Harrison Tyson
 * @version 1.0.0, Sep 22
 */
public abstract class CucumberFxBase extends ApplicationTest {

    /**
     * Launches the test window
     * Use with @BeforeAll
     * 
     * @throws Exception window fails to launch
     */
    public static void setup() throws Exception {
        ApplicationTest.launch(MainWindow.class);
    }

    /**
     * Environment setup for test. IMPORTANT: THE LOCAL CONTROLLER MUST BE SET WITH
     * MainWindow.getController()
     * TODO: Find a way to assign that here using inheritance abstraction
     * Use with @Before
     * 
     * @throws Exception if test cannot be initialized
     */
    public abstract void init() throws Exception;

    /**
     * Free resources after tests
     * User with @AfterEach
     * 
     * @throws TimeoutException application does not respond in time
     */
    public void afterEachTest() throws TimeoutException {
        try {
            FxToolkit.cleanupStages();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        FxToolkit.hideStage();
        release(new KeyCode[] {});
        release(new MouseButton[] {});
    }

    /**
     * Clear program location for future tests
     * Use with @AfterAll
     */
    public static void cleanUp() {
        try {
            FxToolkit.cleanupStages();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    /**
     * Does not execute when using cucumber
     * {@inheritDoc}
     */
    @Override
    public void start(Stage stage) throws Exception {
        // unused
    }

    /**
     * Helper method to retrieve Java FX GUI Components
     */
    @SuppressWarnings("unchecked")
    public <T extends Node> T find(final String query) {
        return (T) lookup(query).queryAll().iterator().next();
    }
}