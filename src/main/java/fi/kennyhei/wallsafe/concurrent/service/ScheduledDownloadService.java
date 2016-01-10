package fi.kennyhei.wallsafe.concurrent.service;

import fi.kennyhei.wallsafe.WallSafeFactory;
import fi.kennyhei.wallsafe.service.DownloaderService;

import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.util.Duration;

public class ScheduledDownloadService extends ScheduledService {

    @Override
    protected Task createTask() {

        DownloaderService downloaderService = WallSafeFactory.getDownloaderService();

        return new Task<Void>() {

            @Override
            protected Void call() throws Exception {

                downloaderService.download();
                return null;
            }
        };
    }

    public void initialize() {
        Duration duration = Duration.seconds(60);

        this.setPeriod(duration);
        this.start();
    }

    public void updateInterval(int value, String timeUnit) {

        Duration duration = null;

        if (timeUnit.equals("seconds")) {
            duration = Duration.seconds(value);
        }

        if (timeUnit.equals("minutes")) {
            duration = Duration.minutes(value);
        }

        if (timeUnit.equals("hours")) {
            duration = Duration.hours(value);
        }

        this.setPeriod(duration);

        // In case if user is still changing the interval value in UI
        this.setDelay(Duration.seconds(5));
        this.restart();
    }

    public void updateState(Boolean value) {

        if (value == true) {
            this.start();
        } else {
            this.cancel();
        }
    }
}
