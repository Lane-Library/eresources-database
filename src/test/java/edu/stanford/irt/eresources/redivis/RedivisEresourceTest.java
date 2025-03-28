package edu.stanford.irt.eresources.redivis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

class RedivisEresourceTest {

    private Result dataset;

    private RedivisEresource eresource;

    @BeforeEach
    void setUp() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        DatasetList datasets = mapper.readValue(RedivisDatasetTest.class.getResourceAsStream("datasets.json"),
                DatasetList.class);
        this.dataset = datasets.getResults().get(0);
        this.eresource = new RedivisEresource(this.dataset);
    }

    @Test
    final void testGetAbbreviatedTitles() {
        assertTrue(this.eresource.getAbbreviatedTitles().isEmpty());
    }

    @Test
    final void testGetAlternativeTitles() {
        assertTrue(this.eresource.getAlternativeTitles().isEmpty());
    }

    @Test
    final void testGetBroadMeshTerms() {
        assertTrue(this.eresource.getBroadMeshTerms().isEmpty());
    }

    @Test
    final void testGetDate() {
        assertEquals("20200101", this.eresource.getDate());
    }

    @Test
    final void testGetDescription() {
        assertTrue(this.eresource.getDescription().contains("Born in Bradford study"));
    }

    @Test
    final void testGetId() {
        assertEquals("redivis-1634130", this.eresource.getId());
    }

    @Test
    final void testGetIssns() {
        assertTrue(this.eresource.getIssns().isEmpty());
    }

    @Test
    final void testGetItemCount() {
        assertEquals(0, this.eresource.getItemCount()[0]);
    }

    @Test
    final void testGetKeywords() {
        assertTrue(this.eresource.getKeywords().contains("wellbeing"));
        assertTrue(this.eresource.getKeywords().contains("SPHS"));
    }

    @Test
    final void testGetMeshTerms() {
        assertTrue(this.eresource.getMeshTerms().isEmpty());
    }

    @Test
    final void testGetPrimaryType() {
        assertEquals("Dataset", this.eresource.getPrimaryType());
    }

    @Test
    final void testGetPublicationAuthors() {
        assertTrue(this.eresource.getPublicationAuthors().isEmpty());
    }

    @Test
    final void testGetPublicationAuthorsText() {
        assertNull(this.eresource.getPublicationAuthorsText());
    }

    @Test
    final void testGetPublicationDate() {
        assertNull(this.eresource.getPublicationDate());
    }

    @Test
    final void testGetPublicationIssue() {
        assertNull(this.eresource.getPublicationIssue());
    }

    @Test
    final void testGetPublicationLanguages() {
        assertTrue(this.eresource.getPublicationLanguages().isEmpty());
    }

    @Test
    final void testGetPublicationPages() {
        assertNull(this.eresource.getPublicationPages());
    }

    @Test
    final void testGetPublicationText() {
        assertNull(this.eresource.getPublicationText());
    }

    @Test
    final void testGetPublicationTitle() {
        assertNull(this.eresource.getPublicationTitle());
    }

    @Test
    final void testGetPublicationTypes() {
        assertTrue(this.eresource.getPublicationTypes().isEmpty());
    }

    @Test
    final void testGetPublicationVolume() {
        assertNull(this.eresource.getPublicationVolume());
    }

    @Test
    final void testGetRecordId() {
        assertEquals("1634130", this.eresource.getRecordId());
    }

    @Test
    final void testGetRecordType() {
        assertEquals("redivis", this.eresource.getRecordType());
    }

    @Test
    final void testGetShortTitle() {
        assertNull(this.eresource.getShortTitle());
    }

    @Test
    final void testGetSortTitle() {
        assertNull(this.eresource.getSortTitle());
    }

    @Test
    final void testGetTitle() {
        assertEquals("Born in Bradford", this.eresource.getTitle());
    }

    @Test
    final void testGetTypes() {
        assertTrue(this.eresource.getTypes().contains("Dataset"));
    }

    @Test
    final void testGetVersions() {
        assertFalse(this.eresource.getVersions().isEmpty());
    }

    @Test
    final void testGetYear() {
        assertEquals(2020, this.eresource.getYear());
    }

    @Test
    final void testIsEnglish() {
        assertTrue(this.eresource.isEnglish());
    }
}
