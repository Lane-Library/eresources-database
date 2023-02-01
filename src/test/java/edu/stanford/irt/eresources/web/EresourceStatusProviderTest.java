package edu.stanford.irt.eresources.web;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.stanford.irt.status.Status;
import edu.stanford.irt.status.StatusItem;

public class EresourceStatusProviderTest {

    private JobManager manager;

    private EresourceStatusProvider provider;

    @Before
    public void setUp() throws Exception {
        this.manager = mock(JobManager.class);
        this.provider = new EresourceStatusProvider(this.manager);
    }

    @Test
    public final void testAddStatusItemsLongRunningJob() {
        List<Job> runningJobs = Collections.singletonList(new Job(Job.Type.UNDEFINED, LocalDateTime.MIN));
        expect(this.manager.getRunningJobs()).andReturn(runningJobs).times(2);
        expect(this.manager.getMaxJobDurationInHours()).andReturn(10);
        replay(this.manager);
        List<StatusItem> list = this.provider.getStatusItems();
        assertEquals(3, list.size());
        assertTrue(list.stream().anyMatch((final StatusItem si) -> si.getStatus() == Status.ERROR));
        assertTrue(list.stream().anyMatch((final StatusItem si) -> si.getMessage().contains("long-running")));
    }

    @Test
    public final void testAddStatusItemsNoRunningJob() {
        expect(this.manager.getRunningJobs()).andReturn(Collections.emptyList());
        replay(this.manager);
        List<StatusItem> list = this.provider.getStatusItems();
        assertEquals(1, list.size());
    }

    @Test
    public final void testAddStatusItemsRunningJob() {
        List<Job> runningJobs = Collections.singletonList(new Job(Job.Type.UNDEFINED, LocalDateTime.now()));
        expect(this.manager.getRunningJobs()).andReturn(runningJobs).times(2);
        expect(this.manager.getMaxJobDurationInHours()).andReturn(10);
        replay(this.manager);
        List<StatusItem> list = this.provider.getStatusItems();
        assertEquals(2, list.size());
    }
}
