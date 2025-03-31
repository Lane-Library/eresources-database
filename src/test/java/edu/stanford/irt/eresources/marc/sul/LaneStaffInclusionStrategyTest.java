package edu.stanford.irt.eresources.marc.sul;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.Record.Field;

class LaneStaffInclusionStrategyTest {

    private Field field;

    private InclusionStrategy inclusionStrategy;

    private Record marcRecord;

    @BeforeEach
    void setUp() {
        this.inclusionStrategy = new LaneStaffInclusionStrategy();
        this.marcRecord = mock(Record.class);
        this.field = mock(Field.class);
    }

    @Test
    final void testIsAcceptableFalse() {
        expect(this.marcRecord.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("900");
        replay(this.marcRecord, this.field);
        assertFalse(this.inclusionStrategy.isAcceptable(this.marcRecord));
        verify(this.marcRecord, this.field);
    }

    @Test
    final void testIsAcceptableTrue() {
        expect(this.marcRecord.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("909");
        replay(this.marcRecord, this.field);
        assertTrue(this.inclusionStrategy.isAcceptable(this.marcRecord));
        verify(this.marcRecord, this.field);
    }
}
