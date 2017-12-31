package fi.kennyhei.wallsafe.service;

import java.util.List;
import java.util.Map;

public interface SettingsService {

    public String url();
    public void buildUrl();
    public void buildUrl(String keyword);

    public String getResolution();
    public void setResolution(String resolution);

    public void addKeyword(String keyword);
    public void removeKeyword(String keyword);

    public List<String> getPlainKeywords();
    public Map<String, Integer> getKeywords();
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

    public void setIndexOfKeyword(String keyword, int index);
    public int getIndexOfKeyword(String currentKeyword);

    public void setFilter(String text, boolean isSelected);
    public boolean isFilterSelected(String text);

    public String getDesktopMode();
    public void setDesktopMode(String mode);
}
