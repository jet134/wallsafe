package fi.kennyhei.wallsafe.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fi.kennyhei.wallsafe.service.SettingsService;
import fi.kennyhei.wallsafe.model.Settings;
import java.util.ArrayList;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
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

        keyword = Settings.WS_KEYWORDS + "." + keyword;
        Map<String, Integer> keywords = settings.getKeywords();

        if (!keywords.containsKey(keyword)) {
            keywords.put(keyword, -1);
        }

        this.updateKeywordsPreference(keywords);
    }

    @Override
    public void removeKeyword(String keyword) {

        keyword = Settings.WS_KEYWORDS + "." + keyword;
        Map<String, Integer> keywords = settings.getKeywords();

        if (keywords.containsKey(keyword)) {
            keywords.remove(keyword);
        }

        this.updateKeywordsPreference(keywords);
    }

    @Override
    public Map<String, Integer> getKeywords() {

        return settings.getKeywords();
    }

    @Override
    public String getRandomKeyword() {

        Random r = new Random();

        Set<String> keys = this.settings.getKeywords().keySet();
        String[] keywords = keys.toArray(new String[keys.size()]);

        int index = r.nextInt(keywords.length);

        String keyword = keywords[index].split("\\.")[1];
        return keyword;
    }

    @Override
    public List<String> getPlainKeywords() {

        Set<String> keys = this.settings.getKeywords().keySet();
        List<String> keywords = new ArrayList<>();

        for (String keyword : keys) {
            keywords.add(keyword.split("\\.")[1]);
        }

        return keywords;
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

    private void updateKeywordsPreference(Map<String, Integer> keywords) {

        ObjectMapper mapper = new ObjectMapper();

        try {

            String mapAsJson = mapper.writeValueAsString(keywords);
            this.updatePreference(Settings.WS_KEYWORDS, mapAsJson);

        } catch (JsonProcessingException ex) {
            System.out.println("Couldn't serialize to JSON.");
        }
    }

    private void updatePreference(String key, String value) {

        Preferences preferences = this.settings.getPreferences();
        preferences.put(key, value);
    }

    @Override
    public void setIndexOfKeyword(String keyword, int index) {

        keyword = Settings.WS_KEYWORDS + "." + keyword;

        Map<String, Integer> keywords = this.settings.getKeywords();
        keywords.put(keyword, index);

        this.updateKeywordsPreference(keywords);
    }

    @Override
    public int getIndexOfKeyword(String keyword) {

        keyword = Settings.WS_KEYWORDS + "." + keyword;

        Map<String, Integer> keywords = this.settings.getKeywords();
        return keywords.get(keyword);
    }
}
