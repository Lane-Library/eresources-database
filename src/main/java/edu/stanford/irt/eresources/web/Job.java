package edu.stanford.irt.eresources.web;

import java.time.LocalDateTime;

/**
 * indexing job
 */
public class Job {

    public enum Type {

        CANCEL_RUNNING_JOB("cancel-running-job", "cancels whatever job is currently running"),
        LANE_MARC_RELOAD("lane/marc-reload","Lane MARC reload - nightly"),
        LANE_MARC_UPDATE("lane/marc-update", "Lane MARC updates - frequently during work hours"),
        LANE_RELOAD("lane/reload","reload all Lane resource types: MARC, classes, laneweb HTML, blog, libguides - nightly"),
        LANE_UPDATE("lane/update","update all Lane resource types: MARC, classes, laneweb HTML, blog, libguides - frequently"),
        LANE_WEBSITES_RELOAD("lane/websites-reload","reload laneweb HTML, classes, blog, libguides - nightly"),
        LANE_WEBSITES_UPDATE("lane/websites-update","update laneweb HTML, classes, blog, libguides - frequently"),
        PMC_RELOAD("pmc/reload", "reload PMC journals - daily"),
        PUBMED_RELOAD("pubmed/reload", "reload all PubMed records - annual or less"),
        PUBMED_UPDATE("pubmed/update", "updates from PubMed - a few times a day"),
        REDIVIS_RELOAD("redivis/reload", "reload Redivis - monthly"),
        SUL_RELOAD("sul/reload", "reload SUL MARC - monthly"),
        SUL_UPDATE("sul/update", "update SUL MARC - daily"),
        UNDEFINED("undefined","for unit testing"),
        UNIT_TESTING("lane/unit-test", "for unit testing");

        public static Type fromString(final String name) {
            for (Job.Type type : Job.Type.values()) {
                if (type.getName().equals(name)) {
                    return type;
                }
            }
            return Job.Type.UNDEFINED;
        }

        private String description;

        private String name;

        Type(final String name, final String description) {
            this.name = name;
            this.description = description;
        }
        
        public String getName() {
            return this.name;
        }
        public String getDescription() {
            return this.description;
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
