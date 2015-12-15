package fi.kennyhei.wallsafe.controller;

import fi.kennyhei.wallsafe.WallSafeFactory;
import fi.kennyhei.wallsafe.service.DownloaderService;
import fi.kennyhei.wallsafe.service.SettingsService;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;

public class MainController implements Initializable {

    // Variables annotated with @FXML have their values injected by FXMLLoader
    @FXML private Parent root;
    @FXML private BorderPane contentArea;

    @FXML private Button downloadButton;
    @FXML private ComboBox<String> resolutionComboBox;

    private DownloaderService downloaderService;
    private SettingsService settingsService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Setup services
        setDownloaderService(WallSafeFactory.getDownloaderService());
        setSettingsService(WallSafeFactory.getSettingsService());

        // Setup event handlers
        resolutionComboBox.setOnAction((ActionEvent event) -> onResolution(event));
        downloadButton.setOnAction((ActionEvent event) -> onDownload(event));

        // Find out native resolution
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) screenSize.getWidth();
        int height = (int) screenSize.getHeight();

        // Set resolution
        String resolution = String.valueOf(width) + "x" + String.valueOf(height);
        resolutionComboBox.getSelectionModel().select(resolution);
    }

    public Parent getView() {

        return root;
    }

    public void showMainView() {

        contentArea.setCenter(this.getView());
    }

    public void onResolution(ActionEvent event) {

        String resolution = resolutionComboBox.getSelectionModel().getSelectedItem();
        settingsService.setResolution(resolution);
    }

    public void onDownload(ActionEvent event) {

        downloaderService.download();
    }

    public void setDownloaderService(DownloaderService downloaderService) {

        this.downloaderService = downloaderService;
    }

    public void setSettingsService(SettingsService settingsService) {

        this.settingsService = settingsService;
    }
}
