package edu.stanford.irt.eresources;

import java.util.Date;

import edu.stanford.lane.catalog.RecordProcessor;

public abstract class AbstractEresourceProcessor implements RecordProcessor, StartDateAware {

    private long startTime;

    public void setStartDate(final Date startDate) {
        this.startTime = startDate.getTime();
    }

    protected long getStartTime() {
        return this.startTime;
    }
}
