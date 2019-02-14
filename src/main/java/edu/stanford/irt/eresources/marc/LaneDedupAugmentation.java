package edu.stanford.irt.eresources.marc;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LaneDedupAugmentation {

    public static final String KEY_CATKEY = "catkey";

    public static final String KEY_ISBN = "isbn";

    public static final String KEY_ISSN = "issn";

    public static final String KEY_LC_CONTROL_NUMBER = "lccntrln";

    public static final String KEY_OCLC_CONTROL_NUMBER = "ocolc";

    public static final String KEY_TITLE_DATE = "title_date";

    public static final String KEY_URL = "url";

    public static final String SEPARATOR = "->";

    private Map<String, String> augmentations;

    private Set<String> manualSkips;

    public LaneDedupAugmentation(final String augmentationsFileName, final AugmentationsService augmentationsService,
            final Set<String> manualSkips) {
        this.manualSkips = new HashSet<>(manualSkips);
        this.augmentations = AugmentationUtility.fetchAugmentations(augmentationsFileName, augmentationsService,
                AugmentationUtility.ONE_DAY);
    }

    public boolean isDuplicate(final String key, final String value) {
        return this.manualSkips.contains(key + SEPARATOR + value)
                || this.augmentations.containsKey(key + SEPARATOR + value);
    }
}
