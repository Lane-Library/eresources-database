package edu.stanford.irt.eresources.marc.sul;

import java.util.List;

import edu.stanford.irt.eresources.marc.MARCRecordSupport;
import edu.stanford.lane.catalog.Record;

public class AcceptableDBCallNumberStrategy implements InclusionStrategy {

    List<String> acceptableDBCallNumbers;

    public AcceptableDBCallNumberStrategy(final List<String> acceptableDBCallNumbers) {
        this.acceptableDBCallNumbers = acceptableDBCallNumbers;
    }

    @Override
    public boolean isAcceptable(final Record marcRecord) {
        return MARCRecordSupport.getSubfieldData(marcRecord, "099", "a")
                .anyMatch(this.acceptableDBCallNumbers::contains);
    }
}
