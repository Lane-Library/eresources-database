package edu.stanford.irt.eresources;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;

public final class IOUtils {

    private IOUtils() {
        // private empty constructor
    }

    public static String getResourceAsString(final Class<?> clazz, final String name) {
        StringBuilder sb = new StringBuilder();
        try (InputStream input = clazz.getResourceAsStream(name);
                InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8);
                BufferedReader b = new BufferedReader(reader)) {
            String s;
            while ((s = b.readLine()) != null) {
                sb.append(s).append('\n');
            }
        } catch (IOException e) {
            throw new EresourceDatabaseException(e);
        }
        return sb.toString();
    }

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
}
