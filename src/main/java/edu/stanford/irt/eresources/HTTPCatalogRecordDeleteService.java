package edu.stanford.irt.eresources;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

import org.apache.http.client.utils.URIBuilder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HTTPCatalogRecordDeleteService {

    private static final String TIME = "time";

    private URI catalogServiceURI;

    private String endpoint;

    private ObjectMapper objectMapper;

    public HTTPCatalogRecordDeleteService(final ObjectMapper objectMapper, final URI catalogServiceURI,
            final String endpoint) {
        this.catalogServiceURI = catalogServiceURI;
        this.endpoint = endpoint;
        this.objectMapper = objectMapper;
    }

    public Collection<String> getDeletes(final long time) {
        String path = this.catalogServiceURI.getPath() + this.endpoint;
        URIBuilder builder = new URIBuilder(this.catalogServiceURI);
        builder.setPath(path);
        builder.setParameter(TIME, Long.toString(time));
        try (InputStream input = IOUtils.getStream(builder.build().toURL())) {
            return this.objectMapper.readValue(input, new TypeReference<Collection<String>>() {
            });
        } catch (IOException | URISyntaxException e) {
            throw new EresourceDatabaseException(e);
        }
    }
}
