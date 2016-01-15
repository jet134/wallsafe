package fi.kennyhei.wallsafe.service;

import java.io.File;

public interface DownloaderService extends BackgroundService {

    public void download();
    public String getLatestFilename();
    public void setDirectory(File selectedDirectory);

}
