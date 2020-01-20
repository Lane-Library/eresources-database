package edu.stanford.irt.eresources.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class EresourcesWebApplicationTest {

    private EresourcesWebApplication application;

    @Before
    public void setUp() {
        this.application = new EresourcesWebApplication();
    }

    @Test
    public final void testBasic() {
        assertEquals("ERROR", this.application.solrLoader("foo"));
        this.application.jobIsRunning = true;
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
    public final void testStatus() {
        ResponseEntity<?> re = this.application.status("version");
        assertEquals(HttpStatus.OK, re.getStatusCode());
        this.application.jobIsRunning = true;
        this.application.maxJobDurationInHours = 0;
        re = this.application.status("version");
        assertNotEquals(HttpStatus.OK, re.getStatusCode());
    }

    @Test
    public final void testSulReload() {
        assertNotNull(this.application.sulReload());
        assertEquals("didn't run", this.application.sulReload(LocalDate.MAX));
        LocalDate knownThirdSundayOfMonth = LocalDate.of(2020, 01, 19);
        assertEquals("ERROR", this.application.sulReload(knownThirdSundayOfMonth));
    }

    @Test
    public final void testSulUpdate() {
        assertEquals("ERROR", this.application.sulUpdate());
    }

    @Test
    public final void testUsage() {
        assertTrue(this.application.usage().contains("reload"));
    }
}
