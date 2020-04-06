package edu.stanford.irt.eresources.redivis;

import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.junit.Before;
import org.junit.Test;

import edu.stanford.irt.eresources.EresourceDatabaseException;
import edu.stanford.irt.eresources.EresourceHandler;

public class RedivisEresourceProcessorTest {

    private EresourceHandler eresourceHandler;

    private String listEndpoint;

    private RedivisEresourceProcessor processor;

    @Before
    public void setUp() throws Exception {
        this.eresourceHandler = mock(EresourceHandler.class);
        this.listEndpoint = RedivisEresourceProcessorTest.class.getResource("datasets.json").toExternalForm();
        this.processor = new RedivisEresourceProcessor(this.listEndpoint, "token", this.eresourceHandler);
    }

    @Test
    public final void testProcess() {
        this.eresourceHandler.handleEresource(isA(RedivisEresource.class));
        expectLastCall().times(10);
        replay(this.eresourceHandler);
        this.processor.process();
        verify(this.eresourceHandler);
    }

    @Test(expected = EresourceDatabaseException.class)
    public final void testProcessBadJson() {
        this.listEndpoint = RedivisEresourceProcessorTest.class.getResource("datasets-exception.json").toExternalForm();
        this.processor = new RedivisEresourceProcessor(this.listEndpoint, "token", this.eresourceHandler);
        replay(this.eresourceHandler);
        this.processor.process();
        verify(this.eresourceHandler);
    }
}
