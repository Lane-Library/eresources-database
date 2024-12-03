package edu.stanford.irt.eresources.web;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

public class JobManagerTest {

    private ExecutorService executor;

    private JobManager manager;

    @Before
    public void setUp() {
        this.executor = Executors.newSingleThreadExecutor();
        this.manager = new JobManager(this.executor, 1);
    }

    @Test
    public final void testCancelRunningJob() {
        this.manager.runningJobs.add(new Job(Job.Type.UNIT_TESTING, null));
        Future future = mock(Future.class);
        this.manager.runningFutures.add(future);
        assertNotNull(this.manager.getRunningJobs());
        expect(future.isDone()).andReturn(false);
        expect(future.cancel(true)).andReturn(true);
        replay(future);
        assertEquals(JobStatus.INTERRUPTED, this.manager.cancelRunningJobs());
        verify(future);
        this.manager.runningFutures.clear();
        assertEquals(JobStatus.COMPLETE, this.manager.cancelRunningJobs());
        assertTrue(this.manager.getRunningJobs().isEmpty());
        assertTrue(this.manager.runningFutures.isEmpty());
    }

    @Test
    public final void testGetMaxJobDurationInHours() {
        assertEquals(1, this.manager.getMaxJobDurationInHours());
    }

    @Test
    public final void testGetPausedDataSources() {
        assertTrue(this.manager.getPausedDataSources().isEmpty());
    }

    @Test
    public final void testGetRunningJobs() {
        assertTrue(this.manager.getRunningJobs().isEmpty());
    }

    @Test
    public final void testPause() {
        assertEquals(JobStatus.PAUSED, this.manager.run(new Job(Job.Type.PAUSE_UNDEFINED, null)));
    }

    @Test
    public final void testPauseAlreadyRunning() {
        this.manager.runningJobs.add(new Job(Job.Type.UNDEFINED, null));
        assertEquals(JobStatus.PAUSED, this.manager.run(new Job(Job.Type.PAUSE_UNDEFINED, null)));
    }

    @Test
    public final void testPauseThenUnpause() {
        assertEquals(JobStatus.PAUSED, this.manager.run(new Job(Job.Type.PAUSE_UNDEFINED, null)));
        assertEquals(JobStatus.RUNNING, this.manager.run(new Job(Job.Type.PAUSE_UNDEFINED, null)));
    }

    @Test
    public final void testRun() {
        Job test = new Job(Job.Type.UNIT_TESTING, LocalDateTime.now());
        assertEquals(JobStatus.COMPLETE, this.manager.run(test));
        assertTrue(test.toString().contains("type: UNIT_TESTING;"));
    }

    @Test
    public final void testRunAlreadyPaused() {
        assertEquals(JobStatus.PAUSED, this.manager.run(new Job(Job.Type.PAUSE_UNDEFINED, null)));
        assertEquals(JobStatus.PAUSED, this.manager.run(new Job(Job.Type.UNDEFINED, null)));
    }

    @Test
    public final void testRunAlreadyRunning() {
        this.manager.runningJobs.add(new Job(Job.Type.UNDEFINED, null));
        assertEquals(JobStatus.RUNNING, this.manager.run(new Job(Job.Type.UNDEFINED, null)));
    }

    @Test
    public final void testRunError() {
        assertEquals(JobStatus.ERROR, this.manager.run(new Job(Job.Type.UNDEFINED, null)));
    }

    @Test
    public final void testRunInterrupt() throws Exception {
        ExecutorService es = mock(ExecutorService.class);
        this.manager = new JobManager(es, 1);
        Future<JobStatus> futureJob = mock(Future.class);
        expect(es.submit(isA(Callable.class))).andReturn(futureJob);
        expect(futureJob.get(1, TimeUnit.HOURS)).andThrow(new ExecutionException("oops", null));
        replay(es, futureJob);
        JobStatus status = this.manager.run(new Job(Job.Type.UNDEFINED, null));
        assertEquals(JobStatus.INTERRUPTED, status);
        verify(es, futureJob);
    }

    @Test
    public final void testRunTimeout() {
        this.manager = new JobManager(this.executor, 0);
        assertEquals(JobStatus.INTERRUPTED, this.manager.run(new Job(Job.Type.UNIT_TESTING, LocalDateTime.now())));
    }
}
