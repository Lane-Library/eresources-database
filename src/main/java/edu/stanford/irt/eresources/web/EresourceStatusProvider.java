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

    private static final String MESSAGE_PAUSED = "jobs with data source \"%s\" are paused";

    private static final String MESSAGE_RUNNING = "(%s) running for %s %s";

    private JobManager jobManager;

    public EresourceStatusProvider(final JobManager manager) {
        this.jobManager = manager;
    }

    @Override
    protected void addStatusItems(final List<StatusItem> items, final RuntimeMXBean mxBean) {
        if (!this.jobManager.getRunningJobs().isEmpty()) {
            for (var runningJob : this.jobManager.getRunningJobs()) {
                var msg = String.format(MESSAGE_RUNNING, runningJob.getType().getQualifiedName(),
                        ChronoUnit.MINUTES.between(runningJob.getStart(), LocalDateTime.now()), ChronoUnit.MINUTES);
                items.add(new StatusItem(Status.INFO, msg));
                if (LocalDateTime.now().isAfter(
                        runningJob.getStart().plus(Duration.ofHours(this.jobManager.getMaxJobDurationInHours())))) {
                    msg = String.format(MESSAGE_RUNNING, "long-running job " + runningJob.getType().getQualifiedName(),
                            ChronoUnit.HOURS.between(runningJob.getStart(), LocalDateTime.now()), ChronoUnit.HOURS);
                    items.add(new StatusItem(Status.ERROR, msg));
                }
            }
        }
        if (!this.jobManager.getPausedDataSources().isEmpty()) {
            for (var pausedDataSource : this.jobManager.getPausedDataSources()) {
                var msg = String.format(MESSAGE_PAUSED, pausedDataSource);
                items.add(new StatusItem(Status.INFO, msg));
            }
        }
    }
}
