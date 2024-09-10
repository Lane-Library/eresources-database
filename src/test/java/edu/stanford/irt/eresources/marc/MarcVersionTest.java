package edu.stanford.irt.eresources.marc;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.stanford.irt.eresources.Eresource;
import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.Record.Field;
import edu.stanford.lane.catalog.Record.Subfield;

public class MarcVersionTest {

    private Eresource eresource;

    private Field field;

    private HTTPLaneLocationsService locationsService;

    private Record record;

    private Subfield subfield;

    private MarcVersion version;

    @Before
    public void setUp() {
        this.record = mock(Record.class);
        this.field = mock(Field.class);
        this.subfield = mock(Subfield.class);
        this.eresource = mock(Eresource.class);
        this.locationsService = mock(HTTPLaneLocationsService.class);
        this.version = new MarcVersion(this.record, this.record, this.eresource, this.locationsService);
    }

    @Test
    public void testGetAdditionalNoZ() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("866");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('u');
        expect(this.record.getFields()).andReturn(Collections.emptyList());
        replay(this.record, this.field, this.subfield);
        assertNull(this.version.getAdditionalText());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetAdditionalText() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("866");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('z');
        expect(this.subfield.getData()).andReturn("description");
        expect(this.record.getFields()).andReturn(Collections.emptyList());
        replay(this.record, this.field, this.subfield);
        assertEquals("description", this.version.getAdditionalText());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetAdditionalTextMultiple866() {
        expect(this.record.getFields()).andReturn(Arrays.asList(new Field[] { this.field, this.field }));
        expect(this.field.getTag()).andReturn("866").times(2);
        expect(this.record.getFields()).andReturn(Collections.emptyList());
        replay(this.record, this.field);
        assertEquals("", this.version.getAdditionalText());
        verify(this.record, this.field);
    }

    @Test
    public void testGetAdditionalTextNo866() {
        expect(this.record.getFields()).andReturn(Collections.emptyList()).times(2);
        replay(this.record);
        assertNull(this.version.getAdditionalText());
        verify(this.record);
    }

    @Test
    public void testGetAdditionalTextWith931() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("866");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('z');
        expect(this.subfield.getData()).andReturn("description");
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("931");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('a');
        expect(this.subfield.getData()).andReturn("shelved under");
        replay(this.record, this.field, this.subfield);
        assertEquals("description shelved under", this.version.getAdditionalText());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetAdditionalTextWith931RelatedTitleBrowse() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("866");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('z');
        expect(this.subfield.getData()).andReturn("description");
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("931");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('a');
        expect(this.subfield.getData())
                .andReturn("Use the \"Related Title Browse\" Index to locate individual volumes of this title.");
        replay(this.record, this.field, this.subfield);
        assertEquals("description", this.version.getAdditionalText());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetCallNumber() {
        Subfield sf2 = mock(Subfield.class);
        List<Subfield> subs = new ArrayList<>();
        subs.add(this.subfield);
        subs.add(sf2);
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field)).times(2);
        expect(this.field.getTag()).andReturn("852").times(2);
        expect(this.field.getSubfields()).andReturn(subs);
        expect(this.subfield.getCode()).andReturn('h').anyTimes();
        expect(this.subfield.getData()).andReturn("cn1234").anyTimes();
        expect(sf2.getCode()).andReturn('i').anyTimes();
        expect(sf2.getData()).andReturn("end").anyTimes();
        replay(this.record, this.field, this.subfield, sf2);
        assertEquals("cn1234 end", this.version.getCallnumber());
        verify(this.record, this.field, this.subfield, sf2);
    }

    @Test
    public void testGetCallNumberNull() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("856");
        replay(this.record, this.field);
        assertEquals(null, this.version.getCallnumber());
        verify(this.record, this.field);
    }

    @Test
    public void testGetDates() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("866");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('y');
        expect(this.subfield.getData()).andReturn("dates");
        replay(this.record, this.field, this.subfield);
        assertEquals("dates", this.version.getDates());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetDatesFromBib() {
        expect(this.record.getFields()).andReturn(Collections.emptyList()).times(3);
        expect(this.eresource.getPublicationText()).andReturn("");
        expect(this.eresource.getPrimaryType()).andReturn("Book");
        replay(this.record, this.eresource, this.field, this.subfield);
        assertEquals("", this.version.getDates());
        verify(this.record, this.eresource, this.field, this.subfield);
    }

    @Test
    public void testGetHoldingsAndDates() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field)).times(2);
        expect(this.field.getTag()).andReturn("866").times(2);
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield)).times(2);
        expect(this.subfield.getCode()).andReturn('y').times(2);
        expect(this.subfield.getData()).andReturn("dates");
        replay(this.record, this.field, this.subfield);
        assertEquals("dates", this.version.getHoldingsAndDates());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetItemCount() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field)).times(3);
        expect(this.field.getTag()).andReturn("888").times(3);
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield)).times(3);
        expect(this.subfield.getCode()).andReturn('t');
        expect(this.subfield.getData()).andReturn("2");
        expect(this.subfield.getCode()).andReturn('a');
        expect(this.subfield.getData()).andReturn("1");
        expect(this.subfield.getCode()).andReturn('c');
        expect(this.subfield.getData()).andReturn("0");
        replay(this.record, this.field, this.subfield);
        int[] count = this.version.getItemCount();
        assertEquals(2, count[0]);
        assertEquals(1, count[1]);
        assertEquals(0, count[2]);
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetItemCountNull() {
        this.version = new MarcVersion(this.record, this.record, this.eresource, null);
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field)).times(3);
        expect(this.field.getTag()).andReturn("999").times(3);
        replay(this.record, this.field);
        assertEquals(3, this.version.getItemCount().length);
        verify(this.record, this.field);
    }

    @Test
    public void testGetLinks() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field)).times(2);
        expect(this.field.getTag()).andReturn("856").times(2);
        replay(this.record, this.field, this.subfield);
        assertEquals(1, this.version.getLinks().size());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetLocationName() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field)).times(2);
        expect(this.field.getTag()).andReturn("not-852");
        expect(this.field.getTag()).andReturn("852");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('b');
        expect(this.subfield.getData()).andReturn("code");
        expect(this.locationsService.getLocationName("code")).andReturn("name");
        int[] intArray = { 1, 1 };
        expect(this.eresource.getItemCount()).andReturn(intArray);
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field)).times(2);
        expect(this.field.getTag()).andReturn("not-856").times(2);
        expect(this.eresource.getRecordId()).andReturn("recordId");
        replay(this.record, this.field, this.subfield, this.locationsService, this.eresource);
        assertEquals("name", this.version.getLocationName());
        verify(this.record, this.field, this.subfield, this.locationsService, this.eresource);
    }

    @Test
    public void testGetLocationNameForBassett() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field)).times(3);
        expect(this.field.getTag()).andReturn("not-852");
        expect(this.field.getTag()).andReturn("852");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('b');
        expect(this.subfield.getData()).andReturn("code");
        expect(this.field.getTag()).andReturn("not-856");
        int[] childBibItemCount = { 0, 0 };
        expect(this.eresource.getItemCount()).andReturn(childBibItemCount);
        expect(this.eresource.getRecordId()).andReturn("recordId").times(2);
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field)).times(3);
        expect(this.field.getTag()).andReturn("773").times(3);
        Subfield sf = mock(Subfield.class);
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(sf));
        expect(sf.getCode()).andReturn('w');
        expect(sf.getData()).andReturn("L254573");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(sf));
        expect(sf.getCode()).andReturn('w');
        expect(sf.getData()).andReturn("L254573");
        replay(this.record, this.field, this.eresource, sf);
        assertNull(this.version.getLocationUrl());
        verify(this.record, this.field, this.eresource, sf);
    }

    @Test
    public void testGetLocationNameForComponent() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field)).times(3);
        expect(this.field.getTag()).andReturn("not-852");
        expect(this.field.getTag()).andReturn("852");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('b');
        expect(this.subfield.getData()).andReturn("code");
        expect(this.field.getTag()).andReturn("not-856");
        int[] childBibItemCount = { 0, 0 };
        expect(this.eresource.getItemCount()).andReturn(childBibItemCount);
        expect(this.eresource.getRecordId()).andReturn("recordId");
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field)).times(7);
        expect(this.field.getTag()).andReturn("773").times(7);
        expect(this.eresource.getPublicationText()).andReturn("eresource publicationText");
        Subfield sf = mock(Subfield.class);
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(sf));
        expect(sf.getCode()).andReturn('w');
        expect(sf.getData()).andReturn("random data");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(sf));
        expect(sf.getCode()).andReturn('w');
        expect(sf.getData()).andReturn("not-bassett-L254573");
        expect(this.eresource.getRecordId()).andReturn("recordId");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(sf));
        expect(sf.getCode()).andReturn('w');
        expect(sf.getData()).andReturn("L123");
        replay(this.record, this.field, this.eresource, sf);
        assertEquals("eresource publicationText", this.version.getLocationName());
        assertEquals("/view/bib/123", this.version.getLocationUrl());
        verify(this.record, this.field, this.eresource, sf);
    }

    @Test
    public void testGetLocationNameNull() {
        this.version = new MarcVersion(this.record, this.record, this.eresource, null);
        assertEquals(null, this.version.getLocationName());
    }

    @Test
    public void testGetLocationUrl() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("852");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield)).times(3);
        expect(this.subfield.getCode()).andReturn('9');
        expect(this.subfield.getData()).andReturn("code");
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field)).times(2);
        expect(this.field.getTag()).andReturn("856").times(2);
        expect(this.subfield.getCode()).andReturn('u').times(2);
        expect(this.subfield.getData()).andReturn("https://test.com").times(2);
        expect(this.locationsService.getLocationUrl("code")).andReturn("url");
        int[] intArray = { 0, 0 };
        expect(this.eresource.getItemCount()).andReturn(intArray);
        replay(this.record, this.field, this.subfield, this.locationsService, this.eresource);
        assertEquals("url", this.version.getLocationUrl());
        verify(this.record, this.field, this.subfield, this.locationsService, this.eresource);
    }

    @Test
    public void testGetLocationUrlForComponent() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field)).times(2);
        expect(this.field.getTag()).andReturn("not-852");
        expect(this.field.getTag()).andReturn("852");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('b');
        expect(this.subfield.getData()).andReturn("code");
        int[] intArray = { 0, 0 };
        expect(this.eresource.getItemCount()).andReturn(intArray);
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field)).times(2);
        expect(this.field.getTag()).andReturn("not-856").times(2);
        expect(this.eresource.getRecordId()).andReturn("recordId").times(2);
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field)).times(7);
        expect(this.field.getTag()).andReturn("830").times(7);
        Subfield sf = mock(Subfield.class);
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(sf));
        expect(sf.getCode()).andReturn('w');
        expect(sf.getData()).andReturn("L123");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(sf));
        expect(sf.getCode()).andReturn('w');
        expect(sf.getData()).andReturn("L123");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(sf));
        expect(sf.getCode()).andReturn('a');
        expect(sf.getData()).andReturn("label from 830 ^a");
        replay(this.record, this.field, this.eresource, sf);
        assertEquals("/view/bib/123", this.version.getLocationUrl());
        assertEquals("label from 830 ^a", this.version.getLocationName());
        verify(this.record, this.field, this.eresource, sf);
    }

    @Test
    public void testGetLocationUrlForRelated() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field)).times(11);
        expect(this.field.getTag()).andReturn("not-852");
        expect(this.field.getTag()).andReturn("852");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('b');
        expect(this.subfield.getData()).andReturn("code");
        int[] intArray = { 0, 0 };
        expect(this.eresource.getItemCount()).andReturn(intArray);
        // getFields() 2
        expect(this.field.getTag()).andReturn("not-856").times(2);
        expect(this.eresource.getRecordId()).andReturn("recordId").times(2);
        // getFields() 6
        expect(this.field.getTag()).andReturn("787").times(7);
        Subfield sf = mock(Subfield.class);
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(sf));
        expect(sf.getCode()).andReturn('w');
        expect(sf.getData()).andReturn("L123");
        Subfield sf2 = mock(Subfield.class);
        List<Subfield> subs = new ArrayList<>();
        subs.add(sf);
        subs.add(sf2);
        expect(this.field.getSubfields()).andReturn(subs);
        expect(sf2.getCode()).andReturn('w');
        expect(sf2.getData()).andReturn("L999");
        expect(sf.getCode()).andReturn('w');
        expect(sf.getData()).andReturn("L123");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(sf));
        expect(sf.getCode()).andReturn('e');
        expect(sf.getData()).andReturn("label from 787 ^e");
        // expect(this.field.getTag()).andReturn("773");
        replay(this.record, this.field, this.locationsService, this.eresource, sf, sf2);
        assertEquals("/view/bib/999", this.version.getLocationUrl());
        assertEquals("label from 787 ^e", this.version.getLocationName());
        verify(this.record, this.field, this.locationsService, this.eresource, sf, sf2);
    }

    @Test
    public void testGetLocationUrlNull() {
        this.version = new MarcVersion(this.record, this.record, this.eresource, null);
        assertEquals(null, this.version.getLocationUrl());
    }

    @Test
    public void testGetPublisher() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field)).times(3);
        expect(this.field.getTag()).andReturn("856").times(3);
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield)).times(2);
        expect(this.subfield.getCode()).andReturn('y');
        expect(this.subfield.getData()).andReturn("publisher");
        expect(this.subfield.getCode()).andReturn('q');
        expect(this.subfield.getData()).andReturn("not publisher");
        replay(this.record, this.field, this.subfield);
        assertEquals("publisher", this.version.getPublisher());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetSummaryHoldings() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field)).times(4);
        expect(this.field.getTag()).andReturn("866").times(4);
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield)).times(4);
        expect(this.subfield.getCode()).andReturn('v').times(4);
        expect(this.subfield.getData()).andReturn("summaryHoldings").times(3);
        replay(this.record, this.field, this.subfield);
        assertEquals("summaryHoldings", this.version.getSummaryHoldings());
        assertEquals("summaryHoldings", this.version.getHoldingsAndDates());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testIsProxy() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.field.getTag()).andReturn("655");
        expect(this.subfield.getCode()).andReturn('a');
        expect(this.subfield.getData()).andReturn("subSet, Noproxy");
        replay(this.record, this.field, this.subfield);
        assertFalse(this.version.isProxy());
        verify(this.record, this.field, this.subfield);
    }
}
