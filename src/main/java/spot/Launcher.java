package spot;

import javafx.application.Application;

/**
 * A launcher class to work around classpath issues when running JavaFX applications.
 */
public class Launcher {
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
