package fi.kennyhei.wallsafe.controller;

import fi.kennyhei.wallsafe.WSSystemTray;
import fi.kennyhei.wallsafe.WallSafeFactory;
import fi.kennyhei.wallsafe.service.DesktopService;

import java.awt.MenuItem;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.util.Map;

import javafx.application.Platform;
import javafx.stage.Stage;

public class TrayController {

    // Services
    private final DesktopService desktopService;

    // System tray
    private final WSSystemTray systemTray;

    public TrayController(WSSystemTray systemTray) {

        this.systemTray = systemTray;
        this.desktopService = WallSafeFactory.getDesktopService();
        this.initialize();
    }

    private void initialize() {

        Map<String, MenuItem> menuItems = this.systemTray.getMenuItems();

        // Open item
        MenuItem openItem = menuItems.get(WSSystemTray.OPEN_ITEM);
        openItem.addActionListener(event -> Platform.runLater(this::showStage));

        // Delete item
        MenuItem deleteItem = menuItems.get(WSSystemTray.DELETE_ITEM);
        deleteItem.addActionListener(event -> onDeleteWallpaper());

        // Next item
        MenuItem nextItem = menuItems.get(WSSystemTray.NEXT_ITEM);
        nextItem.addActionListener(event -> onNextWallpaper());

        // Previous item
        MenuItem previousItem = menuItems.get(WSSystemTray.PREVIOUS_ITEM);
        previousItem.addActionListener(event -> onPreviousWallpaper());

        // Exit item
        MenuItem exitItem = menuItems.get(WSSystemTray.EXIT_ITEM);
        SystemTray tray = this.systemTray.getSystemTray();
        TrayIcon trayIcon = this.systemTray.getTrayIcon();

        // If the user double-clicks on the tray icon, show the main app window
        trayIcon.addActionListener(event -> Platform.runLater(this::showStage));

        // To really exit the application, the user must go to the system tray icon
        // and select the exit option, this will shutdown JavaFX and remove the
        // tray icon (removing the tray icon will also shut down AWT)
        exitItem.addActionListener(event -> {

            Platform.exit();
            tray.remove(tray.getTrayIcons()[0]);
        });
    }

    private void showStage() {

        Stage stage = systemTray.getStage();

        if (stage != null) {
            stage.show();
            stage.toFront();
        }
    }

    private void onDeleteWallpaper() {

        this.desktopService.deleteWallpaper();
    }

    private void onNextWallpaper() {

        this.desktopService.changeToNext();
    }

    private void onPreviousWallpaper() {

        this.desktopService.changeToPrevious();
    }
}
