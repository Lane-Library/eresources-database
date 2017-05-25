package edu.stanford.irt.eresources;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

public class HTTPCatalogRecordService implements CatalogRecordService {

    private static final String END_POINT = "records";

    private static final String END_POINT_WITH_TIME = END_POINT + "?time=%s";

    private URI catalogServiceURI;

    public HTTPCatalogRecordService(final URI catalogServiceURI) {
        this.catalogServiceURI = catalogServiceURI;
    }

    @Override
    public InputStream getRecordStream(final long time) {
        try {
            if (time == 0) {
                return new URL(this.catalogServiceURI.toURL(), END_POINT).openStream();
            } else {
                String endpoint = String.format(END_POINT_WITH_TIME, time);
                return new URL(this.catalogServiceURI.toURL(), endpoint).openStream();
            }
        } catch (IOException e) {
            throw new EresourceDatabaseException(e);
        }
    }
}
