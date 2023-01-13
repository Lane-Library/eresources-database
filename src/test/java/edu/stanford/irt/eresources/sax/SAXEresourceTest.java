package edu.stanford.irt.eresources.sax;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;

import org.junit.Test;

public class SAXEresourceTest {

    @Test
    public final void testSaxEresource() {
        SAXEresource eresource = new SAXEresource();
        SAXVersion version = new SAXVersion();
        eresource.addAbbreviatedTitle("abtitle");
        eresource.addAlternativeTitle("altitle");
        eresource.addBroadMeshTerm("broadMeshTerm");
        eresource.addMeshTerm("meshTerm");
        eresource.addPublicationAuthor("author");
        eresource.addPublicationAuthorFacetable("author");
        eresource.addPublicationLanguage("publicationLanguage");
        eresource.addPublicationType("publicationType");
        eresource.addType("type");
        eresource.addVersion(version);
        version.addLink(new SAXLink());
        eresource.addVersion(version);
        eresource.setDate("");
        eresource.setDescription("description");
        eresource.setId("id");
        eresource.setIsCore(true);
        eresource.setIsDigital(true);
        eresource.setIsLaneConnex(true);
        eresource.setKeywords("keywords");
        eresource.setPrimaryType("type");
        eresource.setPublicationAuthorsText(null);
        eresource.setPublicationDate("date");
        eresource.setPublicationIssue("publicationIssue");
        eresource.setPublicationPages("pages");
        eresource.setPublicationText(null);
        eresource.setPublicationTitle("publicationTitle");
        eresource.setPublicationVolume("publicationVolume");
        eresource.setRecordId("123");
        eresource.setRecordType("recordType");
        eresource.setShortTitle("shortTitle");
        eresource.setSortTitle("sortTitle");
        eresource.setTitle("title");
        eresource.setUpdated(LocalDateTime.now());
        eresource.setYear(2000);
        eresource.addIssn("1550-7416");
        assertNotNull(eresource.toString());
        assertNotNull(eresource.getAbbreviatedTitles());
        assertNotNull(eresource.getAlternativeTitles());
        assertNotNull(eresource.getBroadMeshTerms());
        assertNotNull(eresource.getDate());
        assertNotNull(eresource.getDescription());
        assertNotNull(eresource.getId());
        assertNotNull(eresource.getItemCount());
        assertNotNull(eresource.getKeywords());
        assertNotNull(eresource.getMeshTerms());
        assertNotNull(eresource.getPrimaryType());
        assertNotNull(eresource.getPublicationAuthors());
        assertNotNull(eresource.getPublicationAuthorsFacetable());
        assertNotNull(eresource.getPublicationAuthorsText());
        assertNotNull(eresource.getPublicationAuthorsText());
        assertNotNull(eresource.getPublicationDate());
        assertNotNull(eresource.getPublicationIssue());
        assertNotNull(eresource.getPublicationLanguages());
        assertNotNull(eresource.getPublicationPages());
        assertNotNull(eresource.getPublicationText());
        assertNotNull(eresource.getPublicationText());
        assertNotNull(eresource.getPublicationTitle());
        assertNotNull(eresource.getPublicationTypes());
        assertNotNull(eresource.getPublicationVolume());
        assertEquals("123",eresource.getRecordId());
        assertNotNull(eresource.getRecordType());
        assertNotNull(eresource.getShortTitle());
        assertNotNull(eresource.getSortTitle());
        assertNotNull(eresource.getTitle());
        assertNotNull(eresource.getTypes());
        assertNotNull(eresource.getUpdated());
        assertNotNull(eresource.getVersions());
        assertEquals(2000,eresource.getYear());
        assertTrue(eresource.isCore());
        assertTrue(eresource.isDigital());
        assertFalse(eresource.isEnglish());
        assertTrue(eresource.isLaneConnex());
        assertNotNull(eresource.getIssns());
    }

    @Test
    public final void testSaxEresourceNulls() {
        SAXEresource eresource = new SAXEresource();
        assertNull(eresource.getDate());
        eresource.setYear(2000);
        assertNotNull(eresource.getDate());
        assertTrue(eresource.getIssns().isEmpty());
        assertTrue(eresource.getIsbns().isEmpty());
    }
}
