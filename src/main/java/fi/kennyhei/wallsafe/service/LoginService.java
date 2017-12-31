package fi.kennyhei.wallsafe.service;

import java.util.Map;

public interface LoginService {

    public void credentials(String username, String password);
    public boolean login() throws Exception;
    public Map<String, String> getCookies();
    public boolean hasCredentials();
}
