package edu.stanford.irt.eresources.marc.sul;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.stanford.irt.eresources.Version;
import edu.stanford.lane.catalog.Record.Field;
import edu.stanford.lane.catalog.Record.Subfield;

public class SulMarcLinkTest {

    private Field field;

    private SulMarcLink link;

    private Subfield subfield;

    private Version version;

    @BeforeEach
    public void setUp() {
        this.version = mock(SulMarcVersion.class);
        this.field = mock(Field.class);
        this.link = new SulMarcLink(this.field, this.version);
        this.subfield = mock(Subfield.class);
    }

    @Test
    public void testGetLabelNull() {
        expect(this.field.getSubfields()).andReturn(Collections.emptyList()).times(2);
        replay(this.field, this.subfield);
        assertTrue(this.link.getLabel().isEmpty());
    }

    @Test
    public void testGetLabelZ() {
        List<Subfield> subfieldZs = new ArrayList<>();
        Subfield z1 = mock(Subfield.class);
        Subfield z2 = mock(Subfield.class);
        subfieldZs.add(z1);
        subfieldZs.add(z2);
        expect(this.field.getSubfields()).andReturn(subfieldZs);
        expect(z1.getCode()).andReturn('z').times(2);
        expect(z1.getData()).andReturn("Available to Stanford-affiliated users");
        expect(z2.getCode()).andReturn('z').times(2);
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
