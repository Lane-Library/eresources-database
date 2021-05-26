package edu.stanford.irt.eresources.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Test;

public class JobManagerTest {

    private JobManager manager;

    @Before
    public void setUp() throws Exception {
        this.manager = new JobManager(0);
    }

    @Test
    public final void testClearRunningJob() {
        this.manager.runningJob = new Job("job", null);
        assertNotNull(this.manager.getRunningJob());
        assertEquals(JobStatus.COMPLETE, this.manager.clearRunningJob());
        assertNull(this.manager.getRunningJob());
    }

    @Test
    public final void testGetMaxJobDurationInHours() {
        assertEquals(0, this.manager.getMaxJobDurationInHours());
    }

    @Test
    public final void testGetRunningJob() {
        assertNull(this.manager.getRunningJob());
    }

    @Test
    public final void testRun() {
        assertEquals(JobStatus.COMPLETE, this.manager.run(new Job("lane/unit-test", LocalDateTime.now())));
    }

    @Test
    public final void testRunAlreadyRunning() {
        this.manager.runningJob = new Job("job", null);
        assertEquals(JobStatus.RUNNING, this.manager.run(new Job("another job", null)));
    }

    @Test
    public final void testRunError() {
        assertEquals(JobStatus.ERROR, this.manager.run(new Job("bad job", null)));
    }
}
