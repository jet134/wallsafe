package fi.kennyhei.wallsafe.service.impl;

import fi.kennyhei.wallsafe.concurrent.service.ScheduledDownloadService;
import fi.kennyhei.wallsafe.service.DownloaderService;
import fi.kennyhei.wallsafe.model.Settings;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import javafx.concurrent.Task;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class DefaultDownloaderService extends AbstractBackgroundService implements DownloaderService {

    private String keyword;

    public DefaultDownloaderService() {

        super(new ScheduledDownloadService(), new DefaultSettingsService());
    }

    @Override
    public void download() {

        // TODO: Add option for user to use keywords or download a completely random image
        this.keyword = this.settingsService.getRandomKeyword();

        if (this.keyword == null) {
            this.settingsService.buildUrl();
            this.keyword = "random";
        } else {
            this.settingsService.buildUrl(keyword);
        }

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

        createKeywordDirectory(this.keyword);
        String path = this.settingsService.getDirectoryPath() + "\\" + this.keyword + "\\" + filename;

        System.out.println(path);
        System.out.println();

        File file = new File(path);

        HttpClient httpclient = HttpClients.createDefault();
        HttpGet get = new HttpGet(url);
        get.addHeader("User-Agent", Settings.USER_AGENT);

        HttpResponse response = httpclient.execute(get);
        HttpEntity entity = response.getEntity();

        if (entity != null) {
            FileUtils.copyInputStreamToFile(entity.getContent(), file);
        }
    }

    private void createKeywordDirectory(String keyword) {

        String path = this.settingsService.getDirectoryPath() + "\\" + keyword;
        File keywordDirectory = new File(path);

        if (keywordDirectory.exists()) {
            return;
        }

        keywordDirectory.mkdirs();
    }

    @Override
    public void start() {

        int interval = this.settingsService.getDownloadIntervalValue();
        String timeUnit = this.settingsService.getDownloadIntervalTimeunit();

        this.setInterval(interval, timeUnit);
        this.scheduledService.restart();
        this.setIsRunning(true);
    }

    @Override
    public void updateInterval() {

        int interval = this.settingsService.getDownloadIntervalValue();
        String timeUnit = this.settingsService.getDownloadIntervalTimeunit();

        this.setInterval(interval, timeUnit);
        this.scheduledService.restart();
    }
}
