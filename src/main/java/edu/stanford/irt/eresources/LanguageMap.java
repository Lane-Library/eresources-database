package edu.stanford.irt.eresources;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class LanguageMap {

    private static String FILENAME = "languages.properties";

    private final Properties properties = new Properties();

    public LanguageMap() {
        File file = new File(FILENAME);
        try (InputStream in = new FileInputStream(file)) {
            this.properties.load(in);
        } catch (IOException e) {
            throw new EresourceDatabaseException(e);
        }
    }

    public String getLanguage(final String code) {
        return this.properties.getProperty(code, null);
    }
}
