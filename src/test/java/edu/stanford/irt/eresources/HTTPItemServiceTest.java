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
        this.itemService = new HTTPItemService(this.uri, this.mapper);
        assertEquals(543, this.itemService.getBibsItemCount().itemCount(12)[0]);
        assertEquals(542, this.itemService.getBibsItemCount().itemCount(12)[1]);
    }

    @Test
    public final void testHoldings() {
        this.itemService = new HTTPItemService(this.uri, this.mapper);
        assertEquals(109, this.itemService.getHoldingsItemCount().itemCount(2)[0]);
        assertEquals(0, this.itemService.getHoldingsItemCount().itemCount(2)[1]);
    }
}
