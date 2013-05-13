package edu.stanford.irt.eresources;

import java.util.Date;

import edu.stanford.lane.catalog.RecordProcessor;

public abstract class AbstractEresourceProcessor implements RecordProcessor {

    protected EresourceBuilder eresourceBuilder;

    protected Date startDate = new Date(0);

    public void setEresourceBuilder(final EresourceBuilder eresourceBuilder) {
        if (null == eresourceBuilder) {
            throw new IllegalArgumentException("null eresourceBuilder");
        }
        this.eresourceBuilder = eresourceBuilder;
    }

    public void setStartDate(final Date startDate) {
        if (null == startDate) {
            throw new IllegalArgumentException("null startDate");
        }
        this.startDate.setTime(startDate.getTime());
    }
}
