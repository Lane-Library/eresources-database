package edu.stanford.irt.eresources.marc;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.utils.URIBuilder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.stanford.irt.eresources.EresourceDatabaseException;
import edu.stanford.irt.eresources.IOUtils;

public class HTTPLaneLocationsService {

    private URI catalogServiceURI;

    private String endpoint;

    private Collection<Location> locations;

    private Map<String, Location> locMap = new HashMap<>();

    private ObjectMapper objectMapper;

    public HTTPLaneLocationsService(final ObjectMapper objectMapper, final URI catalogServiceURI,
            final String endpoint) {
        this.catalogServiceURI = catalogServiceURI;
        this.objectMapper = objectMapper;
        this.endpoint = endpoint;
        this.locations = getLocations();
        for (Location loc : this.locations) {
            this.locMap.put(loc.getCode(), loc);
        }
    }

    public String getLocationName(final String code) {
        Location loc = this.locMap.get(code);
        if (null != loc) {
            return loc.getName();
        }
        return null;
    }

    public String getLocationUrl(final String code) {
        Location loc = this.locMap.get(code);
        if (null != loc) {
            return loc.getUrl();
        }
        return null;
    }

    private Collection<Location> getLocations() {
        String path = this.catalogServiceURI.getPath() + this.endpoint;
        URIBuilder builder = new URIBuilder(this.catalogServiceURI);
        builder.setPath(path);
        try (InputStream input = IOUtils.getStream(builder.build().toURL())) {
            return this.objectMapper.readValue(input, new TypeReference<ArrayList<Location>>() {
            });
        } catch (IOException | URISyntaxException e) {
            throw new EresourceDatabaseException(e);
        }
    }
}
