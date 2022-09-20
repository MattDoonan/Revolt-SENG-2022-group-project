package seng202.team3.gui;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


/**
 * Class starts the javaFX application window
 *
 * @author Team 3
 * @version 1.0.1, Aug 22
 */
public class MainWindow extends Application {

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
        Scene scene = new Scene(root, 1500, 820);
        baseController.initHome();
        primaryStage.setScene(scene);
        primaryStage.show();
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
