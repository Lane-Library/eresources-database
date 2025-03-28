package edu.stanford.irt.eresources.marc;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AuthTextAugmentationTest {

    private AuthTextAugmentation augmentation;

    private Map<String, String> augmentations;

    private String objectFile = "/tmp/auth-objects";

    private AugmentationsService service;

    @BeforeEach
    void setUp() {
        this.service = mock(AugmentationsService.class);
        this.augmentations = Collections.singletonMap("1", "variant authority text");
        expect(this.service.buildAugmentations()).andReturn(this.augmentations);
        replay(this.service);
        this.augmentation = new AuthTextAugmentation(this.objectFile, this.service);
        verify(this.service);
    }

    @AfterEach
    void tearDown() throws Exception {
        new File(this.objectFile).delete();
    }

    @Test
    final void testGetAugmentations() {
        assertEquals("variant authority text", this.augmentation.getAuthAugmentations("1"));
    }
}
