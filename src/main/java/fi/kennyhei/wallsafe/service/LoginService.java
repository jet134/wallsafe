package fi.kennyhei.wallsafe.service;

import fi.kennyhei.wallsafe.security.Security;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;

public class LoginService {

    private static final Logger LOG = Logger.getLogger(LoginService.class);

    // LoginService is a singleton class
    private static LoginService instance = null;
    private final SettingsService settingsService;

    private Map<String, String> cookies;
    private static final String LOGIN_URL = "https://alpha.wallhaven.cc/auth/login";
    private static final String USER_AGENT = "Mozilla/5.0";

    private String credentials;

    public static LoginService getInstance() {

        if (instance == null) {
            instance = new LoginService();
        }

        return instance;
    }

    public LoginService() {

        this.cookies = new HashMap<>();

        this.settingsService = new SettingsService();
        this.credentials = this.settingsService.getCredentials();
    }

    public boolean login() throws Exception {

        LOG.info("Logging in WallHaven...");

        if (!this.hasCredentials()) {
            LOG.info("Logging cancelled, user has not provided credentials.\n");
            return false;
        }

        Map<String, char[]> auth = Security.decryptedData(this.credentials);

        // Login to page
        Response response = Jsoup.connect(LOGIN_URL)
                                 .data("username", new String(auth.get("username")),
                                       "password", new String(auth.get("password")))
                                 .method(Connection.Method.POST)
                                 .userAgent(USER_AGENT)
                                 .execute();

        auth.clear();

        if (response.statusCode() == 200) {

            LOG.info("Logged in successfully.\n");

            // This is the real deal
            this.cookies = response.cookies();
            return true;
        }

        return false;
    }

    public void credentials(String username, String password) {

        Map<String, char[]> auth = new HashMap<>();
        auth.put("username", username.toCharArray());
        auth.put("password", password.toCharArray());

        this.settingsService.setCredentials(auth);
        this.credentials = this.settingsService.getCredentials();
    }

    public boolean hasCredentials() {

        return this.settingsService.getCredentials() != null;
    }

    public Map<String, String> getCookies() {

        return cookies;
    }
}
