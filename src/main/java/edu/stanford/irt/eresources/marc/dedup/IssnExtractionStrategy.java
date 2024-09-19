package edu.stanford.irt.eresources.marc.dedup;

import java.util.Set;
import java.util.stream.Collectors;

import edu.stanford.irt.eresources.marc.LaneDedupAugmentation;
import edu.stanford.irt.eresources.marc.MARCRecordSupport;
import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.TextHelper;

public class IssnExtractionStrategy implements KeyExtractionStrategy {

    @Override
    public Set<String> extractKeys(Record marcRecord) {
        return MARCRecordSupport.getSubfieldData(marcRecord, "022", "a").map(String::trim).map(TextHelper::cleanIsxn)
                .filter((final String s) -> !s.isEmpty())
                .map(issn -> LaneDedupAugmentation.KEY_ISSN + LaneDedupAugmentation.SEPARATOR + issn)
                .collect(Collectors.toSet());
    }
}
