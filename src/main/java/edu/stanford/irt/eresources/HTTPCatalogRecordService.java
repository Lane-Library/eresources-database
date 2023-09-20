package edu.stanford.irt.eresources;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.utils.URIBuilder;

public class HTTPCatalogRecordService implements CatalogRecordService {

    private static final String TIME = "time";

    private URI catalogServiceURI;

    private String endpoint;

    public HTTPCatalogRecordService(final URI catalogServiceURI, final String endpoint) {
        this.catalogServiceURI = catalogServiceURI;
        this.endpoint = endpoint;
    }

    @Override
    public InputStream getRecordStream(final long time) {
        try {
            String path = this.catalogServiceURI.getPath() + this.endpoint;
            URIBuilder builder = new URIBuilder(this.catalogServiceURI);
            builder.setPath(path);
            builder.setParameter(TIME, Long.toString(time));
            return IOUtils.getStream(builder.build().toURL());
        } catch (IOException | URISyntaxException e) {
            throw new EresourceDatabaseException(e);
        }
    }
}
