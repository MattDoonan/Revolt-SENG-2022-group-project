package seng202.team3.testFX;

import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.concurrent.TimeoutException;


/**
 * This is Morgan's Code designed to set up TestFX testing
 *
 * @author Morgan English
 * @version 1.0.0, Sep 22
 */
public abstract class TestFXBase extends ApplicationTest {

    @BeforeEach
    public abstract void setUpClass() throws Exception;

    @Override
    public abstract void start(Stage stage) throws Exception;

    @AfterEach
    public void afterEachTest() throws TimeoutException {
        try {
            FxToolkit.cleanupStages();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    /* Helper method to retrieve Java FX GUI Components */
    public <T extends Node> T find (final  String query){
        return (T) lookup(query).queryAll().iterator().next();
    }
}