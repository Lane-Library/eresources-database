package edu.stanford.irt.eresources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URI;

import org.junit.Before;
import org.junit.Test;

import edu.stanford.lane.catalog.FolioRecordCollection;
import edu.stanford.lane.catalog.RecordCollection;

public class HTTPCatalogRecordServiceTest {

    private URI uri;

    HTTPCatalogRecordService recordService;

    @Before
    public void setUp() throws Exception {
        this.uri = HTTPCatalogRecordServiceTest.class.getResource("./").toURI();
        this.recordService = new HTTPCatalogRecordService(this.uri, "marc/folio-records");
    }

    @Test
    public final void testGetRecordStream() {
        FolioRecordCollection rc = new FolioRecordCollection(this.recordService.getRecordStream(0));
        assertNotNull(rc);
        assertEquals("L307325", rc.next().getInstanceHrid());
        assertEquals("in00000000125", rc.next().getInstanceHrid());
    }

    @Test(expected = EresourceDatabaseException.class)
    public final void testGetRecordStreamNullBasePath() throws Exception {
        this.recordService = new HTTPCatalogRecordService(new URI("http://localhost:1/"), "");
        new RecordCollection(this.recordService.getRecordStream(0));
    }
}
