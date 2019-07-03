package edu.stanford.irt.eresources.marc;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LaneDedupAugmentationTest {

    AugmentationsService augmentationService;

    String objectFile = "/tmp/dedup-unit-test.obj";

    @Before
    public void setUp() throws Exception {
        this.augmentationService = mock(AugmentationsService.class);
    }

    @After
    public void tearDown() throws Exception {
        new File(this.objectFile).delete();
    }

    @Test
    public final void testLaneDedupAugmentation() {
        Set<String> skips = new HashSet<>();
        skips.add("key->12345");
        Map<String, String> map = new HashMap<String, String>();
        map.put("foo->bar", null);
        expect(this.augmentationService.buildAugmentations()).andReturn(map);
        replay(this.augmentationService);
        LaneDedupAugmentation dedupAugmentation = new LaneDedupAugmentation(this.objectFile, this.augmentationService,
                skips);
        verify(this.augmentationService);
        assertTrue((new File(this.objectFile).exists()));
        assertTrue(dedupAugmentation.isDuplicate("foo", "bar"));
        assertTrue(dedupAugmentation.isDuplicate("key", "12345"));
        assertTrue(dedupAugmentation.isDuplicate("key->12345"));
        assertFalse(dedupAugmentation.isDuplicate("missing", "missing"));
        reset(this.augmentationService);
        dedupAugmentation = new LaneDedupAugmentation(this.objectFile, this.augmentationService, skips);
        assertTrue(dedupAugmentation.isDuplicate("key", "12345"));
    }
}
