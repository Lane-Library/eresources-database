package edu.stanford.irt.eresources.marc;

import static org.easymock.EasyMock.mock;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.stanford.irt.eresources.CatalogRecordService;
import edu.stanford.lane.catalog.FolioRecord;

class BibFolioEresourceTest extends MARCRecordSupport {

    private BibFolioEresource eresource;

    private HTTPLaneLocationsService locationsService;

    private FolioRecord record;

    CatalogRecordService recordService;

    @BeforeEach
    void setUp() throws Exception {
        this.record = new FolioRecord(
                BibFolioEresourceTest.class.getResourceAsStream("folio-record.json").readAllBytes());
        this.locationsService = mock(HTTPLaneLocationsService.class);
        this.eresource = new BibFolioEresource(this.record, this.locationsService);
    }

    @Test
    void testGetAbbreviatedTitles() {
        assertTrue(this.eresource.getAbbreviatedTitles().isEmpty());
    }

    @Test
    void testGetAlternativeTitles() {
        assertEquals("alt title", this.eresource.getAlternativeTitles().stream().findFirst().get());
    }

    @Test
    void testGetBroadMeshTerms() {
        assertTrue(this.eresource.getBroadMeshTerms().isEmpty());
    }

    @Test
    void testGetDate() {
        assertEquals("19000101", this.eresource.getDate());
    }

    @Test
    void testGetDescription() {
        assertEquals("test note", this.eresource.getDescription());
    }

    @Test
    void testGetId() {
        assertEquals("bib-00000000125", this.eresource.getId());
    }

    @Test
    void testGetIsbn() {
        assertTrue(this.eresource.getIsbns().contains("fake-isbn"));
    }

    @Test
    void testGetIssn() {
        assertTrue(this.eresource.getIssns().contains("fake-issn"));
    }

    @Test
    void testGetItemCount() {
        int[] count = this.eresource.getItemCount();
        assertEquals(1, count[0]);
        assertEquals(1, count[1]);
    }

    @Test
    void testGetKeywords() {
        assertTrue(this.eresource.getKeywords().contains("Lane .Digital: Collection"));
    }

    @Test
    void testGetMeshTerms() {
        assertTrue(this.eresource.getMeshTerms().contains("Test Subject"));
    }

    @Test
    void testGetPrimaryTypeText() throws Exception {
        this.record = new FolioRecord(
                BibFolioEresourceTest.class.getResourceAsStream("folio-record-equipment.json").readAllBytes());
        this.eresource = new BibFolioEresource(this.record, this.locationsService);
        assertEquals("Equipment", this.eresource.getPrimaryType());
        assertTrue(this.eresource.getTypes().contains("Equipment"));
    }

    @Test
    void testGetPublicationAuthorsText() {
        assertEquals("Org 1; Org 2.", this.eresource.getPublicationAuthorsText());
    }

    @Test
    void testGetPublicationLanguages() {
        assertTrue(this.eresource.getPublicationLanguages().isEmpty());
    }

    @Test
    void testGetPublicationTextTitleEtc() {
        assertNull(this.eresource.getPublicationTitle());
    }

    @Test
    void testGetRecordId() {
        assertEquals("00000000125", this.eresource.getRecordId());
    }

    @Test
    void testGetRecordType() {
        assertEquals("bib", this.eresource.getRecordType());
    }

    @Test
    void testGetShortTitle() {
        assertEquals("Test title", this.eresource.getShortTitle());
    }

    @Test
    void testGetSortTitle() {
        assertEquals("Test title", this.eresource.getSortTitle());
    }

    @Test
    void testGetTitle() {
        assertEquals("Test Title", this.eresource.getTitle());
    }

    @Test
    void testGetVersions() {
        assertEquals(1, this.eresource.getVersions().size());
    }

    @Test
    void testGetYear() {
        assertEquals(1900, this.eresource.getYear());
    }

    @Test
    void testIsEnglish() {
        assertTrue(this.eresource.isEnglish());
    }
}
