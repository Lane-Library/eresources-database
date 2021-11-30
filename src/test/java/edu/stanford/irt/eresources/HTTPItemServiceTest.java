package edu.stanford.irt.eresources;

import static org.junit.Assert.assertEquals;

import java.net.URI;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class HTTPItemServiceTest {

    private HTTPItemService itemService;

    private ObjectMapper mapper;

    private URI uri;

    @Before
    public void setUp() throws Exception {
        this.mapper = new ObjectMapper();
        this.uri = HTTPItemServiceTest.class.getResource(".").toURI();
    }

    @Test
    public final void testBib() {
        this.itemService = new HTTPItemService(HTTPItemService.Type.BIB, this.uri, this.mapper);
        assertEquals(2, this.itemService.getTotals().size());
        assertEquals(2, this.itemService.getAvailables().size());
    }

    @Test
    public final void testHoldings() {
        this.itemService = new HTTPItemService(HTTPItemService.Type.HOLDING, this.uri, this.mapper);
        assertEquals(3, this.itemService.getTotals().size());
        assertEquals(2, this.itemService.getAvailables().size());
    }
}
