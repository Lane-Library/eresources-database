package edu.stanford.irt.eresources;

import java.time.LocalDateTime;
import java.time.ZoneId;

public abstract class AbstractEresourceProcessor {

    private long startTime;

    public abstract void process();

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
