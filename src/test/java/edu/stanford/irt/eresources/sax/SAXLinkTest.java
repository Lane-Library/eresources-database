package edu.stanford.irt.eresources.sax;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.stanford.irt.eresources.Version;

public class SAXLinkTest {

    @Test
    public final void testGetLinkText() {
        SAXLink link = new SAXLink();
        link.setLabel("label");
        SAXVersion version = new SAXVersion();
        version.setSummaryHoldings("summaryHoldings");
        version.addLink(new SAXLink());
        version.setDates("dates");
        version.setProxy(true);
        version.setHasGetPasswordLink(false);
        link.setVersion(version);
        assertEquals("summaryHoldings, dates", link.getLinkText());
        link.setLabel("");
        link.setVersion(new SAXVersion());
        assertTrue(link.getLinkText().isEmpty());
    }

    @Test
    public final void testSAXLink() {
        SAXLink link = new SAXLink();
        link.setInstruction("instruction");
        link.setLabel("label");
        link.setUrl("url");
        Version version = new SAXVersion();
        link.setVersion(version);
        assertEquals("instruction", link.getAdditionalText());
        assertEquals("label", link.getLabel());
        assertEquals("label", link.getLinkText());
        assertEquals("url", link.getUrl());
    }

}
