package edu.stanford.irt.eresources.marc;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.stanford.irt.eresources.Version;
import edu.stanford.lane.catalog.Record.Field;
import edu.stanford.lane.catalog.Record.Subfield;

public class SulMarcLinkTest {

    private Field field;

    private SulMarcLink link;

    private Subfield subfield;

    private Version version;

    @Before
    public void setUp() {
        this.version = mock(SulMarcVersion.class);
        this.field = mock(Field.class);
        this.link = new SulMarcLink(this.field, this.version);
        this.subfield = mock(Subfield.class);
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
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('a');
        List<Subfield> subfieldZs = new ArrayList<>();
        Subfield z1 = mock(Subfield.class);
        Subfield z2 = mock(Subfield.class);
        subfieldZs.add(z1);
        subfieldZs.add(z2);
        expect(this.field.getSubfields()).andReturn(subfieldZs);
        expect(z1.getCode()).andReturn('z');
        expect(z1.getData()).andReturn("first z");
        expect(z2.getCode()).andReturn('z');
        expect(z2.getData()).andReturn("last z");
        replay(this.field, this.subfield, z1, z2);
        assertEquals("last z", this.link.getLabel());
    }

    @Test
    public void testGetLabelZSuAffiliation() {
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield)).times(2);
        expect(this.subfield.getCode()).andReturn('z').times(2);
        expect(this.subfield.getData()).andReturn("Available to Stanford-affiliated users at:");
        replay(this.field, this.subfield);
        assertEquals("Available to Stanford-affiliated users", this.link.getLabel());
    }

    @Test
    public void testGetUrl() {
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('u');
        expect(this.subfield.getData()).andReturn("foo");
        replay(this.field, this.subfield);
        assertEquals("foo", this.link.getUrl());
    }

    @Test
    public void testGetUrlSulProxy() {
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('u');
        expect(this.subfield.getData()).andReturn("https://stanford.idm.oclc.org/login?url=https://foo.com");
        replay(this.field, this.subfield);
        assertEquals("https://foo.com", this.link.getUrl());
    }
}
