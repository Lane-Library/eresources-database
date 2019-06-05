package edu.stanford.irt.eresources.marc;

import java.util.Map;

public class ReservesTextAugmentation {

    private Map<String, String> augmentations;

    private AugmentationsService augmentationsService;

    public ReservesTextAugmentation(final AugmentationsService augmentationsService) {
        this.augmentationsService = augmentationsService;
    }

    public String getReservesAugmentations(final String controlNumber) {
        if (this.augmentations == null) {
            this.augmentations = this.augmentationsService.buildAugmentations();
        }
        if (this.augmentations.containsKey(controlNumber)) {
            return this.augmentations.get(controlNumber);
        }
        return "";
    }
}
