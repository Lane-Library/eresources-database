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
        this.searchers.add(new PubmedSearcher("type_foo", "24120354"));
        this.searchers.add(new PubmedSearcher("type_bar", "24120355"));
    }

    @Test
    public final void test() throws Exception {
        PubmedSpecialTypesManager manager = new PubmedSpecialTypesManager(this.searchers);
        assertTrue(manager.getTypes("24120354").contains("type_foo"));
        assertTrue(manager.getTypes("24120355").contains("type_bar"));
    }
}
