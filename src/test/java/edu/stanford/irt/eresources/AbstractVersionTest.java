package edu.stanford.irt.eresources;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class AbstractVersionTest {

    private class TestAbstractVersion extends AbstractVersion {

        @Override
        public String getAdditionalText() {
            return null;
        }

        @Override
        public String getDates() {
            return "dates";
        }

        @Override
        public String getDescription() {
            return "description";
        }

        @Override
        public List<Link> getLinks() {
            return null;
        }

        @Override
        public String getPublisher() {
            return "publisher";
        }

        @Override
        public String getSummaryHoldings() {
            return "summaryHoldings";
        }

        @Override
        public boolean hasGetPasswordLink() {
            return false;
        }

        @Override
        public boolean isProxy() {
            return false;
        }
    }

    private AbstractVersion version;

    @Before
    public void setUp() {
        this.version = new TestAbstractVersion();
    }

    @Test
    public void testCreateAdditionalText() {
        assertEquals(" summaryHoldings, dates, publisher, description ", this.version.createAdditionalText());
    }
}
