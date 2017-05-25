package edu.stanford.irt.eresources;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HTTPItemService implements ItemService {

    private static final String AVAILABLES_PATH = "items/availables";

    private static final String TOTALS_PATH = "items/totals";

    private URI catalogServiceURI;

    private ObjectMapper objectMapper;

    public HTTPItemService(final URI catalogServiceURI, final ObjectMapper objectMapper) {
        this.catalogServiceURI = catalogServiceURI;
        this.objectMapper = objectMapper;
    }

    @Override
    public Map<Integer, Integer> getAvailables() {
        return getMap(AVAILABLES_PATH);
    }

    @Override
    public Map<Integer, Integer> getTotals() {
        return getMap(TOTALS_PATH);
    }

    private Map<Integer, Integer> getMap(final String enpointPath) {
        try (InputStream input = new URL(this.catalogServiceURI.toURL(), enpointPath).openStream()) {
            return this.objectMapper.readValue(input, new TypeReference<Map<Integer, Integer>>() {
            });
        } catch (IOException e) {
            throw new EresourceDatabaseException(e);
        }
    }
}
