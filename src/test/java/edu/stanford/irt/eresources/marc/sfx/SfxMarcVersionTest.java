package edu.stanford.irt.eresources.marc.sfx;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.stanford.irt.eresources.Eresource;
import edu.stanford.lane.catalog.Record;

class SfxMarcVersionTest {

    SfxMarcVersion sfxMarcVersion;

    Record bibRecord;

    Eresource eresource;

    @BeforeEach
    void setUp() {
        this.bibRecord = mock(Record.class);
        this.eresource = mock(Eresource.class);
        this.sfxMarcVersion = new SfxMarcVersion(this.bibRecord, this.eresource);
    }

    @Test
    void testGetAdditionalText() {
        assertNull(this.sfxMarcVersion.getAdditionalText());
    }

    @Test
    void testGetDates() {
        expect(this.eresource.getYear()).andReturn(2027);
        replay(this.eresource);
        assertEquals("2027", this.sfxMarcVersion.getDates());
        verify(this.eresource);
    }

    @Test
    void testGetDatesNull() {
        expect(this.eresource.getYear()).andReturn(0);
        replay(this.eresource);
        assertNull(this.sfxMarcVersion.getDates());
        verify(this.eresource);
    }
}
