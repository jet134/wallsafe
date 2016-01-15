package fi.kennyhei.wallsafe.service;

import java.io.File;

public interface DesktopService extends BackgroundService {

    public void setDirectory(File selectedDirectory);
    public void changeWallpaper(String path);
    public void changeToNext();

}
