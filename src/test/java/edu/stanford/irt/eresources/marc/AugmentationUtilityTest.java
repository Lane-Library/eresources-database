package edu.stanford.irt.eresources.marc;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.stanford.irt.eresources.EresourceDatabaseException;

public class AugmentationUtilityTest {

    Map<String, String> aMap;

    AugmentationsService augmentationsService;

    AugmentationUtility augmentationUtility;

    String objectFile = "unit-test-augmentation-utility.obj";

    @Before
    public void setUp() throws Exception {
        this.augmentationsService = mock(AugmentationsService.class);
        this.aMap = new HashMap<>();
        this.aMap.put("key", "value");
    }

    @After
    public void tearDown() throws Exception {
        new File(this.objectFile).delete();
    }

    @Test
    public final void testFetchAugmentations() throws Exception {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(this.objectFile));
        oos.writeObject(this.aMap);
        oos.close();
        assertEquals(this.aMap,
                AugmentationUtility.fetchAugmentations(this.objectFile, this.augmentationsService, Integer.MAX_VALUE));
    }

    @Test
    public final void testFetchAugmentationsBuild() throws Exception {
        expect(this.augmentationsService.buildAugmentations()).andReturn(this.aMap).times(2);
        replay(this.augmentationsService);
        assertTrue(AugmentationUtility.fetchAugmentations(this.objectFile, this.augmentationsService, Integer.MIN_VALUE)
                .containsKey("key"));
        // obj file now exists
        File of = new File(this.objectFile);
        assertTrue(of.exists());
        of.setLastModified(0);
        assertTrue(AugmentationUtility.fetchAugmentations(this.objectFile, this.augmentationsService, Integer.MAX_VALUE)
                .containsKey("key"));
        verify(this.augmentationsService);
    }

    @Test(expected = EresourceDatabaseException.class)
    public final void testFetchAugmentationsIOException1() throws Exception {
        expect(this.augmentationsService.buildAugmentations()).andReturn(this.aMap);
        replay(this.augmentationsService);
        AugmentationUtility.fetchAugmentations("/", this.augmentationsService, 100);
        verify(this.augmentationsService);
    }

    // failing on gitlab runner only
//    @Test
//    public final void testFetchAugmentationsIOException2() throws Exception {
//        expect(this.augmentationsService.buildAugmentations()).andReturn(Collections.emptyMap());
//        replay(this.augmentationsService);
//        AugmentationUtility.fetchAugmentations("/", this.augmentationsService, 100);
//        verify(this.augmentationsService);
//    }
}
