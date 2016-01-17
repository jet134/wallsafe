package fi.kennyhei.wallsafe.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.prefs.Preferences;

public class Settings {

    // Preference keys
    private static final String WS_CHANGE_INTERVAL_VALUE = "change.interval.value";
    private static final String WS_CHANGE_INTERVAL_TIMEUNIT = "change.interval.timeunit";

    private static final String WS_DOWNLOAD_INTERVAL_VALUE = "download.interval.value";
    private static final String WS_DOWNLOAD_INTERVAL_TIMEUNIT = "download.interval.timeunit";

    private static final String WS_RESOLUTION = "resolution";
    private static final String WS_DOWNLOAD_DIRECTORY = "download.directory";

    private static final String WS_CURRENT_WALLPAPER_INDEX = "current.wallpaper.index";
    private static final String WS_KEYWORDS = "keywords";

    // User-Agent header
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36";

    // User preferences stored in registry
    private final Preferences preferences;

    // Settings is a singleton class
    private static Settings instance = null;

    // Resolution of the wallpaper, defaults to user's native resolution
    private String resolution;

    // Keywords for searching wallpapers
    private List<String> keywords;

    // Download directory
    private String directoryPath;

    // Interval settings
    private int changeIntervalValue;
    private String changeIntervalTimeunit;

    private int downloadIntervalValue;
    private String downloadIntervalTimeunit;

    // Index of current wallpaper
    private int indexOfCurrentWallpaper;

    // Base URL where wallpapers are downloaded from
    private final String baseUrl = "http://alpha.wallhaven.cc/search";
    private String url;

    public static Settings getInstance() {

        if (instance == null) {
            instance = new Settings();
        }

        return instance;
    }

    protected Settings() {

        // Initialize preferences
        this.preferences = Preferences.userRoot().node(this.getClass().getName());

        // Load user preferences
        this.loadPreferences();

        // Build URL where wallpapers are downloaded from
        this.buildUrl();
    }

    public String getResolution() {

        return resolution;
    }

    public void setResolution(String resolution) {

        this.resolution = resolution;

        this.updatePreference(WS_RESOLUTION, resolution);
        this.buildUrl();
    }

    public List<String> getKeywords() {

        return keywords;
    }

    public void setKeywords(List<String> keywords) {

        this.keywords = keywords;
    }

    public String getDirectoryPath() {

        return directoryPath;
    }

    public void setDirectoryPath(String directoryPath) {

        this.directoryPath = directoryPath;
        this.updatePreference(WS_DOWNLOAD_DIRECTORY, directoryPath);
    }

    public int getChangeIntervalValue() {

        return changeIntervalValue;
    }

    public void setChangeIntervalValue(int changeIntervalValue) {

        this.changeIntervalValue = changeIntervalValue;
        this.updatePreference(WS_CHANGE_INTERVAL_VALUE, Integer.toString(changeIntervalValue));
    }

    public String getChangeIntervalTimeunit() {

        return changeIntervalTimeunit;
    }

    public void setChangeIntervalTimeunit(String changeIntervalTimeunit) {

        this.changeIntervalTimeunit = changeIntervalTimeunit;
        this.updatePreference(WS_CHANGE_INTERVAL_TIMEUNIT, changeIntervalTimeunit);
    }

    public int getDownloadIntervalValue() {

        return downloadIntervalValue;
    }

    public void setDownloadIntervalValue(int downloadIntervalValue) {

        this.downloadIntervalValue = downloadIntervalValue;
        this.updatePreference(WS_DOWNLOAD_INTERVAL_VALUE, Integer.toString(downloadIntervalValue));
    }

    public String getDownloadIntervalTimeunit() {

        return downloadIntervalTimeunit;
    }

    public void setDownloadIntervalTimeunit(String downloadIntervalTimeunit) {

        this.downloadIntervalTimeunit = downloadIntervalTimeunit;
        this.updatePreference(WS_DOWNLOAD_INTERVAL_TIMEUNIT, downloadIntervalTimeunit);
    }

    public int getIndexOfCurrentWallpaper() {

        return indexOfCurrentWallpaper;
    }

    public void setIndexOfCurrentWallpaper(int indexOfCurrentWallpaper) {

        this.indexOfCurrentWallpaper = indexOfCurrentWallpaper;
        this.updatePreference(WS_CURRENT_WALLPAPER_INDEX, Integer.toString(indexOfCurrentWallpaper));
    }

    public String getUrl() {

        return url;
    }

    public void buildUrl(String keyword) {

        StringBuilder sb = new StringBuilder(baseUrl);

        sb.append("?")
          .append("q=")
          .append(keyword)
          .append("&")
          .append("categories=101&")
          .append("purity=101&")
          .append("resolutions=")
          .append(resolution)
          .append("&")
          .append("sorting=random&")
          .append("order=desc");

        this.url = sb.toString();
    }

    public void buildUrl() {

        StringBuilder sb = new StringBuilder(baseUrl);

        sb.append("?")
          .append("categories=101&")
          .append("purity=101&")
          .append("resolutions=")
          .append(resolution)
          .append("&")
          .append("sorting=random&")
          .append("order=desc");

        this.url = sb.toString();
    }

    private void loadPreferences() {

        this.changeIntervalValue = Integer.parseInt(preferences.get(WS_CHANGE_INTERVAL_VALUE, "60"));
        this.changeIntervalTimeunit = preferences.get(WS_CHANGE_INTERVAL_TIMEUNIT, "seconds");

        this.downloadIntervalValue = Integer.parseInt(preferences.get(WS_DOWNLOAD_INTERVAL_VALUE, "60"));
        this.downloadIntervalTimeunit = preferences.get(WS_DOWNLOAD_INTERVAL_TIMEUNIT, "seconds");

        this.resolution = preferences.get(WS_RESOLUTION, "1920x1080");
        this.directoryPath = preferences.get(WS_DOWNLOAD_DIRECTORY, System.getProperty("user.home") + "\\Desktop\\Wallpapers");
        this.indexOfCurrentWallpaper = Integer.parseInt(preferences.get(WS_CURRENT_WALLPAPER_INDEX, "0"));

        String[] prefKeywords = preferences.get(WS_KEYWORDS, "space,nature,abstract").split(",");
        this.keywords = new ArrayList<>(Arrays.asList(prefKeywords));
    }

    private void updatePreference(String key, String value) {

        this.preferences.put(key, value);
    }
}
