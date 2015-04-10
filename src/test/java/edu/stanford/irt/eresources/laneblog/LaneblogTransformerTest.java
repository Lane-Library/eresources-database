package edu.stanford.irt.eresources.laneblog;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

import edu.stanford.irt.eresources.Eresource;

public class LaneblogTransformerTest {

    private LaneblogTransformer processor;

    @Before
    public void setUp() {
        this.processor = new LaneblogTransformer(getClass().getResourceAsStream(
                "/edu/stanford/irt/eresources/laneblogRSS2er.xsl"));
    }

    @Test
    public void testProcess() throws Exception {
        List<Eresource> eresources = new ArrayList<Eresource>();
        InputSource source = new InputSource(getClass().getResourceAsStream("laneblog.xml"));
        DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        parser.setErrorHandler(new DefaultHandler());
        eresources = this.processor.transform(parser.parse(source));
        assertEquals(10, eresources.size());
        assertEquals("Lane Webpage", eresources.get(0).getPrimaryType());
        assertEquals("laneblog", eresources.get(0).getRecordType());
        assertEquals("Tech Tip Tuesday: The Many Faces of Lane Search – Part 3", eresources.get(0).getTitle());
        assertEquals("ClinicalKey Is Replacing MD Consult", eresources.get(9).getTitle());
    }
}
