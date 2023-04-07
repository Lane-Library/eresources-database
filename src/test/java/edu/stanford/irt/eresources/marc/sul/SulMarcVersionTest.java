package edu.stanford.irt.eresources.marc.sul;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import edu.stanford.irt.eresources.Eresource;
import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.Record.Field;
import edu.stanford.lane.catalog.Record.Subfield;

public class SulMarcVersionTest {

    private Eresource eresource;

    private Field field;

    private Record record;

    private Subfield subfield;

    private SulMarcVersion version;

    @Before
    public void setUp() {
        this.record = mock(Record.class);
        this.field = mock(Field.class);
        this.subfield = mock(Subfield.class);
        this.eresource = mock(Eresource.class);
        this.version = new SulMarcVersion(this.record, this.eresource);
    }

    @Test
    public void testGetAdditionalNoZ() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("866");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('u');
        replay(this.record, this.field, this.subfield);
        assertNull(this.version.getAdditionalText());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetAdditionalText() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("866");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('z');
        expect(this.subfield.getData()).andReturn("description");
        replay(this.record, this.field, this.subfield);
        assertEquals("description", this.version.getAdditionalText());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetAdditionalTextMultiple866() {
        expect(this.record.getFields()).andReturn(Arrays.asList(new Field[] { this.field, this.field }));
        expect(this.field.getTag()).andReturn("866").times(2);
        replay(this.record, this.field);
        assertEquals("", this.version.getAdditionalText());
        verify(this.record, this.field);
    }

    @Test
    public void testGetAdditionalTextNo866() {
        expect(this.record.getFields()).andReturn(Collections.emptyList());
        replay(this.record);
        assertNull(this.version.getAdditionalText());
        verify(this.record);
    }

    @Test
    public void testGetDates() {
        expect(this.eresource.getPublicationText()).andReturn("");
        expect(this.eresource.getPrimaryType()).andReturn("Book");
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field)).times(2);
        expect(this.field.getTag()).andReturn("999");
        expect(this.field.getTag()).andReturn("260");
        expect(this.eresource.getYear()).andReturn(9999);
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('c');
        expect(this.subfield.getData()).andReturn("dates].");
        replay(this.record, this.eresource, this.field, this.subfield);
        assertEquals("dates", this.version.getDates());
        verify(this.record, this.eresource, this.field, this.subfield);
    }

    @Test
    public void testGetLinks() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field)).times(2);
        expect(this.field.getTag()).andReturn("856").times(2);
        replay(this.record, this.field, this.subfield);
        assertEquals(1, this.version.getLinks().size());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetPublisher() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("844");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('a');
        expect(this.subfield.getData()).andReturn("publisher");
        replay(this.record, this.field, this.subfield);
        assertEquals("publisher", this.version.getPublisher());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetSummaryHoldings() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("362");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('a');
        expect(this.subfield.getData()).andReturn("summaryHoldings");
        replay(this.record, this.field, this.subfield);
        assertEquals("summaryHoldings", this.version.getSummaryHoldings());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testIsProxy() {
        assertFalse(this.version.isProxy());
        this.version.setIsProxy(true);
        assertTrue(this.version.isProxy());
    }
}
