package edu.stanford.irt.eresources.marc.sul;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.stanford.irt.eresources.Eresource;
import edu.stanford.irt.eresources.Link;
import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.Record.Field;
import edu.stanford.lane.catalog.Record.Subfield;

class SulMarcVersionTest {

    private Eresource eresource;

    private Field field;

    private Record rec;

    private Subfield subfield;

    private SulMarcVersion version;

    @BeforeEach
    void setUp() {
        this.rec = mock(Record.class);
        this.field = mock(Field.class);
        this.subfield = mock(Subfield.class);
        this.eresource = mock(Eresource.class);
        this.version = new SulMarcVersion(this.rec, this.eresource);
    }

    @Test
    void testGetAdditionalNoZ() {
        expect(this.rec.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("866");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('u');
        replay(this.rec, this.field, this.subfield);
        assertNull(this.version.getAdditionalText());
        verify(this.rec, this.field, this.subfield);
    }

    @Test
    void testGetAdditionalText() {
        expect(this.rec.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("866");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('z');
        expect(this.subfield.getData()).andReturn("description");
        replay(this.rec, this.field, this.subfield);
        assertEquals("description", this.version.getAdditionalText());
        verify(this.rec, this.field, this.subfield);
    }

    @Test
    void testGetAdditionalTextMultiple866() {
        expect(this.rec.getFields()).andReturn(Arrays.asList(new Field[] { this.field, this.field }));
        expect(this.field.getTag()).andReturn("866").times(2);
        replay(this.rec, this.field);
        assertEquals("", this.version.getAdditionalText());
        verify(this.rec, this.field);
    }

    @Test
    void testGetAdditionalTextNo866() {
        expect(this.rec.getFields()).andReturn(Collections.emptyList());
        replay(this.rec);
        assertNull(this.version.getAdditionalText());
        verify(this.rec);
    }

    @Test
    void testGetDates() {
        expect(this.eresource.getPublicationText()).andReturn("");
        expect(this.eresource.getPrimaryType()).andReturn("Book");
        expect(this.rec.getFields()).andReturn(Collections.singletonList(this.field)).times(2);
        expect(this.field.getTag()).andReturn("999");
        expect(this.field.getTag()).andReturn("260");
        expect(this.eresource.getYear()).andReturn(9999);
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('c');
        expect(this.subfield.getData()).andReturn("dates].");
        replay(this.rec, this.eresource, this.field, this.subfield);
        assertEquals("dates", this.version.getDates());
        verify(this.rec, this.eresource, this.field, this.subfield);
    }

    @Test
    void testGetDatesJournal() {
        expect(this.eresource.getPublicationText()).andReturn("");
        expect(this.eresource.getPrimaryType()).andReturn("Journal");
        expect(this.rec.getFields()).andReturn(Collections.singletonList(this.field)).times(2);
        expect(this.field.getTag()).andReturn("999");
        expect(this.field.getTag()).andReturn("008");
        expect(this.field.getData()).andReturn("010105c20009999pauqr p 0 a0eng d");
        replay(this.rec, this.eresource, this.field);
        assertEquals("2000-", this.version.getDates());
        verify(this.rec, this.eresource, this.field);
    }

    @Test
    void testGetLinks() {
        expect(this.rec.getFields()).andReturn(Collections.singletonList(this.field)).times(2);
        expect(this.field.getTag()).andReturn("856").times(2);
        replay(this.rec, this.field, this.subfield);
        assertEquals(1, this.version.getLinks().size());
        verify(this.rec, this.field, this.subfield);
    }

    @Test
    void testGetLinksSW() {
        expect(this.rec.getFields()).andReturn(Collections.singletonList(this.field)).times(2);
        expect(this.field.getTag()).andReturn("999").times(2);
        replay(this.rec, this.field, this.subfield);
        List<Link> links = this.version.getLinks();
        assertEquals(1, links.size());
        assertEquals("SU Catalog (SearchWorks)", links.get(0).getLabel());
        verify(this.rec, this.field, this.subfield);
    }

    @Test
    void testGetPublisher() {
        expect(this.rec.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("844");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('a');
        expect(this.subfield.getData()).andReturn("publisher");
        replay(this.rec, this.field, this.subfield);
        assertEquals("publisher", this.version.getPublisher());
        verify(this.rec, this.field, this.subfield);
    }

    @Test
    void testGetSummaryHoldings() {
        expect(this.rec.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("362");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('a');
        expect(this.subfield.getData()).andReturn("summaryHoldings");
        replay(this.rec, this.field, this.subfield);
        assertEquals("summaryHoldings", this.version.getSummaryHoldings());
        verify(this.rec, this.field, this.subfield);
    }

    @Test
    void testIsProxy() {
        assertFalse(this.version.isProxy());
        this.version.setIsProxy(true);
        assertTrue(this.version.isProxy());
    }
}
