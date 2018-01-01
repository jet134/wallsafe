package fi.kennyhei.wallsafe.service.concurrent;

import fi.kennyhei.wallsafe.WallSafeFactory;
import fi.kennyhei.wallsafe.service.DesktopService;

import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;

public class ScheduledDesktopService extends ScheduledService {

    @Override
    protected Task createTask() {

        DesktopService desktopService = WallSafeFactory.getDesktopService();

        return new Task<Void>() {

            @Override
            protected Void call() throws Exception {

                desktopService.changeToNext();
                return null;
            }
        };
    }
}
