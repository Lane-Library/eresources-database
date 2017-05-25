package edu.stanford.irt.eresources.sax;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.stanford.irt.eresources.EresourceDatabaseException;

public class HTTPAuthTextAugmentation extends AbstractAuthTextAugmentation {

    private static final String ENDPOINT_PATH = "augmentations/auth";

    private URI catalogServiceURI;

    private ObjectMapper objectMapper;

    public HTTPAuthTextAugmentation(final ObjectMapper objectMapper, final URI catalogServiceURI) {
        this.objectMapper = objectMapper;
        this.catalogServiceURI = catalogServiceURI;
    }

    @Override
    protected Map<String, String> buildAugmentations() {
        try (InputStream input = new URL(this.catalogServiceURI.toURL(), ENDPOINT_PATH).openStream()) {
            return this.objectMapper.readValue(input, new TypeReference<Map<String, String>>() {
            });
        } catch (IOException e) {
            throw new EresourceDatabaseException(e);
        }
    }
}
