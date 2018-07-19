package edu.stanford.irt.eresources.marc;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import edu.stanford.irt.eresources.TextParserHelper;
import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.Record.Field;
import edu.stanford.lane.catalog.Record.Subfield;

public class AuthMarcEresourceTest {

    private AuthMarcEresource eresource;

    private Field field;

    private KeywordsStrategy keywordsStrategy;

    private Record record;

    private Subfield subfield;

    private TypeFactory typeFactory;

    @Before
    public void setUp() {
        this.record = mock(Record.class);
        this.field = mock(Field.class);
        this.subfield = mock(Subfield.class);
        this.keywordsStrategy = mock(KeywordsStrategy.class);
        this.typeFactory = mock(TypeFactory.class);
        this.eresource = new AuthMarcEresource(this.record, this.keywordsStrategy, this.typeFactory);
    }

    @Test
    public void testGetItemCount() {
        assertEquals(0, this.eresource.getItemCount()[0]);
    }

    @Test
    public void testGetMeshTerms() {
        expect(this.record.getFields()).andReturn(Collections.emptyList());
        replay(this.record);
        assertEquals(Collections.emptySet(), this.eresource.getMeshTerms());
        verify(this.record);
    }

    @Test
    public void testGetRecordType() {
        assertEquals("auth", this.eresource.getRecordType());
    }

    @Test
    public void testGetVersions() {
        expect(this.record.getFields()).andReturn(Collections.emptyList());
        replay(this.record);
        assertEquals(Collections.emptyList(), this.eresource.getVersions());
        verify(this.record);
    }

    @Test
    public void testGetVersionsF856() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("856");
        replay(this.record, this.field);
        assertEquals(MarcVersion.class, this.eresource.getVersions().get(0).getClass());
        verify(this.record, this.field);
    }

    @Test
    public void testGetYear() {
        expect(this.record.getFields()).andReturn(Collections.emptyList());
        replay(this.record);
        assertEquals(0, this.eresource.getYear());
        verify(this.record);
    }

    @Test
    public void testGetYearContinuing() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("943");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('b');
        expect(this.subfield.getData()).andReturn("continuing");
        replay(this.record, this.field, this.subfield);
        assertEquals(TextParserHelper.THIS_YEAR, this.eresource.getYear());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetYearValue() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("943");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('b');
        expect(this.subfield.getData()).andReturn("1900");
        replay(this.record, this.field, this.subfield);
        assertEquals(1900, this.eresource.getYear());
        verify(this.record, this.field, this.subfield);
    }
}
