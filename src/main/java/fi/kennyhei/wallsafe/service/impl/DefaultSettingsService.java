package fi.kennyhei.wallsafe.service.impl;

import fi.kennyhei.wallsafe.service.SettingsService;
import fi.kennyhei.wallsafe.model.Settings;

import java.util.Iterator;
import java.util.List;

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

        List<String> tags = settings.getKeywords();
        Iterator<String> keywordIterator = tags.iterator();

        while (keywordIterator.hasNext()) {

            String value = keywordIterator.next();

            if (value.equals(value)) {
                keywordIterator.remove();
                break;
            }
        }
    }

    @Override
    public String URL() {

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
}
