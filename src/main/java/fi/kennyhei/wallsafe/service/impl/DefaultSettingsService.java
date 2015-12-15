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
    public void addTag(String tag) {
        settings.getTags().add(tag);
    }

    @Override
    public void removeTag(String deleteTag) {
        List<String> tags = settings.getTags();

        Iterator<String> tagsIterator = tags.iterator();

        while (tagsIterator.hasNext()) {

            String tag = tagsIterator.next();

            if (tag.equals(deleteTag)) {
                tagsIterator.remove();
                break;
            }
        }
    }

    @Override
    public String URL() {

        return settings.getURL();
    }
}
