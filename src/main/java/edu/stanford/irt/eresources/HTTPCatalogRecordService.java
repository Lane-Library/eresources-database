package edu.stanford.irt.eresources;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

public class HTTPCatalogRecordService implements CatalogRecordService {

    private URI catalogServiceURI;

    private String endPoint;

    public HTTPCatalogRecordService(final URI catalogServiceURI, final String endpoint) {
        this.catalogServiceURI = catalogServiceURI;
        this.endPoint = endpoint;
    }

    @Override
    public InputStream getRecordStream(final long time) {
        try {
            String endpoint = String.format(this.endPoint, time);
            return IOUtils.getStream(new URL(this.catalogServiceURI.toURL(), endpoint));
        } catch (IOException e) {
            throw new EresourceDatabaseException(e);
        }
    }
}
