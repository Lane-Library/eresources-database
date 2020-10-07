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

    private SAXLink link;

    private SAXVersion v1;

    private SAXVersion v2;

    @Before
    public void setUp() {
        this.v1 = new SAXVersion();
        this.v2 = new SAXVersion();
        this.link = new SAXLink();
        this.comparator = new VersionComparator();
    }

    @Test
    public void testCompare() {
        this.v1.setDates("1999.");
        this.v2.setDates("1999.");
        assertEquals(1, this.comparator.compare(this.v1, this.v2));
    }

    @Test
    public void testCompareCatalogBeforeImpactFactor() {
        this.v1.setSummaryHoldings("v. 1.");
        this.v2.setSummaryHoldings("v. 1.");
        this.v1.setDates("1999-2000.");
        this.v2.setDates("1999-2000.");
        SAXLink l1 = new SAXLink();
        l1.setLabel("Impact Factor");
        l1.setUrl("foo");
        SAXLink l2 = new SAXLink();
        l2.setLabel("Catalog Link");
        l2.setUrl("http://lmldb.stanford.edu/cgi-bin/Pwebrecon.cgi?BBID=foo");
        this.v1.addLink(l1);
        this.v2.addLink(l2);
        assertTrue(this.comparator.compare(this.v1, this.v2) > 0);
    }

    @Test
    public void testCompareClosedDates() {
        this.v1.setDates("1999-2000.");
        this.v2.setDates("1999-2000.");
        assertTrue(this.comparator.compare(this.v1, this.v2) == 1);
        this.v1.setDates("1999-2010.");
        this.v2.setDates("1999-2000.");
        assertTrue(this.comparator.compare(this.v1, this.v2) < 0);
        this.v1.setDates("2020-");
        this.v2.setDates("2020.");
        this.v1.addLink(this.link);
        this.v2.addLink(this.link);
        assertTrue(this.comparator.compare(this.v1, this.v2) < 0);
    }

    @Test
    public void testCompareCurrentHoldings() {
        this.v1.setSummaryHoldings("v. 1-");
        this.v1.addLink(this.link);
        this.v2.addLink(this.link);
        this.v2.setSummaryHoldings("v. 1-");
        this.v2.setAdditionalText("current edition");
        assertTrue(this.comparator.compare(this.v2, this.v1) < 0);
    }

    @Test
    public void testCompareDelayedHoldings() {
        this.v1.setSummaryHoldings("v. 1-");
        this.v1.addLink(this.link);
        this.v2.addLink(this.link);
        this.v2.setSummaryHoldings("v. 1-");
        this.v2.setAdditionalText("foo delayed bar");
        assertTrue(this.comparator.compare(this.v1, this.v2) < 0);
    }

    @Test
    public void testCompareHoldings() {
        this.v1.setSummaryHoldings("v. 1-");
        this.v2.setSummaryHoldings("v. 1.");
        this.v1.addLink(this.link);
        this.v2.addLink(this.link);
        assertTrue(this.comparator.compare(this.v1, this.v2) < 0);
        this.v1.setSummaryHoldings("v. 10-20.");
        this.v2.setSummaryHoldings("v. 10-");
        assertTrue(this.comparator.compare(this.v1, this.v2) > 0);
    }

    @Test
    public void testCompareImpactFactorHoldings() {
        this.v1.setSummaryHoldings("v. 1.");
        this.v2.setSummaryHoldings("v. 1.");
        this.v1.setDates("1999-2000.");
        this.v2.setDates("1999-2000.");
        this.v1.addLink(new SAXLink());
        this.link.setLabel("Impact Factor");
        this.v2.addLink(this.link);
        assertTrue(this.comparator.compare(this.v1, this.v2) < 0);
    }

    @Test
    public void testCompareInstalledSoftware() {
        this.link.setLabel("pRoduct Description");
        this.v1.addLink(this.link);
        this.v2.addLink(new SAXLink());
        assertTrue(this.comparator.compare(this.v1, this.v2) < 0);
    }

    @Test
    public void testCompareOpenDates() {
        this.v1.setDates("1999-");
        this.v2.setDates("1999-2000.");
        assertTrue(this.comparator.compare(this.v1, this.v2) < 0);
        this.v1.setDates("1999.");
        this.v2.setDates("1999-");
        this.v1.addLink(this.link);
        this.v2.addLink(this.link);
        assertTrue(this.comparator.compare(this.v1, this.v2) > 0);
    }

    @Test
    public void testComparePublishers() {
        this.v1.setDates("1999.");
        this.v1.setPublisher("ScienceDirect");
        this.v2.setDates("1999.");
        this.v2.setPublisher("Karger");
        assertTrue(this.comparator.compare(this.v1, this.v2) < 0);
        this.v1.setDates("1999.");
        this.v1.setPublisher("Karger");
        this.v2.setDates("1999.");
        this.v2.setPublisher("ScienceDirect");
        assertTrue(this.comparator.compare(this.v1, this.v2) > 0);
        this.v1.setDates("1999.");
        this.v1.setPublisher("PubMed Central");
        this.v2.setDates("1999.");
        this.v2.setPublisher("");
        assertTrue(this.comparator.compare(this.v1, this.v2) < 0);
    }
}
