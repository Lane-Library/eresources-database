package edu.stanford.irt.eresources.marc;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.apache.http.client.utils.URIBuilder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.stanford.irt.eresources.EresourceDatabaseException;
import edu.stanford.irt.eresources.IOUtils;

public class HTTPAugmentationsService implements AugmentationsService {

    private URI catalogServiceURI;

    private String endpoint;

    private ObjectMapper objectMapper;

    public HTTPAugmentationsService(final ObjectMapper objectMapper, final URI catalogServiceURI,
            final String endpoint) {
        this.objectMapper = objectMapper;
        this.catalogServiceURI = catalogServiceURI;
        this.endpoint = endpoint;
    }

    @Override
    public Map<String, String> buildAugmentations() {
        String path = this.catalogServiceURI.getPath() + this.endpoint;
        URIBuilder builder = new URIBuilder(this.catalogServiceURI);
        builder.setPath(path);
        try (InputStream input = IOUtils.getStream(builder.build().toURL())) {
            return this.objectMapper.readValue(input, new TypeReference<Map<String, String>>() {
            });
        } catch (IOException | URISyntaxException e) {
            throw new EresourceDatabaseException(e);
        }
    }
}
