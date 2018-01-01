package fi.kennyhei.wallsafe.config;

import java.util.prefs.Preferences;

public abstract class AbstractAction {

    protected int intervalValue;
    protected String intervalTimeunit;
    protected boolean isEnabled;
    protected final Preferences preferences;

    public AbstractAction(Preferences preferences) {

        this.preferences = preferences;
    }

    public int getIntervalValue() {

        return intervalValue;
    }

    public void setIntervalValue(int intervalValue) {

        this.intervalValue = intervalValue;
    }

    public String getIntervalTimeunit() {

        return intervalTimeunit;
    }

    public void setIntervalTimeunit(String intervalTimeunit) {

        this.intervalTimeunit = intervalTimeunit;
    }

    public boolean isEnabled() {

        return isEnabled;
    }

    public void isEnabled(boolean isEnabled) {

        this.isEnabled = isEnabled;
    }

    public abstract void saveSettings();
    public abstract void loadSettings();
}
