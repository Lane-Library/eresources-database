package edu.stanford.irt.eresources;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HTTPItemService implements ItemService {

    private static final String BIBS_AVAILABLES_PATH = "item-bibs/availables";

    private static final String BIBS_TOTALS_PATH = "item-bibs/totals";

    private static final String HOLDINGS_AVAILABLES_PATH = "item-holdings/availables";

    private static final String HOLDINGS_TOTALS_PATH = "item-holdings/totals";

    private ItemCount bibsItemCount;

    private URI catalogServiceURI;

    private ItemCount holdingsItemCount;

    private ObjectMapper objectMapper;

    public HTTPItemService(final URI catalogServiceURI, final ObjectMapper objectMapper) {
        this.catalogServiceURI = catalogServiceURI;
        this.objectMapper = objectMapper;
    }

    @Override
    public ItemCount getBibsItemCount() {
        if (null == this.bibsItemCount) {
            this.bibsItemCount = new ItemCount(getMap(BIBS_AVAILABLES_PATH), getMap(BIBS_TOTALS_PATH));
        }
        return this.bibsItemCount;
    }

    @Override
    public ItemCount getHoldingsItemCount() {
        if (null == this.holdingsItemCount) {
            this.holdingsItemCount = new ItemCount(getMap(HOLDINGS_AVAILABLES_PATH), getMap(HOLDINGS_TOTALS_PATH));
        }
        return this.holdingsItemCount;
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
