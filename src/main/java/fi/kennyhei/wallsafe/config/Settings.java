package fi.kennyhei.wallsafe.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.prefs.Preferences;

import org.apache.log4j.Logger;

public class Settings {

    private static final Logger LOG = Logger.getLogger(Settings.class);

    // User-Agent header
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36";

    // User preferences stored in registry
    private final Preferences preferences;

    // Settings is a singleton class
    private static Settings instance = null;

    // Resolution of the wallpaper, defaults to user's native resolution
    private String resolution;

    // Defines how to position the wallpaper on screen
    // NOTE: User cannot configure this option because the operation
    // for setting the picture position in Windows is not reliable/instant
    private String desktopMode;

    // Keywords for searching wallpapers
    private Map<String, Integer> keywords;

    // Download directory
    private String directoryPath;

    private final Download downloadAction;
    private final Change changeAction;

    // Base URL where wallpapers are downloaded from
    private final String baseUrl = "http://alpha.wallhaven.cc/search";
    private String url;

    // WallHaven account information
    private String credentials;

    public static Settings getInstance() {

        if (instance == null) {
            instance = new Settings();
        }

        return instance;
    }

    protected Settings() {

        // Initialize preferences
        this.preferences = Preferences.userRoot().node(this.getClass().getName());
        this.downloadAction = new Download(this.preferences);
        this.changeAction = new Change(this.preferences);

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

    public Download getDownloadAction() {

        return downloadAction;
    }

    public Change getChangeAction() {

        return changeAction;
    }

    public String getDesktopMode() {

        return desktopMode;
    }

    public void setDesktopMode(String mode) {

        this.desktopMode = mode;
    }

    public String getCredentials() {

        return credentials;
    }

    public void setCredentials(String credentials) {

        this.credentials = credentials;
    }

    public Preferences getPreferences() {

        return preferences;
    }

    public String getUrl() {

        return url;
    }

    public void buildUrl(String keyword) {

        StringBuilder sb = new StringBuilder(baseUrl);

        try {
            keyword = URLEncoder.encode(keyword, "UTF-8");
        } catch(Exception ex) {}

        String categories = this.categoriesValue();
        String purity = this.purityValue();
        String resolutionParam = resolution.equals("Any") ? "" : resolution;

        sb.append("?")
          .append("q=")
          .append(keyword)
          .append("&")
          .append("categories=")
          .append(categories)
          .append("purity=")
          .append(purity)
          .append("resolutions=")
          .append(resolutionParam)
          .append("&")
          .append("sorting=random&")
          .append("order=desc");

        this.url = sb.toString();
    }

    public void buildUrl() {

        StringBuilder sb = new StringBuilder(baseUrl);

        String categories = this.categoriesValue();
        String purity = this.purityValue();
        String resolutionParam = resolution.equals("Any") ? "" : resolution;

        sb.append("?")
          .append("categories=")
          .append(categories)
          .append("purity=")
          .append(purity)
          .append("resolutions=")
          .append(resolutionParam)
          .append("&")
          .append("sorting=random&")
          .append("order=desc");

        this.url = sb.toString();
    }

    private String categoriesValue() {

        return toNumeral(Filters.isGeneral()) +
               toNumeral(Filters.isAnime()) +
               toNumeral(Filters.isPeople()) + "&";
    }

    private String purityValue() {

        return toNumeral(Filters.isSFW()) +
               toNumeral(Filters.isSketchy()) +
               toNumeral(Filters.isNSFW()) + "&";
    }

    private String toNumeral(Boolean input) {
        return input ? "1" : "0";
    }

    private void loadPreferences() {

        this.changeAction.loadSettings();
        this.downloadAction.loadSettings();

        // Find out native resolution
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) screenSize.getWidth();
        int height = (int) screenSize.getHeight();

        String preferredResolution = String.valueOf(width) + "x" + String.valueOf(height);

        this.resolution = preferences.get(Option.WS_RESOLUTION, preferredResolution);
        this.desktopMode = preferences.get(Option.WS_DESKTOP_MODE, "Fit");
        this.directoryPath = preferences.get(Option.WS_DOWNLOAD_DIRECTORY, System.getProperty("user.home") + "\\Desktop\\Wallpapers");

        Filters.setup(preferences);

        if (this.resolution.equals("All")) {
            preferences.put(Option.WS_RESOLUTION, "Any");
            this.resolution = "Any";
        }

        this.credentials = preferences.get(Option.WS_CREDENTIALS, null);

        File downloadDirectory = new File(this.directoryPath);
        downloadDirectory.mkdirs();

        ObjectMapper mapper = new ObjectMapper();

        // Deserializing JSON might fail for old users as keywords preference value isn't valid JSON for them
        while (true) {
            String keywordMapAsJson = preferences.get(Option.WS_KEYWORDS,
                                                      "{\"ws.keywords.abstract\":-1," +
                                                      "\"ws.keywords.nature\":-1," +
                                                      "\"ws.keywords.space\":-1}");

            try {
                this.keywords = mapper.readValue(keywordMapAsJson, Map.class);
                break;
            } catch (IOException ex) {

                LOG.info("Couldn't deserialize JSON.");
                preferences.remove(Option.WS_KEYWORDS);
            }
        }
    }
}
