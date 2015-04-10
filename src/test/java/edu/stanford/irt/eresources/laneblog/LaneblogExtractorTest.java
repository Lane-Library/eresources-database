package edu.stanford.irt.eresources.laneblog;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import edu.stanford.irt.eresources.StartDate;

public class LaneblogExtractorTest {

    LaneblogExtractor extractor;

    String rssUrl;

    StartDate startDate;

    @Before
    public void setUp() throws Exception {
        this.startDate = createMock(StartDate.class);
        this.rssUrl = "file:" + System.getProperty("user.dir")
                + "/src/test/resources/edu/stanford/irt/eresources/laneblog/laneblog.xml";
    }

    @Test
    public final void testHasNext() throws Exception {
        expect(this.startDate.getStartDate()).andReturn(new Date());
        replay(this.startDate);
        this.extractor = new LaneblogExtractor(new URL(this.rssUrl), this.startDate);
        assertFalse(this.extractor.hasNext());
        verify(this.startDate);
    }

    @Test
    public final void testNext() throws Exception {
        expect(this.startDate.getStartDate()).andReturn(new Date(1));
        replay(this.startDate);
        this.extractor = new LaneblogExtractor(new URL(this.rssUrl), this.startDate);
        assertTrue(this.extractor.hasNext());
        verify(this.startDate);
        Document doc = this.extractor.next();
        assertNotEquals(null, doc);
        assertEquals(10, doc.getElementsByTagName("item").getLength());
        assertFalse(this.extractor.hasNext());
    }
}
