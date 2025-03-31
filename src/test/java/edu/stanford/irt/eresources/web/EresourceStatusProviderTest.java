package edu.stanford.irt.eresources.web;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.stanford.irt.status.Status;
import edu.stanford.irt.status.StatusItem;

class EresourceStatusProviderTest {

    private JobManager manager;

    private EresourceStatusProvider provider;

    @BeforeEach
    void setUp() {
        this.manager = mock(JobManager.class);
        this.provider = new EresourceStatusProvider(this.manager);
    }

    @Test
    final void testAddStatusItemsLongRunningJob() {
        List<Job> runningJobs = Collections.singletonList(new Job(Job.Type.UNDEFINED, LocalDateTime.MIN));
        expect(this.manager.getRunningJobs()).andReturn(runningJobs).times(2);
        expect(this.manager.getPausedDataSources()).andReturn(Collections.emptyList());
        expect(this.manager.getMaxJobDurationInHours()).andReturn(10);
        replay(this.manager);
        List<StatusItem> list = this.provider.getStatusItems();
        assertEquals(3, list.size());
        assertTrue(list.stream().anyMatch((final StatusItem si) -> si.getStatus() == Status.ERROR));
        assertTrue(list.stream().anyMatch((final StatusItem si) -> si.getMessage().contains("long-running")));
        verify(this.manager);
    }

    @Test
    final void testAddStatusItemsNoRunningJob() {
        expect(this.manager.getRunningJobs()).andReturn(Collections.emptyList());
        expect(this.manager.getPausedDataSources()).andReturn(Collections.emptyList());
        replay(this.manager);
        List<StatusItem> list = this.provider.getStatusItems();
        assertEquals(1, list.size());
        verify(this.manager);
    }

    @Test
    final void testAddStatusItemsRunningJob() {
        List<Job> runningJobs = Collections.singletonList(new Job(Job.Type.UNDEFINED, LocalDateTime.now()));
        expect(this.manager.getRunningJobs()).andReturn(runningJobs).times(2);
        expect(this.manager.getPausedDataSources()).andReturn(Collections.emptyList());
        expect(this.manager.getMaxJobDurationInHours()).andReturn(10);
        replay(this.manager);
        List<StatusItem> list = this.provider.getStatusItems();
        assertEquals(2, list.size());
        verify(this.manager);
    }
}
