package fi.kennyhei.wallsafe.service.concurrent;

import fi.kennyhei.wallsafe.WallSafeFactory;
import fi.kennyhei.wallsafe.service.DownloaderService;

import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;

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
}
