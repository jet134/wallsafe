package fi.kennyhei.wallsafe.service.impl;

import fi.kennyhei.wallsafe.service.SettingsService;
import fi.kennyhei.wallsafe.model.Settings;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.prefs.Preferences;

public class DefaultSettingsService implements SettingsService {

    private final Settings settings;

    public DefaultSettingsService() {

        this.settings = Settings.getInstance();
    }

    @Override
    public void setResolution(String resolution) {

        settings.setResolution(resolution);

        this.updatePreference(Settings.WS_RESOLUTION, resolution);
        this.buildUrl();
    }

    @Override
    public void addKeyword(String keyword) {

        List<String> keywords = settings.getKeywords();
        keywords.add(keyword);

        this.updateKeywordsPreference(keywords);
    }

    @Override
    public void removeKeyword(String keyword) {

        List<String> keywords = settings.getKeywords();
        Iterator<String> keywordIterator = keywords.iterator();

        while (keywordIterator.hasNext()) {

            String value = keywordIterator.next();

            if (value.equals(keyword)) {
                keywordIterator.remove();
                break;
            }
        }

        this.updateKeywordsPreference(keywords);
    }

    @Override
    public List<String> getKeywords() {

        return settings.getKeywords();
    }

    @Override
    public String getRandomKeyword() {

        Random r = new Random();

        List<String> keywords = settings.getKeywords();
        int index = r.nextInt(keywords.size());

        return keywords.get(index);
    }

    @Override
    public void buildUrl() {

        settings.buildUrl();
    }

    @Override
    public void buildUrl(String keyword) {

        settings.buildUrl(keyword);
    }

    @Override
    public String url() {

        return settings.getUrl();
    }

    @Override
    public int getChangeIntervalValue() {

        return settings.getChangeIntervalValue();
    }

    @Override
    public void setChangeIntervalValue(int value) {

        settings.setChangeIntervalValue(value);
        this.updatePreference(Settings.WS_CHANGE_INTERVAL_VALUE, Integer.toString(value));
    }

    @Override
    public String getChangeIntervalTimeunit() {

        return settings.getChangeIntervalTimeunit();
    }

    @Override
    public void setChangeIntervalTimeunit(String value) {

        settings.setChangeIntervalTimeunit(value);
        this.updatePreference(Settings.WS_CHANGE_INTERVAL_TIMEUNIT, value);
    }

    @Override
    public int getDownloadIntervalValue() {

        return settings.getDownloadIntervalValue();
    }

    @Override
    public void setDownloadIntervalValue(int value) {

        settings.setDownloadIntervalValue(value);
        this.updatePreference(Settings.WS_DOWNLOAD_INTERVAL_VALUE, Integer.toString(value));
    }

    @Override
    public String getDownloadIntervalTimeunit() {

        return settings.getDownloadIntervalTimeunit();
    }

    @Override
    public void setDownloadIntervalTimeunit(String value) {

        settings.setDownloadIntervalTimeunit(value);
        this.updatePreference(Settings.WS_DOWNLOAD_INTERVAL_TIMEUNIT, value);
    }

    @Override
    public String getDirectoryPath() {

        return settings.getDirectoryPath();
    }

    @Override
    public void setDirectoryPath(String selectedDirectory) {

        settings.setDirectoryPath(selectedDirectory);
        this.updatePreference(Settings.WS_DOWNLOAD_DIRECTORY, selectedDirectory);
    }

    @Override
    public int getIndexOfCurrentWallpaper() {

        return settings.getIndexOfCurrentWallpaper();
    }

    @Override
    public void setIndexOfCurrentWallpaper(int value) {

        settings.setIndexOfCurrentWallpaper(value);
        this.updatePreference(Settings.WS_CURRENT_WALLPAPER_INDEX, Integer.toString(value));
    }

    private void updateKeywordsPreference(List<String> keywords) {

        String words = "";

        for (String value : keywords) {
            words += value;
            words += ",";
        }

        this.updatePreference(Settings.WS_KEYWORDS, words.substring(0, words.length() - 1));
    }

    private void updatePreference(String key, String value) {

        Preferences preferences = this.settings.getPreferences();
        preferences.put(key, value);
    }
}
