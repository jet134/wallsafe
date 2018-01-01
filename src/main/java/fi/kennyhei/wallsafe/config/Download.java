package fi.kennyhei.wallsafe.config;

import java.util.prefs.Preferences;

public class Download extends AbstractAction {

    public Download(Preferences preferences) {

        super(preferences);
    }

    @Override
    public void saveSettings() {

        this.preferences.putInt(Option.WS_DOWNLOAD_INTERVAL_VALUE, this.intervalValue);
        this.preferences.put(Option.WS_DOWNLOAD_INTERVAL_TIMEUNIT, this.intervalTimeunit);
    }

    @Override
    public void loadSettings() {

        this.intervalValue = preferences.getInt(Option.WS_DOWNLOAD_INTERVAL_VALUE, 20);
        this.intervalTimeunit = preferences.get(Option.WS_DOWNLOAD_INTERVAL_TIMEUNIT, "seconds");
    }
}
