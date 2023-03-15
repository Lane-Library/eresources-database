package edu.stanford.irt.eresources.marc;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.util.Collections;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AuthTextAugmentationTest {

    private AuthTextAugmentation augmentation;

    private Map<String, String> augmentations;

    private String objectFile = "/tmp/auth-objects";

    private AugmentationsService service;

    @Before
    public void setUp() throws Exception {
        this.service = mock(AugmentationsService.class);
        this.augmentations = Collections.singletonMap("1", "variant authority text");
        expect(this.service.buildAugmentations()).andReturn(this.augmentations);
        replay(this.service);
        this.augmentation = new AuthTextAugmentation(this.objectFile, this.service);
        verify(this.service);
    }

    @After
    public void tearDown() throws Exception {
        new File(this.objectFile).delete();
    }

    @Test
    public final void testGetAugmentations() {
        assertNull(this.augmentation.getAuthAugmentations("100", "text"));
        assertEquals("variant authority", this.augmentation.getAuthAugmentations("1", "text"));
    }
}
