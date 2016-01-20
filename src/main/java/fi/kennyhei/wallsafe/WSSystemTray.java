package fi.kennyhei.wallsafe;

import fi.kennyhei.wallsafe.controller.TrayController;
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
import java.util.HashMap;
import java.util.Map;

import javafx.application.Platform;
import javafx.stage.Stage;

import javax.imageio.ImageIO;

/* Create system tray UI here and logic is in the TrayController */
public class WSSystemTray {

    private TrayController trayController;

    private SystemTray tray;

    // Tray icon
    private TrayIcon trayIcon;

    // Menu items
    private final Map<String, MenuItem> menuItems;

    public static final String OPEN_ITEM = "open";
    public static final String NEXT_ITEM = "next";
    public static final String EXIT_ITEM = "exit";

    private final Stage stage;

    public WSSystemTray(Stage stage) {

        this.stage = stage;
        this.menuItems = new HashMap<>();
    }

    public void initialize() {

        try {

            // Ensure AWT Toolkit is initialized
            Toolkit.getDefaultToolkit();

            // App requires system tray support, just exit if there is no support
            if (!SystemTray.isSupported()) {

                System.out.println("No system tray support, application exiting.");
                Platform.exit();
            }

            // Set up a system tray icon
            tray = SystemTray.getSystemTray();

            URL imageUrl = new URL(App.ICON_LOCATION);
            Image image = ImageIO.read(imageUrl);
            trayIcon = new TrayIcon(image);

            createOpenItem();
            createNextItem();
            createExitItem();

            PopupMenu popup = createPopupMenu(menuItems);

            trayIcon.setPopupMenu(popup);
            trayIcon.setToolTip("WallSafe");

            // Add the application tray icon to the system tray
            tray.add(trayIcon);

        } catch (AWTException | IOException e) {

            System.out.println("Unable to initialize system tray.");
        }

        // Create controller for system tray handling actions
        this.trayController = new TrayController(this);
    }

    private MenuItem createOpenItem() {

        MenuItem openItem = new MenuItem("Open");

        Font defaultFont = Font.decode(null);
        Font boldFont = defaultFont.deriveFont(Font.BOLD);
        openItem.setFont(boldFont);

        menuItems.put(OPEN_ITEM, openItem);

        return openItem;
    }

    private MenuItem createNextItem() {

        MenuItem nextItem = new MenuItem("Next");
        menuItems.put(NEXT_ITEM, nextItem);

        return nextItem;
    }

    private MenuItem createExitItem() {

        MenuItem exitItem = new MenuItem("Exit");
        menuItems.put(EXIT_ITEM, exitItem);

        return exitItem;
    }

    private PopupMenu createPopupMenu(Map<String, MenuItem> menuItems) {

        // Setup the popup menu for the application
        final PopupMenu popup = new PopupMenu();

        popup.add(menuItems.get(OPEN_ITEM));
        popup.addSeparator();
        popup.add(menuItems.get(NEXT_ITEM));
        popup.addSeparator();
        popup.add(menuItems.get(EXIT_ITEM));

        return popup;
    }

    public Map<String, MenuItem> getMenuItems() {

        return this.menuItems;
    }

    public SystemTray getSystemTray() {

        return this.tray;
    }

    public TrayIcon getTrayIcon() {

        return this.trayIcon;
    }

    public Stage getStage() {

        return this.stage;
    }
}
