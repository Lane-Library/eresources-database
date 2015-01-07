package edu.stanford.irt.eresources.marc;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Subfield;

public class MarcTextUtilTest {

    private DataField field;

    private Subfield subfield;

    @Before
    public void setUp() {
        this.field = createMock(DataField.class);
        this.subfield = createMock(Subfield.class);
    }

    @Test
    public void testGetSubfieldData() {
        expect(this.field.getSubfield('a')).andReturn(this.subfield);
        expect(this.subfield.getData()).andReturn("data");
        replay(this.field, this.subfield);
        assertEquals("data", MarcTextUtil.getSubfieldData(this.field, 'a'));
        verify(this.field, this.subfield);
    }
}
