package fi.kennyhei.wallsafe.concurrent.service;

import fi.kennyhei.wallsafe.WallSafeFactory;
import fi.kennyhei.wallsafe.service.DesktopService;

import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;

public class ScheduledDesktopService extends ScheduledService {

    // Interval in seconds
    private int interval = 60;

    @Override
    protected Task createTask() {

        DesktopService desktopService = WallSafeFactory.getDesktopService();

        return new Task<Void>() {

            @Override
            protected Void call() throws Exception {

                desktopService.changeToLatest();
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
