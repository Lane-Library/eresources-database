package edu.stanford.irt.eresources.web;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.irt.eresources.SolrLoader;

/**
 * Manager to run one indexing {@link Job} at a time
 */
public class JobManager {

    private static final Logger log = LoggerFactory.getLogger(JobManager.class);

    private ExecutorService executor;

    private int maxJobDurationInHours;

    // protected for unit testing
    protected Future<JobStatus> runningFuture;

    // protected for unit testing
    protected Job runningJob;

    public JobManager(final ExecutorService executor, final int maxJobDurationInHours) {
        this.maxJobDurationInHours = maxJobDurationInHours;
        this.executor = executor;
    }

    /**
     * terminate the running job so another can be submitted.
     *
     * @return {@link JobStatus} INTERRUPTED if job can be canceled, otherwise COMPLETE
     */
    public JobStatus cancelRunningJob() {
        this.runningJob = null;
        if (null != this.runningFuture && !this.runningFuture.isDone()) {
            this.runningFuture.cancel(true);
            return JobStatus.INTERRUPTED;
        }
        return JobStatus.COMPLETE;
    }

    public int getMaxJobDurationInHours() {
        return this.maxJobDurationInHours;
    }

    public Job getRunningJob() {
        return this.runningJob;
    }

    public JobStatus run(final Job job) {
        String jobName = job.getType().getName();
        if (null != this.runningJob) {
            log.warn("{} failed to start job; previous {} job sill running", jobName,
                    this.runningJob.getType().getName());
            return JobStatus.RUNNING;
        }
        this.runningJob = job;
        this.runningFuture = doRun(job);
        try {
            return this.runningFuture.get(this.maxJobDurationInHours, TimeUnit.HOURS);
        } catch (TimeoutException e) {
            long duration = ChronoUnit.HOURS.between(job.getStart(), LocalDateTime.now());
            log.error("job {} running for {} hours", jobName, duration, e);
            return JobStatus.INTERRUPTED;
        } catch (InterruptedException | ExecutionException e) {
            log.error("job {} interrupted ", jobName, e);
            return JobStatus.INTERRUPTED;
        } finally {
            this.runningFuture = null;
            Thread.currentThread().interrupt();
        }
    }

    private Future<JobStatus> doRun(final Job job) {
        return this.executor.submit(() -> {
            String jobName = job.getType().getName();
            String[] args = { jobName };
            try {
                SolrLoader.main(args);
            } catch (Exception e) {
                log.error("solrLoader exception ", e);
                this.runningJob = null;
                return JobStatus.ERROR;
            }
            final long later = ChronoUnit.MILLIS.between(job.getStart(), LocalDateTime.now());
            log.info("solrLoader: {}; executed in {}ms", jobName, later);
            this.runningJob = null;
            return JobStatus.COMPLETE;
        });
    }
}
