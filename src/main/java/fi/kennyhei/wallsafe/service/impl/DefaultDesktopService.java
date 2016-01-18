package fi.kennyhei.wallsafe.service.impl;

import com.sun.jna.platform.win32.WinDef;

import fi.kennyhei.wallsafe.concurrent.service.ScheduledDesktopService;
import fi.kennyhei.wallsafe.util.SPI;
import fi.kennyhei.wallsafe.service.DesktopService;
import fi.kennyhei.wallsafe.service.SettingsService;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import javafx.util.Duration;

import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.WildcardFileFilter;

public class DefaultDesktopService implements DesktopService {

    private ScheduledDesktopService scheduledDesktopService;
    private final SettingsService settingsService;

    public DefaultDesktopService() {

        this.settingsService = new DefaultSettingsService();
    }

    @Override
    public void changeWallpaper(String path) {

        SPI.INSTANCE.SystemParametersInfo(
                new WinDef.UINT_PTR(SPI.SPI_SETDESKWALLPAPER),
                new WinDef.UINT_PTR(0),
                path,
                new WinDef.UINT_PTR(SPI.SPIF_UPDATEINIFILE | SPI.SPIF_SENDWININICHANGE));
    }

    @Override
    public void changeToNext() {

        String path = this.settingsService.getDirectoryPath();
        int index = this.settingsService.getIndexOfCurrentWallpaper();

        File directory = new File(path);

        FileFilter fileFilter = new WildcardFileFilter("wallhaven-*");
        File[] wallpapers = directory.listFiles(fileFilter);

        if (wallpapers.length == 0) {
            return;
        }

        if (index >= wallpapers.length) {
            index = 0;
        }

        Arrays.sort(wallpapers, LastModifiedFileComparator.LASTMODIFIED_COMPARATOR);
        path += "\\" + wallpapers[index].getName();

        this.changeWallpaper(path);

        ++index;
        this.settingsService.setIndexOfCurrentWallpaper(index);
    }

    @Override
    public void resetIndex() {

        this.settingsService.setIndexOfCurrentWallpaper(0);
    }

    @Override
    public void start() {

        this.scheduledDesktopService = new ScheduledDesktopService();

        int interval = this.settingsService.getChangeIntervalValue();
        String timeUnit = this.settingsService.getChangeIntervalTimeunit();

        this.setInterval(interval, timeUnit);
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
    public void updateInterval() {

        int interval = this.settingsService.getChangeIntervalValue();
        String timeUnit = this.settingsService.getChangeIntervalTimeunit();

        this.setInterval(interval, timeUnit);
    }

    private void setInterval(int interval, String timeUnit) {

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
        this.scheduledDesktopService.setDelay(duration);
        this.scheduledDesktopService.restart();
    }
}
