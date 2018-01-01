package fi.kennyhei.wallsafe;

import fi.kennyhei.wallsafe.controller.MainController;
import fi.kennyhei.wallsafe.service.DownloaderService;
import fi.kennyhei.wallsafe.service.SettingsService;
import fi.kennyhei.wallsafe.service.DesktopService;

import java.io.IOException;

import javafx.fxml.FXMLLoader;

public class WallSafeFactory {

    /* Controllers */
    private MainController mainController;

    /* Services */
    private static DesktopService desktopService;
    private static DownloaderService downloaderService;
    private static SettingsService settingsService;

    public MainController getMainController() {

        if (mainController == null) {
            try
            {
                FXMLLoader loader = new FXMLLoader();
                loader.load(getClass().getResourceAsStream("/fxml/Main.fxml"));
                mainController = (MainController) loader.getController();
            }
            catch (IOException e)
            {
                throw new RuntimeException("Unable to load Main.fxml", e);
            }
        }

        return mainController;
    }

    public static DesktopService getDesktopService() {

        if (desktopService == null) {
            desktopService = new DesktopService();
        }

        return desktopService;
    }

    public static DownloaderService getDownloaderService() {

        if (downloaderService == null) {
            downloaderService = new DownloaderService();
        }

        return downloaderService;
    }

    public static SettingsService getSettingsService() {

        if (settingsService == null) {
            settingsService = new SettingsService();
        }

        return settingsService;
    }
}
