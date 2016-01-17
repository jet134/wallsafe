package fi.kennyhei.wallsafe.service;

public interface SettingsService {

    public String url();
    public void buildUrl();
    public void buildUrl(String keyword);

    public void setResolution(String resolution);

    public void addKeyword(String keyword);
    public void removeKeyword(String keyword);
    public String getRandomKeyword();

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

    public int getIndexOfCurrentWallpaper();
    public void setIndexOfCurrentWallpaper(int value);
}
