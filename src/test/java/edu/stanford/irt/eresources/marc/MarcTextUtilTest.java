package edu.stanford.irt.eresources.marc;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import edu.stanford.lane.catalog.Record.Field;
import edu.stanford.lane.catalog.Record.Subfield;

public class MarcTextUtilTest {

    private Field field;

    private Subfield subfield;

    @Before
    public void setUp() {
        this.field = mock(Field.class);
        this.subfield = mock(Subfield.class);
    }

    @Test
    public void testGetSubfieldData() {
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('a');
        expect(this.subfield.getData()).andReturn("data");
        replay(this.field, this.subfield);
        assertEquals("data", MarcTextUtil.getSubfieldData(this.field, 'a'));
        verify(this.field, this.subfield);
    }
}
