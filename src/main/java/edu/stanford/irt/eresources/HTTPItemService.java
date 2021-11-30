package edu.stanford.irt.eresources;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HTTPItemService implements ItemService {

    public enum Type {
        BIB, HOLDING
    }

    private static final String BIBS_AVAILABLES_PATH = "items/availables";

    private static final String BIBS_TOTALS_PATH = "items/totals";

    private static final String HOLDINGS_AVAILABLES_PATH = "item-holdings/availables";

    private static final String HOLDINGS_TOTALS_PATH = "item-holdings/totals";

    private URI catalogServiceURI;

    private ObjectMapper objectMapper;

    private Type type;

    public HTTPItemService(final Type type, final URI catalogServiceURI, final ObjectMapper objectMapper) {
        this.type = type;
        this.catalogServiceURI = catalogServiceURI;
        this.objectMapper = objectMapper;
    }

    @Override
    public Map<Integer, Integer> getAvailables() {
        if (this.type.equals(Type.HOLDING)) {
            return getMap(HOLDINGS_AVAILABLES_PATH);
        }
        return getMap(BIBS_AVAILABLES_PATH);
    }

    @Override
    public Map<Integer, Integer> getTotals() {
        if (this.type.equals(Type.HOLDING)) {
            return getMap(HOLDINGS_TOTALS_PATH);
        }
        return getMap(BIBS_TOTALS_PATH);
    }

    private Map<Integer, Integer> getMap(final String enpointPath) {
        try (InputStream input = IOUtils.getStream(new URL(this.catalogServiceURI.toURL(), enpointPath))) {
            return this.objectMapper.readValue(input, new TypeReference<Map<Integer, Integer>>() {
            });
        } catch (IOException e) {
            throw new EresourceDatabaseException(e);
        }
    }
}
