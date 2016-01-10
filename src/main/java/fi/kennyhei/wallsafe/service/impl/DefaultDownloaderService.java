package fi.kennyhei.wallsafe.service.impl;

import fi.kennyhei.wallsafe.service.DesktopService;
import fi.kennyhei.wallsafe.service.DownloaderService;
import fi.kennyhei.wallsafe.service.SettingsService;
import fi.kennyhei.wallsafe.model.Settings;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import javafx.concurrent.Task;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class DefaultDownloaderService implements DownloaderService {

    private final SettingsService settingsService;
    private final DesktopService desktopService;

    // Name of the latest downloaded file
    private String latestFilename;

    public DefaultDownloaderService() {

        this.settingsService = new DefaultSettingsService();
        this.desktopService = new DefaultDesktopService();
    }

    @Override
    public void download() {

        Task<Void> task = new Task<Void>() {

            @Override
            protected Void call() throws Exception {

                System.out.println(settingsService.URL());
                Document doc = Jsoup.connect(settingsService.URL()).userAgent(Settings.USER_AGENT).get();

                String url = getRandomImageLink(doc);
                String filename = parseFilename(url);
                latestFilename = filename;

                String path = System.getProperty("user.home") + "\\Desktop\\Wallpapers\\" + filename;
                System.out.println(path);
                System.out.println();

                downloadImage(url, path);

                return null;
            }
        };

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public String getLatestFilename() {

        return this.latestFilename;
    }

    private static String getRandomImageLink(Document document) throws IOException {

        Elements links = document.select("a.preview");
        Random r = new Random();

        Element link = links.get(r.nextInt(links.size()));

        System.out.println(link.attr("abs:href"));
        document = Jsoup.connect(link.attr("abs:href")).userAgent(Settings.USER_AGENT).get();

        Element image = document.select("img#wallpaper").first();
        String url = image.attr("abs:src");
        System.out.println(url);

        return url;
    }

    private static String parseFilename(String imageURL) {

        String[] parts = imageURL.split("/");
        String filename = parts[parts.length - 1];

        parts = filename.split("\\.");

        String extension = parts[1];
        if (extension.contains("?")) {

            extension = extension.substring(0, extension.indexOf("?"));
        }

        filename = parts[0] + "." + extension;
        return filename;
    }

    private static void downloadImage(String url, String path) throws IOException {

        byte[] bytes = Jsoup.connect(url).userAgent(Settings.USER_AGENT).ignoreContentType(true).execute().bodyAsBytes();

        FileOutputStream fos = new FileOutputStream(new File(path));
        fos.write(bytes);
        fos.close();
    }
}
