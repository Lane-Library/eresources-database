package edu.stanford.irt.eresources.marc;

import static org.easymock.EasyMock.mock;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import edu.stanford.irt.eresources.CatalogRecordService;
import edu.stanford.lane.catalog.FolioRecord;

public class BibFolioEresourceTest extends MARCRecordSupport {

    private BibFolioEresource eresource;

    private HTTPLaneLocationsService locationsService;

    private FolioRecord record;

    CatalogRecordService recordService;

    @Before
    public void setUp() throws Exception {
        this.record = new FolioRecord(
                BibFolioEresourceTest.class.getResourceAsStream("folio-record.json").readAllBytes());
        this.locationsService = mock(HTTPLaneLocationsService.class);
        this.eresource = new BibFolioEresource(this.record, this.locationsService);
    }

    @Test
    public void testGetAbbreviatedTitles() {
        assertTrue(this.eresource.getAbbreviatedTitles().isEmpty());
    }

    @Test
    public void testGetAlternativeTitles() {
        assertEquals("alt title", this.eresource.getAlternativeTitles().stream().findFirst().get());
    }

    @Test
    public void testGetBroadMeshTerms() {
        assertTrue(this.eresource.getBroadMeshTerms().isEmpty());
    }

    @Test
    public void testGetDate() {
        assertEquals("19000101", this.eresource.getDate());
    }

    @Test
    public void testGetDescription() {
        assertEquals("test note", this.eresource.getDescription());
    }

    @Test
    public void testGetId() {
        assertEquals("bib-00000000125", this.eresource.getId());
    }

    @Test
    public void testGetIsbn() {
        assertTrue(this.eresource.getIsbns().contains("fake-isbn"));
    }

    @Test
    public void testGetIssn() {
        assertTrue(this.eresource.getIssns().contains("fake-issn"));
    }

    @Test
    public void testGetItemCount() {
        int[] count = this.eresource.getItemCount();
        assertEquals(1, count[0]);
        assertEquals(1, count[1]);
    }

    @Test
    public void testGetKeywords() {
        assertTrue(this.eresource.getKeywords().contains("Lane .Digital: Collection"));
    }

    @Test
    public void testGetMeshTerms() {
        assertTrue(this.eresource.getMeshTerms().contains("Test Subject"));
    }

    @Test
    public void testGetPrimaryTypeText() throws Exception {
        this.record = new FolioRecord(
                BibFolioEresourceTest.class.getResourceAsStream("folio-record-equipment.json").readAllBytes());
        this.eresource = new BibFolioEresource(this.record, this.locationsService);
        assertEquals("Equipment", this.eresource.getPrimaryType());
        assertTrue(this.eresource.getTypes().contains("Equipment"));
    }

    @Test
    public void testGetPublicationAuthorsText() {
        assertEquals("Org 1; Org 2.", this.eresource.getPublicationAuthorsText());
    }

    @Test
    public void testGetPublicationLanguages() {
        assertTrue(this.eresource.getPublicationLanguages().isEmpty());
    }

    @Test
    public void testGetPublicationTextTitleEtc() {
        assertNull(this.eresource.getPublicationTitle());
    }

    @Test
    public void testGetRecordId() {
        assertEquals("00000000125", this.eresource.getRecordId());
    }

    @Test
    public void testGetRecordType() {
        assertEquals("bib", this.eresource.getRecordType());
    }

    @Test
    public void testGetShortTitle() {
        assertEquals("Test title", this.eresource.getShortTitle());
    }

    @Test
    public void testGetSortTitle() {
        assertEquals("Test title", this.eresource.getSortTitle());
    }

    @Test
    public void testGetTitle() {
        assertEquals("Test Title", this.eresource.getTitle());
    }

    @Test
    public void testGetVersions() {
        assertEquals(1, this.eresource.getVersions().size());
    }

    @Test
    public void testGetYear() {
        assertEquals(1900, this.eresource.getYear());
    }

    @Test
    public void testIsEnglish() {
        assertFalse(this.eresource.isEnglish());
    }
}
