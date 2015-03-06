package edu.stanford.irt.eresources;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StartDate {

    private static final Logger LOG = LoggerFactory.getLogger(StartDate.class);

    private Date startDate;

    public Date getStartDate() {
        return this.startDate;
    }

    public void initialize(final Date startDate) {
        if (this.startDate != null) {
            throw new IllegalStateException("already initialized with " + this.startDate);
        }
        LOG.info(startDate.toString());
        this.startDate = startDate;
    }
}
