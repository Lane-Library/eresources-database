package edu.stanford.irt.eresources.marc;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import edu.stanford.irt.eresources.Version;
import edu.stanford.lane.catalog.Record.Field;
import edu.stanford.lane.catalog.Record.Subfield;

public class MarcLinkTest {

    private Field field;

    private MarcLink link;

    private Subfield subfield;

    private Version version;

    @Before
    public void setUp() {
        this.version = mock(Version.class);
        this.field = mock(Field.class);
        this.link = new MarcLink(this.field, this.version);
        this.subfield = mock(Subfield.class);
    }

    @Test
    public void testGetAdditionalText() {
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('i');
        expect(this.subfield.getData()).andReturn("additional text");
        replay(this.field, this.subfield);
        assertEquals("additional text", this.link.getAdditionalText());
    }

    @Test
    public void testGetAdditionalTextNull() {
        expect(this.field.getSubfields()).andReturn(Collections.emptyList());
        replay(this.field, this.subfield, this.version);
        assertNull(this.link.getAdditionalText());
    }

    @Test
    public void testGetLabelEmptyParens() {
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('q');
        expect(this.subfield.getData()).andReturn("()");
        replay(this.field, this.subfield);
        assertEquals("()", this.link.getLabel());
    }

    @Test
    public void testGetLabelNull() {
        expect(this.field.getSubfields()).andReturn(Collections.emptyList()).times(2);
        replay(this.field, this.subfield);
        assertNull(this.link.getLabel());
    }

    @Test
    public void testGetLabelOpenParens() {
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('q');
        expect(this.subfield.getData()).andReturn("(label");
        replay(this.field, this.subfield);
        assertEquals("(label", this.link.getLabel());
    }

    @Test
    public void testGetLabelParens() {
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('q');
        expect(this.subfield.getData()).andReturn("(label)");
        replay(this.field, this.subfield);
        assertEquals("label", this.link.getLabel());
    }

    @Test
    public void testGetLabelQ() {
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('q');
        expect(this.subfield.getData()).andReturn("q label");
        replay(this.field, this.subfield);
        assertEquals("q label", this.link.getLabel());
    }

    @Test
    public void testGetLabelZ() {
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield)).times(2);
        expect(this.subfield.getCode()).andReturn('z').times(2);
        expect(this.subfield.getData()).andReturn("z label");
        replay(this.field, this.subfield);
        assertEquals("z label", this.link.getLabel());
    }

    @Test
    public void testGetLinkTextHoldingsAndDates() {
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('q');
        expect(this.subfield.getData()).andReturn("label");
        expect(this.version.getHoldingsAndDates()).andReturn("holdings and dates");
        expect(this.version.getLinks()).andReturn(Collections.singletonList(this.link));
        replay(this.version, this.field, this.subfield);
        assertEquals("holdings and dates", this.link.getLinkText());
        verify(this.version);
    }

    @Test
    public void testGetLinkTextHoldingsAndDatesNoLinks() {
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('q');
        expect(this.subfield.getData()).andReturn("label");
        expect(this.version.getHoldingsAndDates()).andReturn("holdings and dates");
        expect(this.version.getLinks()).andReturn(Collections.emptyList());
        replay(this.version, this.field, this.subfield);
        assertEquals("label", this.link.getLinkText());
        verify(this.version);
    }

    @Test
    public void testGetLinkTextHoldingsAndDatesNullLinks() {
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('q');
        expect(this.subfield.getData()).andReturn("label");
        expect(this.version.getHoldingsAndDates()).andReturn("holdings and dates");
        expect(this.version.getLinks()).andReturn(null);
        replay(this.version, this.field, this.subfield);
        assertEquals("label", this.link.getLinkText());
        verify(this.version);
    }

    @Test
    public void testGetLinkTextImpactFactor() {
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('q');
        expect(this.subfield.getData()).andReturn("iMpAcT fAcToR");
        replay(this.field, this.subfield);
        assertEquals("Impact Factor", this.link.getLinkText());
    }

    @Test
    public void testGetLinkTextNoHoldingsAndDates() {
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('q');
        expect(this.subfield.getData()).andReturn("label");
        expect(this.version.getHoldingsAndDates()).andReturn(null);
        expect(this.version.getLinks()).andReturn(null);
        replay(this.version, this.field, this.subfield);
        assertEquals("label", this.link.getLinkText());
        verify(this.version);
    }

    @Test
    public void testGetLinkTextNoHoldingsAndDatesNoLabel() {
        expect(this.field.getSubfields()).andReturn(Collections.emptyList()).times(2);
        expect(this.version.getHoldingsAndDates()).andReturn(null);
        expect(this.version.getLinks()).andReturn(null);
        replay(this.version, this.field, this.subfield);
        assertEquals("null", this.link.getLinkText());
        verify(this.version);
    }

    @Test
    public void testGetUrl() {
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('u');
        expect(this.subfield.getData()).andReturn("url");
        replay(this.field, this.subfield);
        assertEquals("url", this.link.getUrl());
    }
}
