package fi.kennyhei.wallsafe.service.impl;

import fi.kennyhei.wallsafe.concurrent.service.ScheduledDownloadService;
import fi.kennyhei.wallsafe.service.DownloaderService;
import fi.kennyhei.wallsafe.service.SettingsService;
import fi.kennyhei.wallsafe.model.Settings;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import javafx.concurrent.Task;
import javafx.util.Duration;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class DefaultDownloaderService implements DownloaderService {

    private ScheduledDownloadService scheduledDownloadService;
    private final SettingsService settingsService;

    public DefaultDownloaderService() {

        this.settingsService = new DefaultSettingsService();
    }

    @Override
    public void download() {

        // TODO: Add option for user to use keywords or download a completely random image
        String keyword = this.settingsService.getRandomKeyword();
        this.settingsService.buildUrl(keyword);

        Task<Void> task = new Task<Void>() {

            @Override
            protected Void call() throws Exception {

                System.out.println(settingsService.url());
                Document doc = Jsoup.connect(settingsService.url()).userAgent(Settings.USER_AGENT).get();

                String url = getRandomImageLink(doc);
                String filename = parseFilename(url);

                downloadImage(url, filename);

                return null;
            }
        };

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private String getRandomImageLink(Document document) throws IOException {

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

    private String parseFilename(String imageURL) {

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

    private void downloadImage(String url, String filename) throws IOException {

        byte[] bytes = Jsoup.connect(url).userAgent(Settings.USER_AGENT).ignoreContentType(true).execute().bodyAsBytes();

        String filepath = this.settingsService.getDirectoryPath() + "\\" + filename;

        System.out.println(filepath);
        System.out.println();

        FileOutputStream fos = new FileOutputStream(new File(filepath));
        fos.write(bytes);
        fos.close();
    }

    @Override
    public void start() {

        this.scheduledDownloadService = new ScheduledDownloadService();

        int interval = this.settingsService.getDownloadIntervalValue();
        String timeUnit = this.settingsService.getDownloadIntervalTimeunit();

        this.setInterval(interval, timeUnit);
        this.scheduledDownloadService.start();
    }

    @Override
    public void updateState(Boolean value) {

        if (value == true) {
            this.scheduledDownloadService.start();
        } else {
            this.scheduledDownloadService.cancel();
        }
    }

    @Override
    public void updateInterval() {

        int interval = this.settingsService.getDownloadIntervalValue();
        String timeUnit = this.settingsService.getDownloadIntervalTimeunit();

        this.setInterval(interval, timeUnit);
        this.scheduledDownloadService.restart();
    }

    private void setInterval(int interval, String timeUnit) {

        Duration duration = null;

        if (timeUnit.equals("seconds")) {
            duration = Duration.seconds(interval);
        }

        if (timeUnit.equals("minutes")) {
            duration = Duration.minutes(interval);
        }

        if (timeUnit.equals("hours")) {
            duration = Duration.hours(interval);
        }

        this.scheduledDownloadService.setPeriod(duration);
        this.scheduledDownloadService.setDelay(duration.add(Duration.seconds(5)));
    }
}
