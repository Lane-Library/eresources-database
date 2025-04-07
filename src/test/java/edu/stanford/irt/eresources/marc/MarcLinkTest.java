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
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import edu.stanford.irt.eresources.Link;
import edu.stanford.irt.eresources.Version;
import edu.stanford.lane.catalog.Record.Field;
import edu.stanford.lane.catalog.Record.Subfield;

class MarcLinkTest {

    private static Stream<Arguments> provideLabelTestCases() {
        return Stream.of(
                Arguments.of(Collections.singletonList(mock(Subfield.class)), 'q', "()", "()"),
                Arguments.of(Collections.singletonList(mock(Subfield.class)), 'q', "(label", "(label"),
                Arguments.of(Collections.singletonList(mock(Subfield.class)), 'q', "(label)", "label"),
                Arguments.of(Collections.singletonList(mock(Subfield.class)), 'q', "q label", "q label"));
    }

    private static Stream<Arguments> provideLinkTextTestCases() {
        return Stream.of(
                Arguments.of(Collections.singletonList(mock(Subfield.class)), 'q', "label", "holdings and dates",
                        Collections.singletonList(mock(MarcLink.class)), "holdings and dates"),
                Arguments.of(Collections.singletonList(mock(Subfield.class)), 'q', "label", "holdings and dates",
                        Collections.emptyList(), "label"),
                Arguments.of(Collections.singletonList(mock(Subfield.class)), 'q', "label", "holdings and dates", null,
                        "label"),
                Arguments.of(Collections.singletonList(mock(Subfield.class)), 'q', "label", null, null, "label"));
    }

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

    @ParameterizedTest
    @MethodSource("provideLabelTestCases")
    void testGetLabel(final List<Subfield> subfields, final char subfieldCode, final String subfieldData,
            final String expectedLabel) {
        expect(this.field.getSubfields()).andReturn(subfields);
        Subfield subfield = subfields.get(0);
        expect(subfield.getCode()).andReturn(subfieldCode);
        expect(subfield.getData()).andReturn(subfieldData);
        replay(this.field, subfield);
        assertEquals(expectedLabel, this.link.getLabel());
        verify(this.field, subfield);
    }

    @Test
    void testGetLabelNull() {
        expect(this.field.getSubfields()).andReturn(Collections.emptyList()).times(2);
        replay(this.field, this.subfield);
        assertNull(this.link.getLabel());
    }

    @Test
    void testGetLabelSuAffiliation() {
        Subfield sf1 = mock(Subfield.class);
        Subfield sf2 = mock(Subfield.class);
        List<Subfield> sfs = Arrays.asList(sf1, sf2);
        expect(this.field.getSubfields()).andReturn(sfs).times(2);
        expect(sf1.getCode()).andReturn('z').times(3);
        expect(sf2.getCode()).andReturn('z').times(3);
        expect(sf1.getData()).andReturn("Available to Stanford-affiliated users.");
        expect(sf2.getData()).andReturn("z label");
        replay(this.field, this.subfield, sf1, sf2);
        assertEquals("z label", this.link.getLabel());
        verify(this.field, this.subfield, sf1, sf2);
    }

    @Test
    void testGetLabelZ() {
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield)).times(2);
        expect(this.subfield.getCode()).andReturn('z').times(3);
        expect(this.subfield.getData()).andReturn("z label");
        replay(this.field, this.subfield);
        assertEquals("z label", this.link.getLabel());
    }

    @ParameterizedTest
    @MethodSource("provideLinkTextTestCases")
    void testGetLinkText(final List<Subfield> subfields, final char subfieldCode, final String subfieldData,
            final String holdingsAndDates, final List<Link> links, final String expectedLinkText) {
        expect(this.field.getSubfields()).andReturn(subfields);
        Subfield subfield = subfields.get(0);
        expect(subfield.getCode()).andReturn(subfieldCode);
        expect(subfield.getData()).andReturn(subfieldData);
        expect(this.version.getHoldingsAndDates()).andReturn(holdingsAndDates);
        expect(this.version.getLinks()).andReturn(links);
        replay(this.version, this.field, subfield);
        assertEquals(expectedLinkText, this.link.getLinkText());
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
