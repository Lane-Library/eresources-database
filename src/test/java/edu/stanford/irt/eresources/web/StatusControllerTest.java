package edu.stanford.irt.eresources.web;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import edu.stanford.irt.status.ApplicationStatus;
import edu.stanford.irt.status.StatusService;

public class StatusControllerTest {

    private ApplicationStatus applicationStatus;

    private StatusController controller;

    private StatusService service;

    @Before
    public void setUp() {
        this.service = mock(StatusService.class);
        this.controller = new StatusController(this.service);
        this.applicationStatus = mock(ApplicationStatus.class);
    }

    @Test
    public void testGetStatusJson() {
        expect(this.service.getStatus()).andReturn(this.applicationStatus);
        replay(this.service);
        assertEquals(this.applicationStatus, this.controller.getStatusJson());
        verify(this.service);
    }

    @Test
    public void testGetStatusTxt() {
        expect(this.service.getStatus()).andReturn(this.applicationStatus);
        replay(this.service);
        assertEquals(this.applicationStatus.toString(), this.controller.getStatusTxt());
        verify(this.service);
    }
}
