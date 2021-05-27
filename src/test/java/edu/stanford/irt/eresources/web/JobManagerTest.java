package edu.stanford.irt.eresources.web;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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
    public void setUp() throws Exception {
        this.executor = Executors.newSingleThreadExecutor();
        this.manager = new JobManager(this.executor, 1);
    }

    @Test
    public final void testCancelRunningJob() {
        this.manager.runningJob = new Job(Job.Type.UNIT_TESTING, null);
        this.manager.runningFuture = mock(Future.class);
        assertNotNull(this.manager.getRunningJob());
        expect(this.manager.runningFuture.isDone()).andReturn(false);
        expect(this.manager.runningFuture.cancel(true)).andReturn(true);
        replay(this.manager.runningFuture);
        assertEquals(JobStatus.INTERRUPTED, this.manager.cancelRunningJob());
        verify(this.manager.runningFuture);
        this.manager.runningFuture = null;
        assertEquals(JobStatus.COMPLETE, this.manager.cancelRunningJob());
        assertNull(this.manager.getRunningJob());
        assertNull(this.manager.runningFuture);
    }

    @Test
    public final void testGetMaxJobDurationInHours() {
        assertEquals(1, this.manager.getMaxJobDurationInHours());
    }

    @Test
    public final void testGetRunningJob() {
        assertNull(this.manager.getRunningJob());
    }

    @Test
    public final void testRun() {
        assertEquals(JobStatus.COMPLETE, this.manager.run(new Job(Job.Type.UNIT_TESTING, LocalDateTime.now())));
    }

    @Test
    public final void testRunAlreadyRunning() {
        this.manager.runningJob = new Job(Job.Type.UNIT_TESTING, null);
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
