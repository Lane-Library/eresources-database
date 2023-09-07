package edu.stanford.irt.eresources;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Collection;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HTTPCatalogRecordDeleteService {

    private URI catalogServiceURI;

    private String endPoint;

    private ObjectMapper objectMapper;

    public HTTPCatalogRecordDeleteService(final URI catalogServiceURI, final String endpoint,
            final ObjectMapper objectMapper) {
        this.catalogServiceURI = catalogServiceURI;
        this.endPoint = endpoint;
        this.objectMapper = objectMapper;
    }

    public Collection<String> getDeletes(final long time) {
        String endpoint = String.format(this.endPoint, time);
        try (InputStream input = IOUtils.getStream(new URL(this.catalogServiceURI.toURL(), endpoint))) {
            return this.objectMapper.readValue(input, new TypeReference<Collection<String>>() {
            });
        } catch (IOException e) {
            throw new EresourceDatabaseException(e);
        }
    }
}
