package edu.stanford.irt.eresources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpStatus;

public final class IOUtils {

    private static final int POLLING_INTERVAL = 30_000;

    public static InputStream getStream(final URL url) throws IOException {
        InputStream inputStream;
        URLConnection connection = url.openConnection();
        if (HttpURLConnection.class.isAssignableFrom(connection.getClass())) {
            HttpURLConnection httpConnection = HttpURLConnection.class.cast(connection);
            httpConnection.setRequestProperty("Accept-Encoding", "gzip");
            String userInfo = url.getUserInfo();
            if (userInfo != null) {
                String authorization = new StringBuilder("Basic ")
                        .append(Base64.getEncoder().encodeToString(userInfo.getBytes(StandardCharsets.UTF_8)))
                        .toString();
                httpConnection.setRequestProperty("Authorization", authorization);
            }
            if (httpConnection.getResponseCode() == HttpStatus.SC_ACCEPTED) {
                try {
                    Thread.sleep(POLLING_INTERVAL);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new EresourceDatabaseException(e);
                }
                return getStream(url);
            }
            inputStream = httpConnection.getInputStream();
            if ("gzip".equals(httpConnection.getContentEncoding())) {
                inputStream = new GZIPInputStream(inputStream);
            }
        } else {
            inputStream = connection.getInputStream();
        }
        return inputStream;
    }

    public static List<File> getUpdatedFiles(final File directory, final String extension, final long time) {
        File[] files = directory
                .listFiles((final File file) -> file.isDirectory() || file.getName().endsWith(extension));
        List<File> result = new LinkedList<>();
        if (null != files) {
            for (File file : files) {
                if (file.isDirectory()) {
                    result.addAll(getUpdatedFiles(file, extension, time));
                } else if (file.lastModified() >= time) {
                    result.add(file);
                }
            }
        }
        return result;
    }

    private IOUtils() {
        // private empty constructor
    }
}
