package fi.kennyhei.wallsafe.model;

import java.util.List;

public class Settings {

    // Settings is a singleton class
    private static Settings instance = null;

    // Resolution of the wallpaper, defaults to user's native resolution
    private String resolution;

    // Tags for searching wallpapers
    private List<String> tags;

    // Base URL where wallpapers are downloaded from
    private final String baseURL = "http://alpha.wallhaven.cc/search";
    private String URL;

    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36";

    public static Settings getInstance() {

        if (instance == null) {
            instance = new Settings();
        }

        return instance;
    }

    protected Settings() {

        this.buildURL();
    }

    public String getResolution() {

        return resolution;
    }

    public void setResolution(String resolution) {

        this.resolution = resolution;
        this.buildURL();
    }

    public List<String> getTags() {

        return tags;
    }

    public void setTags(List<String> tags) {

        this.tags = tags;
    }

    public String getURL() {

        return URL;
    }

    private void buildURL() {

        StringBuilder sb = new StringBuilder(baseURL);

        sb.append("?")
          .append("categories=101&")
          .append("purity=101&")
          .append("resolutions=")
          .append(resolution)
          .append("&")
          .append("sorting=random&")
          .append("order=desc");

        this.URL = sb.toString();
    }
}
