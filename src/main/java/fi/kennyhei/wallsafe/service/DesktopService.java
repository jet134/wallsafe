package fi.kennyhei.wallsafe.service;

public interface DesktopService extends BackgroundService {

    public void resetIndex();
    public void changeWallpaper(String path);
    public void deleteWallpaper();
    public void changeToNext();
}
