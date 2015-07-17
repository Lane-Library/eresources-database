package edu.stanford.irt.eresources.marc;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.marc4j.marc.VariableField;

import edu.stanford.irt.eresources.Version;

public class AbstractMarcEresourceTest {

    private class TestAbstractMarcEresource extends AbstractMarcEresource {

        public TestAbstractMarcEresource(final Record record, final String keywords) {
            super(record, keywords);
        }

        @Override
        public String getDescription() {
            return null;
        }

        @Override
        public String getRecordType() {
            return "test";
        }

        @Override
        public Date getUpdated() {
            return null;
        }

        @Override
        public Collection<Version> getVersions() {
            return Collections.emptySet();
        }

        @Override
        public int getYear() {
            return 0;
        }

        @Override
        public boolean isCore() {
            return false;
        }

        @Override
        protected String getPrintOrDigital() {
            return null;
        }
    }

    private AbstractMarcEresource eresource;

    private DataField field;

    private Record record;

    private Subfield subfield;

    @Before
    public void setUp() {
        this.record = createMock(Record.class);
        this.eresource = new TestAbstractMarcEresource(this.record, "keywords");
        this.field = createMock(DataField.class);
        this.subfield = createMock(Subfield.class);
    }

    @Test
    public void testAppend() {
        StringBuilder sb = new StringBuilder();
        this.eresource.append(sb, null);
        assertEquals(0, sb.length());
    }

    @Test
    public void testDoPrimaryType() {
        expect(this.record.getVariableFields("655")).andReturn(Collections.singletonList(this.field));
        expect(this.field.getIndicator1()).andReturn('4');
        expect(this.field.getIndicator2()).andReturn('7');
        expect(this.field.getSubfield('a')).andReturn(this.subfield);
        expect(this.subfield.getData()).andReturn("Books.");
        replay(this.record, this.field, this.subfield);
        assertEquals("books", this.eresource.getPrimaryType());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testDoTypes() {
        List<VariableField> fields = new ArrayList<VariableField>();
        for (int i = 0; i < 3; i++) {
            fields.add(this.field);
        }
        expect(this.field.getSubfield('a')).andReturn(this.subfield).times(3);
        expect(this.subfield.getData()).andReturn("bassett.");
        expect(this.subfield.getData()).andReturn("Periodical");
        expect(this.subfield.getData()).andReturn("A big nothingburger");
        expect(this.record.getVariableFields("655")).andReturn(fields).times(2);
        expect(this.field.getIndicator1()).andReturn(' ').times(3);
        replay(this.record, this.field, this.subfield);
        assertArrayEquals(new String[] { "", "bassett", "ej", "catalog" }, this.eresource.getTypes().toArray());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetCompositeType() {
        assertEquals("ej", this.eresource.getCompositeType("periodical"));
    }

    @Test
    public void testGetItemCount() {
        assertEquals(0, this.eresource.getItemCount().getTotal());
        assertEquals(0, this.eresource.getItemCount().getAvailable());
    }

    @Test
    public void testGetKeywords() {
        assertEquals("keywords", this.eresource.getKeywords());
    }

    @Test
    public void testGetMeshTerms() {
        expect(this.record.getVariableFields("650")).andReturn(Collections.singletonList(this.field));
        expect(this.field.getIndicator1()).andReturn('4');
        expect(this.field.getIndicator2()).andReturn('7');
        expect(this.field.getSubfield('a')).andReturn(this.subfield);
        expect(this.subfield.getData()).andReturn("mesh");
        replay(this.record, this.field, this.subfield);
        assertEquals(Collections.singleton("mesh"), this.eresource.getMeshTerms());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetPrimaryType() {
        expect(this.record.getVariableFields("655")).andReturn(Collections.singletonList(this.field));
        expect(this.field.getIndicator1()).andReturn('4');
        expect(this.field.getIndicator2()).andReturn('7');
        expect(this.field.getSubfield('a')).andReturn(this.subfield);
        expect(this.subfield.getData()).andReturn("Books.");
        replay(this.record, this.field, this.subfield);
        assertEquals("books", this.eresource.getPrimaryType());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetRecordId() {
        expect(this.record.getControlNumber()).andReturn("12");
        replay(this.record);
        assertEquals(12, this.eresource.getRecordId());
        verify(this.record);
    }

    @Test
    public void testGetTitle() {
        expect(this.record.getVariableField("245")).andReturn(this.field);
        List<Subfield> subfields = new ArrayList<Subfield>();
        for (int i = 0; i < 5; i++) {
            subfields.add(this.subfield);
        }
        expect(this.field.getSubfields()).andReturn(subfields);
        expect(this.subfield.getCode()).andReturn('a');
        expect(this.subfield.getData()).andReturn("The a");
        expect(this.subfield.getCode()).andReturn('b');
        expect(this.subfield.getData()).andReturn("b /");
        expect(this.subfield.getCode()).andReturn('n');
        expect(this.subfield.getData()).andReturn("n");
        expect(this.subfield.getCode()).andReturn('p');
        expect(this.subfield.getData()).andReturn("p");
        expect(this.subfield.getCode()).andReturn('q');
        expect(this.subfield.getData()).andReturn("q");
        expect(this.record.getVariableField("250")).andReturn(this.field);
        expect(this.field.getSubfield('a')).andReturn(this.subfield);
        expect(this.subfield.getData()).andReturn("3rd ed.");
        expect(this.field.getIndicator2()).andReturn('4');
        replay(this.record, this.field, this.subfield);
        assertEquals("a b n p q. 3rd ed.", this.eresource.getTitle());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetTypes() {
        List<VariableField> fields = new ArrayList<VariableField>();
        for (int i = 0; i < 3; i++) {
            fields.add(this.field);
        }
        expect(this.field.getSubfield('a')).andReturn(this.subfield).times(3);
        expect(this.subfield.getData()).andReturn("bassett.");
        expect(this.subfield.getData()).andReturn("Periodical");
        expect(this.subfield.getData()).andReturn("A big nothingburger");
        expect(this.record.getVariableFields("655")).andReturn(fields).times(2);
        expect(this.field.getIndicator1()).andReturn(' ').times(3);
        replay(this.record, this.field, this.subfield);
        assertArrayEquals(new String[] { "", "bassett", "ej", "catalog" }, this.eresource.getTypes().toArray());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetYear() {
        this.eresource.getYear();
    }

    @Test
    public void testIsAllowedType() {
        assertFalse(this.eresource.isAllowedType(""));
    }

    @Test
    public void testIsClone() {
        assertFalse(this.eresource.isClone());
    }

    @Test
    public void testIsCore() {
        assertFalse(this.eresource.isCore());
    }

    @Test
    public void testToString() {
        expect(this.record.getControlNumber()).andReturn("12");
        expect(this.record.getVariableField("245")).andReturn(this.field);
        List<Subfield> subfields = new ArrayList<Subfield>();
        for (int i = 0; i < 5; i++) {
            subfields.add(this.subfield);
        }
        expect(this.field.getSubfields()).andReturn(subfields);
        expect(this.subfield.getCode()).andReturn('a');
        expect(this.subfield.getData()).andReturn("The a");
        expect(this.subfield.getCode()).andReturn('b');
        expect(this.subfield.getData()).andReturn("b /");
        expect(this.subfield.getCode()).andReturn('n');
        expect(this.subfield.getData()).andReturn("n");
        expect(this.subfield.getCode()).andReturn('p');
        expect(this.subfield.getData()).andReturn("p");
        expect(this.subfield.getCode()).andReturn('q');
        expect(this.subfield.getData()).andReturn("q");
        expect(this.record.getVariableField("250")).andReturn(this.field);
        expect(this.field.getSubfield('a')).andReturn(this.subfield);
        expect(this.subfield.getData()).andReturn("3rd ed.");
        expect(this.field.getIndicator2()).andReturn('4');
        replay(this.record, this.field, this.subfield);
        assertEquals("test:12 a b n p q. 3rd ed.", this.eresource.toString());
        verify(this.record, this.field, this.subfield);
    }
}
