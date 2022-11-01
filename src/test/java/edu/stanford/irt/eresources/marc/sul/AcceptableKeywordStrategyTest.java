package edu.stanford.irt.eresources.marc.sul;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.stanford.irt.eresources.marc.SulTypeFactory;
import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.Record.Field;
import edu.stanford.lane.catalog.Record.Subfield;

public class AcceptableKeywordStrategyTest {

    private Field field;

    private InclusionStrategy inclusionStrategy;

    private Record marcRecord;

    private Subfield subfield;

    private SulTypeFactory typeFactory;

    @Before
    public void setUp() throws Exception {
        this.typeFactory = mock(SulTypeFactory.class);
        List<String> acceptableKeywords = Collections.singletonList("keyword");
        List<String> acceptablePrimaryTypes = Collections.singletonList("pType");
        this.inclusionStrategy = new AcceptableKeywordStrategy(acceptableKeywords, acceptablePrimaryTypes,
                this.typeFactory);
        this.marcRecord = mock(Record.class);
        this.field = mock(Field.class);
        this.subfield = mock(Subfield.class);
    }

    @Test
    public final void testIsAcceptableFiction() {
        expect(this.typeFactory.getPrimaryType(this.marcRecord)).andReturn("pType");
        expect(this.marcRecord.getFields()).andReturn(Collections.singletonList(this.field)).times(3);
        expect(this.field.getTag()).andReturn("655").times(3);
        expect(this.field.getSubfields()).andReturn(Arrays.asList(new Subfield[] { this.subfield }));
        expect(this.subfield.getCode()).andReturn('a');
        expect(this.subfield.getData()).andReturn("fiction");
        replay(this.marcRecord, this.field, this.subfield, this.typeFactory);
        assertFalse(this.inclusionStrategy.isAcceptable(this.marcRecord));
        verify(this.marcRecord, this.field, this.subfield, this.typeFactory);
    }

    @Test
    public final void testIsAcceptableInspectKeywordsFalse() {
        expect(this.typeFactory.getPrimaryType(this.marcRecord)).andReturn("pType");
        expect(this.marcRecord.getFields()).andReturn(Collections.singletonList(this.field)).times(3);
        expect(this.field.getTag()).andReturn("655").times(3);
        expect(this.field.getSubfields()).andReturn(Arrays.asList(new Subfield[] { this.subfield }));
        expect(this.subfield.getCode()).andReturn('a');
        expect(this.subfield.getData()).andReturn("nonfiction");
        expect(this.marcRecord.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("069");
        expect(this.marcRecord.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("059");
        // .toString() cannot be mocked with easymock
        // https://easymock.org/user-guide.html#mocking-limitations
        // this.marcRecord.toString() will return "easymock for class edu.stanford.lane.catalog.record"
        replay(this.marcRecord, this.field, this.subfield, this.typeFactory);
        assertFalse(this.inclusionStrategy.isAcceptable(this.marcRecord));
        verify(this.marcRecord, this.field, this.subfield, this.typeFactory);
    }

    @Test
    public final void testIsAcceptableInspectKeywordsTrue() {
        List<String> acceptableKeywords = Collections.singletonList("easymock");
        List<String> acceptablePrimaryTypes = Collections.singletonList("pType");
        this.inclusionStrategy = new AcceptableKeywordStrategy(acceptableKeywords, acceptablePrimaryTypes,
                this.typeFactory);
        expect(this.typeFactory.getPrimaryType(this.marcRecord)).andReturn("pType");
        expect(this.marcRecord.getFields()).andReturn(Collections.singletonList(this.field)).times(3);
        expect(this.field.getTag()).andReturn("655").times(3);
        expect(this.field.getSubfields()).andReturn(Arrays.asList(new Subfield[] { this.subfield }));
        expect(this.subfield.getCode()).andReturn('a');
        expect(this.subfield.getData()).andReturn("nonfiction");
        expect(this.marcRecord.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("069");
        expect(this.marcRecord.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("059");
        // .toString() cannot be mocked with easymock
        // https://easymock.org/user-guide.html#mocking-limitations
        // this.marcRecord.toString() will return "easymock for class edu.stanford.lane.catalog.record"
        replay(this.marcRecord, this.field, this.subfield, this.typeFactory);
        assertTrue(this.inclusionStrategy.isAcceptable(this.marcRecord));
        verify(this.marcRecord, this.field, this.subfield, this.typeFactory);
    }

    @Test
    public final void testIsAcceptableLCCN() {
        expect(this.typeFactory.getPrimaryType(this.marcRecord)).andReturn("pType");
        expect(this.marcRecord.getFields()).andReturn(Collections.singletonList(this.field)).times(3);
        expect(this.field.getTag()).andReturn("655").times(3);
        expect(this.field.getSubfields()).andReturn(Arrays.asList(new Subfield[] { this.subfield }));
        expect(this.subfield.getCode()).andReturn('a');
        expect(this.subfield.getData()).andReturn("nonfiction");
        expect(this.marcRecord.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("069");
        expect(this.marcRecord.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("050");
        expect(this.field.getSubfields()).andReturn(Arrays.asList(new Subfield[] { this.subfield }));
        expect(this.subfield.getCode()).andReturn('a');
        expect(this.subfield.getData()).andReturn("lccn");
        replay(this.marcRecord, this.field, this.subfield, this.typeFactory);
        assertFalse(this.inclusionStrategy.isAcceptable(this.marcRecord));
        verify(this.marcRecord, this.field, this.subfield, this.typeFactory);
    }

    @Test
    public final void testIsAcceptableNLMCN() {
        expect(this.typeFactory.getPrimaryType(this.marcRecord)).andReturn("pType");
        expect(this.marcRecord.getFields()).andReturn(Collections.singletonList(this.field)).times(3);
        expect(this.field.getTag()).andReturn("655").times(3);
        expect(this.field.getSubfields()).andReturn(Arrays.asList(new Subfield[] { this.subfield }));
        expect(this.subfield.getCode()).andReturn('a');
        expect(this.subfield.getData()).andReturn("nonfiction");
        expect(this.marcRecord.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("060");
        replay(this.marcRecord, this.field, this.subfield, this.typeFactory);
        assertFalse(this.inclusionStrategy.isAcceptable(this.marcRecord));
        verify(this.marcRecord, this.field, this.subfield, this.typeFactory);
    }

    @Test
    public final void testIsAcceptableNotPrimaryType() {
        expect(this.typeFactory.getPrimaryType(this.marcRecord)).andReturn("nope");
        replay(this.marcRecord, this.typeFactory);
        assertFalse(this.inclusionStrategy.isAcceptable(this.marcRecord));
        verify(this.marcRecord, this.typeFactory);
    }
}
