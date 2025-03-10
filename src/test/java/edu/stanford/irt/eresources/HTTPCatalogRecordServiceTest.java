package edu.stanford.irt.eresources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.URI;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.stanford.lane.catalog.FolioRecordCollection;
import edu.stanford.lane.catalog.RecordCollection;

public class HTTPCatalogRecordServiceTest {

    private URI uri;

    HTTPCatalogRecordService recordService;

    @BeforeEach
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

    @Test
    public final void testGetRecordStreamNullBasePath() throws Exception {
        this.recordService = new HTTPCatalogRecordService(new URI("http://localhost:1/"), "");
        assertThrows(EresourceDatabaseException.class, () -> {
            new RecordCollection(this.recordService.getRecordStream(0));
        });
    }
}
