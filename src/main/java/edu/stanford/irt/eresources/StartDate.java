package edu.stanford.irt.eresources;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StartDate {

    private static final Logger LOG = LoggerFactory.getLogger(StartDate.class);

    private Date date;

    public Date getStartDate() {
        return new Date(this.date.getTime());
    }

    public void initialize(final Date date) {
        if (this.date != null) {
            throw new IllegalStateException("already initialized with " + this.date);
        }
        LOG.info(date.toString());
        this.date = new Date(date.getTime());
    }
}
