package edu.stanford.irt.eresources.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class EresourcesWebApplicationTest {

    private EresourcesWebApplication application;

    @Before
    public void setUp() {
        this.application = new EresourcesWebApplication();
    }

    @Test
    public final void testBasic() {
        assertEquals("ERROR", this.application.solrLoader("foo"));
        this.application.running = true;
        assertEquals("WARN", this.application.solrLoader("foo"));
    }

    @Test
    public final void testLane() {
        assertEquals("ERROR", this.application.laneReload());
        assertEquals("ERROR", this.application.laneUpdate());
    }

    @Test
    public final void testPubmed() {
        assertEquals("ERROR", this.application.pubmedDailyFtp());
    }

    @Test
    public final void testRedivis() {
        assertEquals("ERROR", this.application.redivisReload());
    }

    @Test
    public final void testSul() {
        assertEquals("ERROR", this.application.sulUpdate());
        assertEquals("didn't run", this.application.sulReload());
    }

    @Test
    public final void testUsage() {
        assertTrue(this.application.usage().contains("reload"));
    }
}
