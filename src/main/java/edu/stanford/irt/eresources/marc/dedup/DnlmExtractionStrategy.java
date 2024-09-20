package edu.stanford.irt.eresources.marc.dedup;

import java.util.Set;
import java.util.stream.Collectors;

import edu.stanford.irt.eresources.marc.LaneDedupAugmentation;
import edu.stanford.irt.eresources.marc.MARCRecordSupport;
import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.Record.Field;
import edu.stanford.lane.catalog.Record.Subfield;

public class DnlmExtractionStrategy implements KeyExtractionStrategy {

    @Override
    public Set<String> extractKeys(Record marcRecord) {
        return MARCRecordSupport.getSubfieldData(MARCRecordSupport.getFields(marcRecord, "016")
                .filter((final Field f) -> f.getIndicator1() == '7').filter((final Field f) -> {
                    Subfield s2 = f.getSubfields().stream().filter((final Subfield s) -> s.getCode() == '2').findFirst()
                            .orElse(null);
                    Subfield sa = f.getSubfields().stream().filter((final Subfield s) -> s.getCode() == 'a').findFirst()
                            .orElse(null);
                    return s2 != null && sa != null && "DNLM".equalsIgnoreCase(s2.getData());
                }), "a")
                .map(dnlm -> LaneDedupAugmentation.KEY_DNLM_CONTROL_NUMBER + LaneDedupAugmentation.SEPARATOR + dnlm)
                .collect(Collectors.toSet());
    }
}
