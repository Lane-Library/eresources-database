package edu.stanford.irt.eresources;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class LanguageMap {

    private static String FILENAME = "languages.properties";

    private final Properties properties = new Properties();

    public LanguageMap() {
        FileInputStream fis = null;
        try {
            File file = new File(FILENAME);
            if (!file.exists()) {
                throw new EresourceDatabaseException("missing " + FILENAME);
            }
            fis = new FileInputStream(file);
            this.properties.load(fis);
        } catch (IOException e) {
            throw new EresourceDatabaseException(e);
        } finally {
            try {
                if (null != fis) {
                    fis.close();
                }
            } catch (IOException e) {
                throw new EresourceDatabaseException(e);
            }
        }
    }

    public String getLanguage(final String code) {
        return this.properties.getProperty(code, null);
    }
}
