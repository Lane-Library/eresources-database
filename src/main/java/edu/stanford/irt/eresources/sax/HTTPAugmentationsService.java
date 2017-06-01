package edu.stanford.irt.eresources.sax;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.stanford.irt.eresources.EresourceDatabaseException;
import edu.stanford.irt.eresources.IOUtils;

public class HTTPAugmentationsService implements AugmentationsService {

    private URI catalogServiceURI;

    private String endpointPath;

    private ObjectMapper objectMapper;

    public HTTPAugmentationsService(final ObjectMapper objectMapper, final URI catalogServiceURI,
            final String endpointPath) {
        this.objectMapper = objectMapper;
        this.catalogServiceURI = catalogServiceURI;
        this.endpointPath = endpointPath;
    }

    @Override
    public Map<String, String> buildAugmentations() {
        try (InputStream input = IOUtils.getStream(new URL(this.catalogServiceURI.toURL(), this.endpointPath))) {
            return this.objectMapper.readValue(input, new TypeReference<Map<String, String>>() {
            });
        } catch (IOException e) {
            throw new EresourceDatabaseException(e);
        }
    }
}
