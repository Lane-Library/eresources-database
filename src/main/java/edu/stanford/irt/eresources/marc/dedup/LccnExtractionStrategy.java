package edu.stanford.irt.eresources.marc.dedup;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import edu.stanford.irt.eresources.marc.LaneDedupAugmentation;
import edu.stanford.irt.eresources.marc.MARCRecordSupport;
import edu.stanford.lane.catalog.Record;

public class LccnExtractionStrategy implements KeyExtractionStrategy {

    @Override
    public Set<String> extractKeys(Record marcRecord) {
        HashSet<String> keys = new HashSet<>();
        for (String lccn : MARCRecordSupport.getSubfieldData(marcRecord, "010", "a").map(String::trim)
                .collect(Collectors.toSet())) {
            keys.add(LaneDedupAugmentation.KEY_LC_CONTROL_NUMBER + LaneDedupAugmentation.SEPARATOR + lccn);
        }
        return keys;
    }
}