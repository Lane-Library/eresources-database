package edu.stanford.irt.eresources.marc.sul;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.Record.Field;
import edu.stanford.lane.catalog.Record.Subfield;

class AcceptableLCCallNumberStrategyTest {

    private Field field;

    private InclusionStrategy inclusionStrategy;

    private Record marcRecord;

    private Subfield subfield;

    @BeforeEach
    void setUp() {
        this.inclusionStrategy = new AcceptableLCCallNumberStrategy(Collections.singletonList("BF"));
        this.marcRecord = mock(Record.class);
        this.field = mock(Field.class);
        this.subfield = mock(Subfield.class);
    }

    @Test
    final void testIsAcceptableFiction() {
        expect(this.marcRecord.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("050");
        expect(this.field.getSubfields()).andReturn(Arrays.asList(new Subfield[] { this.subfield }));
        expect(this.subfield.getCode()).andReturn('a');
        expect(this.subfield.getData()).andReturn("ZZ 123");
        expect(this.marcRecord.getFields()).andReturn(Collections.singletonList(this.field)).times(3);
        expect(this.field.getTag()).andReturn("655").times(3);
        expect(this.field.getSubfields()).andReturn(Arrays.asList(new Subfield[] { this.subfield }));
        expect(this.subfield.getCode()).andReturn('a');
        expect(this.subfield.getData()).andReturn("fiction");
        replay(this.marcRecord, this.field, this.subfield);
        assertFalse(this.inclusionStrategy.isAcceptable(this.marcRecord));
        verify(this.marcRecord, this.field, this.subfield);
    }

    @Test
    final void testIsAcceptableNotFiction() {
        expect(this.marcRecord.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("050");
        expect(this.field.getSubfields()).andReturn(Arrays.asList(new Subfield[] { this.subfield }));
        expect(this.subfield.getCode()).andReturn('a');
        expect(this.subfield.getData()).andReturn("ZZ 123");
        expect(this.marcRecord.getFields()).andReturn(Collections.singletonList(this.field)).times(3);
        expect(this.field.getTag()).andReturn("655").times(3);
        expect(this.field.getSubfields()).andReturn(Arrays.asList(new Subfield[] { this.subfield }));
        expect(this.subfield.getCode()).andReturn('a').times(2);
        expect(this.subfield.getData()).andReturn("non-fiction");
        expect(this.marcRecord.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("650");
        expect(this.field.getIndicator2()).andReturn('7');
        expect(this.field.getSubfields()).andReturn(Arrays.asList(new Subfield[] { this.subfield }));
        expect(this.subfield.getCode()).andReturn('a');
        expect(this.subfield.getData()).andReturn("send to LCSH mapper");
        replay(this.marcRecord, this.field, this.subfield);
        assertFalse(this.inclusionStrategy.isAcceptable(this.marcRecord));
        verify(this.marcRecord, this.field, this.subfield);
    }

    @Test
    final void testIsAcceptableTrue() {
        expect(this.marcRecord.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("050");
        expect(this.field.getSubfields()).andReturn(Arrays.asList(new Subfield[] { this.subfield }));
        expect(this.subfield.getCode()).andReturn('a');
        expect(this.subfield.getData()).andReturn("BF 123");
        replay(this.marcRecord, this.field, this.subfield);
        assertTrue(this.inclusionStrategy.isAcceptable(this.marcRecord));
        verify(this.marcRecord, this.field, this.subfield);
    }
}
