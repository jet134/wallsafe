package fi.kennyhei.wallsafe.controller;

import fi.kennyhei.wallsafe.WallSafeFactory;
import fi.kennyhei.wallsafe.service.DesktopService;
import fi.kennyhei.wallsafe.service.DownloaderService;
import fi.kennyhei.wallsafe.service.SettingsService;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
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
import javafx.stage.DirectoryChooser;

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

    @FXML private Button chooseDirectoryButton;
    @FXML private ComboBox<String> resolutionComboBox;
    @FXML private Button downloadButton;

    // Services
    private DesktopService desktopService;
    private DownloaderService downloaderService;
    private SettingsService settingsService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Setup services
        setDesktopService(WallSafeFactory.getDesktopService());
        setDownloaderService(WallSafeFactory.getDownloaderService());
        setSettingsService(WallSafeFactory.getSettingsService());

        // Setup interval event handlers
        initializeIntervalHandlers();

        this.chooseDirectoryButton.setOnAction((ActionEvent event) -> onChooseDirectory(event));
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
        this.downloaderService.start();
        this.desktopService.start();
    }

    private void initializeIntervalHandlers() {

        // Change interval handlers
        this.changeIntervalCheckBox.selectedProperty()
                                   .addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) ->
                                                 onChangeIntervalCheckbox(newValue));

        this.changeIntervalTextField.textProperty()
                                    .addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) ->
                                                  onNumericIntervalValueChange(oldValue, newValue, this.changeIntervalTextField));

        this.changeIntervalComboBox.setOnAction((ActionEvent event) -> onIntervalTimeUnitChange(event, this.changeIntervalComboBox));

        // Download interval handlers
        this.downloadIntervalCheckBox.selectedProperty()
                                   .addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) ->
                                                 onDownloadIntervalCheckbox(newValue));

        this.downloadIntervalTextField.textProperty()
                                    .addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) ->
                                                  onNumericIntervalValueChange(oldValue, newValue, this.downloadIntervalTextField));

        this.downloadIntervalComboBox.setOnAction((ActionEvent event) -> onIntervalTimeUnitChange(event, this.downloadIntervalComboBox));
    }

    public Parent getView() {

        return this.contentArea;
    }

    private void onNumericIntervalValueChange(String oldValue, String newValue, TextField intervalTextField) {

        if (!newValue.matches("^-?\\d+$")) {
            intervalTextField.setText(oldValue);
            return;
        }

        int interval = Integer.parseInt(newValue);

        if (intervalTextField.getId().equals("changeIntervalTextField")) {

            String timeUnit = this.changeIntervalComboBox.getSelectionModel().getSelectedItem();
            this.desktopService.updateInterval(interval, timeUnit);
        }

        if (intervalTextField.getId().equals("downloadIntervalTextField")) {

            String timeUnit = this.downloadIntervalComboBox.getSelectionModel().getSelectedItem();
            this.downloaderService.updateInterval(interval, timeUnit);
        }
    }

    private void onIntervalTimeUnitChange(ActionEvent event, ComboBox<String> intervalComboBox) {

        String timeUnit = intervalComboBox.getSelectionModel().getSelectedItem();

        if (intervalComboBox.getId().equals("changeIntervalComboBox")) {

            int interval = Integer.parseInt(this.changeIntervalTextField.getText());
            this.desktopService.updateInterval(interval, timeUnit);
        }

        if (intervalComboBox.getId().equals("downloadIntervalComboBox")) {

            int interval = Integer.parseInt(this.downloadIntervalTextField.getText());
            this.downloaderService.updateInterval(interval, timeUnit);
        }
    }

    private void onChangeIntervalCheckbox(Boolean selected) {

        this.desktopService.updateState(selected);
    }

    private void onDownloadIntervalCheckbox(Boolean selected) {

        this.downloaderService.updateState(selected);
    }

    public void onChooseDirectory(ActionEvent event) {

        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(null);

        if (selectedDirectory != null) {

            this.chooseDirectoryButton.setText(selectedDirectory.getAbsolutePath());

            this.downloaderService.setDirectory(selectedDirectory);
            this.desktopService.setDirectory(selectedDirectory);
        }
    }

    public void onResolution(ActionEvent event) {

        String resolution = this.resolutionComboBox.getSelectionModel().getSelectedItem();
        this.settingsService.setResolution(resolution);
    }

    public void onDownload(ActionEvent event) {

        // Download
        this.downloaderService.download();

        // Set as desktop background
        this.desktopService.changeWallpaper(downloaderService.getLatestFilename());
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
