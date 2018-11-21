package edu.stanford.irt.eresources.marc;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AugmentationUtilityTest {

    AugmentationsService augmentationsService;

    AugmentationUtility augmentationUtility;

    String objectFile = "unit-test-augmentation-utility.obj";

    @Before
    public void setUp() throws Exception {
        this.augmentationsService = mock(AugmentationsService.class);
    }

    @After
    public void tearDown() throws Exception {
        new File(this.objectFile).delete();
    }

    @Test
    public final void testFetchAugmentations() throws Exception {
        Map<String, String> aMap = new HashMap<>();
        aMap.put("foo", "fee");
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(this.objectFile));
        oos.writeObject(aMap);
        oos.close();
        assertEquals(aMap,
                AugmentationUtility.fetchAugmentations(this.objectFile, this.augmentationsService, Integer.MAX_VALUE));
    }

    @Test
    public final void testFetchAugmentationsBuild() throws Exception {
        expect(this.augmentationsService.buildAugmentations()).andReturn(Collections.emptyMap());
        replay(this.augmentationsService);
        assertTrue(AugmentationUtility.fetchAugmentations(this.objectFile, this.augmentationsService, Integer.MIN_VALUE)
                .isEmpty());
        verify(this.augmentationsService);
    }

    @Test(expected = NotSerializableException.class)
    public final void testFetchAugmentationsException() throws Exception {
        Object notAMap = new Object();
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(this.objectFile));
        oos.writeObject(notAMap);
        oos.close();
        AugmentationUtility.fetchAugmentations(this.objectFile, this.augmentationsService, 100);
    }
}
