package edu.stanford.irt.eresources.web;

import java.lang.management.RuntimeMXBean;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import edu.stanford.irt.status.RuntimeMXBeanStatusProvider;
import edu.stanford.irt.status.Status;
import edu.stanford.irt.status.StatusItem;

public class EresourceStatusProvider extends RuntimeMXBeanStatusProvider {

    private static final String MESSAGE = "(%s) running for %s %s";

    private JobManager jobManager;

    public EresourceStatusProvider(final JobManager manager) {
        this.jobManager = manager;
    }

    @Override
    protected void addStatusItems(final List<StatusItem> items, final RuntimeMXBean mxBean) {
        var runningJob = this.jobManager.getRunningJob();
        if (null != runningJob) {
            var msg = String.format(MESSAGE, runningJob.getType().getName(),
                    ChronoUnit.MINUTES.between(runningJob.getStart(), LocalDateTime.now()), ChronoUnit.MINUTES);
            items.add(new StatusItem(Status.INFO, msg));
            if (LocalDateTime.now().isAfter(
                    runningJob.getStart().plus(Duration.ofHours(this.jobManager.getMaxJobDurationInHours())))) {
                msg = String.format(MESSAGE, "long-running job " + runningJob.getType().getName(),
                        ChronoUnit.HOURS.between(runningJob.getStart(), LocalDateTime.now()), ChronoUnit.HOURS);
                items.add(new StatusItem(Status.ERROR, msg));
            }
        }
    }
}
