package edu.stanford.irt.eresources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import edu.stanford.irt.eresources.sax.SAXLink;
import edu.stanford.irt.eresources.sax.SAXVersion;

/**
 * @author ryanmax
 */
public class VersionComparatorTest {

    private VersionComparator comparator;

    private SAXLink saxLink;

    private SAXVersion saxVersion1;

    private SAXVersion saxVersion2;

    @Before
    public void setUp() {
        this.saxVersion1 = new SAXVersion();
        this.saxVersion2 = new SAXVersion();
        this.saxLink = new SAXLink();
        this.comparator = new VersionComparator();
    }

    @Test
    public void testCompare() {
        this.saxVersion1.setDates("1999.");
        this.saxVersion2.setDates("1999.");
        assertEquals(1, this.comparator.compare(this.saxVersion1, this.saxVersion2));
    }

    @Test
    public void testCompareClosedDates() {
        this.saxVersion1.setDates("1999-2000.");
        this.saxVersion2.setDates("1999-2000.");
        assertEquals(1, this.comparator.compare(this.saxVersion1, this.saxVersion2));
        this.saxVersion1.setDates("1999-2010.");
        this.saxVersion2.setDates("1999-2000.");
        assertTrue(this.comparator.compare(this.saxVersion1, this.saxVersion2) < 0);
        this.saxVersion1.setDates("2020-");
        this.saxVersion2.setDates("2020.");
        this.saxVersion1.addLink(this.saxLink);
        this.saxVersion2.addLink(this.saxLink);
        assertTrue(this.comparator.compare(this.saxVersion1, this.saxVersion2) < 0);
    }

    @Test
    public void testCompareCurrentHoldings() {
        this.saxVersion1.setSummaryHoldings("v. 1-");
        this.saxVersion1.addLink(this.saxLink);
        this.saxVersion2.addLink(this.saxLink);
        this.saxVersion2.setSummaryHoldings("v. 1-");
        this.saxVersion2.setAdditionalText("current edition");
        assertTrue(this.comparator.compare(this.saxVersion2, this.saxVersion1) < 0);
    }

    @Test
    public void testCompareDelayedHoldings() {
        this.saxVersion1.setSummaryHoldings("v. 1-");
        this.saxVersion1.addLink(this.saxLink);
        this.saxVersion2.addLink(this.saxLink);
        this.saxVersion2.setSummaryHoldings("v. 1-");
        this.saxVersion2.setAdditionalText("foo delayed bar");
        assertTrue(this.comparator.compare(this.saxVersion1, this.saxVersion2) < 0);
    }

    @Test
    public void testCompareHoldings() {
        this.saxVersion1.setSummaryHoldings("v. 1-");
        this.saxVersion2.setSummaryHoldings("v. 1.");
        this.saxVersion1.addLink(this.saxLink);
        this.saxVersion2.addLink(this.saxLink);
        assertTrue(this.comparator.compare(this.saxVersion1, this.saxVersion2) < 0);
        this.saxVersion1.setSummaryHoldings("v. 10-20.");
        this.saxVersion2.setSummaryHoldings("v. 10-");
        assertTrue(this.comparator.compare(this.saxVersion1, this.saxVersion2) > 0);
    }

    @Test
    public void testCompareOpenDates() {
        this.saxVersion1.setDates("1999-");
        this.saxVersion2.setDates("1999-2000.");
        assertTrue(this.comparator.compare(this.saxVersion1, this.saxVersion2) < 0);
        this.saxVersion1.setDates("1999.");
        this.saxVersion2.setDates("1999-");
        this.saxVersion1.addLink(this.saxLink);
        this.saxVersion2.addLink(this.saxLink);
        assertTrue(this.comparator.compare(this.saxVersion1, this.saxVersion2) > 0);
    }

    @Test
    public void testComparePublishers() {
        this.saxVersion1.setDates("1999.");
        this.saxVersion1.setPublisher("ScienceDirect");
        this.saxVersion2.setDates("1999.");
        this.saxVersion2.setPublisher("Karger");
        assertTrue(this.comparator.compare(this.saxVersion1, this.saxVersion2) < 0);
        this.saxVersion1.setDates("1999.");
        this.saxVersion1.setPublisher("Karger");
        this.saxVersion2.setDates("1999.");
        this.saxVersion2.setPublisher("ScienceDirect");
        assertTrue(this.comparator.compare(this.saxVersion1, this.saxVersion2) > 0);
        this.saxVersion1.setDates("1999.");
        this.saxVersion1.setPublisher("PubMed Central");
        this.saxVersion2.setDates("1999.");
        this.saxVersion2.setPublisher("");
        assertTrue(this.comparator.compare(this.saxVersion1, this.saxVersion2) < 0);
    }
}
