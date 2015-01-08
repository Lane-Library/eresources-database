package edu.stanford.irt.eresources.marc;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;

import edu.stanford.irt.eresources.sax.AuthTextAugmentation;


public class KeywordsStrategyTest {
    
    private KeywordsStrategy strategy;
    private AuthTextAugmentation augmentation;
    private Record record;
    private DataField field;
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
        expect(field.getTag()).andReturn("020");
        expect(this.field.getSubfields()).andReturn(subfields);
        expect(this.subfield.getCode()).andReturn('a');
        expect(this.subfield.getData()).andReturn("020a");
        replay(this.record, this.field, this.subfield);
        assertEquals("020a ", this.strategy.getKeywords(this.record));
        verify(this.record, this.field, this.subfield);
    }
}
