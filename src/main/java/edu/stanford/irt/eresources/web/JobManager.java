package edu.stanford.irt.eresources.web;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.irt.eresources.SolrLoader;

/**
 * Manager to run one indexing {@link Job} at a time consider using {@link CompletableFuture}
 */
public class JobManager {

    public static final String CLEAR_RUNNING_JOB = "clear-running-job";

    private static final Logger log = LoggerFactory.getLogger(JobManager.class);

    private int maxJobDurationInHours;

    // protected for unit testing
    protected Job runningJob;

    public JobManager(final int maxJobDurationInHours) {
        this.maxJobDurationInHours = maxJobDurationInHours;
    }

    /**
     * clear the running job so another can be submitted. Note: makes no attempt to halt the running job!
     */
    public JobStatus clearRunningJob() {
        this.runningJob = null;
        return JobStatus.COMPLETE;
    }

    public int getMaxJobDurationInHours() {
        return this.maxJobDurationInHours;
    }

    public Job getRunningJob() {
        return this.runningJob;
    }

    public JobStatus run(final Job job) {
        if (null == this.runningJob) {
            this.runningJob = job;
            String[] args = { job.getName() };
            try {
                SolrLoader.main(args);
            } catch (Exception e) {
                log.error("solrLoader exception ", e);
                this.runningJob = null;
                return JobStatus.ERROR;
            }
            final long later = ChronoUnit.MILLIS.between(job.getStart(), LocalDateTime.now());
            log.info("solrLoader: {}; executed in {}ms", job, later);
            this.runningJob = null;
            return JobStatus.COMPLETE;
        }
        log.warn("{} failed to start job; previous {} job sill running", job.getName(), this.runningJob.getName());
        return JobStatus.RUNNING;
    }
}
