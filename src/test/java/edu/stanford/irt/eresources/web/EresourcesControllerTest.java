package edu.stanford.irt.eresources.web;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.DayOfWeek;
import java.time.LocalDate;

import org.junit.Before;
import org.junit.Test;

public class EresourcesControllerTest {

    private EresourcesController controller;

    private JobManager manager;

    @Before
    public void setUp() throws Exception {
        this.manager = mock(JobManager.class);
        this.controller = new EresourcesController(this.manager);
    }

    @Test
    public final void testLaneMarcUpdate() {
        expect(this.manager.run(isA(Job.class))).andReturn(JobStatus.COMPLETE);
        replay(this.manager);
        this.controller.laneMarcUpdate();
        verify(this.manager);
    }

    @Test
    public final void testLaneReload() {
        expect(this.manager.run(isA(Job.class))).andReturn(JobStatus.COMPLETE);
        replay(this.manager);
        this.controller.laneReload();
        verify(this.manager);
    }

    @Test
    public final void testLaneWebsitesUpdate() {
        expect(this.manager.run(isA(Job.class))).andReturn(JobStatus.COMPLETE);
        replay(this.manager);
        this.controller.laneWebsitesUpdate();
        verify(this.manager);
    }

    @Test
    public final void testPmcReload() {
        expect(this.manager.run(isA(Job.class))).andReturn(JobStatus.COMPLETE);
        replay(this.manager);
        this.controller.pmcReload();
        verify(this.manager);
    }

    @Test
    public final void testPubmedDailyFtp() {
        expect(this.manager.run(isA(Job.class))).andReturn(JobStatus.COMPLETE);
        replay(this.manager);
        this.controller.pubmedDailyFtp();
        verify(this.manager);
    }

    @Test
    public final void testRedivisReload() {
        expect(this.manager.run(isA(Job.class))).andReturn(JobStatus.COMPLETE);
        replay(this.manager);
        this.controller.redivisReload();
        verify(this.manager);
    }

    @Test
    public final void testSolrLoader() {
        expect(this.manager.run(isA(Job.class))).andReturn(JobStatus.COMPLETE);
        replay(this.manager);
        this.controller.solrLoader("job");
        verify(this.manager);
    }

    @Test
    public final void testSolrLoaderCancelRunningJob() {
        expect(this.manager.cancelRunningJob()).andReturn(JobStatus.INTERRUPTED);
        replay(this.manager);
        this.controller.solrLoader(Job.Type.CANCEL_RUNNING_JOB.getName());
        verify(this.manager);
    }

    @Test
    public final void testSulReload() {
        // null status on third Sunday of the month OK
        JobStatus status = this.controller.sulReload();
        if (null == status) {
            assertEquals(DayOfWeek.SUNDAY, LocalDate.now().getDayOfWeek());
        } else {
            assertEquals(JobStatus.SKIPPED, status);
        }
    }

    @Test
    public final void testSulReloadSkipped() {
        assertEquals(JobStatus.SKIPPED, this.controller.sulReload(LocalDate.MAX));
    }

    @Test
    public final void testSulReloadThirdSunday() {
        LocalDate knownThirdSundayOfMonth = LocalDate.of(2020, 01, 19);
        expect(this.manager.run(isA(Job.class))).andReturn(JobStatus.COMPLETE);
        replay(this.manager);
        this.controller.sulReload(knownThirdSundayOfMonth);
        verify(this.manager);
    }

    @Test
    public final void testSulUpdate() {
        expect(this.manager.run(isA(Job.class))).andReturn(JobStatus.COMPLETE);
        replay(this.manager);
        this.controller.sulUpdate();
        verify(this.manager);
    }

    @Test
    public final void testUsage() {
        assertTrue(this.controller.usage("version").contains("reload"));
    }
}
