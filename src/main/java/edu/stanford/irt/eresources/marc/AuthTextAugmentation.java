package edu.stanford.irt.eresources.marc;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AuthTextAugmentation {

    private Map<String, String> augmentations;

    public AuthTextAugmentation(final String augmentationsFile, final AugmentationsService augmentationsService) {
        this.augmentations = AugmentationUtility.fetchAugmentations(augmentationsFile, augmentationsService,
                AugmentationUtility.ONE_DAY);
    }

    public String getAuthAugmentations(final String controlNumber, final String fieldStrings) {
        if (!this.augmentations.containsKey(controlNumber)) {
            return null;
        }
        List<String> tokenizedFieldString = tokenize(fieldStrings);
        String potentialAugments = this.augmentations.get(controlNumber).replaceAll("\\p{Punct}", "");
        Set<String> augments = new LinkedHashSet<>();
        for (String word : tokenize(potentialAugments)) {
            if (!tokenizedFieldString.contains(word)) {
                augments.add(word);
            }
        }
        return String.join(" ", augments);
    }

    private List<String> tokenize(final String string) {
        String normalized = string.replaceAll("\\p{Punct}", "");
        return Arrays.asList(normalized.split(" "));
    }
}
