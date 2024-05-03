package edu.stanford.irt.eresources.sax;

import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.Attributes;

import edu.stanford.irt.eresources.EresourceDatabaseException;
import edu.stanford.irt.eresources.EresourceHandler;

public class DefaultEresourceBuilderTest {

    private Attributes attributes;

    private DefaultEresourceBuilder builder;

    private EresourceHandler eresourceHandler;

    @Before
    public void setUp() throws Exception {
        this.attributes = EasyMock.mock(Attributes.class);
        this.eresourceHandler = EasyMock.mock(EresourceHandler.class);
        this.builder = new DefaultEresourceBuilder();
        this.builder.setEresourceHandler(this.eresourceHandler);
        this.builder.startDocument();
    }

    @Test
    public void testCharacters() throws Exception {
        assertTrue(this.builder.currentText.isEmpty());
        this.builder.characters("data".toCharArray(), 0, 4);
        assertEquals("data", this.builder.currentText.toString());
    }

    @Test
    public void testEndElementDate() throws Exception {
        this.builder.currentText.append("date");
        this.builder.currentVersion = new SAXVersion();
        this.builder.endElement(null, null, "date");
        assertEquals("date", this.builder.currentVersion.getDates());
    }

    @Test
    public void testEndElementDescription() throws Exception {
        this.builder.currentText.append("description");
        this.builder.currentEresource = new SAXEresource();
        this.builder.endElement(null, null, "description");
        assertEquals("description", this.builder.currentEresource.getDescription());
    }

    @Test
    public void testEndElementErDate() throws Exception {
        this.builder.currentText.append("20140324");
        this.builder.currentEresource = new SAXEresource();
        this.builder.endElement(null, null, "er-date");
        assertEquals("20140324", this.builder.currentEresource.getDate());
    }

    @Test
    public void testEndElementEresource() throws Exception {
        this.eresourceHandler.handleEresource(isA(SAXEresource.class));
        expectLastCall();
        EasyMock.replay(this.eresourceHandler);
        this.builder.currentEresource = new SAXEresource();
        this.builder.endElement(null, null, "eresource");
        assertNull(this.builder.currentEresource);
        EasyMock.verify(this.eresourceHandler);
    }

    @Test
    public void testEndElementEresources() throws Exception {
        this.builder.endElement(null, null, "eresources");
        assertNull(this.builder.currentEresource);
    }

    @Test
    public void testEndElementInstruction() throws Exception {
        this.builder.currentText.append("instruction");
        this.builder.currentLink = new SAXLink();
        this.builder.endElement(null, null, "instruction");
        assertNotNull(this.builder.currentLink);
        assertEquals("instruction", this.builder.currentLink.getAdditionalText());
    }

    @Test
    public void testEndElementIssn() throws Exception {
        this.builder.currentText.append("issn");
        this.builder.currentEresource = new SAXEresource();
        this.builder.endElement(null, null, "issn");
        assertEquals("issn", this.builder.currentEresource.getIssns().stream().findFirst().get());
    }

    @Test
    public void testEndElementKeywords() throws Exception {
        this.builder.currentText.append("keywords");
        this.builder.currentEresource = new SAXEresource();
        this.builder.endElement(null, null, "keywords");
        assertEquals("keywords", this.builder.currentEresource.getKeywords());
    }

    @Test
    public void testEndElementLabel() throws Exception {
        this.builder.currentText.append("labelData");
        this.builder.currentLink = new SAXLink();
        this.builder.endElement(null, null, "label");
        assertNotNull(this.builder.currentLink);
        assertEquals("labelData", this.builder.currentLink.getLabel());
    }

    @Test
    public void testEndElementLink() throws Exception {
        this.builder.currentVersion = new SAXVersion();
        this.builder.currentLink = new SAXLink();
        this.builder.endElement(null, null, "link");
        assertNull(this.builder.currentLink);
        assertEquals(1, this.builder.currentVersion.getLinks().size());
    }

    @Test
    public void testEndElementMesh() throws Exception {
        this.builder.currentText.append("mesh");
        this.builder.currentEresource = new SAXEresource();
        this.builder.endElement(null, null, "mesh");
        assertEquals("mesh", this.builder.currentEresource.getMeshTerms().stream().findFirst().get());
    }

    @Test
    public void testEndElementMeshBroad() throws Exception {
        this.builder.currentText.append("mesh_broad");
        this.builder.currentEresource = new SAXEresource();
        this.builder.endElement(null, null, "mesh_broad");
        assertEquals("mesh_broad", this.builder.currentEresource.getBroadMeshTerms().stream().findFirst().get());
    }

    @Test
    public void testEndElementPrimaryType() throws Exception {
        this.builder.currentText.append("ptype");
        this.builder.currentEresource = new SAXEresource();
        this.builder.endElement(null, null, "primaryType");
        assertEquals("ptype", this.builder.currentEresource.getPrimaryType());
    }

    @Test
    public void testEndElementPublicationAuthor() throws Exception {
        this.builder.currentText.append("author data");
        this.builder.currentEresource = new SAXEresource();
        this.builder.endElement(null, null, "publicationAuthor");
        String authorData = this.builder.currentEresource.getPublicationAuthors().stream().findFirst().get();
        assertTrue(authorData.startsWith("author data"));
    }

    @Test
    public void testEndElementPublicationAuthorFacetable() throws Exception {
        this.builder.currentText.append("publicationAuthorFacetable");
        this.builder.currentEresource = new SAXEresource();
        this.builder.endElement(null, null, "publicationAuthorFacetable");
        assertEquals("publicationAuthorFacetable",
                this.builder.currentEresource.getPublicationAuthorsFacetable().stream().findFirst().get());
    }

    @Test
    public void testEndElementPublicationAuthorsText() throws Exception {
        this.builder.currentText.append("publicationAuthorsText");
        this.builder.currentEresource = new SAXEresource();
        this.builder.endElement(null, null, "publicationAuthorsText");
        assertEquals("publicationAuthorsText", this.builder.currentEresource.getPublicationAuthorsText());
    }

    @Test
    public void testEndElementPublicationAuthorTooLong() throws Exception {
        this.builder.currentText.append("publicationAuthor");
        this.builder.currentEresource = new SAXEresource();
        this.builder.endElement(null, null, "publicationAuthor");
        assertEquals("publicationAuthor",
                this.builder.currentEresource.getPublicationAuthors().stream().findFirst().get());
    }

    @Test
    public void testEndElementPublisher() throws Exception {
        this.builder.currentText.append("publisher");
        this.builder.currentVersion = new SAXVersion();
        this.builder.endElement(null, null, "publisher");
        assertEquals("publisher", this.builder.currentVersion.getPublisher());
    }

    @Test
    public void testEndElementSummaryHoldings() throws Exception {
        this.builder.currentText.append("summary-holdings");
        this.builder.currentVersion = new SAXVersion();
        this.builder.endElement(null, null, "summary-holdings");
        assertEquals("summary-holdings", this.builder.currentVersion.getSummaryHoldings());
    }

    @Test
    public void testEndElementTitle() throws Exception {
        this.builder.currentText.append("title");
        this.builder.currentEresource = new SAXEresource();
        this.builder.endElement(null, null, "title");
        assertEquals("title", this.builder.currentEresource.getTitle());
    }

    @Test
    public void testEndElementTitleAbbr() throws Exception {
        this.builder.currentText.append("title_abbr");
        this.builder.currentEresource = new SAXEresource();
        this.builder.endElement(null, null, "title_abbr");
        assertEquals("title_abbr", this.builder.currentEresource.getAbbreviatedTitles().stream().findFirst().get());
    }

    @Test
    public void testEndElementTitleAlt() throws Exception {
        this.builder.currentText.append("title_alt");
        this.builder.currentEresource = new SAXEresource();
        this.builder.endElement(null, null, "title_alt");
        assertEquals("title_alt", this.builder.currentEresource.getAlternativeTitles().stream().findFirst().get());
    }

    @Test
    public void testEndElementTitleShort() throws Exception {
        this.builder.currentText.append("title_short");
        this.builder.currentEresource = new SAXEresource();
        this.builder.endElement(null, null, "title_short");
        assertEquals("title_short", this.builder.currentEresource.getShortTitle());
    }

    @Test
    public void testEndElementType() throws Exception {
        this.builder.currentText.append("new type");
        this.builder.currentEresource = new SAXEresource();
        this.builder.endElement(null, null, "type");
        assertEquals("new type", this.builder.currentEresource.getTypes().stream().findFirst().get());
    }

    @Test(expected = EresourceDatabaseException.class)
    public void testEndElementUnknown() throws Exception {
        this.builder.endElement(null, null, "unknown-tag");
    }

    @Test
    public void testEndElementUrl() throws Exception {
        this.builder.currentText.append("urlData");
        this.builder.currentLink = new SAXLink();
        this.builder.endElement(null, null, "url");
        assertNotNull(this.builder.currentLink);
        assertEquals("urlData", this.builder.currentLink.getUrl());
    }

    @Test
    public void testEndElementVersion() throws Exception {
        this.builder.currentEresource = new SAXEresource();
        this.builder.currentVersion = new SAXVersion();
        this.builder.currentVersion.addLink(new SAXLink());
        this.builder.endElement(null, null, "version");
        assertNull(this.builder.currentVersion);
        assertEquals(1, this.builder.currentEresource.getVersions().size());
    }

    @Test
    public void testEndElementVersionAdditionalText() throws Exception {
        this.builder.currentText.append("version-additionalText");
        this.builder.currentVersion = new SAXVersion();
        this.builder.endElement(null, null, "version-additionalText");
        assertEquals("version-additionalText", this.builder.currentVersion.getAdditionalText());
    }

    @Test
    public void testEndElementYear() throws Exception {
        this.builder.currentText.append("2012");
        this.builder.currentEresource = new SAXEresource();
        this.builder.endElement(null, null, "year");
        assertEquals(2012, this.builder.currentEresource.getYear());
    }

    @Test(expected = IllegalStateException.class)
    public void testStartDocumentNullErHandler() throws Exception {
        this.builder.setEresourceHandler(null);
        this.builder.startDocument();
    }

    @Test
    public void testStartElementLink() throws Exception {
        EasyMock.replay(this.eresourceHandler);
        this.builder.startElement(null, null, "link", this.attributes);
        EasyMock.verify(this.eresourceHandler);
        assertNotNull(this.builder.currentLink);
    }

    @Test
    public void testStartElementVersion() throws Exception {
        EasyMock.replay(this.eresourceHandler);
        this.builder.startElement(null, null, "version", this.attributes);
        EasyMock.verify(this.eresourceHandler);
        assertNotNull(this.builder.currentVersion);
    }
}