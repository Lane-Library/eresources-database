package edu.stanford.irt.eresources.marc;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.marc4j.MarcReader;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;

public class AuthTextAugmentationTest {

    private AuthTextAugmentation augmentation;

    private DataField field;

    private MarcReader marcReader;

    private Record record;

    private Subfield subfield;

    @Before
    public void setUp() {
        this.marcReader = createMock(MarcReader.class);
        this.augmentation = new AuthTextAugmentation(this.marcReader);
        this.record = createMock(Record.class);
        this.field = createMock(DataField.class);
        this.subfield = createMock(Subfield.class);
    }

    @Test
    public void testGetAuthAugmentations() {
        expect(this.marcReader.hasNext()).andReturn(true);
        expect(this.marcReader.next()).andReturn(this.record);
        expect(this.record.getDataFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("400");
        expect(this.field.getSubfields('a')).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getData()).andReturn("augmentation");
        expect(this.marcReader.hasNext()).andReturn(false);
        replay(this.marcReader, this.record, this.field, this.subfield);
        assertEquals("augmentation", this.augmentation.getAuthAugmentations("term", "tag"));
        verify(this.marcReader, this.record, this.field, this.subfield);
    }
}
