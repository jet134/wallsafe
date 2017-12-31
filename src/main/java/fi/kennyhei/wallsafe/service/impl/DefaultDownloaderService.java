package fi.kennyhei.wallsafe.service.impl;

import fi.kennyhei.wallsafe.concurrent.service.ScheduledDownloadService;
import fi.kennyhei.wallsafe.service.DownloaderService;
import fi.kennyhei.wallsafe.config.Settings;
import fi.kennyhei.wallsafe.service.LoginService;

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

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class DefaultDownloaderService extends AbstractBackgroundService implements DownloaderService {

    private final LoginService loginService;

    public DefaultDownloaderService() {

        super(new ScheduledDownloadService(), new DefaultSettingsService());
        this.loginService = DefaultLoginService.getInstance();
    }

    @Override
    public void download() {

        // TODO: Add option for user to use keywords or download a completely random image
        final String keyword = this.settingsService.getRandomKeyword();

        if (keyword == null) {
            this.settingsService.buildUrl();
        } else {
            this.settingsService.buildUrl(keyword);
        }

        Task<Void> task = new Task<Void>() {

            @Override
            protected Void call() throws Exception {

                System.out.println(settingsService.url());
                Response response = Jsoup.connect(settingsService.url())
                                         .cookies(loginService.getCookies())
                                         .userAgent(Settings.USER_AGENT)
                                         .timeout(10000)
                                         .execute();

                System.out.println(response.statusCode() + " " + response.statusMessage());
                Document doc = response.parse();

                String url = getRandomImageLink(doc);
                String filename = parseFilename(url);

                downloadImage(url, keyword, filename);
                return null;
            }
        };

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private String getRandomImageLink(Document document) throws IOException {

        Elements links = document.select("a.preview");
        System.out.println(links.size());

        Random r = new Random();

        Element link = links.get(r.nextInt(links.size()));

        System.out.println(link.attr("abs:href"));
        Response response = Jsoup.connect(link.attr("abs:href"))
                                 .cookies(loginService.getCookies())
                                 .userAgent(Settings.USER_AGENT)
                                 .timeout(10000)
                                 .execute();

        System.out.println(response.statusCode() + " " + response.statusMessage());
        document = response.parse();

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

    private void downloadImage(String url, String keyword, String filename) throws IOException {

        createKeywordDirectory(keyword);
        String path = this.settingsService.getDirectoryPath() + "\\" + keyword + "\\" + filename;

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
