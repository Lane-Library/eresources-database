package edu.stanford.irt.eresources.marc;

import java.util.Map;

public class AuthTextAugmentation {

    private Map<String, String> augmentations;

    public AuthTextAugmentation(final String augmentationsFile, final AugmentationsService augmentationsService) {
        this.augmentations = AugmentationUtility.fetchAugmentations(augmentationsFile, augmentationsService,
                AugmentationUtility.ONE_DAY);
    }

    public String getAuthAugmentations(final String controlNumber) {
        return this.augmentations.get(controlNumber);
    }
}
