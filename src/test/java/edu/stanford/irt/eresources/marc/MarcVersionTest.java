package edu.stanford.irt.eresources.marc;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.marc4j.marc.VariableField;

public class MarcVersionTest {

    private DataField field;

    private Record record;

    private Subfield subfield;

    private MarcVersion version;

    @Before
    public void setUp() {
        this.record = createMock(Record.class);
        this.version = new MarcVersion(this.record);
        this.field = createMock(DataField.class);
        this.subfield = createMock(Subfield.class);
    }

    @Test
    public void testGetAdditionalText() {
        expect(this.record.getVariableField("866")).andReturn(this.field);
        expect(this.field.getSubfield('v')).andReturn(this.subfield);
        expect(this.subfield.getData()).andReturn("summaryHoldings");
        expect(this.record.getVariableField("866")).andReturn(this.field);
        expect(this.field.getSubfield('y')).andReturn(this.subfield);
        expect(this.subfield.getData()).andReturn("dates");
        expect(this.record.getVariableField("844")).andReturn(this.field);
        expect(this.field.getSubfield('a')).andReturn(this.subfield);
        expect(this.subfield.getData()).andReturn("publisher");
        expect(this.record.getVariableFields("866")).andReturn(Collections.<VariableField> singletonList(this.field));
        expect(this.field.getSubfields('z')).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getData()).andReturn("description");
        expect(this.record.getVariableFields("856")).andReturn(Collections.<VariableField> singletonList(this.field));
        expect(this.field.getSubfield('u')).andReturn(this.subfield);
        expect(this.subfield.getData()).andReturn("url");
        expect(this.field.getSubfield('q')).andReturn(this.subfield);
        expect(this.subfield.getData()).andReturn("label");
        expect(this.field.getSubfields('i')).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getData()).andReturn("instruction");
        replay(this.record, this.field, this.subfield);
        assertEquals(" summaryHoldings, dates, publisher, description, instruction ", this.version.getAdditionalText());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetDates() {
        expect(this.record.getVariableField("866")).andReturn(this.field);
        expect(this.field.getSubfield('y')).andReturn(this.subfield);
        expect(this.subfield.getData()).andReturn("dates");
        replay(this.record, this.field, this.subfield);
        assertEquals("dates", this.version.getDates());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetDescription() {
        expect(this.record.getVariableFields("866")).andReturn(Collections.<VariableField> singletonList(this.field));
        expect(this.field.getSubfields('z')).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getData()).andReturn("description");
        replay(this.record, this.field, this.subfield);
        assertEquals("description", this.version.getDescription());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetLinks() {
        expect(this.record.getVariableFields("856")).andReturn(Collections.<VariableField> singletonList(this.field));
        expect(this.field.getSubfield('u')).andReturn(this.subfield);
        expect(this.subfield.getData()).andReturn("url");
        replay(this.record, this.field, this.subfield);
        assertEquals(1, this.version.getLinks().size());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetPublisher() {
        expect(this.record.getVariableField("844")).andReturn(this.field);
        expect(this.field.getSubfield('a')).andReturn(this.subfield);
        expect(this.subfield.getData()).andReturn("publisher");
        replay(this.record, this.field, this.subfield);
        assertEquals("publisher", this.version.getPublisher());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetSubsets() {
        expect(this.record.getVariableFields("655")).andReturn(Collections.<VariableField> singletonList(this.field));
        expect(this.field.getSubfield('a')).andReturn(this.subfield);
        expect(this.subfield.getData()).andReturn("Subset, Biotools");
        expect(this.record.getVariableFields("856")).andReturn(Collections.<VariableField> emptyList());
        replay(this.record, this.field, this.subfield);
        assertArrayEquals(new String[] { "biotools" }, this.version.getSubsets().toArray());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetSummaryHoldings() {
        expect(this.record.getVariableField("866")).andReturn(this.field);
        expect(this.field.getSubfield('v')).andReturn(this.subfield);
        expect(this.subfield.getData()).andReturn("summaryHoldings");
        replay(this.record, this.field, this.subfield);
        assertEquals("summaryHoldings", this.version.getSummaryHoldings());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testHasGetPasswordLink() {
        expect(this.record.getVariableFields("856")).andReturn(Collections.<VariableField> singletonList(this.field));
        expect(this.field.getSubfield('u')).andReturn(this.subfield);
        expect(this.subfield.getData()).andReturn("http://lane.stanford.edu/secure/ejpw.html");
        replay(this.record, this.field, this.subfield);
        assertTrue(this.version.hasGetPasswordLink());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testIsProxy() {
        expect(this.record.getVariableFields("655")).andReturn(Collections.<VariableField> singletonList(this.field));
        expect(this.field.getSubfield('a')).andReturn(this.subfield);
        expect(this.subfield.getData()).andReturn("Subset, NoProxy");
        replay(this.record, this.field, this.subfield);
        assertFalse(this.version.isProxy());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testToString() {
        assertEquals(this.record.toString(), this.version.toString());
    }
}
