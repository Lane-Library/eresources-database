package edu.stanford.irt.eresources.marc.dedup;

import java.util.Set;
import java.util.stream.Collectors;

import edu.stanford.irt.eresources.marc.LaneDedupAugmentation;
import edu.stanford.irt.eresources.marc.MARCRecordSupport;
import edu.stanford.lane.catalog.Record;

public class OclcExtractionStrategy implements KeyExtractionStrategy {

    @Override
    public Set<String> extractKeys(Record marcRecord) {
        return MARCRecordSupport.getSubfieldData(marcRecord, "035", "a")
                .filter((final String s) -> s.startsWith("(OCoLC"))
                .map((final String s) -> s.substring(s.indexOf(')') + 1, s.length()))
                .map(ocolc -> LaneDedupAugmentation.KEY_OCLC_CONTROL_NUMBER + LaneDedupAugmentation.SEPARATOR + ocolc)
                .collect(Collectors.toSet());
    }
}
