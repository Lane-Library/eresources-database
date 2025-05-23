package edu.stanford.irt.eresources.sax;

import static org.easymock.EasyMock.expect;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;

import org.easymock.EasyMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.Attributes;

import edu.stanford.irt.eresources.EresourceDatabaseException;
import edu.stanford.irt.eresources.EresourceHandler;
import edu.stanford.irt.eresources.pubmed.PubmedSpecialTypesManager;

class PubmedEresourceBuilderTest {

    private Attributes attributes;

    private PubmedEresourceBuilder builder;

    private EresourceHandler eresourceHandler;

    private PubmedSpecialTypesManager specialTypesManager;

    @BeforeEach
    void setUp() throws Exception {
        this.attributes = EasyMock.mock(Attributes.class);
        this.eresourceHandler = EasyMock.mock(EresourceHandler.class);
        this.specialTypesManager = EasyMock.mock(PubmedSpecialTypesManager.class);
        this.builder = new PubmedEresourceBuilder();
        this.builder.setEresourceHandler(this.eresourceHandler);
        this.builder.startDocument();
        this.builder.setSpecialTypesManager(this.specialTypesManager);
    }

    @Test
    void testEndElementPublicationAuthor() throws Exception {
        this.builder.currentText.append("long author data".repeat(2_500));
        this.builder.currentEresource = new SAXEresource();
        this.builder.endElement(null, null, "publicationAuthor");
        String authorData = this.builder.currentEresource.getPublicationAuthors().stream().findFirst().get();
        assertTrue(authorData.startsWith("long author data"));
        assertEquals(32766, authorData.length());
    }

    @Test
    void testEndElementPublicationAuthorFacetable() throws Exception {
        this.builder.currentText.append("publicationAuthorFacetable");
        this.builder.currentEresource = new SAXEresource();
        this.builder.endElement(null, null, "publicationAuthorFacetable");
        assertEquals("publicationAuthorFacetable",
                this.builder.currentEresource.getPublicationAuthorsFacetable().stream().findFirst().get());
    }

    @Test
    void testEndElementPublicationAuthorsText() throws Exception {
        this.builder.currentText.append("publicationAuthorsText");
        this.builder.currentEresource = new SAXEresource();
        this.builder.endElement(null, null, "publicationAuthorsText");
        assertEquals("publicationAuthorsText", this.builder.currentEresource.getPublicationAuthorsText());
    }

    @Test
    void testEndElementPublicationAuthorTooLong() throws Exception {
        this.builder.currentText.append("publicationAuthor");
        this.builder.currentEresource = new SAXEresource();
        this.builder.endElement(null, null, "publicationAuthor");
        assertEquals("publicationAuthor",
                this.builder.currentEresource.getPublicationAuthors().stream().findFirst().get());
    }

    @Test
    void testEndElementPublicationDate() throws Exception {
        this.builder.currentText.append("publicationDate");
        this.builder.currentEresource = new SAXEresource();
        this.builder.endElement(null, null, "publicationDate");
        assertEquals("publicationDate", this.builder.currentEresource.getPublicationDate());
    }

    @Test
    void testEndElementPublicationIssue() throws Exception {
        this.builder.currentText.append("publicationIssue");
        this.builder.currentEresource = new SAXEresource();
        this.builder.endElement(null, null, "publicationIssue");
        assertEquals("publicationIssue", this.builder.currentEresource.getPublicationIssue());
    }

    @Test
    void testEndElementPublicationLanguage() throws Exception {
        this.builder.currentText.append("spa");
        this.builder.currentEresource = new SAXEresource();
        this.builder.endElement(null, null, "publicationLanguage");
        assertEquals("Spanish", this.builder.currentEresource.getPublicationLanguages().stream().findFirst().get());
    }

    @Test
    void testEndElementPublicationPages() throws Exception {
        this.builder.currentText.append("publicationPages");
        this.builder.currentEresource = new SAXEresource();
        this.builder.endElement(null, null, "publicationPages");
        assertEquals("publicationPages", this.builder.currentEresource.getPublicationPages());
    }

    @Test
    void testEndElementPublicationTitle() throws Exception {
        this.builder.currentText.append("publicationTitle");
        this.builder.currentEresource = new SAXEresource();
        this.builder.endElement(null, null, "publicationTitle");
        assertEquals("publicationTitle", this.builder.currentEresource.getPublicationTitle());
    }

    @Test
    void testEndElementPublicationType() throws Exception {
        this.builder.currentText.append("publicationType");
        this.builder.currentEresource = new SAXEresource();
        this.builder.endElement(null, null, "publicationType");
        assertEquals("publicationType", this.builder.currentEresource.getPublicationTypes().stream().findFirst().get());
    }

    @Test
    void testEndElementPublicationVolume() throws Exception {
        this.builder.currentText.append("publicationVolume");
        this.builder.currentEresource = new SAXEresource();
        this.builder.endElement(null, null, "publicationVolume");
        assertEquals("publicationVolume", this.builder.currentEresource.getPublicationVolume());
    }

    @Test
    void testEndElementUnknown() {
        assertThrows(EresourceDatabaseException.class, () -> {
            this.builder.endElement(null, null, "unknown-tag");
        });
    }

    @Test
    void testStartElementEresource() throws Exception {
        expect(this.attributes.getValue("recordId")).andReturn("recordId").times(2);
        expect(this.attributes.getValue("type")).andReturn("type");
        expect(this.attributes.getValue("id")).andReturn("id");
        expect(this.specialTypesManager.getTypes("recordId"))
                .andReturn(Collections.singletonList(new String[] { "publicationType", "special" }));
        EasyMock.replay(this.eresourceHandler, this.attributes, this.specialTypesManager);
        this.builder.startElement(null, null, "eresource", this.attributes);
        EasyMock.verify(this.eresourceHandler, this.attributes, this.specialTypesManager);
    }
}