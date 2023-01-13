package edu.stanford.irt.eresources.redivis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RedivisEresourceTest {

    private Result dataset;

    private RedivisEresource eresource;

    @Before
    public void setUp() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        DatasetList datasets = mapper.readValue(RedivisDatasetTest.class.getResourceAsStream("datasets.json"),
                DatasetList.class);
        this.dataset = datasets.getResults().get(0);
        this.eresource = new RedivisEresource(this.dataset);
    }

    @Test
    public final void testGetAbbreviatedTitles() {
        assertTrue(this.eresource.getAbbreviatedTitles().isEmpty());
    }

    @Test
    public final void testGetAlternativeTitles() {
        assertTrue(this.eresource.getAlternativeTitles().isEmpty());
    }

    @Test
    public final void testGetBroadMeshTerms() {
        assertTrue(this.eresource.getBroadMeshTerms().isEmpty());
    }

    @Test
    public final void testGetDate() {
        assertEquals("20200101", this.eresource.getDate());
    }

    @Test
    public final void testGetDescription() {
        assertTrue(this.eresource.getDescription().contains("Born in Bradford study"));
    }

    @Test
    public final void testGetId() {
        assertEquals("redivis-1634130", this.eresource.getId());
    }

    @Test
    public final void testGetIssns() {
        assertTrue(this.eresource.getIssns().isEmpty());
    }

    @Test
    public final void testGetItemCount() {
        assertEquals(0, this.eresource.getItemCount()[0]);
    }

    @Test
    public final void testGetKeywords() {
        assertTrue(this.eresource.getKeywords().contains("wellbeing"));
        assertTrue(this.eresource.getKeywords().contains("SPHS"));
    }

    @Test
    public final void testGetMeshTerms() {
        assertTrue(this.eresource.getMeshTerms().isEmpty());
    }

    @Test
    public final void testGetPrimaryType() {
        assertEquals("Dataset", this.eresource.getPrimaryType());
    }

    @Test
    public final void testGetPublicationAuthors() {
        assertTrue(this.eresource.getPublicationAuthors().isEmpty());
    }

    @Test
    public final void testGetPublicationAuthorsText() {
        assertNull(this.eresource.getPublicationAuthorsText());
    }

    @Test
    public final void testGetPublicationDate() {
        assertNull(this.eresource.getPublicationDate());
    }

    @Test
    public final void testGetPublicationIssue() {
        assertNull(this.eresource.getPublicationIssue());
    }

    @Test
    public final void testGetPublicationLanguages() {
        assertTrue(this.eresource.getPublicationLanguages().isEmpty());
    }

    @Test
    public final void testGetPublicationPages() {
        assertNull(this.eresource.getPublicationPages());
    }

    @Test
    public final void testGetPublicationText() {
        assertNull(this.eresource.getPublicationText());
    }

    @Test
    public final void testGetPublicationTitle() {
        assertNull(this.eresource.getPublicationTitle());
    }

    @Test
    public final void testGetPublicationTypes() {
        assertTrue(this.eresource.getPublicationTypes().isEmpty());
    }

    @Test
    public final void testGetPublicationVolume() {
        assertNull(this.eresource.getPublicationVolume());
    }

    @Test
    public final void testGetRecordId() {
        assertEquals("1634130", this.eresource.getRecordId());
    }

    @Test
    public final void testGetRecordType() {
        assertEquals("redivis", this.eresource.getRecordType());
    }

    @Test
    public final void testGetShortTitle() {
        assertNull(this.eresource.getShortTitle());
    }

    @Test
    public final void testGetSortTitle() {
        assertNull(this.eresource.getSortTitle());
    }

    @Test
    public final void testGetTitle() {
        assertEquals("Born in Bradford", this.eresource.getTitle());
    }

    @Test
    public final void testGetTypes() {
        assertTrue(this.eresource.getTypes().contains("Dataset"));
    }

    @Test
    public final void testGetUpdated() {
        assertTrue(this.eresource.getUpdated().isBefore(LocalDateTime.now()));
    }

    @Test
    public final void testGetVersions() {
        assertFalse(this.eresource.getVersions().isEmpty());
    }

    @Test
    public final void testGetYear() {
        assertEquals(2020, this.eresource.getYear());
    }

    @Test
    public final void testIsCore() {
        assertFalse(this.eresource.isCore());
    }

    @Test
    public final void testIsEnglish() {
        assertTrue(this.eresource.isEnglish());
    }

    @Test
    public final void testIsLaneConnex() {
        assertFalse(this.eresource.isLaneConnex());
    }
}
