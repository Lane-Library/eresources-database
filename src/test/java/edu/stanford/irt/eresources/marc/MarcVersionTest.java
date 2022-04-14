package edu.stanford.irt.eresources.marc;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.stanford.irt.eresources.Eresource;
import edu.stanford.irt.eresources.ItemCount;
import edu.stanford.irt.eresources.ItemService;
import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.Record.Field;
import edu.stanford.lane.catalog.Record.Subfield;

public class MarcVersionTest {

    private Eresource eresource;

    private Field field;

    private ItemService itemService;

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
        this.itemService = mock(ItemService.class);
        this.locationsService = mock(HTTPLaneLocationsService.class);
        this.version = new MarcVersion(this.record, this.record, this.eresource, this.itemService,
                this.locationsService);
    }

    @Test
    public void testGetAdditionalNoZ() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("866");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('u');
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
        replay(this.record, this.field, this.subfield);
        assertEquals("description", this.version.getAdditionalText());
        verify(this.record, this.field, this.subfield);
    }

    @Test
    public void testGetAdditionalTextMultiple866() {
        expect(this.record.getFields()).andReturn(Arrays.asList(new Field[] { this.field, this.field }));
        expect(this.field.getTag()).andReturn("866").times(2);
        replay(this.record, this.field);
        assertEquals("", this.version.getAdditionalText());
        verify(this.record, this.field);
    }

    @Test
    public void testGetAdditionalTextNo866() {
        expect(this.record.getFields()).andReturn(Collections.emptyList());
        replay(this.record);
        assertNull(this.version.getAdditionalText());
        verify(this.record);
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
        expect(this.record.getFields()).andReturn(Collections.emptyList()).times(7);
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
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("001");
        expect(this.field.getData()).andReturn("123");
        ItemCount itemCount = mock(ItemCount.class);
        expect(this.itemService.getHoldingsItemCount()).andReturn(itemCount);
        int[] intArray = { 2, 1 };
        expect(itemCount.itemCount(123)).andReturn(intArray);
        replay(this.record, this.field, this.subfield, this.itemService, itemCount);
        assertEquals(intArray, this.version.getItemCount());
        verify(this.record, this.field, this.subfield, this.itemService, itemCount);
    }

    @Test
    public void testGetItemCountNull() {
        this.version = new MarcVersion(this.record, this.record, this.eresource, null, null);
        assertEquals(0, this.version.getItemCount().length);
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
        expect(this.field.getTag()).andReturn("001");
        expect(this.field.getData()).andReturn("001");
        expect(this.field.getTag()).andReturn("852");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('b');
        expect(this.subfield.getData()).andReturn("code");
        expect(this.locationsService.getTemporaryHoldingLocations()).andReturn(Collections.emptyMap());
        expect(this.locationsService.getLocationName("code")).andReturn("name");
        int[] intArray = { 1, 1 };
        expect(this.eresource.getItemCount()).andReturn(intArray);
        replay(this.record, this.field, this.subfield, this.locationsService, this.eresource);
        assertEquals("name", this.version.getLocationName());
        verify(this.record, this.field, this.subfield, this.locationsService, this.eresource);
    }

    @Test
    public void testGetLocationNameForComponent() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("001");
        expect(this.field.getData()).andReturn("001");
        expect(this.locationsService.getTemporaryHoldingLocations()).andReturn(Collections.singletonMap(1, "code"))
                .times(2);
        int[] childBibItemCount = { 0, 0 };
        expect(this.eresource.getItemCount()).andReturn(childBibItemCount);
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("999");
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field)).times(5);
        expect(this.field.getTag()).andReturn("773").times(5);
        expect(this.eresource.getPublicationText()).andReturn("eresource publicationText");
        Subfield sf = mock(Subfield.class);
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(sf));
        expect(sf.getCode()).andReturn('w');
        expect(sf.getData()).andReturn("L123");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(sf));
        expect(sf.getCode()).andReturn('w');
        expect(sf.getData()).andReturn("L123");
        ItemCount itemCount = mock(ItemCount.class);
        expect(this.itemService.getBibsItemCount()).andReturn(itemCount);
        int[] parentBibItemCount = { 1, 0 };
        expect(itemCount.itemCount(123)).andReturn(parentBibItemCount);
        replay(this.record, this.field, this.locationsService, this.eresource, sf, this.itemService, itemCount);
        assertEquals("eresource publicationText", this.version.getLocationName());
        assertEquals("/view/bib/123", this.version.getLocationUrl());
        verify(this.record, this.field, this.locationsService, this.eresource, sf, this.itemService, itemCount);
    }

    @Test
    public void testGetLocationNameNull() {
        this.version = new MarcVersion(this.record, this.record, this.eresource, this.itemService, null);
        assertEquals(null, this.version.getLocationName());
    }

    @Test
    public void testGetLocationUrl() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("001");
        expect(this.field.getData()).andReturn("001");
        expect(this.locationsService.getTemporaryHoldingLocations()).andReturn(Collections.singletonMap(1, "code"))
                .times(2);
        expect(this.locationsService.getLocationUrl("code")).andReturn("url");
        int[] intArray = { 0, 0 };
        expect(this.eresource.getItemCount()).andReturn(intArray);
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("856");
        Subfield sf = mock(Subfield.class);
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(sf));
        expect(sf.getCode()).andReturn('u');
        expect(sf.getData()).andReturn("foo");
        replay(this.record, this.field, this.locationsService, this.eresource, sf);
        assertEquals("url", this.version.getLocationUrl());
        verify(this.record, this.field, this.locationsService, this.eresource, sf);
    }

    @Test
    public void testGetLocationUrlForComponent() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("001");
        expect(this.field.getData()).andReturn("001");
        expect(this.locationsService.getTemporaryHoldingLocations()).andReturn(Collections.singletonMap(1, "code"))
                .times(2);
        int[] intArray = { 0, 0 };
        expect(this.eresource.getItemCount()).andReturn(intArray);
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("999");
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field)).times(6);
        expect(this.field.getTag()).andReturn("830").times(6);
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
        ItemCount itemCount = mock(ItemCount.class);
        expect(this.itemService.getBibsItemCount()).andReturn(itemCount);
        int[] parentBibItemCount = { 1, 0 };
        expect(itemCount.itemCount(123)).andReturn(parentBibItemCount);
        replay(this.record, this.field, this.locationsService, this.eresource, sf, this.itemService, itemCount);
        assertEquals("/view/bib/123", this.version.getLocationUrl());
        assertEquals("label from 830 ^a", this.version.getLocationName());
        verify(this.record, this.field, this.locationsService, this.eresource, sf, this.itemService, itemCount);
    }

    @Test
    public void testGetLocationUrlForRelated() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("001");
        expect(this.field.getData()).andReturn("001");
        expect(this.locationsService.getTemporaryHoldingLocations()).andReturn(Collections.singletonMap(1, "code"))
                .times(2);
        int[] intArray = { 0, 0 };
        expect(this.eresource.getItemCount()).andReturn(intArray);
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("999");
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field)).times(6);
        expect(this.field.getTag()).andReturn("787").times(6);
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
        ItemCount itemCount = mock(ItemCount.class);
        expect(this.itemService.getBibsItemCount()).andReturn(itemCount).times(2);
        int[] parentBibItemCount = { 0, 0 };
        expect(itemCount.itemCount(123)).andReturn(parentBibItemCount);
        expect(itemCount.itemCount(999)).andReturn(parentBibItemCount);
        replay(this.record, this.field, this.locationsService, this.eresource, sf, sf2, this.itemService, itemCount);
        assertEquals("/view/bib/999", this.version.getLocationUrl());
        assertEquals("label from 787 ^e", this.version.getLocationName());
        verify(this.record, this.field, this.locationsService, this.eresource, sf, sf2, this.itemService, itemCount);
    }

    @Test
    public void testGetLocationUrlNull() {
        this.version = new MarcVersion(this.record, this.record, this.eresource, this.itemService, null);
        assertEquals(null, this.version.getLocationUrl());
    }

    @Test
    public void testGetPublisher() {
        expect(this.record.getFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("844");
        expect(this.field.getSubfields()).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getCode()).andReturn('a');
        expect(this.subfield.getData()).andReturn("publisher");
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
