package fi.kennyhei.wallsafe.concurrent.service;

import fi.kennyhei.wallsafe.WallSafeFactory;
import fi.kennyhei.wallsafe.service.DownloaderService;

import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;

public class ScheduledDownloadService extends ScheduledService {

    // Interval in seconds
    private int interval = 60;

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

    public int getInterval() {

        return interval;
    }

    public void setInterval(int interval) {

        this.interval = interval;
    }

}
