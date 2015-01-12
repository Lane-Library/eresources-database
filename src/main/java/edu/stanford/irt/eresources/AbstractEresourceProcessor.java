package edu.stanford.irt.eresources;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.lane.catalog.RecordProcessor;

public abstract class AbstractEresourceProcessor implements RecordProcessor, StartDateAware {

    private long startTime;

    public void setStartDate(final Date startDate) {
        Logger log = LoggerFactory.getLogger(getClass());
        log.info("enter setStartDate(" + startDate + ");");
        this.startTime = startDate.getTime();
        log.info("return setStartDate();");
    }

    protected long getStartTime() {
        return this.startTime;
    }
}
