package edu.stanford.irt.eresources.marc;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.stanford.irt.eresources.Version;
import edu.stanford.lane.catalog.Record.Field;
import edu.stanford.lane.catalog.Record.Subfield;

class MarcLinkTest {

    private Field field;

    private MarcLink link;

    private Subfield subfield;

    private Version version;

    @BeforeEach
    void setUp() {
        this.version = mock(Version.class);
        this.field = mock(Field.class);
        this.link = new MarcLink(this.field, this.version);
        this.subfield = mock(Subfield.class);
    }

    @Test
    void testGetAdditionalText() {
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('i');
        expect(this.subfield.getData()).andReturn("additional text");
        replay(this.field, this.subfield);
        assertEquals("additional text", this.link.getAdditionalText());
    }

    @Test
    void testGetAdditionalTextNull() {
        expect(this.field.getSubfields()).andReturn(Collections.emptyList());
        replay(this.field, this.subfield, this.version);
        assertNull(this.link.getAdditionalText());
    }

    @Test
    void testGetLabelEmptyParens() {
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('q');
        expect(this.subfield.getData()).andReturn("()");
        replay(this.field, this.subfield);
        assertEquals("()", this.link.getLabel());
    }

    @Test
    void testGetLabelNull() {
        expect(this.field.getSubfields()).andReturn(Collections.emptyList()).times(2);
        replay(this.field, this.subfield);
        assertNull(this.link.getLabel());
    }

    @Test
    void testGetLabelOpenParens() {
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('q');
        expect(this.subfield.getData()).andReturn("(label");
        replay(this.field, this.subfield);
        assertEquals("(label", this.link.getLabel());
    }

    @Test
    void testGetLabelParens() {
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('q');
        expect(this.subfield.getData()).andReturn("(label)");
        replay(this.field, this.subfield);
        assertEquals("label", this.link.getLabel());
    }

    @Test
    void testGetLabelQ() {
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('q');
        expect(this.subfield.getData()).andReturn("q label");
        replay(this.field, this.subfield);
        assertEquals("q label", this.link.getLabel());
    }

    @Test
    void testGetLabelSuAffiliation() {
        Subfield sf1 = mock(Subfield.class);
        Subfield sf2 = mock(Subfield.class);
        List<Subfield> sfs = Arrays.asList(sf1, sf2);
        expect(this.field.getSubfields()).andReturn(sfs).times(2);
        expect(sf1.getCode()).andReturn('z').times(2);
        expect(sf2.getCode()).andReturn('z').times(2);
        expect(sf1.getData()).andReturn("Available to Stanford-affiliated users.");
        expect(sf2.getData()).andReturn("z label");
        replay(this.field, this.subfield, sf1, sf2);
        assertEquals("z label", this.link.getLabel());
        verify(this.field, this.subfield, sf1, sf2);
    }

    @Test
    void testGetLabelZ() {
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield)).times(2);
        expect(this.subfield.getCode()).andReturn('z').times(2);
        expect(this.subfield.getData()).andReturn("z label");
        replay(this.field, this.subfield);
        assertEquals("z label", this.link.getLabel());
    }

    @Test
    void testGetLinkTextHoldingsAndDates() {
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
    void testGetLinkTextHoldingsAndDatesNoLinks() {
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
    void testGetLinkTextHoldingsAndDatesNullLinks() {
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
    void testGetLinkTextNoHoldingsAndDates() {
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
    void testGetLinkTextNoHoldingsAndDatesNoLabel() {
        expect(this.field.getSubfields()).andReturn(Collections.emptyList()).times(2);
        expect(this.version.getHoldingsAndDates()).andReturn(null);
        expect(this.version.getLinks()).andReturn(null);
        replay(this.version, this.field, this.subfield);
        assertEquals("", this.link.getLinkText());
        verify(this.version);
    }

    @Test
    void testGetUrl() {
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('u');
        expect(this.subfield.getData()).andReturn("url");
        replay(this.field, this.subfield);
        assertEquals("url", this.link.getUrl());
    }
}
