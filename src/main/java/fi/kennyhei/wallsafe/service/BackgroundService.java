package fi.kennyhei.wallsafe.service;

import javafx.beans.property.BooleanProperty;

/**
 *
 * Classes which implement this interface perform scheduled background tasks.
 */
public interface BackgroundService {

    public void start();
    public void updateState(Boolean onOff);
    public void updateInterval();
    public BooleanProperty isRunningProperty();
}
