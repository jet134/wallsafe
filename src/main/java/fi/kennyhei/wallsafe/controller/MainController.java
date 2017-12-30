package fi.kennyhei.wallsafe.controller;

import fi.kennyhei.wallsafe.App;
import fi.kennyhei.wallsafe.WallSafeFactory;
import fi.kennyhei.wallsafe.service.DesktopService;
import fi.kennyhei.wallsafe.service.DownloaderService;
import fi.kennyhei.wallsafe.service.SettingsService;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
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

    // Filter checkboxes
    @FXML private HBox contentFilterHbox;
    @FXML private HBox sfwFilterHbox;

    // Keywords
    @FXML private ListView<String> keywordsListView;
    @FXML private Button addKeywordButton;
    @FXML private Button removeKeywordButton;

    @FXML private Button chooseDirectoryButton;
    @FXML private ComboBox<String> resolutionComboBox;

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

        // Setup interval values
        initializeIntervalValues();

        // Setup interval event handlers
        initializeIntervalHandlers();

        // Setup filters
        initializeFilters();

        // Setup keywords event handlers
        initializeKeywordHandlers();

        String directoryPath = this.settingsService.getDirectoryPath();
        this.chooseDirectoryButton.setText(directoryPath);
        this.chooseDirectoryButton.setOnAction(event -> onChooseDirectory());

        this.resolutionComboBox.setOnAction(event -> onResolution());

        // Find out native resolution
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) screenSize.getWidth();
        int height = (int) screenSize.getHeight();

        // Set resolution
        String resolution = String.valueOf(width) + "x" + String.valueOf(height);
        this.resolutionComboBox.getSelectionModel().select(resolution);
        this.settingsService.setResolution(resolution);

        // Initialize background tasks
        this.downloaderService.start();
        this.desktopService.start();
    }

    private void initializeIntervalValues() {

        String interval;

        interval = Integer.toString(this.settingsService.getChangeIntervalValue());
        this.changeIntervalTextField.setText(interval);

        interval = Integer.toString(this.settingsService.getDownloadIntervalValue());
        this.downloadIntervalTextField.setText(interval);

        String timeUnit;

        timeUnit = this.settingsService.getChangeIntervalTimeunit();
        this.changeIntervalComboBox.setValue(timeUnit);

        timeUnit = this.settingsService.getDownloadIntervalTimeunit();
        this.downloadIntervalComboBox.setValue(timeUnit);
    }

    private void initializeIntervalHandlers() {

        // Change interval handlers
        // Use bidirectional binding, if checkbox value is changed, desktop service state is automatically updated and vice versa
        this.changeIntervalCheckBox.selectedProperty().bindBidirectional(this.desktopService.isRunningProperty());
        this.changeIntervalCheckBox.selectedProperty().addListener(listener -> {

            if (App.getSystemTray() != null) {

                App.getSystemTray().getTrayController().togglePlaybackText();
            }
        });

        this.changeIntervalTextField.textProperty()
                                    .addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) ->
                                                  onNumericIntervalValueChange(oldValue, newValue, this.changeIntervalTextField));

        this.changeIntervalComboBox.setOnAction(event -> onIntervalTimeUnitChange(this.changeIntervalComboBox));

        // Download interval handlers
        // Use bidirectional binding, if checkbox value is changed, downloader service state is automatically updated and vice versa
        this.downloadIntervalCheckBox.selectedProperty().bindBidirectional(this.downloaderService.isRunningProperty());

        this.downloadIntervalTextField.textProperty()
                                      .addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) ->
                                                    onNumericIntervalValueChange(oldValue, newValue, this.downloadIntervalTextField));

        this.downloadIntervalComboBox.setOnAction(event -> onIntervalTimeUnitChange(this.downloadIntervalComboBox));
    }

    private void initializeKeywordHandlers() {

        ObservableList<String> keywords = FXCollections.observableArrayList(this.settingsService.getPlainKeywords());
        this.keywordsListView.setItems(keywords);

        this.addKeywordButton.setOnAction(event -> onAddKeyword());
        this.removeKeywordButton.setOnAction(event -> onRemoveKeyword());
    }

    private void initializeFilters() {

        ObservableList<Node> filters = this.contentFilterHbox.getChildren();
        ObservableList<Node> sfwFilters = this.sfwFilterHbox.getChildren();

        setupFilterCheckboxes(filters);
        setupFilterCheckboxes(sfwFilters);
    }

    private void setupFilterCheckboxes(ObservableList<Node> filters) {
        for( Node node : filters ) {
            CheckBox filter = (CheckBox) node;

            filter.setOnAction(event -> {
                this.settingsService.setFilter(filter.getText(), filter.isSelected());
            });

            String text = filter.getText();
            boolean isSelected = this.settingsService.isFilterSelected(text);

            filter.setSelected(isSelected);
        }
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

            this.settingsService.setChangeIntervalValue(interval);
            this.desktopService.updateInterval();
        }

        if (intervalTextField.getId().equals("downloadIntervalTextField")) {

            this.settingsService.setDownloadIntervalValue(interval);
            this.downloaderService.updateInterval();
        }
    }

    private void onIntervalTimeUnitChange(ComboBox<String> intervalComboBox) {

        String timeUnit = intervalComboBox.getSelectionModel().getSelectedItem();

        if (intervalComboBox.getId().equals("changeIntervalComboBox")) {

            this.settingsService.setChangeIntervalTimeunit(timeUnit);
            this.desktopService.updateInterval();
        }

        if (intervalComboBox.getId().equals("downloadIntervalComboBox")) {

            this.settingsService.setDownloadIntervalTimeunit(timeUnit);
            this.downloaderService.updateInterval();
        }
    }

    public void onResolution() {

        String resolution = this.resolutionComboBox.getSelectionModel().getSelectedItem();
        this.settingsService.setResolution(resolution);
    }

    public void onChooseDirectory() {

        DirectoryChooser directoryChooser = new DirectoryChooser();

        File currentDirectory = new File(this.settingsService.getDirectoryPath());
        directoryChooser.setInitialDirectory(currentDirectory);

        File selectedDirectory = directoryChooser.showDialog(null);

        if (selectedDirectory != null) {

            this.chooseDirectoryButton.setText(selectedDirectory.getAbsolutePath());

            this.settingsService.setDirectoryPath(selectedDirectory.getAbsolutePath());
            this.desktopService.resetIndex();
        }
    }

    private void onAddKeyword() {

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add keyword");
        dialog.setHeaderText(null);
        dialog.setContentText("Add keyword:");

        Optional<String> result = dialog.showAndWait();

        // The Java 8 way to get the response value (with lambda expression).
        result.ifPresent(keyword -> {

            this.settingsService.addKeyword(keyword);
            this.keywordsListView.getItems().add(keyword);
        });
    }

    private void onRemoveKeyword() {

        String selectedItem = this.keywordsListView.getSelectionModel().getSelectedItem();

        if (selectedItem == null) {
            return;
        }

        this.settingsService.removeKeyword(selectedItem);
        this.keywordsListView.getItems().remove(selectedItem);
        this.keywordsListView.getSelectionModel().clearSelection();
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
