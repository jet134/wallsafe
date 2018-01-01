package fi.kennyhei.wallsafe.config;

import java.util.prefs.Preferences;

public class Change extends AbstractAction {

    public Change(Preferences preferences) {

        super(preferences);
    }

    @Override
    public void saveSettings() {

        this.preferences.putInt(Option.WS_CHANGE_INTERVAL_VALUE, this.intervalValue);
        this.preferences.put(Option.WS_CHANGE_INTERVAL_TIMEUNIT, this.intervalTimeunit);
    }

    @Override
    public void loadSettings() {

        this.intervalValue = preferences.getInt(Option.WS_CHANGE_INTERVAL_VALUE, 60);
        this.intervalTimeunit = preferences.get(Option.WS_CHANGE_INTERVAL_TIMEUNIT, "seconds");
    }

}
