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
import seng202.team3.gui.MainController;


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
public class RunCucumberTestMainPage {



}
