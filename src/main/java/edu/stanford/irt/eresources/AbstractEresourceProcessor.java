package edu.stanford.irt.eresources;

import java.util.Date;

import edu.stanford.lane.catalog.RecordProcessor;

public abstract class AbstractEresourceProcessor implements RecordProcessor {

    protected Date startDate = new Date(0);

    public void setStartDate(final Date startDate) {
        if (null == startDate) {
            throw new IllegalArgumentException("null startDate");
        }
        this.startDate.setTime(startDate.getTime());
    }
}
