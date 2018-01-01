package fi.kennyhei.wallsafe.service;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.ScheduledService;
import javafx.util.Duration;

public abstract class AbstractBackgroundService {

    protected ScheduledService scheduledService;
    protected SettingsService settingsService;

    protected BooleanProperty isRunning;

    public AbstractBackgroundService(ScheduledService scheduledService, SettingsService settingsService) {

        this.isRunning = new SimpleBooleanProperty();
        this.scheduledService = scheduledService;
        this.settingsService = settingsService;

        this.initializeRunningListener();
    }

    private void initializeRunningListener() {

        this.isRunning.addListener(event -> {

            updateState(isRunning());
        });
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

     // Define a getter for the property itself
    public BooleanProperty isRunningProperty() {

        return isRunning;
    }

   // Define a getter for the property's value
    protected final boolean isRunning() {

        return isRunning.get();
    }

    // Define a setter for the property's value
    protected final void setIsRunning(boolean value){

        isRunning.set(value);
    }
}
