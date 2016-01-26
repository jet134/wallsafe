package fi.kennyhei.wallsafe.controller;

import fi.kennyhei.wallsafe.WSSystemTray;
import fi.kennyhei.wallsafe.WallSafeFactory;
import fi.kennyhei.wallsafe.service.DesktopService;
import fi.kennyhei.wallsafe.service.DownloaderService;

import java.awt.MenuItem;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.util.Map;

import javafx.application.Platform;
import javafx.stage.Stage;

public class TrayController {

    // Services
    private final DesktopService desktopService;
    private final DownloaderService downloaderService;

    // System tray
    private final WSSystemTray systemTray;

    public TrayController(WSSystemTray systemTray) {

        this.systemTray = systemTray;

        this.desktopService = WallSafeFactory.getDesktopService();
        this.downloaderService = WallSafeFactory.getDownloaderService();

        this.initialize();
    }

    private void initialize() {

        Map<String, MenuItem> menuItems = this.systemTray.getMenuItems();

        // Open item
        MenuItem openItem = menuItems.get(WSSystemTray.OPEN_ITEM);
        openItem.addActionListener(event -> Platform.runLater(this::showStage));

        // Playback item
        MenuItem playbackItem = menuItems.get(WSSystemTray.PLAYBACK_ITEM);
        playbackItem.addActionListener(event -> onPlayback());

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

    private void onPlayback() {

        Map<String, MenuItem> menuItems = this.systemTray.getMenuItems();
        MenuItem playbackItem = menuItems.get(WSSystemTray.PLAYBACK_ITEM);

        // Pause services
        if (playbackItem.getLabel().equals("Pause")) {

            // Scheduled services can only be called from a Java FX Application
            // thread. Platform.runLater runs the specified code on the Java FX
            // application thread sometime in the future.
            Platform.runLater(() -> {

                desktopService.isRunningProperty().set(false);
                playbackItem.setLabel("Play");
            });

        } else if (playbackItem.getLabel().equals("Play")) {

            Platform.runLater(() -> {

                desktopService.isRunningProperty().set(true);
                playbackItem.setLabel("Pause");
            });
        }
    }

    public void togglePlaybackText() {

        Map<String, MenuItem> menuItems = this.systemTray.getMenuItems();
        MenuItem playbackItem = menuItems.get(WSSystemTray.PLAYBACK_ITEM);

        if (playbackItem.getLabel().equals("Pause")) {
            playbackItem.setLabel("Play");
        } else if (playbackItem.getLabel().equals("Play")) {
            playbackItem.setLabel("Pause");
        }
    }
}
