package edu.stanford.irt.eresources.pubmed;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

public class PubMedSearcherTest {

    private Collection<PubmedSearcher> searchers;

    @Before
    public void setUp() throws Exception {
        this.searchers = new ArrayList<PubmedSearcher>();
        this.searchers.add(new PubmedSearcher("field_foo", "value_foo", "24120354", null, "version"));
    }

    @Test
    public final void testBadApiKey() throws Exception {
        PubmedSearcher search = new PubmedSearcher("field", "value", "24120355", "foo", "version");
        assertTrue(search.getPmids().isEmpty());
    }

    @Test(expected = IllegalStateException.class)
    public final void testNullQuery() throws Exception {
        PubmedSearcher search = new PubmedSearcher("field", "value", null, null, "version");
        search.getPmids();
    }

    @Test
    public final void testSearcher() throws Exception {
        if (EutilsIsReachable.eutilsIsReachable()) {
            PubmedSpecialTypesManager manager = new PubmedSpecialTypesManager(this.searchers);
            assertEquals("field_foo", manager.getTypes("24120354").iterator().next()[0]);
            assertEquals("value_foo", manager.getTypes("24120354").iterator().next()[1]);
        }
    }
}
