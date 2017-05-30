package edu.stanford.irt.eresources;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

public class HTTPCatalogRecordService implements CatalogRecordService {

    private static final String END_POINT = "records?time=%s";

    private URI catalogServiceURI;

    public HTTPCatalogRecordService(final URI catalogServiceURI) {
        this.catalogServiceURI = catalogServiceURI;
    }

    @Override
    public InputStream getRecordStream(final long time) {
        try {
            String endpoint = String.format(END_POINT, time);
            return IOUtils.getStream(new URL(this.catalogServiceURI.toURL(), endpoint));
        } catch (IOException e) {
            throw new EresourceDatabaseException(e);
        }
    }
}
