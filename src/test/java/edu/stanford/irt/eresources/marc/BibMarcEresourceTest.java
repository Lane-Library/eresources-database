package edu.stanford.irt.eresources.marc;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.ZoneId;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import edu.stanford.irt.eresources.CatalogRecordService;
import edu.stanford.irt.eresources.Eresource;
import edu.stanford.irt.eresources.EresourceDatabaseException;
import edu.stanford.irt.eresources.ItemCount;
import edu.stanford.irt.eresources.SulFileCatalogRecordService;
import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.Record.Field;
import edu.stanford.lane.catalog.Record.Subfield;
import edu.stanford.lane.catalog.RecordCollection;

public class BibMarcEresourceTest extends MARCRecordSupport {

    private BibMarcEresource eresource;

    private Field field;

    private ItemCount itemCountBibs;

    private ItemCount itemCountHoldings;

    private KeywordsStrategy keywordsStrategy;

    private HTTPLaneLocationsService locationsService;

    private Record record;

    private Subfield subfield;

    private TypeFactory typeFactory;

    CatalogRecordService recordService;

    @Before
    public void setUp() {
        this.record = mock(Record.class);
        this.keywordsStrategy = mock(KeywordsStrategy.class);
        this.itemCountBibs = mock(ItemCount.class);
        this.itemCountHoldings = mock(ItemCount.class);
        this.typeFactory = mock(TypeFactory.class);
        this.locationsService = mock(HTTPLaneLocationsService.class);
        this.eresource = new BibMarcEresource(Arrays.asList(new Record[] { this.record, this.record }),
                this.keywordsStrategy, this.itemCountBibs, this.itemCountHoldings, this.typeFactory,
                this.locationsService);
        this.field = mock(Field.class);
        this.subfield = mock(Subfield.class);
    }

    @Test
    public void testGetAbbreviatedTitles() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("246");
        expect(this.field.getSubfields()).andReturn(Arrays.asList(new Subfield[] { this.subfield, this.subfield }))
                .times(2);
        expect(this.subfield.getCode()).andReturn('i');
        expect(this.subfield.getCode()).andReturn('a');
        // expect(this.subfield.getData()).andReturn("abbrv.");
        expect(this.subfield.getData()).andReturn("Acronym/initialism:");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('a');
        expect(this.subfield.getData()).andReturn("abbrv.");
        // expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        replay(this.record, this.field, this.subfield);
        assertEquals("abbrv.", this.eresource.getAbbreviatedTitles().stream().findFirst().get());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetAlternativeTitles() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("130");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('a');
        expect(this.subfield.getData()).andReturn("alt title");
        replay(this.record, this.field, this.subfield);
        assertEquals("alt title", this.eresource.getAlternativeTitles().stream().findFirst().get());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetAuthorsText() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("245");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('c');
        expect(this.subfield.getData()).andReturn("author");
        replay(this.record, this.field, this.subfield);
        assertEquals("author", this.eresource.getPublicationAuthorsText());
        verify(this.record);
    }

    @Test
    public void testGetBroadMeshTerms() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("650");
        expect(this.field.getIndicator1()).andReturn('4');
        expect(this.field.getIndicator2()).andReturn('2');
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('a');
        expect(this.subfield.getData()).andReturn("broad mesh.");
        replay(this.record, this.field, this.subfield);
        assertEquals("broad mesh", this.eresource.getBroadMeshTerms().stream().findFirst().get());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetDate() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("773");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('d').times(2);
        expect(this.subfield.getData()).andReturn("1951Sept;");
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field)).times(2);
        expect(this.field.getTag()).andReturn("008").times(2);
        expect(this.field.getData()).andReturn("950714s1951____xx____________00|_0_eng_d").times(2);
        replay(this.record, this.field, this.subfield);
        assertEquals("19510101", this.eresource.getDate());
    }

    @Test
    public void testGetDate77319690505() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("773");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('d').times(2);
        expect(this.subfield.getData()).andReturn("19690505");
        replay(this.record, this.field, this.subfield);
        assertEquals("19690505", this.eresource.getDate());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetDate77319910418() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("773");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('d').times(2);
        expect(this.subfield.getData()).andReturn("1991 Apr 18;");
        replay(this.record, this.field, this.subfield);
        assertEquals("19910418", this.eresource.getDate());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetDateTwo773() {
        expect(this.record.getFields()).andReturn(Arrays.asList(new Field[] { this.field, this.field }));
        expect(this.field.getTag()).andReturn("773").times(2);
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield)).times(2);
        expect(this.subfield.getCode()).andReturn('d').times(4);
        expect(this.subfield.getData()).andReturn("1843 Jul;");
        expect(this.subfield.getData()).andReturn("1843 Oct;");
        replay(this.record, this.field, this.subfield);
        assertEquals("18431001", this.eresource.getDate());
    }

    @Test
    public void testGetDescription505() {
        expect(this.record.getFields()).andReturn(Collections.emptyList());
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("505");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getData()).andReturn("data");
        replay(this.record, this.field, this.subfield);
        assertEquals("data", this.eresource.getDescription());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetDescription520() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("520");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getData()).andReturn("data");
        replay(this.record, this.field, this.subfield);
        assertEquals("data", this.eresource.getDescription());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetDescriptionNull() {
        expect(this.record.getFields()).andReturn(Collections.emptyList()).times(2);
        replay(this.record, this.field, this.subfield);
        assertNull(this.eresource.getDescription());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetIsbns() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("020");
        expect(this.field.getSubfields())
                .andReturn(Arrays.asList(new Subfield[] { this.subfield, this.subfield, this.subfield }));
        expect(this.subfield.getCode()).andReturn('a');
        expect(this.subfield.getData()).andReturn("123456789");
        expect(this.subfield.getCode()).andReturn('z');
        expect(this.subfield.getData()).andReturn("987654321");
        expect(this.subfield.getCode()).andReturn('l');
        replay(this.record, this.field, this.subfield);
        Collection<String> isbns = this.eresource.getIsbns();
        assertTrue(isbns.contains("123456789"));
        assertTrue(isbns.contains("987654321"));
        assertFalse(isbns.contains("X987654321"));
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetIssns() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("022");
        expect(this.field.getSubfields())
                .andReturn(Arrays.asList(new Subfield[] { this.subfield, this.subfield, this.subfield }));
        expect(this.subfield.getCode()).andReturn('a');
        expect(this.subfield.getData()).andReturn("123456789");
        expect(this.subfield.getCode()).andReturn('z');
        expect(this.subfield.getData()).andReturn("987654321");
        expect(this.subfield.getCode()).andReturn('l');
        expect(this.subfield.getData()).andReturn("X987654321");
        replay(this.record, this.field, this.subfield);
        Collection<String> issns = this.eresource.getIssns();
        assertTrue(issns.contains("123456789"));
        assertTrue(issns.contains("987654321"));
        assertTrue(issns.contains("x987654321"));
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetItemCount() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("001");
        expect(this.field.getData()).andReturn("1");
        expect(this.itemCountBibs.itemCount(1)).andReturn(new int[] { 1, 1 });
        replay(this.record, this.field, this.subfield, this.itemCountBibs);
        int[] count = this.eresource.getItemCount();
        assertEquals(1, count[0]);
        assertEquals(1, count[1]);
        verify(this.record, this.field, this.subfield, this.itemCountBibs);
    }

    @Test
    public void testGetMeshTerms() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("650").times(2);
        expect(this.field.getIndicator2()).andReturn('2');
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('a');
        expect(this.subfield.getData()).andReturn("data");
        replay(this.record, this.field, this.subfield);
        this.eresource.getMeshTerms();
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetPublicationAuthorsText() {
        expect(this.field.getTag()).andReturn("100").times(4);
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield)).times(2);
        expect(this.subfield.getCode()).andReturn('a').times(2);
        expect(this.subfield.getData()).andReturn("author").times(2);
        expect(this.record.getFields()).andReturn(Collections.emptyList());
        expect(this.record.getFields()).andReturn(Arrays.asList(new Field[] { this.field, this.field }));
        replay(this.record, this.field, this.subfield);
        assertEquals("author; author.", this.eresource.getPublicationAuthorsText());
        verify(this.record);
    }

    @Test
    public void testGetPublicationAuthorsText245TwoC() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("245");
        expect(this.field.getSubfields()).andReturn(Arrays.asList(new Subfield[] { this.subfield, this.subfield }));
        expect(this.subfield.getCode()).andReturn('c').times(2);
        expect(this.subfield.getData()).andReturn("first c");
        expect(this.subfield.getData()).andReturn("author");
        replay(this.record, this.field, this.subfield);
        assertEquals("author", this.eresource.getPublicationAuthorsText());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetPublicationLanguages() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("008");
        expect(this.field.getData()).andReturn("860402s1982____ja_______d____00|10_jpn_d");
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("041");
        expect(this.field.getSubfields()).andReturn(Arrays.asList(new Subfield[] { this.subfield, this.subfield }));
        expect(this.subfield.getData()).andReturn("Jpn");
        expect(this.subfield.getData()).andReturn("eng");
        replay(this.record, this.field, this.subfield);
        assertArrayEquals(new String[] { "English", "Japanese" },
                this.eresource.getPublicationLanguages().toArray(new String[2]));
    }

    @Test
    public void testGetPublicationTextTitleEtc() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.initialize();
        this.recordService = new SulFileCatalogRecordService("src/test/resources/edu/stanford/irt/eresources/marc/lane",
                executor);
        RecordCollection rc = new RecordCollection(this.recordService.getRecordStream(0));
        while (rc.hasNext()) {
            Record rec = rc.next();
            if (168269 == getRecordId(rec)) {
                Eresource er = new BibMarcEresource(Arrays.asList(new Record[] { rec, this.record }),
                        this.keywordsStrategy, this.itemCountBibs, this.itemCountHoldings, this.typeFactory,
                        this.locationsService);
                assertEquals("bib-168269", er.getId());
                assertEquals(null, er.getPublicationDate());
                assertEquals(null, er.getPublicationIssue());
                assertEquals(null, er.getPublicationPages());
                assertTrue(er.getPublicationTypes().isEmpty());
                assertEquals(null, er.getPublicationVolume());
                assertFalse(er.isLaneConnex());
                expect(this.typeFactory.getTypes(rec)).andReturn(Collections.singletonList("a type"));
                expect(this.keywordsStrategy.getKeywords(isA(Record.class))).andReturn("keywords").times(2);
                replay(this.typeFactory, this.keywordsStrategy);
                assertTrue(er.getKeywords().contains("keywords"));
                assertTrue(er.getTypes().contains("a type"));
                verify(this.typeFactory, this.keywordsStrategy);
                assertEquals("Petrosilinum vel persil, materia medica].", er.getShortTitle());
                assertEquals("[Opera chirurgica]..  [ca. 1400] fol. 66 [i.e. 26]", er.getPublicationText());
                assertEquals("[Opera chirurgica].", er.getPublicationTitle());
            }
            if (67043 == getRecordId(rec)) {
                Eresource er = new BibMarcEresource(Arrays.asList(new Record[] { rec, this.record }),
                        this.keywordsStrategy, this.itemCountBibs, this.itemCountHoldings, this.typeFactory,
                        this.locationsService);
                assertEquals("[Collection of reprints by John Uri Lloyd from the Western Druggist]. ",
                        er.getPublicationText());
                assertEquals("[Collection of reprints by John Uri Lloyd from the Western Druggist]",
                        er.getPublicationTitle());
            }
            if (77614 == getRecordId(rec)) {
                Eresource er = new BibMarcEresource(Arrays.asList(new Record[] { rec, this.record }),
                        this.keywordsStrategy, this.itemCountBibs, this.itemCountHoldings, this.typeFactory,
                        this.locationsService);
                assertEquals("Stanford University Medical Center Records. ", er.getPublicationText());
                assertEquals("Stanford University Medical Center Records", er.getPublicationTitle());
            }
            if (21171 == getRecordId(rec)) {
                Eresource er = new BibMarcEresource(Arrays.asList(new Record[] { rec, this.record }),
                        this.keywordsStrategy, this.itemCountBibs, this.itemCountHoldings, this.typeFactory,
                        this.locationsService);
                assertEquals(
                        "Clinical pharmacology and therapeutics.  1981 Sep-; 30(3)-; Journal of the American Medical Association 1972 Nov 27-1981 Feb 27; 222(9)-245(8)",
                        er.getPublicationText());
                assertEquals("Clinical pharmacology and therapeutics", er.getPublicationTitle());
            }
        }
    }

    @Test
    public void testGetRecordId() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("001");
        expect(this.field.getData()).andReturn("12");
        replay(this.record, this.field, this.subfield);
        assertEquals(12, this.eresource.getRecordId());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetRecordType() {
        replay(this.record, this.field, this.subfield);
        assertEquals("bib", this.eresource.getRecordType());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetSortTitle() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("245");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('a').times(2);
        expect(this.subfield.getData()).andReturn("title");
        expect(this.record.getFields()).andReturn(Collections.emptyList());
        replay(this.record, this.field, this.subfield);
        assertEquals("title", this.eresource.getSortTitle());
    }

    @Test
    public void testGetTitle() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("245");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('a').times(2);
        expect(this.subfield.getData()).andReturn("title");
        expect(this.record.getFields()).andReturn(Collections.emptyList());
        replay(this.record, this.field, this.subfield);
        assertEquals("title", this.eresource.getTitle());
    }

    @Test
    public void testGetTitleTrailingSlash() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("245");
        expect(this.field.getSubfields()).andReturn(Arrays.asList(new Subfield[] { this.subfield, this.subfield }));
        expect(this.subfield.getCode()).andReturn('a').times(2);
        expect(this.subfield.getData()).andReturn("HELP with nursing audit and quality assurance");
        expect(this.subfield.getCode()).andReturn('b').times(2);
        expect(this.subfield.getData()).andReturn("management guide.../");
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("250");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('a');
        expect(this.subfield.getData()).andReturn("3rd ed.");
        replay(this.record, this.field, this.subfield);
        assertEquals("HELP with nursing audit and quality assurance : management guide.... 3rd ed.",
                this.eresource.getTitle());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetUpdated() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field)).times(2);
        expect(this.field.getTag()).andReturn("005").times(2);
        expect(this.field.getData()).andReturn("19550519120000").times(2);
        replay(this.record, this.field, this.subfield);
        Calendar cal = Calendar.getInstance();
        cal.set(1955, 4, 19, 12, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        assertEquals(cal.getTimeInMillis(),
                this.eresource.getUpdated().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetUpdatedHoldingsNewer() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field)).times(2);
        expect(this.field.getTag()).andReturn("005").times(2);
        expect(this.field.getData()).andReturn("19550519120000");
        expect(this.field.getData()).andReturn("19690505120000");
        replay(this.record, this.field, this.subfield);
        Calendar cal = Calendar.getInstance();
        cal.set(1969, 4, 5, 12, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        assertEquals(cal.getTimeInMillis(),
                this.eresource.getUpdated().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        verify(this.record, this.field, this.subfield);
    }

    @Test(expected = EresourceDatabaseException.class)
    public void testGetUpdatedParseException() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("005");
        expect(this.field.getData()).andReturn("notavaliddatestring");
        replay(this.record, this.field, this.subfield);
        this.eresource.getUpdated();
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetVersions() {
        BibMarcEresource e = new BibMarcEresource(Arrays.asList(new Record[] { this.record, this.record }), null, null,
                null, this.typeFactory, this.locationsService);
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field)).atLeastOnce();
        expect(this.field.getTag()).andReturn("856").atLeastOnce();
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield)).atLeastOnce();
        expect(this.subfield.getCode()).andReturn('u').atLeastOnce();
        expect(this.subfield.getData()).andReturn("url").atLeastOnce();
        expect(this.field.getIndicator1()).andReturn('4').atLeastOnce();
        expect(this.field.getIndicator2()).andReturn('1').atLeastOnce();
        expect(this.typeFactory.getPrimaryType(this.record)).andReturn("primary type").atLeastOnce();
        replay(this.record, this.field, this.subfield, this.typeFactory);
        assertEquals(1, e.getVersions().size());
        verify(this.record, this.field, this.subfield, this.typeFactory);
    }

    @Test
    public void testGetYear() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("008");
        expect(this.field.getData()).andReturn("012345619551969");
        replay(this.record, this.field, this.subfield);
        assertEquals(1969, this.eresource.getYear());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetYearNullDates() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("008");
        expect(this.field.getData()).andReturn("0123456xxxxxxxx");
        replay(this.record, this.field, this.subfield);
        assertEquals(0, this.eresource.getYear());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetYearNullEnddate() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("008");
        expect(this.field.getData()).andReturn("01234561955xxxx");
        replay(this.record, this.field, this.subfield);
        assertEquals(1955, this.eresource.getYear());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testIsCore() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("655");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('a');
        expect(this.subfield.getData()).andReturn("Core Material");
        replay(this.record, this.field, this.subfield);
        assertTrue(this.eresource.isCore());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testIsCoreNot() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("655");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('a');
        expect(this.subfield.getData()).andReturn("not core");
        replay(this.record, this.field, this.subfield);
        assertFalse(this.eresource.isCore());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testIsEnglish() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("008");
        expect(this.field.getData()).andReturn("860402s1982____ja_______d____00|10_jpn_d");
        replay(this.record, this.field);
        assertFalse(this.eresource.isEnglish());
        reset(this.record, this.field);
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("008");
        expect(this.field.getData()).andReturn("950714s1951____xx____________00|_0_eng_d");
        replay(this.record, this.field);
        assertTrue(this.eresource.isEnglish());
    }
}
