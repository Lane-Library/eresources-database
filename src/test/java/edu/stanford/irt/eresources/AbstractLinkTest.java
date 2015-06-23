package edu.stanford.irt.eresources;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

public class AbstractLinkTest {

    private class TestAbstractLink extends AbstractLink {

        @Override
        public String getAdditionalText() {
            return null;
        }

        @Override
        public String getInstruction() {
            return null;
        }

        @Override
        public String getLabel() {
            return null;
        }

        @Override
        public String getLinkText() {
            return null;
        }

        @Override
        public String getUrl() {
            return null;
        }

        @Override
        public void setVersion(final Version version) {
        }
    }

    private AbstractLink link;

    private Version version;

    @Before
    public void setUp() {
        this.link = new TestAbstractLink();
        this.version = createMock(Version.class);
    }

    @Test
    public void testGetAdditionalText() {
        assertEquals("publisher, additional text, instruction", this.link.getAdditionalText("instruction", "additional text", "publisher"));
    }

    @Test
    public void testGetLinkText() {
        expect(this.version.getHoldingsAndDates()).andReturn("summaryHoldings, dates");
        expect(this.version.getLinks()).andReturn(Collections.singletonList(this.link));
        replay(this.version);
        assertEquals("summaryHoldings, dates", this.link.getLinkText("label", this.version));
        verify(this.version);
    }
}
