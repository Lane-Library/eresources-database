package edu.stanford.irt.eresources.marc.sfx;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import edu.stanford.irt.eresources.Eresource;
import edu.stanford.lane.catalog.Record;

public class SfxMarcVersionTest {

    SfxMarcVersion sfxMarcVersion;

    Record bibRecord;

    Eresource eresource;

    @Before
    public void setUp() {
        this.bibRecord = mock(Record.class);
        this.eresource = mock(Eresource.class);
        this.sfxMarcVersion = new SfxMarcVersion(this.bibRecord, this.eresource);
    }

    @Test
    public void testGetAdditionalText() {
        assertNull(this.sfxMarcVersion.getAdditionalText());
    }

    @Test
    public void testGetDates() {
        expect(this.eresource.getYear()).andReturn(2027);
        replay(this.eresource);
        assertEquals("2027", this.sfxMarcVersion.getDates());
        verify(this.eresource);
    }

    @Test
    public void testGetDatesNull() {
        expect(this.eresource.getYear()).andReturn(0);
        replay(this.eresource);
        assertNull(this.sfxMarcVersion.getDates());
        verify(this.eresource);
    }
}
