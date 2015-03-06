package edu.stanford.irt.eresources.marc;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.marc4j.marc.VariableField;

import edu.stanford.irt.eresources.EresourceException;

public class BibMarcEresourceTest {

    private ControlField controlfield;

    private BibMarcEresource eresource;

    private DataField field;

    private Record record;

    private Subfield subfield;

    @Before
    public void setUp() {
        this.record = createMock(Record.class);
        this.eresource = new BibMarcEresource(Arrays.asList(new Record[] { this.record, this.record }), "keywords",
                new int[] { 1, 1 });
        this.field = createMock(DataField.class);
        this.subfield = createMock(Subfield.class);
        this.controlfield = createMock(ControlField.class);
    }

    @Test
    public void testAddCustomTypes() {
        setupLinks();
        expect(this.record.getVariableFields("655")).andReturn(Collections.singletonList(this.field));
        expect(this.field.getSubfield('a')).andReturn(this.subfield);
        expect(this.subfield.getData()).andReturn("655a");
        expect(this.record.getVariableFields("035")).andReturn(Collections.emptyList());
        replay(this.record, this.field, this.subfield);
        Set<String> types = new HashSet<String>();
        types.add("software, installed");
        this.eresource.addCustomTypes(types);
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testAddCustomTypesBassett() {
        expect(this.record.getVariableFields("856")).andReturn(Collections.<VariableField> emptyList());
        expect(this.record.getVariableFields("035")).andReturn(Collections.singletonList(this.field));
        expect(this.field.getSubfield('a')).andReturn(this.subfield);
        expect(this.subfield.getData()).andReturn("Bassett");
        replay(this.record, this.field, this.subfield);
        Set<String> types = new HashSet<String>();
        this.eresource.addCustomTypes(types);
        assertTrue(types.contains("bassett"));
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testAddCustomTypesBiotools() {
        setupLinks();
        expect(this.record.getVariableFields("655")).andReturn(Collections.singletonList(this.field));
        expect(this.field.getSubfield('a')).andReturn(this.subfield);
        expect(this.subfield.getData()).andReturn("Subset, Biotools");
        expect(this.record.getVariableFields("035")).andReturn(Collections.<VariableField> emptyList());
        replay(this.record, this.field, this.subfield);
        Set<String> types = new HashSet<String>();
        this.eresource.addCustomTypes(types);
        assertTrue(types.contains("software"));
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testAddCustomTypesInstalledSoftware() {
        expect(this.record.getVariableFields("856")).andReturn(Collections.<VariableField> emptyList());
        expect(this.record.getVariableFields("035")).andReturn(Collections.<VariableField> emptyList());
        replay(this.record, this.field, this.subfield);
        Set<String> types = new HashSet<String>();
        types.add("newspaper");
        this.eresource.addCustomTypes(types);
        assertTrue(types.contains("ej"));
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testAddPrimaryType() {
        expect(this.record.getVariableFields("655")).andReturn(Collections.singletonList(this.field));
        expect(this.field.getIndicator1()).andReturn('4');
        expect(this.field.getIndicator2()).andReturn('7');
        expect(this.field.getSubfield('a')).andReturn(this.subfield);
        expect(this.subfield.getData()).andReturn("primary type");
        replay(this.record, this.field, this.subfield);
        Set<String> types = new HashSet<String>();
        this.eresource.addPrimaryType(types);
        assertTrue(types.contains("primarytype"));
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testAddPrimaryTypeBook() {
        expect(this.record.getVariableFields("655")).andReturn(Collections.singletonList(this.field));
        expect(this.field.getIndicator1()).andReturn('4');
        expect(this.field.getIndicator2()).andReturn('7');
        expect(this.field.getSubfield('a')).andReturn(this.subfield);
        expect(this.subfield.getData()).andReturn("book");
        replay(this.record, this.field, this.subfield);
        Set<String> types = new HashSet<String>();
        this.eresource.addPrimaryType(types);
        assertTrue(types.contains("bookdigital"));
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testAddPrimaryTypeSerial() {
        expect(this.record.getVariableFields("655")).andReturn(Collections.singletonList(this.field));
        expect(this.field.getIndicator1()).andReturn('4');
        expect(this.field.getIndicator2()).andReturn('7');
        expect(this.field.getSubfield('a')).andReturn(this.subfield);
        expect(this.subfield.getData()).andReturn("serial");
        expect(this.record.getVariableFields("655")).andReturn(Collections.emptyList());
        replay(this.record, this.field, this.subfield);
        Set<String> types = new HashSet<String>();
        this.eresource.addPrimaryType(types);
        assertTrue(types.contains("journal"));
        assertTrue(types.contains("journaldigital"));
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testAddPrimaryTypeSerialBook() {
        expect(this.record.getVariableFields("655")).andReturn(Collections.singletonList(this.field));
        expect(this.field.getIndicator1()).andReturn('4');
        expect(this.field.getIndicator2()).andReturn('7');
        expect(this.field.getSubfield('a')).andReturn(this.subfield);
        expect(this.subfield.getData()).andReturn("serial");
        expect(this.record.getVariableFields("655")).andReturn(Collections.singletonList(this.field));
        expect(this.field.getSubfield('a')).andReturn(this.subfield);
        expect(this.subfield.getData()).andReturn("book");
        replay(this.record, this.field, this.subfield);
        Set<String> types = new HashSet<String>();
        this.eresource.addPrimaryType(types);
        assertTrue(types.contains("bookdigital"));
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testAddPrimaryTypeSerialDatabase() {
        expect(this.record.getVariableFields("655")).andReturn(Collections.singletonList(this.field));
        expect(this.field.getIndicator1()).andReturn('4');
        expect(this.field.getIndicator2()).andReturn('7');
        expect(this.field.getSubfield('a')).andReturn(this.subfield);
        expect(this.subfield.getData()).andReturn("serial");
        expect(this.record.getVariableFields("655")).andReturn(Collections.singletonList(this.field));
        expect(this.field.getSubfield('a')).andReturn(this.subfield);
        expect(this.subfield.getData()).andReturn("database");
        replay(this.record, this.field, this.subfield);
        Set<String> types = new HashSet<String>();
        this.eresource.addPrimaryType(types);
        assertTrue(types.isEmpty());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testAddPrimaryTypeVisualMaterial() {
        expect(this.record.getVariableFields("655")).andReturn(Collections.singletonList(this.field));
        expect(this.field.getIndicator1()).andReturn('4');
        expect(this.field.getIndicator2()).andReturn('7');
        expect(this.field.getSubfield('a')).andReturn(this.subfield);
        expect(this.subfield.getData()).andReturn("Visual Material");
        expect(this.record.getVariableFields("655")).andReturn(Collections.emptyList());
        replay(this.record, this.field, this.subfield);
        Set<String> types = new HashSet<String>();
        this.eresource.addPrimaryType(types);
        assertTrue(types.contains("image"));
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testAddPrimaryTypeVisualMaterialVideo() {
        expect(this.record.getVariableFields("655")).andReturn(Collections.singletonList(this.field));
        expect(this.field.getIndicator1()).andReturn('4');
        expect(this.field.getIndicator2()).andReturn('7');
        expect(this.field.getSubfield('a')).andReturn(this.subfield);
        expect(this.subfield.getData()).andReturn("Visual Material");
        expect(this.record.getVariableFields("655")).andReturn(Collections.singletonList(this.field));
        expect(this.field.getSubfield('a')).andReturn(this.subfield);
        expect(this.subfield.getData()).andReturn("Digital Video, local");
        replay(this.record, this.field, this.subfield);
        Set<String> types = new HashSet<String>();
        this.eresource.addPrimaryType(types);
        assertTrue(types.contains("video"));
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetDescription505() {
        expect(this.record.getVariableFields("520")).andReturn(Collections.emptyList());
        expect(this.record.getVariableFields("505")).andReturn(Collections.<VariableField> singletonList(this.field));
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getData()).andReturn("data");
        replay(this.record, this.field, this.subfield);
        assertEquals("data", this.eresource.getDescription());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetDescription520() {
        expect(this.record.getVariableFields("520")).andReturn(Collections.<VariableField> singletonList(this.field));
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getData()).andReturn("data");
        replay(this.record, this.field, this.subfield);
        assertEquals("data", this.eresource.getDescription());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetDescriptionNull() {
        expect(this.record.getVariableFields("520")).andReturn(Collections.emptyList());
        expect(this.record.getVariableFields("505")).andReturn(Collections.emptyList());
        replay(this.record, this.field, this.subfield);
        assertNull(this.eresource.getDescription());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetItemCount() {
        replay(this.record, this.field, this.subfield);
        assertArrayEquals(new int[] { 1, 1 }, this.eresource.getItemCount());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetMeshTerms() {
        expect(this.record.getVariableFields("650")).andReturn(Collections.<VariableField> singletonList(this.field));
        expect(this.field.getIndicator1()).andReturn('4');
        expect(this.field.getIndicator2()).andReturn('7');
        expect(this.field.getSubfield('a')).andReturn(this.subfield);
        expect(this.subfield.getData()).andReturn("data");
        replay(this.record, this.field, this.subfield);
        Collection<String> mesh = this.eresource.getMeshTerms();
        assertEquals("data", mesh.iterator().next());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetRealPrimaryType() {
        replay(this.record, this.field, this.subfield);
        assertEquals("type", this.eresource.getRealPrimaryType("type"));
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetRealPrimaryTypeBook() {
        replay(this.record, this.field, this.subfield);
        assertEquals("Book Digital", this.eresource.getRealPrimaryType("book"));
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetRealPrimaryTypeSerial() {
        expect(this.record.getVariableFields("655")).andReturn(Collections.emptyList());
        replay(this.record, this.field, this.subfield);
        assertEquals("Journal Digital", this.eresource.getRealPrimaryType("serial"));
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetRealPrimaryTypeSerialBook() {
        expect(this.record.getVariableFields("655")).andReturn(Collections.singletonList(this.field));
        expect(this.field.getSubfield('a')).andReturn(this.subfield);
        expect(this.subfield.getData()).andReturn("Books.");
        replay(this.record, this.field, this.subfield);
        assertEquals("Book Digital", this.eresource.getRealPrimaryType("serial"));
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetRealPrimaryTypeSerialDatabase() {
        expect(this.record.getVariableFields("655")).andReturn(Collections.singletonList(this.field));
        expect(this.field.getSubfield('a')).andReturn(this.subfield);
        expect(this.subfield.getData()).andReturn("Databases");
        replay(this.record, this.field, this.subfield);
        assertEquals("Database", this.eresource.getRealPrimaryType("serial"));
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetRealPrimaryTypeVisualMaterial() {
        expect(this.record.getVariableFields("655")).andReturn(Collections.emptyList());
        setupLinks();
        expect(this.record.getVariableFields("655")).andReturn(Collections.emptyList());
        expect(this.record.getVariableFields("035")).andReturn(Collections.emptyList());
        expect(this.record.getVariableFields("655")).andReturn(Collections.emptyList());
        replay(this.record, this.field, this.subfield);
        assertEquals("Image", this.eresource.getRealPrimaryType("visual material"));
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetRealPrimaryTypeVisualMaterialVideo() {
        expect(this.record.getVariableFields("655")).andReturn(Collections.singletonList(this.field));
        expect(this.field.getSubfield('a')).andReturn(this.subfield);
        expect(this.subfield.getData()).andReturn("Digital Video.");
        setupLinks();
        expect(this.record.getVariableFields("655")).andReturn(Collections.emptyList());
        expect(this.record.getVariableFields("035")).andReturn(Collections.emptyList());
        expect(this.record.getVariableFields("655")).andReturn(Collections.emptyList());
        replay(this.record, this.field, this.subfield);
        assertEquals("Video", this.eresource.getRealPrimaryType("visual material"));
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetRecordId() {
        expect(this.record.getControlNumber()).andReturn("12");
        replay(this.record, this.field, this.subfield);
        assertEquals(12, this.eresource.getRecordId());
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

    @Test
    public void testGetUpdated() {
        expect(this.record.getVariableField("005")).andReturn(this.controlfield).times(2);
        expect(this.controlfield.getData()).andReturn("19550519120000").times(2);
        replay(this.record, this.field, this.subfield, this.controlfield);
        Calendar cal = Calendar.getInstance();
        cal.set(1955, 4, 19, 12, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        assertEquals(cal.getTime(), this.eresource.getUpdated());
        verify(this.record, this.field, this.subfield, this.controlfield);
    }

    @Test
    public void testGetUpdatedHoldingsNewer() {
        expect(this.record.getVariableField("005")).andReturn(this.controlfield).times(2);
        expect(this.controlfield.getData()).andReturn("19550519120000");
        expect(this.controlfield.getData()).andReturn("19690505120000");
        replay(this.record, this.field, this.subfield, this.controlfield);
        Calendar cal = Calendar.getInstance();
        cal.set(1969, 4, 5, 12, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        assertEquals(cal.getTime(), this.eresource.getUpdated());
        verify(this.record, this.field, this.subfield, this.controlfield);
    }

    @Test(expected = EresourceException.class)
    public void testGetUpdatedParseException() {
        expect(this.record.getVariableField("005")).andReturn(this.controlfield);
        expect(this.controlfield.getData()).andReturn("notavaliddatestring");
        replay(this.record, this.field, this.subfield, this.controlfield);
        this.eresource.getUpdated();
        verify(this.record, this.field, this.subfield, this.controlfield);
    }

    @Test
    public void testGetVersions() {
        setupLinks();
        replay(this.record, this.field, this.subfield);
        assertEquals(1, this.eresource.getVersions().size());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetYear() {
        expect(this.record.getVariableField("008")).andReturn(this.controlfield);
        expect(this.controlfield.getData()).andReturn("012345619551969");
        replay(this.record, this.field, this.subfield, this.controlfield);
        assertEquals(1969, this.eresource.getYear());
        verify(this.record, this.field, this.subfield, this.controlfield);
    }

    @Test
    public void testGetYearNullDates() {
        expect(this.record.getVariableField("008")).andReturn(this.controlfield);
        expect(this.controlfield.getData()).andReturn("0123456xxxxxxxx");
        replay(this.record, this.field, this.subfield, this.controlfield);
        assertEquals(0, this.eresource.getYear());
        verify(this.record, this.field, this.subfield, this.controlfield);
    }

    @Test
    public void testGetYearNullEnddate() {
        expect(this.record.getVariableField("008")).andReturn(this.controlfield);
        expect(this.controlfield.getData()).andReturn("01234561955xxxx");
        replay(this.record, this.field, this.subfield, this.controlfield);
        assertEquals(1955, this.eresource.getYear());
        verify(this.record, this.field, this.subfield, this.controlfield);
    }

    @Test
    public void testIsCore() {
        expect(this.record.getVariableFields("655")).andReturn(
                Arrays.asList(new VariableField[] { this.field, this.field }));
        expect(this.field.getSubfield('a')).andReturn(this.subfield);
        expect(this.subfield.getData()).andReturn("Core Material");
        replay(this.record, this.field, this.subfield);
        assertTrue(this.eresource.isCore());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testIsCoreNOt() {
        expect(this.record.getVariableFields("655")).andReturn(
                Arrays.asList(new VariableField[] { this.field, this.field }));
        expect(this.field.getSubfield('a')).andReturn(this.subfield);
        expect(this.subfield.getData()).andReturn("not core");
        expect(this.field.getSubfield('a')).andReturn(this.subfield);
        expect(this.subfield.getData()).andReturn("also not core");
        replay(this.record, this.field, this.subfield);
        assertFalse(this.eresource.isCore());
        verify(this.record, this.field, this.subfield);
    }

    private void setupLinks() {
        expect(this.record.getVariableFields("856")).andReturn(Collections.<VariableField> singletonList(this.field));
        expect(this.field.getSubfield('u')).andReturn(this.subfield);
        expect(this.subfield.getData()).andReturn("856u");
        expect(this.field.getSubfield('q')).andReturn(this.subfield);
        expect(this.subfield.getData()).andReturn("856q");
        expect(this.record.getVariableField("866")).andReturn(this.field);
        expect(this.field.getSubfield('v')).andReturn(this.subfield);
        expect(this.subfield.getData()).andReturn("866v");
        expect(this.record.getVariableField("866")).andReturn(this.field);
        expect(this.field.getSubfield('y')).andReturn(this.subfield);
        expect(this.subfield.getData()).andReturn("866y");
        expect(this.record.getVariableFields("866")).andReturn(Collections.<VariableField> singletonList(this.field));
        expect(this.field.getSubfields('z')).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getData()).andReturn("866z");
        expect(this.record.getVariableField("866")).andReturn(this.field);
        expect(this.field.getSubfield('v')).andReturn(this.subfield);
        expect(this.subfield.getData()).andReturn("866v");
        expect(this.record.getVariableField("866")).andReturn(this.field);
        expect(this.field.getSubfield('y')).andReturn(this.subfield);
        expect(this.subfield.getData()).andReturn("866y");
        expect(this.record.getVariableFields("866")).andReturn(Collections.<VariableField> singletonList(this.field));
        expect(this.field.getSubfields('z')).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getData()).andReturn("866z");
        expect(this.record.getVariableField("866")).andReturn(this.field);
        expect(this.field.getSubfield('y')).andReturn(this.subfield);
        expect(this.subfield.getData()).andReturn("866y");
        expect(this.record.getVariableField("866")).andReturn(this.field);
        expect(this.field.getSubfield('y')).andReturn(this.subfield);
        expect(this.subfield.getData()).andReturn("866y");
        expect(this.record.getVariableField("844")).andReturn(this.field);
        expect(this.field.getSubfield('a')).andReturn(this.subfield);
        expect(this.subfield.getData()).andReturn("844a");
        expect(this.record.getVariableField("844")).andReturn(this.field);
        expect(this.field.getSubfield('a')).andReturn(this.subfield);
        expect(this.subfield.getData()).andReturn("844a");
    }
}
