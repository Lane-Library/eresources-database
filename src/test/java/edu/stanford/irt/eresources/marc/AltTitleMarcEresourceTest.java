package edu.stanford.irt.eresources.marc;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.Record.Field;
import edu.stanford.lane.catalog.Record.Subfield;

public class AltTitleMarcEresourceTest {

    private AltTitleMarcEresource eresource;

    private Field field;

    private Record record;

    private Subfield subfield;

    @Before
    public void setUp() {
        this.record = mock(Record.class);
        this.eresource = new AltTitleMarcEresource(Collections.singletonList(this.record), null, null, null, null, 1, null);
        this.field = mock(Field.class);
        this.subfield = mock(Subfield.class);
    }

    @Test
    public void testGetTitle() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("249");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('a');
        expect(this.subfield.getData()).andReturn("prefered title");
        replay(this.record, this.field, this.subfield);
        assertEquals("prefered title", this.eresource.getTitle());
        verify(this.record, this.field, this.subfield);
    }
}
