package edu.stanford.irt.eresources.sax;

import java.util.Map;

public abstract class AbstractReservesTextAugmentation implements ReservesTextAugmentation {

    private Map<String, String> augmentations;

    @Override
    public String getReservesAugmentations(final String controlNumber) {
        if (this.augmentations == null) {
            this.augmentations = buildAugmentations();
        }
        if (this.augmentations.containsKey(controlNumber)) {
            return this.augmentations.get(controlNumber);
        }
        return "";
    }

    protected abstract Map<String, String> buildAugmentations();
}