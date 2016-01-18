package fi.kennyhei.wallsafe;

import fi.kennyhei.wallsafe.controller.MainController;

import java.awt.AWTException;
import java.awt.Font;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.application.Platform;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import static javafx.application.Application.launch;

public class App extends Application {

    private static final String ICON_LOCATION = "http://icons.iconarchive.com/icons/mcdo-design/aqua-candy/16/Wallpaper-Folder-graphite-icon.png";

    private Stage stage;

    @Override
    public void start(Stage stage) throws Exception {

        this.stage = stage;

        // Instructs the JavaFX system not to exit implicitly when the last application window is shut
        Platform.setImplicitExit(false);

        // Set up a system tray for application
        SwingUtilities.invokeLater(this::addAppToTray);

        WallSafeFactory factory = new WallSafeFactory();
        MainController mainController = factory.getMainController();

        Scene scene = new Scene(mainController.getView());
        scene.getStylesheets().add("/styles/Styles.css");

        stage.setScene(scene);
        stage.setTitle("WallSafe");
        stage.show();
    }

    // Sets up a system tray icon for the application.
    private void addAppToTray() {

        try {

            // Ensure AWT Toolkit is initialized.
            Toolkit.getDefaultToolkit();

            // App requires system tray support, just exit if there is no support.
            if (!SystemTray.isSupported()) {

                System.out.println("No system tray support, application exiting.");
                Platform.exit();
            }

            // Set up a system tray icon
            SystemTray tray = SystemTray.getSystemTray();

            URL imageUrl = new URL(ICON_LOCATION);
            Image image = ImageIO.read(imageUrl);
            TrayIcon trayIcon = new TrayIcon(image);

            // If the user double-clicks on the tray icon, show the main app window
            trayIcon.addActionListener(event -> Platform.runLater(this::showStage));

            // If the user selects the default menu item (which includes the app name),
            // show the main app stage
            MenuItem openItem = new MenuItem("Open WallSafe");
            openItem.addActionListener(event -> Platform.runLater(this::showStage));

            Font defaultFont = Font.decode(null);
            Font boldFont = defaultFont.deriveFont(Font.BOLD);
            openItem.setFont(boldFont);

            // To really exit the application, the user must go to the system tray icon
            // and select the exit option, this will shutdown JavaFX and remove the
            // tray icon (removing the tray icon will also shut down AWT)
            MenuItem exitItem = new MenuItem("Exit");

            exitItem.addActionListener(event -> {
                Platform.exit();
                tray.remove(trayIcon);
            });

            // Setup the popup menu for the application
            final PopupMenu popup = new PopupMenu();

            popup.add(openItem);
            popup.addSeparator();
            popup.add(exitItem);

            trayIcon.setPopupMenu(popup);
            trayIcon.setToolTip("WallSafe");

            // Add the application tray icon to the system tray
            tray.add(trayIcon);

        } catch (AWTException | IOException e) {

            System.out.println("Unable to initialize system tray.");
        }
    }

    // Shows the application stage and ensures that it is brought to the front of all stages.
    private void showStage() {

        if (stage != null) {
            stage.show();
            stage.toFront();
        }
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
