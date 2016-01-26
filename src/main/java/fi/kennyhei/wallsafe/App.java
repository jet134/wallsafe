package fi.kennyhei.wallsafe;

import fi.kennyhei.wallsafe.controller.MainController;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.application.Platform;

import javax.swing.SwingUtilities;

import static javafx.application.Application.launch;

public class App extends Application {

    public static final String ICON_LOCATION = "http://icons.iconarchive.com/icons/mcdo-design/aqua-candy/16/Wallpaper-Folder-graphite-icon.png";
    private static WSSystemTray systemTray;

    @Override
    public void start(Stage stage) throws Exception {

        // Instructs the JavaFX system not to exit implicitly when the last application window is shut
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
        stage.getIcons().add(new javafx.scene.image.Image(ICON_LOCATION));
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
