package edu.stanford.irt.eresources;

import java.util.Date;

import edu.stanford.lane.catalog.RecordProcessor;

public abstract class AbstractEresourceProcessor implements RecordProcessor {

    private long startTime;

    public void setStartDate(final Date startDate) {
        if (null == startDate) {
            throw new IllegalArgumentException("null startDate");
        }
        this.startTime = startDate.getTime();
    }

    protected long getStartTime() {
        return this.startTime;
    }
}
