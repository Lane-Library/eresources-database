package edu.stanford.irt.eresources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URI;

import org.junit.Before;
import org.junit.Test;

import edu.stanford.lane.catalog.FolioRecordCollection;
import edu.stanford.lane.catalog.RecordCollection;

public class SulHTTPCatalogRecordServiceTest {

    private URI uri;

    SulHTTPCatalogRecordService recordService;

    @Before
    public void setUp() throws Exception {
        this.uri = SulHTTPCatalogRecordServiceTest.class.getResource("./marc/sul/").toURI();
        this.recordService = new SulHTTPCatalogRecordService(this.uri);
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
        this.recordService = new SulHTTPCatalogRecordService(new URI("http://localhost:1/"));
        new RecordCollection(this.recordService.getRecordStream(0));
    }
}
