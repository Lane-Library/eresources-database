package edu.stanford.irt.eresources.marc;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.stanford.irt.eresources.EresourceDatabaseException;
import edu.stanford.irt.eresources.IOUtils;

public class HTTPLaneLocationsService {

    private URI catalogServiceURI;

    private Collection<Location> locations;

    private Map<String, Location> locMap = new HashMap<>();

    private ObjectMapper objectMapper;

    public HTTPLaneLocationsService(final URI catalogServiceURI, final ObjectMapper objectMapper) {
        this.catalogServiceURI = catalogServiceURI;
        this.objectMapper = objectMapper;
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
        try (InputStream input = IOUtils.getStream(new URL(this.catalogServiceURI.toURL(), "locations"))) {
            return this.objectMapper.readValue(input, new TypeReference<ArrayList<Location>>() {
            });
        } catch (IOException e) {
            throw new EresourceDatabaseException(e);
        }
    }
}
