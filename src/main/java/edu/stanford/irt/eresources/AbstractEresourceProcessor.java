package edu.stanford.irt.eresources;

import java.time.LocalDateTime;
import java.time.ZoneId;

import edu.stanford.lane.catalog.RecordProcessor;

public abstract class AbstractEresourceProcessor implements RecordProcessor {

    private long startTime;

    public void setStartDate(final LocalDateTime startDate) {
        if (null == startDate) {
            throw new IllegalArgumentException("null startDate");
        }
        this.startTime = startDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    protected long getStartTime() {
        return this.startTime;
    }
}
