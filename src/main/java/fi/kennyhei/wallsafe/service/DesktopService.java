package fi.kennyhei.wallsafe.service;

public interface DesktopService extends BackgroundService {

    public void changeWallpaper(String path);
    public void changeToLatest();

}
