package edu.stanford.irt.eresources.marc;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
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

public class MarcVersionTest {

    private Eresource eresource;

    private Field field;

    private Record record;

    private Subfield subfield;

    private MarcVersion version;

    @Before
    public void setUp() {
        this.record = mock(Record.class);
        this.eresource = mock(Eresource.class);
        this.version = new MarcVersion(this.record, this.record, this.eresource);
        this.field = mock(Field.class);
        this.subfield = mock(Subfield.class);
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
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("866");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('y');
        expect(this.subfield.getData()).andReturn("dates");
        replay(this.record, this.field, this.subfield);
        assertEquals("dates", this.version.getDates());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetDatesFromBib() {
        expect(this.record.getFields()).andReturn(Collections.emptyList()).times(7);
        expect(this.eresource.getPublicationText()).andReturn("");
        expect(this.eresource.getPrimaryType()).andReturn("Book");
        replay(this.record, this.eresource, this.field, this.subfield);
        assertEquals("", this.version.getDates());
        verify(this.record, this.eresource, this.field, this.subfield);
    }

    @Test
    public void testGetHoldingsAndDates() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field)).times(2);
        expect(this.field.getTag()).andReturn("866").times(2);
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield)).times(2);
        expect(this.subfield.getCode()).andReturn('y').times(2);
        expect(this.subfield.getData()).andReturn("dates");
        replay(this.record, this.field, this.subfield);
        assertEquals("dates", this.version.getHoldingsAndDates());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetLinks() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field)).times(2);
        expect(this.field.getTag()).andReturn("856").times(2);
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('u');
        expect(this.subfield.getData()).andReturn("url");
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
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field)).times(4);
        expect(this.field.getTag()).andReturn("866").times(4);
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield)).times(4);
        expect(this.subfield.getCode()).andReturn('v').times(4);
        expect(this.subfield.getData()).andReturn("summaryHoldings").times(3);
        replay(this.record, this.field, this.subfield);
        assertEquals("summaryHoldings", this.version.getSummaryHoldings());
        assertEquals("summaryHoldings", this.version.getHoldingsAndDates());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testHasGetPasswordLink() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.field.getTag()).andReturn("856");
        expect(this.subfield.getCode()).andReturn('u');
        expect(this.subfield.getData()).andReturn("http://lane.stanford.edu/secure/ejpw.html");
        replay(this.record, this.field, this.subfield);
        assertTrue(this.version.hasGetPasswordLink());
        verify(this.record, this.field, this.subfield);
    }
}
