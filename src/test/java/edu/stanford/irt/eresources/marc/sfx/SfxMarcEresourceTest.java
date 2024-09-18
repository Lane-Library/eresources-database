package edu.stanford.irt.eresources.marc.sfx;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.lcsh.LcshMapManager;

public class SfxMarcEresourceTest {

    Record record;
    SfxMarcEresource sfxMarcEresource;
    LcshMapManager lcshMapManager;

    @Before
    public void setUp() throws Exception {
        byte[] marc = Files
                .readAllBytes(Paths.get("src/test/resources/edu/stanford/irt/eresources/marc/sfx/sfx-export.marc"));
        this.record = new Record(marc);
        this.lcshMapManager = mock(LcshMapManager.class);
        this.sfxMarcEresource = new SfxMarcEresource(record, null, lcshMapManager);
    }

    @Test
    public void testCreateVersion() {

    }

    @Test
    public void testGetMeshTerms() {
        expect(lcshMapManager.getMeshForHeading(isA(String.class))).andReturn(Collections.singleton("mappedMesh"))
                .atLeastOnce();
        replay(lcshMapManager);
        assertEquals("Law", this.sfxMarcEresource.getMeshTerms().toArray()[0]);
        assertEquals("mappedMesh", this.sfxMarcEresource.getMeshTerms().toArray()[1]);
        verify(lcshMapManager);
    }

    @Test
    public void testGetPrimaryType() {
        assertEquals("Book Digital", this.sfxMarcEresource.getPrimaryType());

    }

    @Test
    public void testGetRecordId() {
        assertEquals("110978984449203", this.sfxMarcEresource.getRecordId());

    }

    @Test
    public void testGetRecordType() {
        assertEquals("sfx", this.sfxMarcEresource.getRecordType());
    }

    @Test
    public void testGetTypes() {
        assertEquals("Book", this.sfxMarcEresource.getTypes().toArray()[0]);
    }
}
