package fi.kennyhei.wallsafe.service.impl;

import fi.kennyhei.wallsafe.service.SettingsService;
import fi.kennyhei.wallsafe.model.Settings;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class DefaultSettingsService implements SettingsService {

    private final Settings settings;

    public DefaultSettingsService() {

        this.settings = Settings.getInstance();
    }

    @Override
    public void setResolution(String resolution) {

        settings.setResolution(resolution);
    }

    @Override
    public void addKeyword(String keyword) {

        settings.getKeywords().add(keyword);
    }

    @Override
    public void removeKeyword(String keyword) {

        List<String> keywords = settings.getKeywords();
        Iterator<String> keywordIterator = keywords.iterator();

        while (keywordIterator.hasNext()) {

            String value = keywordIterator.next();

            if (value.equals(keyword)) {
                keywordIterator.remove();
                break;
            }
        }
    }

    @Override
    public String getRandomKeyword() {

        Random r = new Random();

        List<String> keywords = settings.getKeywords();
        int index = r.nextInt(keywords.size());

        return keywords.get(index);
    }

    @Override
    public void buildUrl() {

        settings.buildURL();
    }

    @Override
    public void buildUrl(String keyword) {

        settings.buildURL(keyword);
    }

    @Override
    public String url() {

        return settings.getURL();
    }

    @Override
    public int getChangeIntervalValue() {

        return settings.getChangeIntervalValue();
    }

    @Override
    public void setChangeIntervalValue(int value) {

        settings.setChangeIntervalValue(value);
    }

    @Override
    public String getChangeIntervalTimeunit() {

        return settings.getChangeIntervalTimeunit();
    }

    @Override
    public void setChangeIntervalTimeunit(String value) {

        settings.setChangeIntervalTimeunit(value);
    }

    @Override
    public int getDownloadIntervalValue() {

        return settings.getDownloadIntervalValue();
    }

    @Override
    public void setDownloadIntervalValue(int value) {

        settings.setDownloadIntervalValue(value);
    }

    @Override
    public String getDownloadIntervalTimeunit() {

        return settings.getDownloadIntervalTimeunit();
    }

    @Override
    public void setDownloadIntervalTimeunit(String value) {

        settings.setDownloadIntervalTimeunit(value);
    }

    @Override
    public String getDirectoryPath() {

        return settings.getDirectoryPath();
    }

    @Override
    public void setDirectoryPath(String selectedDirectory) {

        settings.setDirectoryPath(selectedDirectory);
    }

    @Override
    public int getIndexOfCurrentWallpaper() {

        return settings.getIndexOfCurrentWallpaper();
    }

    @Override
    public void setIndexOfCurrentWallpaper(int value) {

        settings.setIndexOfCurrentWallpaper(value);
    }
}
