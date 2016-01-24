package fi.kennyhei.wallsafe.service.impl;

import fi.kennyhei.wallsafe.service.SettingsService;

import javafx.concurrent.ScheduledService;
import javafx.util.Duration;

public abstract class AbstractBackgroundService {

    protected ScheduledService scheduledService;
    protected SettingsService settingsService;

    public AbstractBackgroundService(ScheduledService scheduledService, SettingsService settingsService) {

        this.scheduledService = scheduledService;
        this.settingsService = settingsService;
    }

    public abstract void start();

    public void updateState(Boolean value) {

        if (value == true) {
            this.scheduledService.restart();
        } else {
            this.scheduledService.cancel();
        }
    }

    public abstract void updateInterval();

    protected void setInterval(int interval, String timeUnit) {

        Duration duration = null;

        if (timeUnit.equals("seconds")) {
            duration = Duration.seconds(interval);
        }

        if (timeUnit.equals("minutes")) {
            duration = Duration.minutes(interval);
        }

        if (timeUnit.equals("hours")) {
            duration = Duration.hours(interval);
        }

        this.scheduledService.setPeriod(duration);
        this.scheduledService.setDelay(duration.add(Duration.seconds(5)));
    }
}
