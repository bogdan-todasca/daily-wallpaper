package com.todasca;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class DailyWallpaper {
    private static final String URL = "https://www.pexels.com/search/HD%20wallpaper/?page=";
    private static final String PAGE = "page";
    private static final int numberOfPages = 55;

    public static void main(String[] args) throws IOException {
        final DailyWallpaper dw = new DailyWallpaper();
        dw.download(URL + new Random().nextInt(numberOfPages), PAGE);
    }

    private void download(final String url, final String localFilename) throws IOException {
        System.out.println(String.format("Downloading from %s ", url));
        System.setProperty("http.agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
        final String result = System.getProperty("java.io.tmpdir") +
                System.getProperty("file.separator") +
                localFilename;
        FileUtils.copyURLToFile(new URL(url),
                new File(result));
        System.out.println(String.format("Saved locally at %s ", result));
        downloadImage(parseImages(result));
    }

    private List<String> parseImages(final String localPath) throws IOException {
        final List<String> urls = new ArrayList<>();
        final Document document = Jsoup.parse(new File(localPath), StandardCharsets.UTF_8.name());
        final Elements elements = document.getElementsByClass("photo-item__img");
        for (Element element : elements) {
            urls.add(element.attr("data-large-src"));
        }
        return urls;
    }

    private void downloadImage(final List<String> urls) throws IOException {
        System.out.println(String.format("Obtained %d images", urls.size()));
        final int index = new Random().nextInt(urls.size());
        System.out.println(String.format("Will download image: %d", index));
        final String result = System.getProperty("java.io.tmpdir") +
                System.getProperty("file.separator") +
                "wallpaper" + new Date();
        FileUtils.copyURLToFile(new URL(urls.get(index)),
                new File(result));
        System.out.println(String.format("Saved image as: %s", result));
        setWallpaper(result);
    }

    private void setWallpaper(final String filePath) throws IOException {
        String as[] = {
                "osascript",
                "-e", "tell application \"Finder\"",
                "-e", "set desktop picture to POSIX file \"" + filePath + "\"",
                "-e", "end tell"
        };
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(as);
    }

}
