package fi.kennyhei.wallsafe;

import fi.kennyhei.wallsafe.controller.MainController;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import javax.swing.SwingUtilities;

import static javafx.application.Application.launch;

public class App extends Application {

    public static final String ICON_LOCATION = "images/wallsafe-icon-round-16-transparent.png";
    private static WSSystemTray systemTray;

    @Override
    public void start(Stage stage) throws Exception {

        // Instructs the JavaFX system not to exit implicitly when the last application window is shut
        // This is because application keeps running in system tray
        Platform.setImplicitExit(false);

        WallSafeFactory factory = new WallSafeFactory();
        MainController mainController = factory.getMainController();

        Scene scene = new Scene(mainController.getView());
        scene.getStylesheets().add("/styles/Styles.css");

        // Set up a system tray for application
        systemTray = new WSSystemTray(stage);
        SwingUtilities.invokeLater(systemTray::initialize);

        stage.setScene(scene);
        stage.setTitle("WallSafe");
        stage.getIcons().add(new Image(ClassLoader.getSystemResourceAsStream(ICON_LOCATION)));
        stage.show();
    }

    public static WSSystemTray getSystemTray() {

        return systemTray;
    }

   /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
