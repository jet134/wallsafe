package fi.kennyhei.wallsafe.service;

import fi.kennyhei.wallsafe.service.concurrent.ScheduledDownloadService;
import fi.kennyhei.wallsafe.config.Settings;

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
import org.apache.log4j.Logger;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class DownloaderService extends AbstractBackgroundService implements BackgroundService {

    private static final Logger LOG = Logger.getLogger(DownloaderService.class);

    private final LoginService loginService;

    public DownloaderService() {

        super(new ScheduledDownloadService(), new SettingsService());
        this.loginService = LoginService.getInstance();
    }

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

                String downloadUrl = settingsService.url();
                String query = downloadUrl.substring(downloadUrl.indexOf("?"));

                LOG.info("Starting download process using query:");
                LOG.info(query);
                Response response = Jsoup.connect(settingsService.url())
                                         .cookies(loginService.getCookies())
                                         .userAgent(Settings.USER_AGENT)
                                         .timeout(10000)
                                         .execute();

                LOG.info("Request returned: " + response.statusCode() + " " + response.statusMessage());
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
        LOG.info("Found " + links.size() + " images");

        Random r = new Random();

        Element link = links.get(r.nextInt(links.size()));

        LOG.info("Selected image " + link.attr("abs:href"));
        Response response = Jsoup.connect(link.attr("abs:href"))
                                 .cookies(loginService.getCookies())
                                 .userAgent(Settings.USER_AGENT)
                                 .timeout(10000)
                                 .execute();

        document = response.parse();

        Element image = document.select("img#wallpaper").first();
        String url = image.attr("abs:src");

        LOG.info("Downloading image: " + url);

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

        LOG.info("Saving file to: " + path + "\n");

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

        int interval = this.settingsService.getIntervalValue("download");
        String timeUnit = this.settingsService.getIntervalTimeunit("download");

        this.setInterval(interval, timeUnit);
        this.scheduledService.restart();
        this.setIsRunning(true);
    }

    @Override
    public void updateInterval() {

        int interval = this.settingsService.getIntervalValue("download");
        String timeUnit = this.settingsService.getIntervalTimeunit("download");

        this.setInterval(interval, timeUnit);
        this.scheduledService.restart();
    }
}
