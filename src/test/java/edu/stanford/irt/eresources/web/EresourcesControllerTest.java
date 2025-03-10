package edu.stanford.irt.eresources.web;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EresourcesControllerTest {

    private EresourcesController controller;

    private JobManager manager;

    @BeforeEach
    public void setUp() {
        this.manager = mock(JobManager.class);
        this.controller = new EresourcesController(this.manager);
    }

    @Test
    public final void testGideonReload() {
        expect(this.manager.run(isA(Job.class))).andReturn(JobStatus.COMPLETE);
        replay(this.manager);
        this.controller.gideonReload();
        verify(this.manager);
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
    public final void testSfxReload() {
        expect(this.manager.run(isA(Job.class))).andReturn(JobStatus.COMPLETE);
        replay(this.manager);
        this.controller.sfxReload();
        verify(this.manager);
    }

    @Test
    public final void testSolrLoader() {
        expect(this.manager.run(isA(Job.class))).andReturn(JobStatus.COMPLETE);
        replay(this.manager);
        this.controller.solrLoader("job", null);
        verify(this.manager);
    }

    @Test
    public final void testSolrLoaderCancelRunningJobs() {
        expect(this.manager.cancelRunningJobs()).andReturn(JobStatus.INTERRUPTED);
        replay(this.manager);
        this.controller.solrLoader(Job.Type.CANCEL_RUNNING_JOBS.getQualifiedName(), null);
        verify(this.manager);
    }

    @Test
    public final void testSulReload() {
        expect(this.manager.run(isA(Job.class))).andReturn(JobStatus.COMPLETE);
        replay(this.manager);
        this.controller.sulReload();
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
        expect(this.manager.getRunningJobs()).andReturn(Collections.emptyList());
        expect(this.manager.getPausedDataSources()).andReturn(Collections.emptyList());
        replay(this.manager);
        assertTrue(this.controller.usage().contains("reload"));
        verify(this.manager);
    }
}
