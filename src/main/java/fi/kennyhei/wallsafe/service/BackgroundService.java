package fi.kennyhei.wallsafe.service;

/**
 *
 * Classes which implement this interface perform scheduled background tasks.
 */
public interface BackgroundService {

    public void start();
    public void updateState(Boolean onOff);
    public void updateInterval();
}
