package fi.kennyhei.wallsafe.service;

public interface DownloaderService extends BackgroundService {

    public void download();
    public String getLatestFilename();
}
