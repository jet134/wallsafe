package fi.kennyhei.wallsafe;

import fi.kennyhei.wallsafe.controller.MainController;
import fi.kennyhei.wallsafe.service.impl.DefaultDownloaderService;
import fi.kennyhei.wallsafe.service.impl.DefaultSettingsService;
import fi.kennyhei.wallsafe.service.DownloaderService;
import fi.kennyhei.wallsafe.service.SettingsService;

import java.io.IOException;

import javafx.fxml.FXMLLoader;

public class WallSafeFactory {

    /* Controllers */
    private MainController mainController;

    /* Services */
    private DownloaderService downloaderService;
    private SettingsService settingsService;

    public MainController getMainController() {

        if (mainController == null) {
            try
            {
                FXMLLoader loader = new FXMLLoader();
                loader.load(getClass().getResourceAsStream("/fxml/Main.fxml"));
                mainController = (MainController) loader.getController();

                mainController.setDownloaderService(getDownloaderService());
                mainController.setSettingsService(getSettingsService());
            }
            catch (IOException e)
            {
                throw new RuntimeException("Unable to load Main.fxml", e);
            }
        }

        return mainController;
    }

    public DownloaderService getDownloaderService() {

        if (downloaderService == null) {
            downloaderService = new DefaultDownloaderService();
        }

        return downloaderService;
    }

    private SettingsService getSettingsService() {

        if (settingsService == null) {
            settingsService = new DefaultSettingsService();
        }

        return settingsService;
    }
}
