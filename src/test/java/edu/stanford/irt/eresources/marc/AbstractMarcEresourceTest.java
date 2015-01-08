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
        public String getRecordType() {
            return null;
        }

        @Override
        protected String doDescription() {
            return "description";
        }

        @Override
        protected int doId() {
            return Integer.MAX_VALUE;
        }

        @Override
        protected boolean doIsCore() {
            return false;
        }

        @Override
        protected Collection<String> doMeshTerms() {
            return Collections.singleton("mesh");
        }

        @Override
        protected Date doUpdated() {
            return new Date(0);
        }

        @Override
        protected List<Version> doVersions() {
            return Collections.emptyList();
        }

        @Override
        protected int doYear() {
            return 1955;
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
    public void testDoDescription() {
        assertEquals("description", this.eresource.doDescription());
    }

    @Test
    public void testDoPrimaryType() {
        expect(this.record.getVariableFields("655")).andReturn(Collections.<VariableField> singletonList(this.field));
        expect(this.field.getIndicator1()).andReturn('4');
        expect(this.field.getIndicator2()).andReturn('7');
        expect(this.field.getSubfield('a')).andReturn(this.subfield);
        expect(this.subfield.getData()).andReturn("Books.");
        replay(this.record, this.field, this.subfield);
        assertEquals("books", this.eresource.doPrimaryType());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testDoTitle() {
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
    public void testDoTypes() {
        List<VariableField> fields = new ArrayList<VariableField>();
        for (int i = 0; i < 3; i++) {
            fields.add(this.field);
        }
        expect(this.field.getSubfield('a')).andReturn(this.subfield).times(3);
        expect(this.subfield.getData()).andReturn("bassett.");
        expect(this.subfield.getData()).andReturn("Periodical");
        expect(this.subfield.getData()).andReturn("A big nothingburger");
        expect(this.record.getVariableFields("655")).andReturn(fields);
        replay(this.record, this.field, this.subfield);
        assertArrayEquals(new String[] { "bassett", "ej" }, this.eresource.doTypes().toArray());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetCompositeType() {
        assertEquals("ej", this.eresource.getCompositeType("periodical"));
    }

    @Test
    public void testGetDescription() {
        this.testDoDescription();
        assertEquals("description", this.eresource.getDescription());
    }

    @Test
    public void testGetItemCount() {
        assertArrayEquals(new int[] { 0, 0 }, this.eresource.getItemCount());
    }

    @Test
    public void testGetKeywords() {
        assertEquals("keywords", this.eresource.getKeywords());
    }

    @Test
    public void testGetMeshTerms() {
        assertEquals(Collections.singleton("mesh"), this.eresource.getMeshTerms());
    }

    @Test
    public void testGetPrimaryType() {
        expect(this.record.getVariableFields("655")).andReturn(Collections.<VariableField> singletonList(this.field));
        expect(this.field.getIndicator1()).andReturn('4');
        expect(this.field.getIndicator2()).andReturn('7');
        expect(this.field.getSubfield('a')).andReturn(this.subfield);
        expect(this.subfield.getData()).andReturn("Books.");
        replay(this.record, this.field, this.subfield);
        assertEquals("Book", this.eresource.getPrimaryType());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetRecordId() {
        assertEquals(Integer.MAX_VALUE, this.eresource.getRecordId());
    }

    @Test
    public void testGetTitle() {
        this.testDoTitle();
        assertEquals("a b n p q. 3rd ed.", this.eresource.getTitle());
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
        expect(this.record.getVariableFields("655")).andReturn(fields);
        replay(this.record, this.field, this.subfield);
        assertArrayEquals(new String[] { "bassett", "ej" }, this.eresource.getTypes().toArray());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetUpdated() {
        assertEquals(new Date(0), this.eresource.getUpdated());
    }

    @Test
    public void testGetVersions() {
        assertEquals(0, this.eresource.getVersions().size());
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
        this.testDoTitle();
        assertEquals("a b n p q. 3rd ed.", this.eresource.toString());
    }
}
