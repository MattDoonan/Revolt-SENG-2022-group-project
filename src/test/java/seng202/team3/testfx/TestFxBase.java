package seng202.team3.testfx;

import java.util.concurrent.TimeoutException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;

import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import seng202.team3.gui.MainWindow;
import seng202.team3.gui.MapHandler;

/**
 * This is Morgan's Code designed to set up TestFX testing
 *
 * @author Morgan English
 * @version 1.0.0, Sep 22
 */
public abstract class TestFxBase extends ApplicationTest {
    /**
     * Logger
     */
    private static final Logger logManager = LogManager.getLogger();

    @BeforeAll
    public static void setUp() throws Exception {
        System.gc();
        // Disable map by default
        MapHandler.setMapRequested(false);
        MapHandler.setLocationAccepted(false);
        ApplicationTest.launch(MainWindow.class);
    }

    @Override
    public abstract void start(Stage stage) throws Exception;

    /**
     * Free resources after tests
     * 
     * @throws TimeoutException application does not respond in time
     */
    @AfterEach
    public void afterEachTest() throws TimeoutException {
        try {
            FxToolkit.cleanupStages();
        } catch (TimeoutException e) {
            logManager.error(e.getMessage());
            ;
        }
        FxToolkit.hideStage();
        release(new KeyCode[] {});
        release(new MouseButton[] {});
    }

    /**
     * Helper method to retrieve Java FX GUI Components
     */
    @SuppressWarnings("unchecked")
    public <T extends Node> T find(final String query) {
        return (T) lookup(query).queryAll().iterator().next();
    }
}