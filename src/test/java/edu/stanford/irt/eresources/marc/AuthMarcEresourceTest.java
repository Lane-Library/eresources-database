package edu.stanford.irt.eresources.marc;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.marc4j.marc.VariableField;


public class AuthMarcEresourceTest {
    
    private AuthMarcEresource eresource;
    private Record record;
    private DataField field;
    private Subfield subfield;
    private ControlField controlfield;

    @Before
    public void setUp() {
        this.record = createMock(Record.class);
        this.eresource = new AuthMarcEresource(this.record, "keywords");
        this.field = createMock(DataField.class);
        this.subfield = createMock(Subfield.class);
        this.controlfield = createMock(ControlField.class);
    }

    @Test
    public void testGetItemCount() {
        assertArrayEquals(new int[] {0, 0}, this.eresource.getItemCount());
    }

    @Test
    public void testDoDescription() {
        assertNull(this.eresource.getDescription());
    }

    @Test
    public void testDoId() {
        expect(this.record.getControlNumber()).andReturn("100");
        replay(this.record);
        assertEquals(100, this.eresource.getRecordId());
        verify(this.record);
    }

    @Test
    public void testDoIsCore() {
        assertFalse(this.eresource.isCore());
    }

    @Test
    public void testDoMeshTerms() {
        expect(this.record.getVariableFields("650")).andReturn(Collections.<VariableField>singletonList(this.field));
        expect(this.field.getIndicator1()).andReturn('4');
        expect(this.field.getIndicator2()).andReturn('7');
        expect(this.field.getSubfield('a')).andReturn(this.subfield);
        expect(this.subfield.getData()).andReturn("mesh");
        replay(this.record, this.field, this.subfield);
        assertArrayEquals(new String[] {"mesh"}, this.eresource.getMeshTerms().toArray());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testDoUpdated() {
        expect(this.record.getVariableField("005")).andReturn(this.controlfield);
        expect(this.controlfield.getData()).andReturn("19550519120000");
        replay(this.record, this.field, this.subfield, this.controlfield);
        this.eresource.getUpdated();
        verify(this.record, this.field, this.subfield, this.controlfield);
    }

    @Test
    public void testDoVersions() {
        expect(this.record.getVariableFields("856")).andReturn(Collections.<VariableField>singletonList(this.field));
        expect(this.field.getSubfield('u')).andReturn(this.subfield);
        expect(this.subfield.getData()).andReturn("url");
        replay(this.record, this.field, this.subfield);
        assertEquals(1, this.eresource.getVersions().size());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testDoYear() {
        expect(this.record.getVariableFields("943")).andReturn(Collections.<VariableField>singletonList(this.field));
        expect(this.field.getSubfield('b')).andReturn(this.subfield);
        expect(this.subfield.getData()).andReturn("19uu");
        replay(this.record, this.field, this.subfield);
        assertEquals(1955, this.eresource.getYear());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetRecordType() {
        assertEquals("auth", this.eresource.getRecordType());
    }
}
