package edu.stanford.irt.eresources.web;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
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
    protected List<String> pausedDataSources;

    // protected for unit testing
    protected List<Future<Job>> runningFutures;

    // protected for unit testing
    protected List<Job> runningJobs;

    public JobManager(final ExecutorService executor, final int maxJobDurationInHours) {
        this.maxJobDurationInHours = maxJobDurationInHours;
        this.executor = executor;
        this.runningFutures = new ArrayList<>();
        this.runningJobs = new ArrayList<>();
        this.pausedDataSources = new ArrayList<>();
    }

    /**
     * terminate all running jobs
     *
     * @return {@link JobStatus} INTERRUPTED if we canceled at least one job, otherwise COMPLETE
     */
    public JobStatus cancelRunningJobs() {
        this.runningJobs.clear();
        boolean interrupted = false;
        for (Future<Job> future : this.runningFutures) {
            if (!future.isDone()) {
                future.cancel(true);
                interrupted = true;
            }
        }
        this.runningFutures.clear();
        if (interrupted) {
            return JobStatus.INTERRUPTED;
        }
        return JobStatus.COMPLETE;
    }

    public int getMaxJobDurationInHours() {
        return this.maxJobDurationInHours;
    }

    public List<String> getPausedDataSources() {
        return this.pausedDataSources;
    }

    public List<Job> getRunningJobs() {
        return this.runningJobs;
    }

    public JobStatus run(final Job job) {
        String jobName = job.getType().getName();
        String jobQualifiedName = job.getType().getQualifiedName();
        String jobDataSource = job.getType().getDataSource();
        Job jobOfSameDataSource = this.runningJobs.stream()
                .filter((final Job j) -> jobDataSource.equals(j.getType().getDataSource())).findFirst().orElse(null);
        boolean pausedDataSource = this.pausedDataSources.contains(jobDataSource);
        if ("pause".equals(jobName)) {
            if (null != jobOfSameDataSource) {
                log.warn(
                        "job with data source ({}) already running and won't be interrupted; consider canceling all running jobs",
                        jobDataSource);
            }
            if (this.pausedDataSources.contains(jobDataSource)) {
                this.pausedDataSources.remove(job.getType().getDataSource());
                return JobStatus.RUNNING;
            }
            this.pausedDataSources.add(job.getType().getDataSource());
            return JobStatus.PAUSED;
        }
        if (pausedDataSource) {
            log.warn("{} failed to start job; data source ({}) paused", jobQualifiedName, jobDataSource);
            return JobStatus.PAUSED;
        }
        if (null != jobOfSameDataSource) {
            log.warn("{} failed to start job; conflicting job with same data source ({}) still running",
                    jobQualifiedName, jobDataSource);
            return JobStatus.RUNNING;
        }
        this.runningJobs.add(job);
        Future<Job> future = doRun(job);
        this.runningFutures.add(future);
        try {
            return future.get(this.maxJobDurationInHours, TimeUnit.HOURS).getStatus();
        } catch (TimeoutException e) {
            long duration = ChronoUnit.HOURS.between(job.getStart(), LocalDateTime.now());
            log.error("job {} running for {} hours", jobQualifiedName, duration, e);
            job.setStatus(JobStatus.INTERRUPTED);
        } catch (InterruptedException | ExecutionException e) {
            log.error("job {} interrupted ", jobQualifiedName, e);
            job.setStatus(JobStatus.INTERRUPTED);
        } finally {
            this.runningFutures.remove(future);
            Thread.currentThread().interrupt();
        }
        return job.getStatus();
    }

    private Future<Job> doRun(final Job job) {
        return this.executor.submit(() -> {
            String jobName = job.getType().getQualifiedName();
            String[] args = { jobName, job.getDataUpdateOverride() };
            try {
                SolrLoader.main(args);
            } catch (Exception e) {
                log.error("solrLoader exception ", e);
                this.runningJobs.remove(job);
                job.setStatus(JobStatus.ERROR);
                return job;
            }
            final long later = ChronoUnit.SECONDS.between(job.getStart(), LocalDateTime.now());
            log.info("solrLoader: {}; executed in {}s", jobName, later);
            this.runningJobs.remove(job);
            job.setStatus(JobStatus.COMPLETE);
            return job;
        });
    }
}
