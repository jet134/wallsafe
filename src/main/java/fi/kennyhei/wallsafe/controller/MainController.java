package fi.kennyhei.wallsafe.controller;

import fi.kennyhei.wallsafe.WallSafeFactory;
import fi.kennyhei.wallsafe.concurrent.service.ScheduledDesktopService;
import fi.kennyhei.wallsafe.concurrent.service.ScheduledDownloadService;
import fi.kennyhei.wallsafe.service.DesktopService;
import fi.kennyhei.wallsafe.service.DownloaderService;
import fi.kennyhei.wallsafe.service.SettingsService;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class MainController implements Initializable {

    // Variables annotated with @FXML have their values injected by FXMLLoader
    @FXML private Parent root;
    @FXML private GridPane contentArea;

    // Change interval
    @FXML private CheckBox changeIntervalCheckBox;
    @FXML private TextField changeIntervalTextField;
    @FXML private ComboBox<String> changeIntervalComboBox;

    // Download interval
    @FXML private CheckBox downloadIntervalCheckBox;
    @FXML private TextField downloadIntervalTextField;
    @FXML private ComboBox<String> downloadIntervalComboBox;

    @FXML private ComboBox<String> resolutionComboBox;
    @FXML private Button downloadButton;

    // Services
    private DesktopService desktopService;
    private DownloaderService downloaderService;
    private SettingsService settingsService;

    // Background tasks
    private ScheduledDownloadService scheduledDownloadService;
    private ScheduledDesktopService scheduledDesktopService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Setup services
        setDesktopService(WallSafeFactory.getDesktopService());
        setDownloaderService(WallSafeFactory.getDownloaderService());
        setSettingsService(WallSafeFactory.getSettingsService());

        // Setup event handlers
        initializeIntervalHandlers();

        this.resolutionComboBox.setOnAction((ActionEvent event) -> onResolution(event));
        this.downloadButton.setOnAction((ActionEvent event) -> onDownload(event));

        // Find out native resolution
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) screenSize.getWidth();
        int height = (int) screenSize.getHeight();

        // Set resolution
        String resolution = String.valueOf(width) + "x" + String.valueOf(height);
        this.resolutionComboBox.getSelectionModel().select(resolution);

        // Initialize background tasks
        this.scheduledDownloadService = new ScheduledDownloadService();
        this.scheduledDownloadService.initialize();

        this.scheduledDesktopService = new ScheduledDesktopService();
        this.scheduledDesktopService.initialize();
    }

    private void initializeIntervalHandlers() {

        // Change interval handlers
        this.changeIntervalCheckBox.selectedProperty()
                                   .addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) ->
                                                 onChangeIntervalSelected(newValue));

        this.changeIntervalTextField.textProperty()
                                    .addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) ->
                                                  onChangeIntervalValue(newValue));

        this.changeIntervalComboBox.setOnAction((ActionEvent event) -> onChangeIntervalTimeUnit(event));

        // Download interval handlers
        this.downloadIntervalCheckBox.selectedProperty()
                                   .addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) ->
                                                 onDownloadIntervalSelected(newValue));

        this.downloadIntervalTextField.textProperty()
                                    .addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) ->
                                                  onDownloadIntervalValue(newValue));

        this.downloadIntervalComboBox.setOnAction((ActionEvent event) -> onDownloadIntervalTimeUnit(event));
    }

    public Parent getView() {

        return contentArea;
    }

    private void onChangeIntervalSelected(Boolean selected) {

        this.scheduledDesktopService.updateState(selected);
    }

    private void onChangeIntervalValue(String newValue) {

        int interval = Integer.parseInt(newValue);
        String timeUnit = changeIntervalComboBox.getSelectionModel().getSelectedItem();

        this.scheduledDesktopService.updateInterval(interval, timeUnit);
    }

    private void onChangeIntervalTimeUnit(ActionEvent event) {

        int interval = Integer.parseInt(changeIntervalTextField.getText());
        String timeUnit = changeIntervalComboBox.getSelectionModel().getSelectedItem();

        this.scheduledDesktopService.updateInterval(interval, timeUnit);
    }

    private void onDownloadIntervalSelected(Boolean selected) {

        this.scheduledDownloadService.updateState(selected);
    }

    private void onDownloadIntervalValue(String newValue) {

        int interval = Integer.parseInt(newValue);
        String timeUnit = changeIntervalComboBox.getSelectionModel().getSelectedItem();

        this.scheduledDownloadService.updateInterval(interval, timeUnit);
    }

    private void onDownloadIntervalTimeUnit(ActionEvent event) {

        int interval = Integer.parseInt(changeIntervalTextField.getText());
        String timeUnit = changeIntervalComboBox.getSelectionModel().getSelectedItem();

        this.scheduledDownloadService.updateInterval(interval, timeUnit);
    }

    public void onResolution(ActionEvent event) {

        String resolution = resolutionComboBox.getSelectionModel().getSelectedItem();
        settingsService.setResolution(resolution);
    }

    public void onDownload(ActionEvent event) {

        // Download
        downloaderService.download();

        // Set as desktop background
        desktopService.changeWallpaper(downloaderService.getLatestFilename());
    }

    public void setDesktopService(DesktopService desktopService) {

        this.desktopService = desktopService;
    }

    public void setDownloaderService(DownloaderService downloaderService) {

        this.downloaderService = downloaderService;
    }

    public void setSettingsService(SettingsService settingsService) {

        this.settingsService = settingsService;
    }
}
