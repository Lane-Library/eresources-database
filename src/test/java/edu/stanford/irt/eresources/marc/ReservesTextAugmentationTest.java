package edu.stanford.irt.eresources.marc;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ReservesTextAugmentationTest {

    private ReservesTextAugmentation augmentation;

    private Map<String, String> augmentations;

    private AugmentationsService service;

    @BeforeEach
    public void setUp() throws Exception {
        this.service = mock(AugmentationsService.class);
        this.augmentation = new ReservesTextAugmentation(this.service);
        this.augmentations = Collections.singletonMap("1", "aug1");
    }

    @Test
    public final void testGetReservesAugmentations() {
        expect(this.service.buildAugmentations()).andReturn(this.augmentations);
        replay(this.service);
        assertEquals("aug1", this.augmentation.getReservesAugmentations("1"));
        assertEquals("", this.augmentation.getReservesAugmentations("100"));
        verify(this.service);
    }
}
