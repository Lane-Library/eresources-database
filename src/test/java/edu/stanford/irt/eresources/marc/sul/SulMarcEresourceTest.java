package edu.stanford.irt.eresources.marc.sul;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.ZoneId;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import edu.stanford.irt.eresources.CatalogRecordService;
import edu.stanford.irt.eresources.EresourceDatabaseException;
import edu.stanford.irt.eresources.FileCatalogRecordService;
import edu.stanford.irt.eresources.marc.KeywordsStrategy;
import edu.stanford.irt.eresources.marc.MARCRecordSupport;
import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.Record.Field;
import edu.stanford.lane.catalog.Record.Subfield;
import edu.stanford.lane.catalog.RecordCollection;
import edu.stanford.lane.lcsh.LcshMapManager;

public class SulMarcEresourceTest extends MARCRecordSupport {

    private SulMarcEresource eresource;

    private Field field;

    private KeywordsStrategy keywordsStrategy;

    private Record record;

    private Subfield subfield;

    RecordCollection recordCollection;

    HashMap<String, Record> records = new HashMap<>();

    CatalogRecordService recordService;

    @Before
    public void setUp() {
        this.record = mock(Record.class);
        this.keywordsStrategy = mock(KeywordsStrategy.class);
        this.eresource = new SulMarcEresource(this.record, this.keywordsStrategy, null);
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
    public final void testGetAbbreviatedTitles() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("246");
        expect(this.field.getSubfields()).andReturn(Arrays.asList(new Subfield[] { this.subfield, this.subfield }))
                .times(2);
        expect(this.subfield.getCode()).andReturn('i');
        expect(this.subfield.getCode()).andReturn('a');
        expect(this.subfield.getData()).andReturn("Also known as:");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('a');
        expect(this.subfield.getData()).andReturn("ABBRV.");
        replay(this.record, this.field, this.subfield);
        assertEquals("ABBRV.", this.eresource.getAbbreviatedTitles().stream().findFirst().get());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public final void testGetAbbreviatedTitlesMissing() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("246");
        expect(this.field.getSubfields()).andReturn(Arrays.asList(new Subfield[] { this.subfield, this.subfield }))
                .times(2);
        expect(this.subfield.getCode()).andReturn('i');
        expect(this.subfield.getCode()).andReturn('a');
        expect(this.subfield.getData()).andReturn("Also known as:");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('a');
        expect(this.subfield.getData()).andReturn("abbrv.");
        replay(this.record, this.field, this.subfield);
        assertTrue(this.eresource.getAbbreviatedTitles().isEmpty());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public final void testGetDescription520() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field)).anyTimes();
        expect(this.field.getTag()).andReturn("520").anyTimes();
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('a');
        expect(this.subfield.getData()).andReturn("Just text");
        replay(this.record, this.field, this.subfield);
        assertEquals("Summary:<br/>Just text", this.eresource.getDescription());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public final void testGetDescription905() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field)).anyTimes();
        expect(this.field.getTag()).andReturn("905").anyTimes();
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('a');
        expect(this.subfield.getData()).andReturn("Appendix I : thing -- Appendix II : thing 2.");
        replay(this.record, this.field, this.subfield);
        assertEquals("Contents:<br/>Appendix I : thing<br/>Appendix II : thing 2.", this.eresource.getDescription());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public final void testGetDescription905And920() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field)).anyTimes();
        expect(this.field.getTag()).andReturn("920").times(3);
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('b');
        expect(this.subfield.getData()).andReturn("920 data");
        expect(this.field.getTag()).andReturn("905").anyTimes();
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('a');
        expect(this.subfield.getData()).andReturn("905 data");
        replay(this.record, this.field, this.subfield);
        assertEquals("Summary:<br/>920 data<br/><br/>Contents:<br/> 905 data", this.eresource.getDescription());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public final void testGetDescriptionEmpty() {
        expect(this.record.getFields()).andReturn(Collections.emptyList()).anyTimes();
        replay(this.record);
        assertNull(this.eresource.getDescription());
        verify(this.record);
    }

    @Test
    public final void testGetKeywords() {
        expect(this.keywordsStrategy.getKeywords(this.record)).andReturn("keywords");
        replay(this.keywordsStrategy);
        assertEquals("keywords", this.eresource.getKeywords());
        verify(this.keywordsStrategy);
    }

    @Test
    public final void testGetMesh() {
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

//    @Test
//    public final void testGetPrimaryType() {
//        expect(this.typeFactory.getPrimaryType(this.record)).andReturn("primary");
//        replay(this.typeFactory);
//        assertEquals("primary", this.eresource.getPrimaryType());
//        assertEquals("primary", this.eresource.getPrimaryType());
//        verify(this.typeFactory);
//    }
//
    @Test
    public final void testGetRecordId() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("001");
        expect(this.field.getData()).andReturn("1234");
        replay(this.record, this.field);
        assertEquals("1234", this.eresource.getRecordId());
        verify(this.record, this.field);
    }

    @Test
    public final void testGetRecordIdNotNumber() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("001");
        expect(this.field.getData()).andReturn("not a number");
        replay(this.record, this.field);
        assertEquals("", this.eresource.getRecordId());
        verify(this.record, this.field);
    }

    @Test
    public final void testGetRecordType() {
        assertEquals("sul", this.eresource.getRecordType());
    }

    @Test
    public final void testGetShortTitle() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("222");
        expect(this.field.getSubfields()).andReturn(Collections.emptyList());
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("245");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('a').times(2);
        expect(this.subfield.getData()).andReturn("title");
        expect(this.record.getFields()).andReturn(Collections.emptyList());
        replay(this.record, this.field, this.subfield);
        assertEquals("title", this.eresource.getShortTitle());
    }

    @Test
    public final void testGetTitleLinked() {
        SulMarcEresource linkedTitleEr = new SulMarcEresource(this.records.get("10494697"), this.keywordsStrategy,
                null);
        assertEquals("Рост Кристаллоь / Rost Kristallov / Growth of Crystals : Volume 12", linkedTitleEr.getTitle());
    }
//    @Test
//    public final void testGetTypes() {
//        List<String> types = new ArrayList<>();
//        expect(this.typeFactory.getTypes(this.record)).andReturn(types);
//        expect(this.typeFactory.getPrimaryType(this.record)).andReturn("Other");
//        replay(this.typeFactory, this.record);
//        assertTrue(this.eresource.getTypes().isEmpty());
//    }

    @Test
    public final void testGetUpdated() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("005");
        expect(this.field.getData()).andReturn("20120216180000");
        replay(this.record, this.field, this.subfield);
        Calendar cal = Calendar.getInstance();
        cal.set(2012, 1, 16, 18, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        assertEquals(cal.getTimeInMillis(),
                this.eresource.getUpdated().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        verify(this.record, this.field, this.subfield);
    }

    @Test(expected = EresourceDatabaseException.class)
    public final void testGetUpdatedBadDate() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("005");
        expect(this.field.getData()).andReturn("not a date");
        replay(this.record, this.field, this.subfield);
        this.eresource.getUpdated();
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public final void testGetVersions() {
        SulMarcEresource e = new SulMarcEresource(this.record, null, null);
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field)).atLeastOnce();
        expect(this.field.getTag()).andReturn("956").atLeastOnce();
        expect(this.field.getIndicator1()).andReturn('4').atLeastOnce();
        expect(this.field.getIndicator2()).andReturn('0').atLeastOnce();
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield)).atLeastOnce();
        expect(this.subfield.getCode()).andReturn('z').atLeastOnce();
        expect(this.subfield.getData()).andReturn("label").atLeastOnce();
        byte b = 0;
        expect(this.record.getLeaderByte(EasyMock.anyInt())).andReturn(b).atLeastOnce();
        replay(this.record, this.field, this.subfield);
        assertEquals(1, e.getVersions().size());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public final void testGetYear() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("008");
        expect(this.field.getData()).andReturn("000000000001999");
        replay(this.record, this.field);
        assertEquals(1999, this.eresource.getYear());
        assertEquals(1999, this.eresource.getYear());
        verify(this.record, this.field);
    }

    @Test
    public final void testGetYearRealMarc() {
        SulMarcEresource badDate = new SulMarcEresource(this.records.get("90009616"), this.keywordsStrategy, null);
        assertEquals(2005, badDate.getYear());
        assertEquals("20050101", badDate.getDate());
    }
//    @Test
//    public final void testGetYearNo() {
//        List<Field> fields = new ArrayList<>();
//        fields.add(this.field);
//        fields.add(this.field);
//        fields.add(this.field);
//        expect(this.record.getFields()).andReturn(fields).times(3);
//        expect(this.field.getTag()).andReturn("008");
//        expect(this.field.getData()).andReturn("000000000007999");
//        expect(this.field.getTag()).andReturn("264");
//        expect(this.field.getIndicator2()).andReturn('z').atLeastOnce();
//        expect(this.field.getTag()).andReturn("264");
//        expect(this.subfield.getCode()).andReturn('x');
//        expect(this.field.getTag()).andReturn("260");
//        expect(this.subfield.getCode()).andReturn('c');
//        expect(this.subfield.getData()).andReturn("1999");
//        replay(this.record, this.field, this.subfield);
//        assertEquals(1999, this.eresource.getYear());
//        assertEquals(1999, this.eresource.getYear());
//        verify(this.record, this.field, this.subfield);
//    }
}
