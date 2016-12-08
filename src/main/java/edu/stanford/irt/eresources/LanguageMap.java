package edu.stanford.irt.eresources;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class LanguageMap {

    private final Properties properties = new Properties();

    public LanguageMap() {
        try (InputStream in = LanguageMap.class.getResourceAsStream("languages.properties")) {
            this.properties.load(in);
        } catch (IOException e) {
            throw new EresourceDatabaseException(e);
        }
    }

    public String getLanguage(final String code) {
        return this.properties.getProperty(code, null);
    }
}
