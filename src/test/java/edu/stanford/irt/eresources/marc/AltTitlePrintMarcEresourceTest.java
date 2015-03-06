package edu.stanford.irt.eresources.marc;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;

public class AltTitlePrintMarcEresourceTest {

    private AltTitlePrintMarcEresource eresource;

    private DataField field;

    private Record record;

    private Subfield subfield;

    @Before
    public void setUp() {
        this.record = createMock(Record.class);
        this.eresource = new AltTitlePrintMarcEresource(Collections.singletonList(this.record), null, null);
        this.field = createMock(DataField.class);
        this.subfield = createMock(Subfield.class);
    }

    @Test
    public void testGetTitle() {
        expect(this.record.getVariableField("249")).andReturn(this.field);
        List<Subfield> subfields = new ArrayList<Subfield>();
        for (int i = 0; i < 5; i++) {
            subfields.add(this.subfield);
        }
        expect(this.field.getSubfields()).andReturn(subfields);
        expect(this.subfield.getCode()).andReturn('a');
        expect(this.subfield.getData()).andReturn("The a");
        expect(this.subfield.getCode()).andReturn('b');
        expect(this.subfield.getData()).andReturn("b");
        expect(this.subfield.getCode()).andReturn('n');
        expect(this.subfield.getData()).andReturn("n");
        expect(this.subfield.getCode()).andReturn('p');
        expect(this.subfield.getData()).andReturn("p");
        expect(this.subfield.getCode()).andReturn('q');
        replay(this.record, this.field, this.subfield);
        assertEquals("The a b n p", this.eresource.getTitle());
        verify(this.record, this.field, this.subfield);
    }
}
