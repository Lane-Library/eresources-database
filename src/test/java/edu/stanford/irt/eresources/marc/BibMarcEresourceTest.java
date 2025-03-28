package edu.stanford.irt.eresources.marc;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Stream;

import org.easymock.EasyMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import edu.stanford.irt.eresources.CatalogRecordService;
import edu.stanford.irt.eresources.Eresource;
import edu.stanford.irt.eresources.FileCatalogRecordService;
import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.Record.Field;
import edu.stanford.lane.catalog.Record.Subfield;
import edu.stanford.lane.catalog.RecordCollection;

class BibMarcEresourceTest extends MARCRecordSupport {

    private static Stream<Arguments> provideYearTestCases() {
        return Stream.of(
                Arguments.of("012345619551969", 1969), // Valid year
                Arguments.of("0123456xxxxxxxx", 0), // Null dates
                Arguments.of("01234561955xxxx", 1955) // Null end date
        );
    }

    private BibMarcEresource eresource;

    private Field field;

    private KeywordsStrategy keywordsStrategy;

    private HTTPLaneLocationsService locationsService;

    private Record rec;

    private Subfield subfield;

    CatalogRecordService recordService;

    @BeforeEach
    void setUp() {
        this.rec = mock(Record.class);
        this.keywordsStrategy = mock(KeywordsStrategy.class);
        this.locationsService = mock(HTTPLaneLocationsService.class);
        this.eresource = new BibMarcEresource(Arrays.asList(new Record[] { this.rec, this.rec }),
                this.keywordsStrategy, this.locationsService);
        this.field = mock(Field.class);
        this.subfield = mock(Subfield.class);
    }

    @Test
    void testGetAbbreviatedTitles() {
        expect(this.rec.getFields()).andReturn(Collections.singletonList(this.field));
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
        // expect(this.rec.getFields()).andReturn(Collections.singletonList(this.field));
        replay(this.rec, this.field, this.subfield);
        assertEquals("abbrv.", this.eresource.getAbbreviatedTitles().stream().findFirst().get());
        verify(this.rec, this.field, this.subfield);
    }

    @Test
    void testGetAbbreviatedTitlesNull() {
        expect(this.rec.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("246");
        expect(this.field.getSubfields()).andReturn(Arrays.asList(new Subfield[] { this.subfield, this.subfield }))
                .times(2);
        expect(this.subfield.getCode()).andReturn('z').times(4);
        replay(this.rec, this.field, this.subfield);
        assertTrue(this.eresource.getAbbreviatedTitles().isEmpty());
        verify(this.rec, this.field, this.subfield);
    }

    @Test
    void testGetAlternativeTitles() {
        expect(this.rec.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("130");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('a');
        expect(this.subfield.getData()).andReturn("alt title");
        replay(this.rec, this.field, this.subfield);
        assertEquals("alt title", this.eresource.getAlternativeTitles().stream().findFirst().get());
        verify(this.rec, this.field, this.subfield);
    }

    @Test
    void testGetAuthorsText() {
        expect(this.rec.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("245");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('c');
        expect(this.subfield.getData()).andReturn("author");
        replay(this.rec, this.field, this.subfield);
        assertEquals("author", this.eresource.getPublicationAuthorsText());
        verify(this.rec);
    }

    @Test
    void testGetBroadMeshTerms() {
        expect(this.rec.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("650");
        expect(this.field.getIndicator1()).andReturn('4');
        expect(this.field.getIndicator2()).andReturn('2');
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('a');
        expect(this.subfield.getData()).andReturn("broad mesh.");
        replay(this.rec, this.field, this.subfield);
        assertEquals("broad mesh", this.eresource.getBroadMeshTerms().stream().findFirst().get());
        verify(this.rec, this.field, this.subfield);
    }

    @Test
    void testGetDate() {
        expect(this.rec.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("773");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('d').times(2);
        expect(this.subfield.getData()).andReturn("1951Sept;");
        expect(this.rec.getFields()).andReturn(Collections.singletonList(this.field)).times(2);
        expect(this.field.getTag()).andReturn("008").times(2);
        expect(this.field.getData()).andReturn("950714s1951____xx____________00|_0_eng_d").times(2);
        replay(this.rec, this.field, this.subfield);
        assertEquals("19510101", this.eresource.getDate());
    }

    @Test
    void testGetDate77319690505() {
        expect(this.rec.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("773");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('d').times(2);
        expect(this.subfield.getData()).andReturn("19690505");
        replay(this.rec, this.field, this.subfield);
        assertEquals("19690505", this.eresource.getDate());
        verify(this.rec, this.field, this.subfield);
    }

    @Test
    void testGetDate77319910418() {
        expect(this.rec.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("773");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('d').times(2);
        expect(this.subfield.getData()).andReturn("1991 Apr 18;");
        replay(this.rec, this.field, this.subfield);
        assertEquals("19910418", this.eresource.getDate());
        verify(this.rec, this.field, this.subfield);
    }

    @Test
    void testGetDateTwo773() {
        expect(this.rec.getFields()).andReturn(Arrays.asList(new Field[] { this.field, this.field }));
        expect(this.field.getTag()).andReturn("773").times(2);
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield)).times(2);
        expect(this.subfield.getCode()).andReturn('d').times(4);
        expect(this.subfield.getData()).andReturn("1843 Jul;");
        expect(this.subfield.getData()).andReturn("1843 Oct;");
        replay(this.rec, this.field, this.subfield);
        assertEquals("18431001", this.eresource.getDate());
    }

    @Test
    void testGetDescription505() {
        expect(this.rec.getFields()).andReturn(Collections.singletonList(this.field)).anyTimes();
        expect(this.field.getTag()).andReturn("505").anyTimes();
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield)).anyTimes();
        expect(this.subfield.getData()).andReturn(
                "1 Introduction and general issues -- 2 Tools, facilities, and the operating team -- 3 Anesthesia for cataract surgery");
        replay(this.rec, this.field, this.subfield);
        assertEquals(
                "::Contents##<br/>1 Introduction and general issues<br/>2 Tools, facilities, and the operating team<br/>3 Anesthesia for cataract surgery",
                this.eresource.getDescription());
        verify(this.rec, this.field, this.subfield);
    }

    @Test
    void testGetDescription520() {
        expect(this.rec.getFields()).andReturn(Collections.singletonList(this.field)).anyTimes();
        expect(this.field.getTag()).andReturn("520").anyTimes();
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield)).anyTimes();
        expect(this.subfield.getData()).andReturn("data");
        replay(this.rec, this.field, this.subfield);
        assertEquals("::Summary## data", this.eresource.getDescription());
        verify(this.rec, this.field, this.subfield);
    }

    @Test
    void testGetDescriptionNull() {
        expect(this.rec.getFields()).andReturn(Collections.emptyList()).anyTimes();
        replay(this.rec, this.field, this.subfield);
        assertNull(this.eresource.getDescription());
        verify(this.rec, this.field, this.subfield);
    }

    @Test
    void testGetIsbns() {
        expect(this.rec.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("020");
        expect(this.field.getSubfields())
                .andReturn(Arrays.asList(new Subfield[] { this.subfield, this.subfield, this.subfield }));
        expect(this.subfield.getCode()).andReturn('a');
        expect(this.subfield.getData()).andReturn("123456789");
        expect(this.subfield.getCode()).andReturn('z');
        expect(this.subfield.getData()).andReturn("987654321");
        expect(this.subfield.getCode()).andReturn('l');
        replay(this.rec, this.field, this.subfield);
        Collection<String> isbns = this.eresource.getIsbns();
        assertTrue(isbns.contains("123456789"));
        assertTrue(isbns.contains("987654321"));
        assertFalse(isbns.contains("X987654321"));
        verify(this.rec, this.field, this.subfield);
    }

    @Test
    void testGetIssns() {
        expect(this.rec.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("022");
        expect(this.field.getSubfields())
                .andReturn(Arrays.asList(new Subfield[] { this.subfield, this.subfield, this.subfield }));
        expect(this.subfield.getCode()).andReturn('a');
        expect(this.subfield.getData()).andReturn("123456789");
        expect(this.subfield.getCode()).andReturn('z');
        expect(this.subfield.getData()).andReturn("987654321");
        expect(this.subfield.getCode()).andReturn('l');
        expect(this.subfield.getData()).andReturn("X987654321");
        replay(this.rec, this.field, this.subfield);
        Collection<String> issns = this.eresource.getIssns();
        assertTrue(issns.contains("123456789"));
        assertTrue(issns.contains("987654321"));
        assertTrue(issns.contains("x987654321"));
        verify(this.rec, this.field, this.subfield);
    }

    @Test
    void testGetItemCount() {
        // this.rec is really holdings here
        expect(this.rec.getFields()).andReturn(Collections.singletonList(this.field)).times(3);
        expect(this.field.getTag()).andReturn("888").times(3);
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield)).times(3);
        expect(this.subfield.getCode()).andReturn('t');
        expect(this.subfield.getData()).andReturn("1");
        expect(this.subfield.getCode()).andReturn('a');
        expect(this.subfield.getData()).andReturn("1");
        expect(this.subfield.getCode()).andReturn('c');
        expect(this.subfield.getData()).andReturn("0");
        replay(this.rec, this.field, this.subfield);
        int[] count = this.eresource.getItemCount();
        assertEquals(1, count[0]);
        assertEquals(1, count[1]);
        assertEquals(0, count[2]);
        verify(this.rec, this.field, this.subfield);
    }

    @Test
    void testGetItemCountNullItemCount() {
        expect(this.rec.getFields()).andReturn(Collections.singletonList(this.field)).times(3);
        expect(this.field.getTag()).andReturn("444").times(3);
        replay(this.rec, this.field);
        int[] count = this.eresource.getItemCount();
        assertEquals(3, count.length);
        assertEquals(0, count[0]);
        assertEquals(0, count[1]);
        assertEquals(0, count[2]);
        verify(this.rec, this.field);
    }

    @Test
    void testGetMeshTerms() {
        expect(this.rec.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("650").times(2);
        expect(this.field.getIndicator2()).andReturn('2');
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('a');
        expect(this.subfield.getData()).andReturn("data");
        replay(this.rec, this.field, this.subfield);
        this.eresource.getMeshTerms();
        verify(this.rec, this.field, this.subfield);
    }

    @Test
    void testGetPublicationAuthorsText() {
        expect(this.field.getTag()).andReturn("100").times(4);
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield)).times(2);
        expect(this.subfield.getCode()).andReturn('a').times(2);
        expect(this.subfield.getData()).andReturn("author");
        expect(this.subfield.getData()).andReturn("author.");
        expect(this.rec.getFields()).andReturn(Collections.emptyList());
        expect(this.rec.getFields()).andReturn(Arrays.asList(new Field[] { this.field, this.field }));
        replay(this.rec, this.field, this.subfield);
        assertEquals("author; author.", this.eresource.getPublicationAuthorsText());
        verify(this.rec);
    }

    @Test
    void testGetPublicationAuthorsText245TwoC() {
        expect(this.rec.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("245");
        expect(this.field.getSubfields()).andReturn(Arrays.asList(new Subfield[] { this.subfield, this.subfield }));
        expect(this.subfield.getCode()).andReturn('c').times(2);
        expect(this.subfield.getData()).andReturn("first c");
        expect(this.subfield.getData()).andReturn("author");
        replay(this.rec, this.field, this.subfield);
        assertEquals("author", this.eresource.getPublicationAuthorsText());
        verify(this.rec, this.field, this.subfield);
    }

    @Test
    void testGetPublicationLanguages() {
        expect(this.rec.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("008");
        expect(this.field.getData()).andReturn("860402s1982____ja_______d____00|10_jpn_d");
        expect(this.rec.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("041");
        expect(this.field.getSubfields()).andReturn(Arrays.asList(new Subfield[] { this.subfield, this.subfield }));
        expect(this.subfield.getData()).andReturn("Jpn");
        expect(this.subfield.getData()).andReturn("eng");
        replay(this.rec, this.field, this.subfield);
        assertArrayEquals(new String[] { "English", "Japanese" },
                this.eresource.getPublicationLanguages().toArray(new String[2]));
    }

    @Test
    void testGetPublicationTextTitleEtc() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.initialize();
        this.recordService = new FileCatalogRecordService("src/test/resources/edu/stanford/irt/eresources/marc/lane",
                executor);
        RecordCollection rc = new RecordCollection(this.recordService.getRecordStream(0));
        while (rc.hasNext()) {
            Record rec = rc.next();
            if ("168269".equals(getRecordId(rec))) {
                Eresource er = new BibMarcEresource(Arrays.asList(new Record[] { rec, this.rec }),
                        this.keywordsStrategy, this.locationsService);
                assertEquals("bib-168269", er.getId());
                assertEquals(null, er.getPublicationDate());
                assertEquals(null, er.getPublicationIssue());
                assertEquals(null, er.getPublicationPages());
                assertTrue(er.getPublicationTypes().isEmpty());
                assertEquals(null, er.getPublicationVolume());
                expect(this.keywordsStrategy.getKeywords(isA(Record.class))).andReturn("keywords").times(2);
                replay(this.keywordsStrategy);
                assertTrue(er.getKeywords().contains("keywords"));
                assertTrue(er.getTypes().contains("Image"));
                verify(this.keywordsStrategy);
                assertEquals("[Petrosilinum vel persil, materia medica].", er.getShortTitle());
                assertEquals("[Opera chirurgica]..  [ca. 1400] fol. 66 [i.e. 26]", er.getPublicationText());
                assertEquals("[Opera chirurgica].", er.getPublicationTitle());
            }
            if ("67043".equals(getRecordId(rec))) {
                Eresource er = new BibMarcEresource(Arrays.asList(new Record[] { rec, this.rec }),
                        this.keywordsStrategy, this.locationsService);
                assertEquals("[Collection of reprints by John Uri Lloyd from the Western Druggist]. ",
                        er.getPublicationText());
                assertEquals("[Collection of reprints by John Uri Lloyd from the Western Druggist]",
                        er.getPublicationTitle());
            }
            if ("77614".equals(getRecordId(rec))) {
                Eresource er = new BibMarcEresource(Arrays.asList(new Record[] { rec, this.rec }),
                        this.keywordsStrategy, this.locationsService);
                assertEquals("Stanford University Medical Center Records. ", er.getPublicationText());
                assertEquals("Stanford University Medical Center Records", er.getPublicationTitle());
            }
            if ("21171".equals(getRecordId(rec))) {
                Eresource er = new BibMarcEresource(Arrays.asList(new Record[] { rec, this.rec }),
                        this.keywordsStrategy, this.locationsService);
                assertEquals(
                        "Clinical pharmacology and therapeutics.  1981 Sep-; 30(3)-; Journal of the American Medical Association 1972 Nov 27-1981 Feb 27; 222(9)-245(8)",
                        er.getPublicationText());
                assertEquals("Clinical pharmacology and therapeutics", er.getPublicationTitle());
            }
        }
    }

    @Test
    void testGetRecordId() {
        expect(this.rec.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("001");
        expect(this.field.getData()).andReturn("12");
        replay(this.rec, this.field, this.subfield);
        assertEquals("12", this.eresource.getRecordId());
        verify(this.rec, this.field, this.subfield);
    }

    @Test
    void testGetRecordType() {
        replay(this.rec, this.field, this.subfield);
        assertEquals("bib", this.eresource.getRecordType());
        verify(this.rec, this.field, this.subfield);
    }

    @Test
    void testGetSortTitle() {
        expect(this.rec.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("245");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('a').times(2);
        expect(this.subfield.getData()).andReturn("title");
        expect(this.rec.getFields()).andReturn(Collections.emptyList());
        replay(this.rec, this.field, this.subfield);
        assertEquals("title", this.eresource.getSortTitle());
    }

    @Test
    void testGetTitle() {
        expect(this.rec.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("245");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('a').times(2);
        expect(this.subfield.getData()).andReturn("title");
        Field f008 = mock(Field.class);
        expect(f008.getTag()).andReturn("008");
        expect(f008.getData()).andReturn("950714s1951____xx____________00|_0_eng_d");
        expect(this.rec.getFields()).andReturn(Collections.singletonList(f008));
        expect(this.rec.getFields()).andReturn(Collections.emptyList());
        replay(this.rec, f008, this.field, this.subfield);
        assertEquals("Title", this.eresource.getTitle());
        verify(this.rec, f008, this.field, this.subfield);
    }

    @Test
    void testGetTitleTrailingSlash() {
        expect(this.rec.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("245");
        expect(this.field.getSubfields()).andReturn(Arrays.asList(new Subfield[] { this.subfield, this.subfield }));
        expect(this.subfield.getCode()).andReturn('a').times(2);
        expect(this.subfield.getData()).andReturn("HELP with nursing audit and quality assurance");
        expect(this.subfield.getCode()).andReturn('b').times(2);
        expect(this.subfield.getData()).andReturn("management guide.../");
        Field f008 = mock(Field.class);
        expect(f008.getTag()).andReturn("008");
        expect(f008.getData()).andReturn("950714s1951____xx____________00|_0_eng_d");
        expect(this.rec.getFields()).andReturn(Collections.singletonList(f008));
        expect(this.rec.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("250");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('a');
        expect(this.subfield.getData()).andReturn("3rd ed.");
        replay(this.rec, this.field, f008, this.subfield);
        assertEquals("HELP with Nursing Audit and Quality Assurance : Management Guide... 3rd ed.",
                this.eresource.getTitle());
        verify(this.rec, this.field, f008, this.subfield);
    }

    @Test
    void testGetVersions() {
        BibMarcEresource e = new BibMarcEresource(Arrays.asList(new Record[] { this.rec, this.rec }), null, null);
        expect(this.rec.getFields()).andReturn(Collections.singletonList(this.field)).atLeastOnce();
        expect(this.field.getTag()).andReturn("856").atLeastOnce();
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield)).atLeastOnce();
        expect(this.subfield.getCode()).andReturn('z').atLeastOnce();
        expect(this.subfield.getData()).andReturn("label").atLeastOnce();
        expect(this.field.getIndicator1()).andReturn('4').atLeastOnce();
        expect(this.field.getIndicator2()).andReturn('1').atLeastOnce();
        byte b = 0;
        expect(this.rec.getLeaderByte(EasyMock.anyInt())).andReturn(b).atLeastOnce();
        replay(this.rec, this.field, this.subfield);
        assertEquals(1, e.getVersions().size());
        assertEquals(1, e.getVersions().size());
        verify(this.rec, this.field, this.subfield);
    }

    @ParameterizedTest
    @MethodSource("provideYearTestCases")
    void testGetYear(final String fieldData, final int expectedYear) {
        expect(this.rec.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("008");
        expect(this.field.getData()).andReturn(fieldData);
        replay(this.rec, this.field, this.subfield);
        assertEquals(expectedYear, this.eresource.getYear());
        verify(this.rec, this.field, this.subfield);
    }

    @Test
    void testIsEnglish() {
        expect(this.rec.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("008");
        expect(this.field.getData()).andReturn("860402s1982____ja_______d____00|10_jpn_d");
        replay(this.rec, this.field);
        assertFalse(this.eresource.isEnglish());
        reset(this.rec, this.field);
        expect(this.rec.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("008");
        expect(this.field.getData()).andReturn("950714s1951____xx____________00|_0_eng_d");
        replay(this.rec, this.field);
        assertTrue(this.eresource.isEnglish());
    }
}
