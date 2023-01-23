package edu.stanford.irt.eresources;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

public class SulHTTPCatalogRecordService implements CatalogRecordService {

    private static final String END_POINT = "records?limitToLane=false&time=%s";

    private URI catalogServiceURI;

    public SulHTTPCatalogRecordService(final URI catalogServiceURI) {
        this.catalogServiceURI = catalogServiceURI;
    }

    @Override
    public InputStream getRecordStream(final long time) {
        try {
            //1668887154000 = Your time zone: Saturday, November 19, 2022 11:45:54 AM GMT-08:00
            //1669009909255 = Your time zone: Sunday, November 20, 2022 9:51:49.255 PM GMT-08:00
            //1669906909255 = Your time zone: Thursday, December 1, 2022 7:01:49.255 AM GMT-08:00
            String endpoint = String.format(END_POINT, time);
            return IOUtils.getStream(new URL(this.catalogServiceURI.toURL(), endpoint));
        } catch (IOException e) {
            throw new EresourceDatabaseException(e);
        }
    }
}
