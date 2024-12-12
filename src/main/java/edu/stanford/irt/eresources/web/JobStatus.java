package edu.stanford.irt.eresources.web;

/**
 * indexing job statuses
 */
public enum JobStatus {
    /**
     * job executed successfully
     */
    COMPLETE,
    /**
     * job failed
     */
    ERROR,
    /**
     * job was stopped prematurely
     */
    INTERRUPTED,
    /**
     * job (data source) is paused; no impact on running jobs
     */
    PAUSED,
    /**
     * job currently executing
     */
    RUNNING,
    /**
     * job did not run
     */
    SKIPPED,
    /**
     * job started but not yet running
     */
    STARTED;
}
