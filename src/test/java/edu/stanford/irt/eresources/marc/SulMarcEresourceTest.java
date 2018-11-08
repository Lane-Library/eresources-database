package edu.stanford.irt.eresources.marc;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.stanford.irt.eresources.EresourceDatabaseException;
import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.Record.Field;
import edu.stanford.lane.catalog.Record.Subfield;

public class SulMarcEresourceTest {

    private SulMarcEresource eresource;

    private Field field;

    private KeywordsStrategy keywordsStrategy;

    private Record record;

    private Subfield subfield;

    private SulTypeFactory typeFactory;

    @Before
    public void setUp() {
        this.record = mock(Record.class);
        this.keywordsStrategy = mock(KeywordsStrategy.class);
        this.typeFactory = mock(SulTypeFactory.class);
        this.eresource = new SulMarcEresource(this.record, this.keywordsStrategy, this.typeFactory);
        this.field = mock(Field.class);
        this.subfield = mock(Subfield.class);
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
        expect(this.subfield.getData()).andReturn("abbrv.");
        replay(this.record, this.field, this.subfield);
        assertEquals("abbrv.", this.eresource.getAbbreviatedTitles().stream().findFirst().get());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public final void testGetItemCount() {
        assertEquals(0, this.eresource.getItemCount()[0]);
        assertEquals(0, this.eresource.getItemCount()[1]);
    }

    @Test
    public final void testGetKeywords() {
        expect(this.keywordsStrategy.getKeywords(this.record)).andReturn("keywords");
        replay(this.keywordsStrategy);
        assertEquals("keywords", this.eresource.getKeywords());
        verify(this.keywordsStrategy);
    }

    @Test
    public final void testGetPrimaryType() {
        expect(this.typeFactory.getPrimaryType(this.record)).andReturn("primary");
        replay(this.typeFactory);
        assertEquals("primary", this.eresource.getPrimaryType());
        assertEquals("primary", this.eresource.getPrimaryType());
        verify(this.typeFactory);
    }

    @Test
    public final void testGetRecordId() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("001");
        expect(this.field.getData()).andReturn("1234");
        replay(this.record, this.field);
        assertEquals(1234, this.eresource.getRecordId());
        verify(this.record, this.field);
    }

    @Test
    public final void testGetRecordIdNotNumber() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("001");
        expect(this.field.getData()).andReturn("not a number");
        replay(this.record, this.field);
        assertEquals(0, this.eresource.getRecordId());
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
    public final void testGetTypes() {
        List<String> types = new ArrayList<>();
        expect(this.typeFactory.getTypes(this.record)).andReturn(types);
        expect(this.typeFactory.getPrimaryType(this.record)).andReturn("Other");
        replay(this.typeFactory, this.record);
        assertTrue(this.eresource.getTypes().isEmpty());
    }

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
        SulMarcEresource e = new SulMarcEresource(this.record, null, this.typeFactory);
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field)).atLeastOnce();
        expect(this.field.getTag()).andReturn("956").atLeastOnce();
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield)).atLeastOnce();
        expect(this.subfield.getCode()).andReturn('u').atLeastOnce();
        expect(this.subfield.getData()).andReturn("url").atLeastOnce();
        replay(this.record, this.field, this.subfield, this.typeFactory);
        assertEquals(1, e.getVersions().size());
        verify(this.record, this.field, this.subfield, this.typeFactory);
    }

    @Test
    public final void testIsCore() {
        assertFalse(this.eresource.isCore());
    }

    @Test
    public final void testIsLaneConnex() {
        assertFalse(this.eresource.isLaneConnex());
    }
}
