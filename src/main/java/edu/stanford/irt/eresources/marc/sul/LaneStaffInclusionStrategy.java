package edu.stanford.irt.eresources.marc.sul;

import edu.stanford.irt.eresources.marc.MARCRecordSupport;
import edu.stanford.lane.catalog.Record;

public class LaneStaffInclusionStrategy implements InclusionStrategy {

    // LANECAT-776: presence of a 909 in SUL records triggers inclusion
    @Override
    public boolean isAcceptable(final Record marcRecord) {
        return MARCRecordSupport.getFields(marcRecord, "909").count() > 0;
    }
}
