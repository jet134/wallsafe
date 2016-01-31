package fi.kennyhei.wallsafe.model;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.prefs.Preferences;

public class Settings {

    // Preference keys
    public static final String WS_CHANGE_INTERVAL_VALUE = "change.interval.value";
    public static final String WS_CHANGE_INTERVAL_TIMEUNIT = "change.interval.timeunit";

    public static final String WS_DOWNLOAD_INTERVAL_VALUE = "download.interval.value";
    public static final String WS_DOWNLOAD_INTERVAL_TIMEUNIT = "download.interval.timeunit";

    public static final String WS_RESOLUTION = "resolution";
    public static final String WS_DOWNLOAD_DIRECTORY = "download.directory";

    public static final String WS_KEYWORDS = "keywords";

    // User-Agent header
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36";

    // User preferences stored in registry
    private final Preferences preferences;

    // Settings is a singleton class
    private static Settings instance = null;

    // Resolution of the wallpaper, defaults to user's native resolution
    private String resolution;

    // Keywords for searching wallpapers
    private Map<String, Integer> keywords;

    // Download directory
    private String directoryPath;

    // Interval settings
    private int changeIntervalValue;
    private String changeIntervalTimeunit;

    private int downloadIntervalValue;
    private String downloadIntervalTimeunit;

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
    }

    public Map<String, Integer> getKeywords() {

        return keywords;
    }

    public void setKeywords(Map<String, Integer> keywords) {

        this.keywords = keywords;
    }

    public String getDirectoryPath() {

        return directoryPath;
    }

    public void setDirectoryPath(String directoryPath) {

        this.directoryPath = directoryPath;
    }

    public int getChangeIntervalValue() {

        return changeIntervalValue;
    }

    public void setChangeIntervalValue(int changeIntervalValue) {

        this.changeIntervalValue = changeIntervalValue;
    }

    public String getChangeIntervalTimeunit() {

        return changeIntervalTimeunit;
    }

    public void setChangeIntervalTimeunit(String changeIntervalTimeunit) {

        this.changeIntervalTimeunit = changeIntervalTimeunit;
    }

    public int getDownloadIntervalValue() {

        return downloadIntervalValue;
    }

    public void setDownloadIntervalValue(int downloadIntervalValue) {

        this.downloadIntervalValue = downloadIntervalValue;
    }

    public String getDownloadIntervalTimeunit() {

        return downloadIntervalTimeunit;
    }

    public void setDownloadIntervalTimeunit(String downloadIntervalTimeunit) {

        this.downloadIntervalTimeunit = downloadIntervalTimeunit;
    }

    public Preferences getPreferences() {

        return preferences;
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

        File downloadDirectory = new File(this.directoryPath);
        downloadDirectory.mkdirs();

        ObjectMapper mapper = new ObjectMapper();

        // Deserializing JSON might fail for old users as keywords preference value isn't valid JSON for them
        while (true) {
            String keywordMapAsJson = preferences.get(WS_KEYWORDS, "{\"keywords.abstract\":-1,\"keywords.nature\":-1,\"keywords.space\":-1}");

            try {
                this.keywords = mapper.readValue(keywordMapAsJson, Map.class);
                break;
            } catch (IOException ex) {

                System.out.println("Couldn't deserialize JSON.");
                preferences.remove(WS_KEYWORDS);
            }
        }
    }
}
