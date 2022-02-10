package edu.stanford.irt.eresources.web;

import java.time.LocalDateTime;

/**
 * indexing job
 */
public class Job {

    public enum Type {

        /**
         * job type to cancel whatever job is currently running
         */
        CANCEL_RUNNING_JOB("cancel-running-job"),
        /**
         * daily lane reload
         */
        LANE_RELOAD("lane/reload"),
        /**
         * routine lane updates
         */
        LANE_UPDATE("lane/update"),
        /**
         * reload PMC journal
         */
        PMC_RELOAD("pmc/reload"),
        /**
         * a few times a day pubmed updates
         */
        PUBMED_RELOAD("pubmed/run-annual-reload"),
        /**
         * a few times a day pubmed updates
         */
        PUBMED_UPDATE("pubmed/run-daily-ftp"),
        /**
         * monthly redivis reload
         */
        REDIVIS_RELOAD("redivis/reload"),
        /**
         * monthly sul reload
         */
        SUL_RELOAD("sul/reload"),
        /**
         * daily sul updates
         */
        SUL_UPDATE("sul/update"),
        /**
         * unknown ... also for unit testing
         */
        UNDEFINED("undefined"),
        /**
         * for unit testing
         */
        UNIT_TESTING("lane/unit-test");

        public static Type fromString(final String name) {
            for (Job.Type type : Job.Type.values()) {
                if (type.getName().equals(name)) {
                    return type;
                }
            }
            return Job.Type.UNDEFINED;
        }

        private String name;

        Type(final String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }

    private LocalDateTime start;

    private Type type;

    public Job(final Type jobType, final LocalDateTime start) {
        this.type = jobType;
        this.start = start;
    }

    public LocalDateTime getStart() {
        return this.start;
    }

    public Type getType() {
        return this.type;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append("type: ").append(this.type);
        sb.append("; start: ").append(this.start);
        return sb.toString();
    }
}
