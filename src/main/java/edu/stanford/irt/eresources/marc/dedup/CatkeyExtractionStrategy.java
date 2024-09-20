package edu.stanford.irt.eresources.marc.dedup;

import java.util.Set;

import edu.stanford.irt.eresources.marc.LaneDedupAugmentation;
import edu.stanford.irt.eresources.marc.MARCRecordSupport;
import edu.stanford.lane.catalog.Record;

public class CatkeyExtractionStrategy implements KeyExtractionStrategy {

    @Override
    public Set<String> extractKeys(Record marcRecord) {
        return Set.of(LaneDedupAugmentation.KEY_CATKEY + LaneDedupAugmentation.SEPARATOR
                + MARCRecordSupport.getRecordId(marcRecord));
    }
}