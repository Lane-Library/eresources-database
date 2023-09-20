package edu.stanford.irt.eresources;

import static org.junit.Assert.assertNotNull;

import java.net.URI;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class HTTPCatalogRecordDeleteServiceTest {

    private ObjectMapper mapper;

    private URI uri;

    HTTPCatalogRecordDeleteService deletesService;

    @Before
    public void setUp() throws Exception {
        this.mapper = new ObjectMapper();
        this.uri = HTTPCatalogRecordDeleteServiceTest.class.getResource("./").toURI();
        this.deletesService = new HTTPCatalogRecordDeleteService(this.mapper, this.uri, "folio-deletes.json");
    }

    @Test
    public final void testGetDeletes() {
        assertNotNull(this.deletesService.getDeletes(0));
    }

    @Test
    public final void testGetRecordStreamNullBasePath() throws Exception {
        this.deletesService = new HTTPCatalogRecordDeleteService(this.mapper, new URI("http://localhost:1/"), "");
        Assert.assertThrows(EresourceDatabaseException.class, () -> this.deletesService.getDeletes(0));
    }
}
