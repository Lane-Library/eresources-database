package edu.stanford.irt.eresources.redivis;

import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.stanford.irt.eresources.EresourceDatabaseException;
import edu.stanford.irt.eresources.EresourceHandler;

class RedivisEresourceProcessorTest {

    private EresourceHandler eresourceHandler;

    private String listEndpoint;

    private RedivisEresourceProcessor processor;

    @BeforeEach
    void setUp() {
        this.eresourceHandler = mock(EresourceHandler.class);
        this.listEndpoint = RedivisEresourceProcessorTest.class.getResource("datasets.json").toExternalForm();
        this.processor = new RedivisEresourceProcessor(this.listEndpoint, "token", this.eresourceHandler);
    }

    @Test
    final void testProcess() {
        this.eresourceHandler.handleEresource(isA(RedivisEresource.class));
        expectLastCall().times(85);
        replay(this.eresourceHandler);
        this.processor.process();
        verify(this.eresourceHandler);
    }

    @Test
    final void testProcessBadJson() {
        this.listEndpoint = RedivisEresourceProcessorTest.class.getResource("datasets-exception.json").toExternalForm();
        this.processor = new RedivisEresourceProcessor(this.listEndpoint, "token", this.eresourceHandler);
        replay(this.eresourceHandler);
        assertThrows(EresourceDatabaseException.class, () -> {
            this.processor.process();
            verify(this.eresourceHandler);
        });
    }
}
