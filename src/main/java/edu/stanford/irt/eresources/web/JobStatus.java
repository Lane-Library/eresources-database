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
     * job currently executing
     */
    RUNNING,
    /**
     * job was stopped prematurely
     */
    INTERRUPTED,
    /**
     * job did not run
     */
    SKIPPED;
}
