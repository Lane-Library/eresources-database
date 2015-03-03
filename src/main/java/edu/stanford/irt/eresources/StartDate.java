package edu.stanford.irt.eresources;

import java.util.Date;

public class StartDate {

    private Date startDate;

    public Date getStartDate() {
        return this.startDate;
    }

    public void initialize(final Date startDate) {
        if (this.startDate != null) {
            throw new IllegalStateException("already initialized with " + this.startDate);
        }
        this.startDate = startDate;
    }
}
