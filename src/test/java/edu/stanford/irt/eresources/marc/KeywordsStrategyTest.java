package edu.stanford.irt.eresources.marc;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import edu.stanford.irt.eresources.sax.AuthTextAugmentation;
import edu.stanford.irt.eresources.sax.ReservesTextAugmentation;
import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.Record.Field;
import edu.stanford.lane.catalog.Record.Subfield;

public class KeywordsStrategyTest {

    private AuthTextAugmentation augmentation;

    private Field field;

    private Record record;

    private ReservesTextAugmentation reservesAugmentation;

    private KeywordsStrategy strategy;

    private Subfield subfield;

    @Before
    public void setUp() {
        this.augmentation = mock(AuthTextAugmentation.class);
        this.reservesAugmentation = mock(ReservesTextAugmentation.class);
        this.strategy = new KeywordsStrategy(this.augmentation, this.reservesAugmentation);
        this.record = mock(Record.class);
        this.field = mock(Field.class);
        this.subfield = mock(Subfield.class);
    }

    @Test
    public void testGetKeywords() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.record.getLeaderByte(6)).andReturn((byte) 'a');
        expect(this.field.getTag()).andReturn("020").times(2);
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('a');
        expect(this.subfield.getData()).andReturn("020a");
        replay(this.record, this.field, this.subfield);
        assertEquals("020a ", this.strategy.getKeywords(this.record));
        verify(this.record, this.field, this.subfield);
    }
}
