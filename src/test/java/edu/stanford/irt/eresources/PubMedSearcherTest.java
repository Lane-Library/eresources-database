package edu.stanford.irt.eresources;

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
        this.searchers.add(new PubmedSearcher("field_foo", "value_foo", "24120354"));
        this.searchers.add(new PubmedSearcher("field_bar", "value_bar", "24120355"));
    }

    @Test
    public final void test() throws Exception {
        PubmedSpecialTypesManager manager = new PubmedSpecialTypesManager(this.searchers);
        assertTrue(manager.getTypes("24120354").iterator().next()[0].equals("field_foo"));
        assertTrue(manager.getTypes("24120354").iterator().next()[1].equals("value_foo"));
        assertTrue(manager.getTypes("24120355").iterator().next()[0].equals("field_bar"));
        assertTrue(manager.getTypes("24120355").iterator().next()[1].equals("value_bar"));
    }

    @Test(expected = IllegalStateException.class)
    public final void testNullQuery() throws Exception {
        PubmedSearcher search = new PubmedSearcher("field", "value", null);
        search.getPmids();
    }
}
