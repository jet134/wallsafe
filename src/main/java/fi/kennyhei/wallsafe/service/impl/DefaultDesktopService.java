package fi.kennyhei.wallsafe.service.impl;

import com.sun.jna.platform.win32.WinDef;

import fi.kennyhei.wallsafe.concurrent.service.ScheduledDesktopService;
import fi.kennyhei.wallsafe.util.SPI;
import fi.kennyhei.wallsafe.service.DesktopService;

import java.io.File;
import java.util.Arrays;
import javafx.util.Duration;

import org.apache.commons.io.comparator.LastModifiedFileComparator;

public class DefaultDesktopService implements DesktopService {

    private ScheduledDesktopService scheduledDesktopService;
    private String path = System.getProperty("user.home") + "\\Desktop\\Wallpapers";

    // Index of the current wallpaper
    private int currentIndex = 0;

    @Override
    public void changeWallpaper(String filename) {

        SPI.INSTANCE.SystemParametersInfo(
                new WinDef.UINT_PTR(SPI.SPI_SETDESKWALLPAPER),
                new WinDef.UINT_PTR(0),
                path + "\\" + filename,
                new WinDef.UINT_PTR(SPI.SPIF_UPDATEINIFILE | SPI.SPIF_SENDWININICHANGE));
    }

    @Override
    public void changeToNext() {

        File directory = new File(path);
        File[] wallpapers = directory.listFiles();

        if (wallpapers.length == 0) {
            return;
        }

        if (currentIndex >= wallpapers.length) {
            currentIndex = wallpapers.length - 1;
        }

        Arrays.sort(wallpapers, LastModifiedFileComparator.LASTMODIFIED_COMPARATOR);
        this.changeWallpaper(wallpapers[currentIndex].getName());

        ++currentIndex;
    }

    @Override
    public void setDirectory(File selectedDirectory) {

        this.path = selectedDirectory.getAbsolutePath();
        this.currentIndex = 0;
    }

    @Override
    public void start() {

        this.scheduledDesktopService = new ScheduledDesktopService();
        Duration duration = Duration.seconds(60);

        this.scheduledDesktopService.setPeriod(duration);
        this.scheduledDesktopService.setDelay(duration);
        this.scheduledDesktopService.start();
    }

    @Override
    public void updateState(Boolean value) {

        if (value == true) {
            this.scheduledDesktopService.start();
        } else {
            this.scheduledDesktopService.cancel();
        }
    }

    @Override
    public void updateInterval(int interval, String timeUnit) {

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

        this.scheduledDesktopService.setPeriod(duration);

        // Small delay in case if user is still changing the interval value in UI
        this.scheduledDesktopService.setDelay(Duration.seconds(5));
        this.scheduledDesktopService.restart();
    }
}
