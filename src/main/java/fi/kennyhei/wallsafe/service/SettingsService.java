package fi.kennyhei.wallsafe.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fi.kennyhei.wallsafe.config.Filters;
import fi.kennyhei.wallsafe.config.Option;
import fi.kennyhei.wallsafe.service.SettingsService;
import fi.kennyhei.wallsafe.config.Settings;
import fi.kennyhei.wallsafe.security.Security;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.prefs.Preferences;
import org.apache.log4j.Logger;

public class SettingsService {

    private static final Logger LOG = Logger.getLogger(SettingsService.class);

    private final Settings settings;
    private final ObjectMapper mapper;
    private final Random random;

    public SettingsService() {

        this.settings = Settings.getInstance();
        this.mapper = new ObjectMapper();
        this.random = new Random();
    }

    public String getResolution() {

        return settings.getResolution();
    }

    public void setResolution(String resolution) {

        settings.setResolution(resolution);

        this.updatePreference(Option.WS_RESOLUTION, resolution);
        this.buildUrl();
    }

    public boolean addKeyword(String keyword) {

        keyword = Option.WS_KEYWORDS + "." + keyword;
        Map<String, Integer> keywords = settings.getKeywords();

        if (!keywords.containsKey(keyword)) {
            keywords.put(keyword, -1);
            this.updateKeywordsPreference(keywords);

            return true;
        }

        return false;
    }

    public void removeKeyword(String keyword) {

        keyword = Option.WS_KEYWORDS + "." + keyword;
        Map<String, Integer> keywords = settings.getKeywords();

        if (keywords.containsKey(keyword)) {
            keywords.remove(keyword);
        }

        this.updateKeywordsPreference(keywords);
    }

    public Map<String, Integer> getKeywords() {

        return settings.getKeywords();
    }

    public String getRandomKeyword() {

        Set<String> keys = this.settings.getKeywords().keySet();

        if (keys.isEmpty()) {
            return "random";
        }

        String[] keywords = keys.toArray(new String[keys.size()]);

        int index = random.nextInt(keywords.length);

        String keyword = keywords[index].split("\\.")[2];
        return keyword;
    }

    public List<String> getPlainKeywords() {

        Set<String> keys = this.settings.getKeywords().keySet();
        List<String> keywords = new ArrayList<>();

        for (String keyword : keys) {
            keywords.add(keyword.split("\\.")[2]);
        }

        return keywords;
    }

    public void buildUrl() {

        settings.buildUrl();
    }

    public void buildUrl(String keyword) {

        settings.buildUrl(keyword);
    }

    public String url() {

        return settings.getUrl();
    }

    public int getChangeIntervalValue() {

        return settings.getChangeIntervalValue();
    }

    public void setChangeIntervalValue(int value) {

        settings.setChangeIntervalValue(value);
        this.updatePreference(Option.WS_CHANGE_INTERVAL_VALUE, Integer.toString(value));
    }

    public String getChangeIntervalTimeunit() {

        return settings.getChangeIntervalTimeunit();
    }

    public void setChangeIntervalTimeunit(String value) {

        settings.setChangeIntervalTimeunit(value);
        this.updatePreference(Option.WS_CHANGE_INTERVAL_TIMEUNIT, value);
    }

    public int getDownloadIntervalValue() {

        return settings.getDownloadIntervalValue();
    }

    public void setDownloadIntervalValue(int value) {

        settings.setDownloadIntervalValue(value);
        this.updatePreference(Option.WS_DOWNLOAD_INTERVAL_VALUE, Integer.toString(value));
    }

    public String getDownloadIntervalTimeunit() {

        return settings.getDownloadIntervalTimeunit();
    }

    public void setDownloadIntervalTimeunit(String value) {

        settings.setDownloadIntervalTimeunit(value);
        this.updatePreference(Option.WS_DOWNLOAD_INTERVAL_TIMEUNIT, value);
    }

    public String getDirectoryPath() {

        return settings.getDirectoryPath();
    }

    public void setDirectoryPath(String selectedDirectory) {

        settings.setDirectoryPath(selectedDirectory);
        this.updatePreference(Option.WS_DOWNLOAD_DIRECTORY, selectedDirectory);
    }


    public String getDesktopMode() {

        return settings.getDesktopMode();
    }

    public void setDesktopMode(String mode) {

        settings.setDesktopMode(mode);
        this.updatePreference(Option.WS_DESKTOP_MODE, mode);
    }

    private void updateKeywordsPreference(Map<String, Integer> keywords) {

        try {

            String mapAsJson = mapper.writeValueAsString(keywords);
            this.updatePreference(Option.WS_KEYWORDS, mapAsJson);

        } catch (JsonProcessingException ex) {
            LOG.info("Couldn't serialize to JSON.");
        }
    }

    private void updatePreference(String key, String value) {

        Preferences preferences = this.settings.getPreferences();
        preferences.put(key, value);
    }

    public void setIndexOfKeyword(String keyword, int index) {

        keyword = Option.WS_KEYWORDS + "." + keyword;

        Map<String, Integer> keywords = this.settings.getKeywords();
        keywords.put(keyword, index);

        this.updateKeywordsPreference(keywords);
    }

    public int getIndexOfKeyword(String keyword) {

        keyword = Option.WS_KEYWORDS + "." + keyword;

        Map<String, Integer> keywords = this.settings.getKeywords();
        return keywords.get(keyword);
    }

    public void setFilter(String text, boolean isSelected) {
        switch (text) {
            case "General":
                Filters.isGeneral(isSelected);
                this.updatePreference(Option.WS_IS_GENERAL, Boolean.toString(isSelected));
                break;
            case "Anime":
                Filters.isAnime(isSelected);
                this.updatePreference(Option.WS_IS_ANIME, Boolean.toString(isSelected));
                break;
            case "People":
                Filters.isPeople(isSelected);
                this.updatePreference(Option.WS_IS_PEOPLE, Boolean.toString(isSelected));
                break;
            case "SFW":
                Filters.isSFW(isSelected);
                this.updatePreference(Option.WS_IS_SFW, Boolean.toString(isSelected));
                break;
            case "Sketchy":
                Filters.isSketchy(isSelected);
                this.updatePreference(Option.WS_IS_SKETCHY, Boolean.toString(isSelected));
                break;
            case "NSFW":
                Filters.isNSFW(isSelected);
                this.updatePreference(Option.WS_IS_NSFW, Boolean.toString(isSelected));
                break;
        }
    }

    public boolean isFilterSelected(String text) {
        switch (text) {
            case "General":
                return Filters.isGeneral();
            case "Anime":
                return Filters.isAnime();
            case "People":
                return Filters.isPeople();
            case "SFW":
                return Filters.isSFW();
            case "Sketchy":
                return Filters.isSketchy();
            case "NSFW":
                return Filters.isNSFW();
        }

        return false;
    }

    public void setCredentials(Map<String, char[]> credentials) {
        try {
            String encrypted = Security.createEncryptedData(credentials, 2);
            settings.setCredentials(encrypted);

            this.updatePreference(Option.WS_CREDENTIALS, encrypted);
        } catch (Exception ex) {}
    }

    public String getCredentials() {

        return this.settings.getCredentials();
    }
}
