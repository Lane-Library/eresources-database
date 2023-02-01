package edu.stanford.irt.eresources.web;

import java.time.LocalDateTime;

/**
 * indexing job
 */
public class Job {

    public enum Type {

        CANCEL_RUNNING_JOBS("all", "cancel-running-jobs", "cancels all currently running jobs"),
        LANE_FOLIO_RELOAD("lane", "folio-reload","Lane MARC and native FOLIO formats reload - nightly"),
        LANE_FOLIO_UPDATE("lane", "folio-update", "Lane MARC and native FOLIO formats updates - frequently during work hours"),
        LANE_RELOAD("lane", RELOAD,"reload all Lane resource types: MARC, classes, laneweb HTML, blog, libguides - nightly"),
        LANE_UPDATE("lane", UPDATE,"update all Lane resource types: MARC, classes, laneweb HTML, blog, libguides - frequently"),
        LANE_WEBSITES_RELOAD("lane", "websites-reload","reload laneweb HTML, classes, blog, libguides - nightly"),
        LANE_WEBSITES_UPDATE("lane", "websites-update","update laneweb HTML, classes, blog, libguides - frequently"),
        PMC_RELOAD("pmc", RELOAD, "reload PMC journals - daily"),
        PUBMED_RELOAD("pubmed", UPDATE, "reload all PubMed records - annual or less"),
        PUBMED_UPDATE("pubmed", UPDATE, "updates from PubMed - a few times a day"),
        REDIVIS_RELOAD("redivis",RELOAD, "reload Redivis - monthly"),
        SUL_RELOAD("sul",RELOAD, "reload SUL MARC - monthly"),
        SUL_UPDATE("sul", UPDATE, "update SUL MARC - daily"),
        UNDEFINED("none", "undefined","for unit testing"),
        UNIT_TESTING("lane", "unit-test", "for unit testing");

        public static Type fromString(final String name) {
            for (Job.Type type : Job.Type.values()) {
                if (type.getQualifiedName().equals(name)) {
                    return type;
                }
            }
            return Job.Type.UNDEFINED;
        }

        private String dataSource;

        private String description;

        private String name;

        Type(final String dataSource, final String name, final String description) {
            this.name = name;
            this.description = description;
            this.dataSource = dataSource;
        }

        public String getDataSource() {
            return this.dataSource;
        }

        public String getDescription() {
            return this.description;
        }

        public String getQualifiedName() {
            return this.dataSource + "/" + this.name;
        }
    }

    private static final String RELOAD = "reload";

    private static final String UPDATE = "update";

    private LocalDateTime start;

    private JobStatus status;

    private Type type;

    public Job(final Type jobType, final LocalDateTime start) {
        this.type = jobType;
        this.start = start;
        this.status = JobStatus.STARTED;
    }

    public LocalDateTime getStart() {
        return this.start;
    }

    public JobStatus getStatus() {
        return this.status;
    }

    public Type getType() {
        return this.type;
    }

    public void setStatus(final JobStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append("type: ").append(this.type);
        sb.append("; start: ").append(this.start);
        sb.append("; status: ").append(this.status);
        return sb.toString();
    }
}
