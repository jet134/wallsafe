package fi.kennyhei.wallsafe.service.impl;

import com.sun.jna.platform.win32.WinDef;

import fi.kennyhei.wallsafe.util.SPI;
import fi.kennyhei.wallsafe.service.DesktopService;

public class DefaultDesktopService implements DesktopService {

    private final String path = System.getProperty("user.home") + "\\Desktop\\Wallpapers\\";

    @Override
    public void changeWallpaper(String filename) {

        SPI.INSTANCE.SystemParametersInfo(
                new WinDef.UINT_PTR(SPI.SPI_SETDESKWALLPAPER),
                new WinDef.UINT_PTR(0),
                path + filename,
                new WinDef.UINT_PTR(SPI.SPIF_UPDATEINIFILE | SPI.SPIF_SENDWININICHANGE));
    }
}
