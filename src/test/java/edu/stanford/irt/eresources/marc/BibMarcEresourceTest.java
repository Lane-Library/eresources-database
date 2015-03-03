package edu.stanford.irt.eresources.marc;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.marc4j.marc.VariableField;

public class BibMarcEresourceTest {

    private ControlField controlfield;

    private BibMarcEresource eresource;

    private DataField field;

    private Record record;

    private Subfield subfield;

    @Before
    public void setUp() {
        this.record = createMock(Record.class);
        this.eresource = new BibMarcEresource(Arrays.asList(new Record[] {this.record, this.record}), "keywords",
                new int[] { 1, 1 });
        this.field = createMock(DataField.class);
        this.subfield = createMock(Subfield.class);
        this.controlfield = createMock(ControlField.class);
    }

    @Test
    public void testAddCustomTypes() {
        expect(this.record.getVariableFields("856")).andReturn(Collections.<VariableField> emptyList());
        expect(this.record.getVariableFields("035")).andReturn(Collections.<VariableField> emptyList());
        replay(this.record, this.field, this.subfield);
        this.eresource.addCustomTypes(Collections.<String> emptySet());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testDoDescription() {
        expect(this.record.getVariableFields("520")).andReturn(Collections.<VariableField> singletonList(this.field));
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getData()).andReturn("data");
        replay(this.record, this.field, this.subfield);
        assertEquals("data", this.eresource.doDescription());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testDoId() {
        expect(this.record.getControlNumber()).andReturn("12");
        replay(this.record, this.field, this.subfield);
        assertEquals(12, this.eresource.doId());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testDoIsCore() {
        expect(this.record.getVariableFields("655")).andReturn(Collections.<VariableField> singletonList(this.field));
        expect(this.field.getSubfield('a')).andReturn(this.subfield);
        expect(this.subfield.getData()).andReturn("Core Material");
        replay(this.record, this.field, this.subfield);
        assertTrue(this.eresource.doIsCore());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testDoMeshTerms() {
        expect(this.record.getVariableFields("650")).andReturn(Collections.<VariableField> singletonList(this.field));
        expect(this.field.getIndicator1()).andReturn('4');
        expect(this.field.getIndicator2()).andReturn('7');
        expect(this.field.getSubfield('a')).andReturn(this.subfield);
        expect(this.subfield.getData()).andReturn("data");
        replay(this.record, this.field, this.subfield);
        Collection<String> mesh = this.eresource.doMeshTerms();
        assertEquals("data", mesh.iterator().next());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testDoUpdated() {
        expect(this.record.getVariableField("005")).andReturn(this.controlfield).times(2);
        expect(this.controlfield.getData()).andReturn("19550519120000").times(2);
        replay(this.record, this.field, this.subfield, this.controlfield);
        this.eresource.doUpdated();
        verify(this.record, this.field, this.subfield, this.controlfield);
    }

    @Test
    public void testDoVersions() {
        expect(this.record.getVariableFields("856")).andReturn(Collections.<VariableField> emptyList());
        replay(this.record, this.field, this.subfield);
        this.eresource.doVersions();
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testDoYear() {
        expect(this.record.getVariableField("008")).andReturn(this.controlfield);
        expect(this.controlfield.getData()).andReturn("012345678901955");
        replay(this.record, this.field, this.subfield, this.controlfield);
        assertEquals(1955, this.eresource.doYear());
        verify(this.record, this.field, this.subfield, this.controlfield);
    }

    @Test
    public void testGetItemCount() {
        replay(this.record, this.field, this.subfield);
        assertArrayEquals(new int[] { 1, 1 }, this.eresource.getItemCount());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetRecordType() {
        replay(this.record, this.field, this.subfield);
        assertEquals("bib", this.eresource.getRecordType());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetType() {
        replay(this.record, this.field, this.subfield);
        assertEquals("bib", this.eresource.getType());
        verify(this.record, this.field, this.subfield);
    }
}
