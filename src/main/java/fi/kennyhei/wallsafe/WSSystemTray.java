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
import org.apache.log4j.Logger;

/* Create system tray UI here and logic is in the TrayController */
public class WSSystemTray {

    private static final Logger LOG = Logger.getLogger(WSSystemTray.class);

    private TrayController trayController;

    private SystemTray tray;

    // Tray icon
    private TrayIcon trayIcon;

    // Menu items
    private final Map<String, MenuItem> menuItems;

    public static final String OPEN_ITEM = "open";
    public static final String PLAYBACK_ITEM = "playback";
    public static final String DELETE_ITEM = "delete";
    public static final String NEXT_ITEM = "next";
    public static final String PREVIOUS_ITEM = "previous";
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

                LOG.info("No system tray support, application exiting.");
                Platform.exit();
            }

            // Set up a system tray icon
            tray = SystemTray.getSystemTray();

            URL imageUrl = ClassLoader.getSystemClassLoader().getResource(App.ICON_LOCATION);
            Image image = ImageIO.read(imageUrl);
            trayIcon = new TrayIcon(image);

            // Create menu buttons for system tray
            createItem(OPEN_ITEM, "Open", true);
            createItem(PLAYBACK_ITEM, "Stop", true);
            createItem(NEXT_ITEM, "Next", false);
            createItem(PREVIOUS_ITEM, "Previous", false);
            createItem(DELETE_ITEM, "Delete", false);
            createItem(EXIT_ITEM, "Exit", false);

            PopupMenu popup = createPopupMenu(menuItems);

            trayIcon.setPopupMenu(popup);
            trayIcon.setToolTip("WallSafe");

            // Add the application tray icon to the system tray
            tray.add(trayIcon);

        } catch (AWTException | IOException e) {

            LOG.info("Unable to initialize system tray.");
        }

        // Create controller for system tray handling actions
        this.trayController = new TrayController(this);
    }

    private void createItem(String key, String text, boolean bold) {

        MenuItem item = new MenuItem(text);

        if (bold) {
            Font defaultFont = Font.decode(null);
            Font boldFont = defaultFont.deriveFont(Font.BOLD);
            item.setFont(boldFont);
        }

        menuItems.put(key, item);
    }

    private PopupMenu createPopupMenu(Map<String, MenuItem> menuItems) {

        // Setup the popup menu for the application
        final PopupMenu popup = new PopupMenu();

        popup.add(menuItems.get(OPEN_ITEM));
        popup.addSeparator();
        popup.add(menuItems.get(PLAYBACK_ITEM));
        popup.addSeparator();
        popup.add(menuItems.get(NEXT_ITEM));
        popup.add(menuItems.get(PREVIOUS_ITEM));
        popup.add(menuItems.get(DELETE_ITEM));
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

    public TrayController getTrayController() {

        return this.trayController;
    }

    public Stage getStage() {

        return this.stage;
    }
}
