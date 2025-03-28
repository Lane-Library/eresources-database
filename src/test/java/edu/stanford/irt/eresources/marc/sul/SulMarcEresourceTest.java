package edu.stanford.irt.eresources.marc.sul;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.stream.Stream;

import org.easymock.EasyMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import edu.stanford.irt.eresources.CatalogRecordService;
import edu.stanford.irt.eresources.FileCatalogRecordService;
import edu.stanford.irt.eresources.marc.KeywordsStrategy;
import edu.stanford.irt.eresources.marc.MARCRecordSupport;
import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.Record.Field;
import edu.stanford.lane.catalog.Record.Subfield;
import edu.stanford.lane.catalog.RecordCollection;
import edu.stanford.lane.lcsh.LcshMapManager;

public class SulMarcEresourceTest extends MARCRecordSupport {

    public static Stream<Arguments> data() {
        return Stream.of(
                // get ip with callback
                Arguments.of(
                        "505",
                        "Ch. 1 Introduction -- Ch. 2 Functional organization of the visual system -- Pt. I Development of the visual system -- ",
                        "::Contents##<br/>Ch. 1 Introduction<br/>Ch. 2 Functional organization of the visual system<br/>Pt. I Development of the visual system<br/>"),
                Arguments.of("520", "Just text", "::Summary## Just text"),
                Arguments.of("905", "Appendix I : thing -- Appendix II : thing 2.",
                        "::Contents##<br/>Appendix I : thing<br/>Appendix II : thing 2."));
    }

    private SulMarcEresource eresource;

    private Field field;

    private KeywordsStrategy keywordsStrategy;

    private Record rec;

    private Subfield subfield;

    RecordCollection recordCollection;

    HashMap<String, Record> records = new HashMap<>();

    CatalogRecordService recordService;

    @BeforeEach
    void setUp() {
        this.rec = mock(Record.class);
        this.keywordsStrategy = mock(KeywordsStrategy.class);
        this.eresource = new SulMarcEresource(this.rec, this.keywordsStrategy, null);
        this.field = mock(Field.class);
        this.subfield = mock(Subfield.class);
        // real marc to simplify testing for getYear, mesh, etc.
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.initialize();
        this.recordService = new FileCatalogRecordService("src/test/resources/edu/stanford/irt/eresources/marc/sul/",
                executor);
        this.recordCollection = new RecordCollection(this.recordService.getRecordStream(0));
        while (this.recordCollection.hasNext()) {
            Record rec = this.recordCollection.next();
            this.records.put(getRecordId(rec), rec);
        }
    }

    @Test
    final void testGetAbbreviatedTitles() {
        expect(this.rec.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("246");
        expect(this.field.getSubfields()).andReturn(Arrays.asList(new Subfield[] { this.subfield, this.subfield }))
                .times(2);
        expect(this.subfield.getCode()).andReturn('i');
        expect(this.subfield.getCode()).andReturn('a');
        expect(this.subfield.getData()).andReturn("Also known as:");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('a');
        expect(this.subfield.getData()).andReturn("ABBRV.");
        replay(this.rec, this.field, this.subfield);
        assertEquals("ABBRV.", this.eresource.getAbbreviatedTitles().stream().findFirst().get());
        verify(this.rec, this.field, this.subfield);
    }

    @Test
    final void testGetAbbreviatedTitlesMissing() {
        expect(this.rec.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("246");
        expect(this.field.getSubfields()).andReturn(Arrays.asList(new Subfield[] { this.subfield, this.subfield }))
                .times(2);
        expect(this.subfield.getCode()).andReturn('i');
        expect(this.subfield.getCode()).andReturn('a');
        expect(this.subfield.getData()).andReturn("Also known as:");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('a');
        expect(this.subfield.getData()).andReturn("abbrv.");
        replay(this.rec, this.field, this.subfield);
        assertTrue(this.eresource.getAbbreviatedTitles().isEmpty());
        verify(this.rec, this.field, this.subfield);
    }

    @Test
    final void testGetDescription905And920() {
        expect(this.rec.getFields()).andReturn(Collections.singletonList(this.field)).anyTimes();
        expect(this.field.getTag()).andReturn("920").times(3);
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getData()).andReturn("920 data");
        expect(this.field.getTag()).andReturn("905").anyTimes();
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getData()).andReturn("905 data");
        replay(this.rec, this.field, this.subfield);
        assertEquals("::Summary## 920 data<br/>::Contents##<br/>905 data", this.eresource.getDescription());
        verify(this.rec, this.field, this.subfield);
    }

    @Test
    final void testGetDescriptionEmpty() {
        expect(this.rec.getFields()).andReturn(Collections.emptyList()).anyTimes();
        replay(this.rec);
        assertNull(this.eresource.getDescription());
        verify(this.rec);
    }

    @ParameterizedTest
    @MethodSource("data")
    void testGetDescriptionParameterized(String tag, String subfieldData, String expectedDescription) {
        expect(this.rec.getFields()).andReturn(Collections.singletonList(this.field)).anyTimes();
        expect(this.field.getTag()).andReturn(tag).anyTimes();
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getData()).andReturn(subfieldData);
        replay(this.rec, this.field, this.subfield);
        assertEquals(expectedDescription, this.eresource.getDescription());
        verify(this.rec, this.field, this.subfield);
    }

    @Test
    final void testGetKeywords() {
        expect(this.keywordsStrategy.getKeywords(this.rec)).andReturn("keywords");
        replay(this.keywordsStrategy);
        assertEquals("keywords", this.eresource.getKeywords());
        verify(this.keywordsStrategy);
    }

    @Test
    final void testGetMesh() {
        LcshMapManager lcshMapManager = mock(LcshMapManager.class);
        SulMarcEresource mesh = new SulMarcEresource(this.records.get("7811516"), this.keywordsStrategy,
                lcshMapManager);
        expect(lcshMapManager.getMeshForHeading(isA(String.class))).andReturn(Collections.singleton("mappedMesh"))
                .atLeastOnce();
        replay(lcshMapManager);
        assertTrue(mesh.getMeshTerms().contains("mappedMesh"));
        assertTrue(mesh.getMeshTerms().contains("Surgical Procedures, Operative"));
        verify(lcshMapManager);
    }

    // @Test
    // public final void testGetPrimaryType() {
    // expect(this.typeFactory.getPrimaryType(this.record)).andReturn("primary");
    // replay(this.typeFactory);
    // assertEquals("primary", this.eresource.getPrimaryType());
    // assertEquals("primary", this.eresource.getPrimaryType());
    // verify(this.typeFactory);
    // }
    //
    @Test
    final void testGetRecordId() {
        expect(this.rec.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("001");
        expect(this.field.getData()).andReturn("1234");
        replay(this.rec, this.field);
        assertEquals("1234", this.eresource.getRecordId());
        verify(this.rec, this.field);
    }

    @Test
    final void testGetRecordIdNotNumber() {
        expect(this.rec.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("001");
        expect(this.field.getData()).andReturn("not a number");
        replay(this.rec, this.field);
        assertEquals("", this.eresource.getRecordId());
        verify(this.rec, this.field);
    }

    @Test
    final void testGetRecordType() {
        assertEquals("sul", this.eresource.getRecordType());
    }

    @Test
    final void testGetShortTitle() {
        expect(this.rec.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("222");
        expect(this.field.getSubfields()).andReturn(Collections.emptyList());
        expect(this.rec.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("245");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('a').times(2);
        expect(this.subfield.getData()).andReturn("title");
        expect(this.rec.getFields()).andReturn(Collections.emptyList());
        replay(this.rec, this.field, this.subfield);
        assertEquals("title", this.eresource.getShortTitle());
    }

    @Test
    final void testGetTitleLinked() {
        SulMarcEresource linkedTitleEr = new SulMarcEresource(this.records.get("10494697"), this.keywordsStrategy,
                null);
        assertEquals("Рост Кристаллоь / Rost Kristallov / Growth of Crystals : Volume 12", linkedTitleEr.getTitle());
    }
    // @Test
    // public final void testGetTypes() {
    // List<String> types = new ArrayList<>();
    // expect(this.typeFactory.getTypes(this.record)).andReturn(types);
    // expect(this.typeFactory.getPrimaryType(this.record)).andReturn("Other");
    // replay(this.typeFactory, this.record);
    // assertTrue(this.eresource.getTypes().isEmpty());
    // }

    @Test
    final void testGetVersions() {
        SulMarcEresource e = new SulMarcEresource(this.rec, null, null);
        expect(this.rec.getFields()).andReturn(Collections.singletonList(this.field)).atLeastOnce();
        expect(this.field.getTag()).andReturn("956").atLeastOnce();
        expect(this.field.getIndicator1()).andReturn('4').atLeastOnce();
        expect(this.field.getIndicator2()).andReturn('0').atLeastOnce();
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield)).atLeastOnce();
        expect(this.subfield.getCode()).andReturn('z').atLeastOnce();
        expect(this.subfield.getData()).andReturn("label").atLeastOnce();
        byte b = 0;
        expect(this.rec.getLeaderByte(EasyMock.anyInt())).andReturn(b).atLeastOnce();
        replay(this.rec, this.field, this.subfield);
        assertEquals(1, e.getVersions().size());
        verify(this.rec, this.field, this.subfield);
    }

    @Test
    final void testGetYear() {
        expect(this.rec.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("008");
        expect(this.field.getData()).andReturn("000000000001999");
        replay(this.rec, this.field);
        assertEquals(1999, this.eresource.getYear());
        assertEquals(1999, this.eresource.getYear());
        verify(this.rec, this.field);
    }

    @Test
    final void testGetYearRealMarc() {
        SulMarcEresource badDate = new SulMarcEresource(this.records.get("90009616"), this.keywordsStrategy, null);
        assertEquals(2005, badDate.getYear());
        assertEquals("20050101", badDate.getDate());
    }
    // @Test
    // public final void testGetYearNo() {
    // List<Field> fields = new ArrayList<>();
    // fields.add(this.field);
    // fields.add(this.field);
    // fields.add(this.field);
    // expect(this.rec.getFields()).andReturn(fields).times(3);
    // expect(this.field.getTag()).andReturn("008");
    // expect(this.field.getData()).andReturn("000000000007999");
    // expect(this.field.getTag()).andReturn("264");
    // expect(this.field.getIndicator2()).andReturn('z').atLeastOnce();
    // expect(this.field.getTag()).andReturn("264");
    // expect(this.subfield.getCode()).andReturn('x');
    // expect(this.field.getTag()).andReturn("260");
    // expect(this.subfield.getCode()).andReturn('c');
    // expect(this.subfield.getData()).andReturn("1999");
    // replay(this.rec, this.field, this.subfield);
    // assertEquals(1999, this.eresource.getYear());
    // assertEquals(1999, this.eresource.getYear());
    // verify(this.rec, this.field, this.subfield);
    // }
}
