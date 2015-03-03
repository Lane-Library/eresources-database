package edu.stanford.irt.eresources.classes;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import edu.stanford.irt.eresources.EresourceException;
import edu.stanford.irt.eresources.Extractor;

public class ClassesExtractor implements Extractor<InputStream> {

    private URL allClassesURL;
    private boolean hasNext;

    public ClassesExtractor(final URL allClassesURL) {
        this.allClassesURL = allClassesURL;
        this.hasNext = true;
    }

    @Override
    public boolean hasNext() {
        return this.hasNext;
    }

    @Override
    public InputStream next() {
        this.hasNext = false;
        try {
            return this.allClassesURL.openConnection().getInputStream();
        } catch (IOException e) {
            throw new EresourceException(e);
        }
    }
}
