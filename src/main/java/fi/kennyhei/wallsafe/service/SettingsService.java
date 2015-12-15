package fi.kennyhei.wallsafe.service;

public interface SettingsService {

    public String URL();
    public void setResolution(String resolution);
    public void addTag(String tag);
    public void removeTag(String tag);
}
