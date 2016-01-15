package fi.kennyhei.wallsafe.service;

public interface SettingsService {

    public String URL();
    public void setResolution(String resolution);

    public void addKeyword(String keyword);
    public void removeKeyword(String keyword);

    public int getChangeIntervalValue();
    public void setChangeIntervalValue(int value);

    public String getChangeIntervalTimeunit();
    public void setChangeIntervalTimeunit(String value);

    public int getDownloadIntervalValue();
    public void setDownloadIntervalValue(int value);

    public String getDownloadIntervalTimeunit();
    public void setDownloadIntervalTimeunit(String value);

    public String getDirectoryPath();
    public void setDirectoryPath(String selectedDirectory);
}
