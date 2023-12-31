package edu.stanford.irt.eresources;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.boolex.EvaluationException;
import ch.qos.logback.core.boolex.EventEvaluatorBase;

public class TimeLimitedOnErrorEvaluator extends EventEvaluatorBase<ILoggingEvent> {

    private static final int DEFAULT_MESSAGE_LIMIT = 5;

    private static final int DEFAULT_TIME_LIMIT = 60;

    private int messageLimit;

    private LocalDateTime startTime;

    private int timeLimitMinutes;

    private int visitCounter;

    public TimeLimitedOnErrorEvaluator() {
        this.messageLimit = DEFAULT_MESSAGE_LIMIT;
        this.timeLimitMinutes = DEFAULT_TIME_LIMIT;
    }

    @Override
    public boolean evaluate(final ILoggingEvent event) throws EvaluationException {
        if (event.getLevel().levelInt >= Level.ERROR_INT) {
            LocalDateTime now = LocalDateTime.now();
            if (null == this.startTime || this.startTime.until(now, ChronoUnit.MINUTES) >= this.timeLimitMinutes) {
                this.startTime = now;
                this.visitCounter = 0;
            }
            this.visitCounter++;
            return this.visitCounter <= this.messageLimit;
        }
        return false;
    }

    public void setMessageLimit(final int limit) {
        this.messageLimit = limit;
    }

    public void setTimeLimitMinutes(final int minutes) {
        this.timeLimitMinutes = minutes;
    }
}
