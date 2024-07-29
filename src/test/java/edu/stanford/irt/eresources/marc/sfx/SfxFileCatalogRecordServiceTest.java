package edu.stanford.irt.eresources.marc.sfx;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import edu.stanford.irt.eresources.marc.MARCRecordSupport;
import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.RecordCollection;

public class SfxFileCatalogRecordServiceTest extends MARCRecordSupport {

    ThreadPoolTaskExecutor executor;

    SfxFileCatalogRecordService recordService;

    @Before
    public void setUp() {
        this.executor = new ThreadPoolTaskExecutor();
        this.executor.initialize();
        this.recordService = new SfxFileCatalogRecordService("src/test/resources/edu/stanford/irt/eresources/marc/sfx",
                this.executor);
    }

    @Test
    public final void testGetRecordStream() {
        RecordCollection rc = new RecordCollection(this.recordService.getRecordStream(0));
        assertNotNull(rc);
        while (rc.hasNext()) {
            Record mr = rc.next();
            assertTrue(mr.toString().contains("Madison: Proposed Amendments to the Constitution"));
        }
    }

    @Test(expected = IllegalStateException.class)
    public final void testGetRecordStreamNullBasePath() {
        this.recordService = new SfxFileCatalogRecordService(null, this.executor);
        new RecordCollection(this.recordService.getRecordStream(0));
    }

}
