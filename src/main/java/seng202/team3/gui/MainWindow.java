package seng202.team3.gui;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Class starts the javaFX application window
 *
 * @author Team 3
 * @version 1.0.1, Aug 22
 */
public class MainWindow extends Application {

    /**
     * Activce scene displayed in the window
     */
    private static Scene activeScene;

    /**
     * unused constructor
     */
    public MainWindow() {
        // unused
    }

    /**
     * {@inheritDoc}
     * Opens the gui with the fxml content specified in resources/fxml/main.fxml
     */
    @Override
    public void start(Stage primaryStage) throws IOException {

        FXMLLoader baseLoader = new FXMLLoader(getClass().getResource("/fxml/menu_bar.fxml"));
        Parent root = baseLoader.load();
        MenuController baseController = baseLoader.getController();
        baseController.init(primaryStage);
        primaryStage.setTitle("Revolt App");
        double height = Screen.getPrimary().getBounds().getHeight() - 80;
        double width = Screen.getPrimary().getBounds().getWidth();
        activeScene = new Scene(root, width, height);
        baseController.initHome();
        primaryStage.setScene(activeScene);
        primaryStage.show();
        MainWindow.setController(baseController);
    }

    /**
     * Set controller stored in active scene
     * 
     * @param controller the active scene controller
     */
    public static void setController(Object controller) {
        if (activeScene != null) {
            activeScene.setUserData(controller);
        }
    }

    /**
     * Get the controller from the active scene
     * 
     * @return the active scene controller
     */
    public static Object getController() {
        if (activeScene != null) {
            return activeScene.getUserData();
        } else {
            return null;
        }
    }

    /**
     * Launches the FXML application, this must be called from another class
     * (in this cass App.java) otherwise JavaFX
     * errors out and does not run
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
