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

public class AcceptableDBCallNumberStrategyTest {

    private Field field;

    private InclusionStrategy inclusionStrategy;

    private Record marcRecord;

    private Subfield subfield;

    @BeforeEach
    public void setUp() throws Exception {
        this.inclusionStrategy = new AcceptableDBCallNumberStrategy(Collections.singletonList("A"));
        this.marcRecord = mock(Record.class);
        this.field = mock(Field.class);
        this.subfield = mock(Subfield.class);
    }

    @Test
    public final void testIsAcceptableFalse() {
        expect(this.marcRecord.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("099");
        expect(this.field.getSubfields()).andReturn(Arrays.asList(new Subfield[] { this.subfield }));
        expect(this.subfield.getCode()).andReturn('a');
        expect(this.subfield.getData()).andReturn("Z");
        replay(this.marcRecord, this.field, this.subfield);
        assertFalse(this.inclusionStrategy.isAcceptable(this.marcRecord));
        verify(this.marcRecord, this.field, this.subfield);
    }

    @Test
    public final void testIsAcceptableTrue() {
        expect(this.marcRecord.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("099");
        expect(this.field.getSubfields()).andReturn(Arrays.asList(new Subfield[] { this.subfield }));
        expect(this.subfield.getCode()).andReturn('a');
        expect(this.subfield.getData()).andReturn("A");
        replay(this.marcRecord, this.field, this.subfield);
        assertTrue(this.inclusionStrategy.isAcceptable(this.marcRecord));
        verify(this.marcRecord, this.field, this.subfield);
    }
}
