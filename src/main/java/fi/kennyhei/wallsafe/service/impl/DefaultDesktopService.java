package fi.kennyhei.wallsafe.service.impl;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;
import fi.kennyhei.wallsafe.concurrent.service.ScheduledDesktopService;
import fi.kennyhei.wallsafe.service.DesktopService;
import fi.kennyhei.wallsafe.util.User32;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.WildcardFileFilter;

public class DefaultDesktopService extends AbstractBackgroundService implements DesktopService {

    // Contains filepaths for all the wallpapers user has used as desktop background
    private final List<String> history;
    private int historyIndex = -1;

    private String currentFilePath;
    private String currentKeyword;

    public DefaultDesktopService() {

        super(new ScheduledDesktopService(), new DefaultSettingsService());

        this.history = new ArrayList<>();
    }

    @Override
    public void changeWallpaper(String path) {

            System.out.println("Changing wallpaper to: " + path);
            User32.INSTANCE.SystemParametersInfo(0x0014, 0, path, 1);

            String mode = this.settingsService.getDesktopMode();
            this.updateDesktopMode(mode);
            /*        SPI.INSTANCE.SystemParametersInfo(
            new WinDef.UINT_PTR(SPI.SPI_SETDESKWALLPAPER),
            new WinDef.UINT_PTR(0),
            path,
            new WinDef.UINT_PTR(SPI.SPIF_UPDATEINIFILE | SPI.SPIF_SENDWININICHANGE));*/
    }

    @Override
    public void changeToNext() {

        // Change from history
        if (this.historyIndex != this.history.size() - 1) {

            this.historyIndex += 1;
            this.changeFromHistory();
            return;
        }

        // Change to a new wallpaper
        String keyword = this.settingsService.getRandomKeyword();
        int index = this.settingsService.getIndexOfKeyword(keyword);

        this.changeToIndex(index + 1, keyword);
    }

    @Override
    public void changeToPrevious() {

        // Change from history
        if (this.historyIndex > 0) {

            this.historyIndex -= 1;
            this.changeFromHistory();
            return;
        }

        // Change to a new wallpaper
        String keyword = this.settingsService.getRandomKeyword();
        int index = this.settingsService.getIndexOfKeyword(keyword);

        this.changeToIndex(index - 1, keyword);
    }

    private void changeToIndex(int index, String keyword) {

        // User hasn't specified any keywords
        if (keyword == null) {
            keyword = "random";
        }

        // Select wallpaper and return filepath of the selected wallpaper
        String path = selectWallpaper(index, keyword);

        if (path == null) {
            return;
        }

        // New wallpaper is exactly the same that's currently
        // as desktop background, pick a new one
        if (path.equals(this.currentFilePath)) {

            this.changeToNext();
            return;
        }

        this.currentKeyword = keyword;
        this.currentFilePath = path;

        // Add selected wallpaper to history
        this.history.add(path);
        this.historyIndex = this.history.size() - 1;

        this.changeWallpaper(path);
    }

    private String selectWallpaper(int index, String keyword) {

        String path = this.settingsService.getDirectoryPath();
        path += "\\" + keyword;

        System.out.println("Selecting wallpaper from: " + path);

        File directory = new File(path);

        if (!directory.exists()) {
            return null;
        }

        FileFilter fileFilter = new WildcardFileFilter("wallhaven-*");
        File[] wallpapers = directory.listFiles(fileFilter);

        if (wallpapers.length == 0) {
            return null;
        }

        if (index >= wallpapers.length) {
            index = 0;
        }

        if (index < 0) {
            index = wallpapers.length - 1;
        }

        this.settingsService.setIndexOfKeyword(keyword, index);

        Arrays.sort(wallpapers, LastModifiedFileComparator.LASTMODIFIED_COMPARATOR);
        path += "\\" + wallpapers[index].getName();

        return path;
    }

    private void changeFromHistory() {

        this.currentFilePath = this.history.get(this.historyIndex);

        File file = new File(this.currentFilePath);

        if (!file.exists()) {
            this.deleteWallpaper();
            return;
        }

        this.changeWallpaper(this.currentFilePath);
    }

    @Override
    public void resetIndex() {

        Map<String, Integer> keywords = this.settingsService.getKeywords();

        for (String key : keywords.keySet()) {
            this.settingsService.setIndexOfKeyword(key, -1);
        }
    }

    @Override
    public void deleteWallpaper() {

        if (this.currentFilePath == null) {
            // This is cheating
            this.changeToNext();
        }

        // Remove file
        File file = new File(this.currentFilePath);
        file.delete();

        // Remove path of the deleted file from history
        this.history.remove(this.historyIndex);
        this.historyIndex -= 1;

        int index = this.settingsService.getIndexOfKeyword(this.currentKeyword);

        if (index > 0) {
            this.settingsService.setIndexOfKeyword(this.currentKeyword, index - 1);
        }

        this.changeToNext();
    }

    @Override
    public void start() {

        this.scheduledService = new ScheduledDesktopService();

        int interval = this.settingsService.getChangeIntervalValue();
        String timeUnit = this.settingsService.getChangeIntervalTimeunit();

        this.setInterval(interval, timeUnit);
        this.scheduledService.restart();
        this.setIsRunning(true);
    }

    @Override
    public void updateInterval() {

        int interval = this.settingsService.getChangeIntervalValue();
        String timeUnit = this.settingsService.getChangeIntervalTimeunit();

        this.setInterval(interval, timeUnit);
        this.scheduledService.restart();
    }

    @Override
    public void updateDesktopMode(String mode) {

        /* WallpaperStyle in CMD:
           REG ADD "HKCU\Control Panel\Desktop" /V WallpaperStyle /T REG_SZ /F /D 6
           RUNDLL32.EXE USER32.DLL,UpdatePerUserSystemParameters 1, True

           0: Image is centered
           2: Image is stretched
           6: Image is resized to fit the screen while maintaining aspect ratio
           10: Image is resized and cropped to fill the screen while maintaining aspect ratio
        */
        this.settingsService.setDesktopMode(mode);

        switch(mode) {
            case "Fill":
                mode = "10";
                break;
            case "Fit":
                mode = "6";
                break;
            case "Stretch":
                mode = "2";
                break;
            case "Center":
                mode = "0";
                break;
        }

        try {

            Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER, "Control Panel\\Desktop", "WallpaperStyle", "6");
            Runtime.getRuntime().exec("RUNDLL32.EXE USER32.DLL,UpdatePerUserSystemParameters 1, True");
        } catch (IOException ex) {
            System.out.println("Couldn't change picture position.");
        }
    }
}
