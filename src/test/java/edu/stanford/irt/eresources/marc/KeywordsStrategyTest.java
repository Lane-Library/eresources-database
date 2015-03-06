package edu.stanford.irt.eresources.marc;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;

public class KeywordsStrategyTest {

    private AuthTextAugmentation augmentation;

    private DataField field;

    private Record record;

    private KeywordsStrategy strategy;

    private Subfield subfield;

    @Before
    public void setUp() {
        this.augmentation = createMock(AuthTextAugmentation.class);
        this.strategy = new KeywordsStrategy(this.augmentation);
        this.record = createMock(Record.class);
        this.field = createMock(DataField.class);
        this.subfield = createMock(Subfield.class);
    }

    @Test
    public void testGetKeywords() {
        List<DataField> fields = new ArrayList<DataField>();
        for (int i = 0; i < 1; i++) {
            fields.add(this.field);
        }
        List<Subfield> subfields = new ArrayList<Subfield>();
        for (int i = 0; i < 1; i++) {
            subfields.add(this.subfield);
        }
        expect(this.record.getDataFields()).andReturn(fields);
        expect(this.field.getTag()).andReturn("020");
        expect(this.field.getSubfields()).andReturn(subfields);
        expect(this.subfield.getCode()).andReturn('a');
        expect(this.subfield.getData()).andReturn("020a");
        replay(this.record, this.field, this.subfield);
        assertEquals("020a ", this.strategy.getKeywords(this.record));
        verify(this.record, this.field, this.subfield);
    }
}
