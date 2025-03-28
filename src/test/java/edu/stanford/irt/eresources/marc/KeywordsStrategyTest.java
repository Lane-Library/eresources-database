package edu.stanford.irt.eresources.marc;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.Record.Field;
import edu.stanford.lane.catalog.Record.Subfield;

class KeywordsStrategyTest {

    private AuthTextAugmentation augmentation;

    private Field field;

    private Record record;

    private ReservesTextAugmentation reservesAugmentation;

    private KeywordsStrategy strategy;

    private Subfield subfield;

    private List<Field> twoFields;

    @BeforeEach
    void setUp() {
        this.augmentation = mock(AuthTextAugmentation.class);
        this.reservesAugmentation = mock(ReservesTextAugmentation.class);
        this.strategy = new KeywordsStrategy(this.augmentation, this.reservesAugmentation);
        this.record = mock(Record.class);
        this.field = mock(Field.class);
        this.subfield = mock(Subfield.class);
        this.twoFields = new ArrayList<>();
        this.twoFields.add(this.field);
        this.twoFields.add(this.field);
    }

    @Test
    void testGetKeywordsBib() {
        List<Subfield> subs = new ArrayList<>();
        subs.add(this.subfield);
        subs.add(this.subfield);
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.record.getLeaderByte(6)).andReturn((byte) 'a');
        expect(this.field.getTag()).andReturn("024").times(2);
        expect(this.field.getSubfields()).andReturn(subs);
        expect(this.subfield.getCode()).andReturn('a');
        expect(this.subfield.getData()).andReturn("10.5694/j.1326-5377.1916.tb117256.x");
        expect(this.subfield.getCode()).andReturn('2');
        expect(this.subfield.getData()).andReturn("doi");
        replay(this.record, this.field, this.subfield);
        assertEquals("10.5694/j.1326-5377.1916.tb117256.x  doi ", this.strategy.getKeywords(this.record));
        verify(this.record, this.field, this.subfield);
    }

    @Test
    void testGetKeywordsBibAugmentable() {
        expect(this.record.getFields()).andReturn(this.twoFields);
        expect(this.record.getLeaderByte(6)).andReturn((byte) 'a');
        expect(this.field.getTag()).andReturn("100").times(2);
        expect(this.field.getTag()).andReturn("001");
        expect(this.field.getData()).andReturn("cn");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield)).times(2);
        expect(this.subfield.getCode()).andReturn('0').times(2);
        expect(this.subfield.getData()).andReturn("1000").times(4);
        expect(this.augmentation.getAuthAugmentations("1000")).andReturn("augmentation").times(2);
        expect(this.reservesAugmentation.getReservesAugmentations("cn")).andReturn("reservesAug");
        replay(this.record, this.field, this.subfield, this.augmentation, this.reservesAugmentation);
        assertEquals("1000  augmentation 1000  augmentation reservesAug", this.strategy.getKeywords(this.record));
        verify(this.record, this.field, this.subfield, this.augmentation, this.reservesAugmentation);
    }

    @Test
    void testGetKeywordsHolding() {
        List<Field> fields = new ArrayList<>();
        Field f1 = mock(Field.class);
        Field f2 = mock(Field.class);
        fields.add(f1);
        fields.add(f2);
        Subfield sf1 = mock(Subfield.class);
        List<Subfield> subs = new ArrayList<>();
        Subfield sf2 = mock(Subfield.class);
        Subfield sf3 = mock(Subfield.class);
        subs.add(sf2);
        subs.add(sf3);
        expect(this.record.getFields()).andReturn(fields);
        expect(this.record.getLeaderByte(6)).andReturn((byte) 'u');
        expect(f1.getTag()).andReturn("852");
        expect(f1.getSubfields()).andReturn(Collections.singletonList(sf1));
        expect(sf1.getCode()).andReturn('b');
        expect(sf1.getData()).andReturn("852-b-indexed");
        expect(f2.getTag()).andReturn("907");
        expect(f2.getSubfields()).andReturn(subs);
        expect(sf2.getCode()).andReturn('x');
        expect(sf2.getData()).andReturn("907-x-indexed");
        // 907 a should not be indexed
        expect(sf3.getCode()).andReturn('a');
        replay(this.record, f1, f2, sf1, sf2, sf3);
        assertEquals("852-b-indexed  907-x-indexed ", this.strategy.getKeywords(this.record));
        verify(this.record, f1, f2, sf1, sf2, sf3);
    }
}
