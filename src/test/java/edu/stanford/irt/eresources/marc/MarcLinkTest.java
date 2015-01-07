package edu.stanford.irt.eresources.marc;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Subfield;

import edu.stanford.irt.eresources.Link;
import edu.stanford.irt.eresources.Version;

public class MarcLinkTest {

    private DataField field;

    private MarcLink link;

    private Subfield subfield;

    private Version version;

    @Before
    public void setUp() {
        this.field = createMock(DataField.class);
        this.version = createMock(Version.class);
        this.link = new MarcLink(this.field, this.version);
        this.subfield = createMock(Subfield.class);
    }

    @Test
    public void testGetAdditionalText() {
        expect(this.field.getSubfields('i')).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getData()).andReturn("subfieldI");
        expect(this.version.getPublisher()).andReturn("publisher");
        replay(this.field, this.version, this.subfield);
        assertEquals(" subfieldI publisher", this.link.getAdditionalText());
        verify(this.field, this.version, this.subfield);
    }

    @Test
    public void testGetInstruction() {
        expect(this.field.getSubfields('i')).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getData()).andReturn("subfieldI");
        replay(this.field, this.version, this.subfield);
        assertEquals("subfieldI", this.link.getInstruction());
        verify(this.field, this.version, this.subfield);
    }

    @Test
    public void testGetLabel() {
        expect(this.field.getSubfield('q')).andReturn(this.subfield);
        expect(this.subfield.getData()).andReturn("subfieldQ");
        replay(this.field, this.version, this.subfield);
        assertEquals("subfieldQ", this.link.getLabel());
        verify(this.field, this.version, this.subfield);
    }

    @Test
    public void testGetLinkText() {
        expect(this.field.getSubfield('q')).andReturn(this.subfield);
        expect(this.subfield.getData()).andReturn("subfieldQ");
        expect(this.version.getSummaryHoldings()).andReturn("summaryHoldings");
        expect(this.version.getLinks()).andReturn(Collections.<Link> singletonList(this.link));
        expect(this.version.getDates()).andReturn("dates");
        expect(this.version.getDescription()).andReturn("description");
        replay(this.field, this.version, this.subfield);
        assertEquals("summaryHoldings, dates description", this.link.getLinkText());
        verify(this.field, this.version, this.subfield);
    }

    @Test
    public void testGetUrl() {
        expect(this.field.getSubfield('u')).andReturn(this.subfield);
        expect(this.subfield.getData()).andReturn("subfieldU");
        replay(this.field, this.version, this.subfield);
        assertEquals("subfieldU", this.link.getUrl());
        verify(this.field, this.version, this.subfield);
    }
}
