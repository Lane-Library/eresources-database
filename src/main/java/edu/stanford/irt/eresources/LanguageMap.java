package edu.stanford.irt.eresources;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class LanguageMap {

    private final Properties properties = new Properties();

    public LanguageMap() {
        try {
            File file = new File("languages.properties");
            this.properties.load(new FileInputStream(file));
        } catch (IOException e) {
            throw new EresourceDatabaseException("missing language properties file", e);
        }
    }

    public String getLanguage(final String code) {
        return this.properties.getProperty(code, null);
    }
}
