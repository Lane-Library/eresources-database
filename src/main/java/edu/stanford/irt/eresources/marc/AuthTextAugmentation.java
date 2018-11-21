package edu.stanford.irt.eresources.marc;

import java.util.Map;

public class AuthTextAugmentation extends AbstractAugmentation {

    private Map<String, String> augmentations;

    public AuthTextAugmentation(final AugmentationsService augmentationsService) {
        this.augmentations = fetchAugmentations("auth-augmentations.obj", augmentationsService, ONE_DAY);
    }

    public String getAuthAugmentations(final String controlNumber) {
        return this.augmentations.get(controlNumber);
    }
}
