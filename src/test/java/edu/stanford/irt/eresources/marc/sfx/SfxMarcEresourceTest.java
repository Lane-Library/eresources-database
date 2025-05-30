package edu.stanford.irt.eresources.marc.sfx;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.lcsh.LcshMapManager;

class SfxMarcEresourceTest {

    LcshMapManager lcshMapManager;

    Record rec;

    SfxMarcEresource sfxMarcEresource;

    @BeforeEach
    void setUp() throws Exception {
        byte[] marc = Files
                .readAllBytes(Paths.get("src/test/resources/edu/stanford/irt/eresources/marc/sfx/sfx-export.marc"));
        this.rec = new Record(marc);
        this.lcshMapManager = mock(LcshMapManager.class);
        this.sfxMarcEresource = new SfxMarcEresource(this.rec, null, this.lcshMapManager);
    }

    @Test
    void testCreateVersion() {
        assertEquals("2017", this.sfxMarcEresource.createVersion(this.rec).getHoldingsAndDates());
    }

    @Test
    void testGetMeshTerms() {
        expect(this.lcshMapManager.getMeshForHeading(isA(String.class))).andReturn(Collections.singleton("mappedMesh"))
                .atLeastOnce();
        replay(this.lcshMapManager);
        assertEquals("Law", this.sfxMarcEresource.getMeshTerms().toArray()[0]);
        assertEquals("mappedMesh", this.sfxMarcEresource.getMeshTerms().toArray()[1]);
        verify(this.lcshMapManager);
    }

    @Test
    void testGetPrimaryType() {
        assertEquals("Book Digital", this.sfxMarcEresource.getPrimaryType());
    }

    @Test
    void testGetRecordId() {
        assertEquals("110978984449203", this.sfxMarcEresource.getRecordId());
    }

    @Test
    void testGetRecordType() {
        assertEquals("sfx", this.sfxMarcEresource.getRecordType());
    }

    @Test
    void testGetTypes() {
        assertEquals("Book", this.sfxMarcEresource.getTypes().toArray()[0]);
    }
}
