package fi.kennyhei.wallsafe.service.impl;

import fi.kennyhei.wallsafe.service.LoginService;
import fi.kennyhei.wallsafe.service.SettingsService;
import fi.kennyhei.wallsafe.security.Security;

import java.util.HashMap;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;

public class DefaultLoginService implements LoginService {

    // LoginService is a singleton class
    private static DefaultLoginService instance = null;
    private final SettingsService settingsService;

    private Map<String, String> cookies;
    private static final String LOGIN_URL = "https://alpha.wallhaven.cc/auth/login";
    private static final String USER_AGENT = "Mozilla/5.0";

    private String credentials;

    public static DefaultLoginService getInstance() {

        if (instance == null) {
            instance = new DefaultLoginService();
        }

        return instance;
    }

    public DefaultLoginService() {

        this.cookies = new HashMap<>();

        this.settingsService = new DefaultSettingsService();
        this.credentials = this.settingsService.getCredentials();
    }

    @Override
    public boolean login() throws Exception {

        System.out.println("Logging in WallHaven...");

        if (!this.hasCredentials()) {
            System.out.println("Logging cancelled, user has not provided credentials.\n");
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

            System.out.println("Logged in successfully.\n");

            // This is the real deal
            this.cookies = response.cookies();
            return true;
        }

        return false;
    }

    @Override
    public void credentials(String username, String password) {

        Map<String, char[]> auth = new HashMap<>();
        auth.put("username", username.toCharArray());
        auth.put("password", password.toCharArray());

        this.settingsService.setCredentials(auth);
        this.credentials = this.settingsService.getCredentials();
    }

    @Override
    public boolean hasCredentials() {

        return this.settingsService.getCredentials() != null;
    }

    @Override
    public Map<String, String> getCookies() {

        return cookies;
    }
}
