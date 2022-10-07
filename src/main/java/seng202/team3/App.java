package seng202.team3;

import seng202.team3.gui.MainWindow;

/**
 * Default entry point class
 *
 * @author seng202 teaching team
 */
public class App {

    /**
     * Unused constructor
     */
    public App() {
        // Unused
    }

    /**
     * Entry point which runs the javaFX application
     * since it is non-modular.
     * Causes Unsupported JavaFX config error log - this can be ignored
     *
     * @param args program arguments from command line
     */
    public static void main(String[] args) {
        MainWindow.main(args);
    }
}
