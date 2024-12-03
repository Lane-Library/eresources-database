package edu.stanford.irt.eresources.web;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * indexing job
 */
public class Job {

    public enum DataSource {

        ALL, FOLIO_DELETE, LANE, PMC, PUBMED, REDIVIS, SFX, SUL, UNDEFINED;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    public enum Type {

        CANCEL_RUNNING_JOBS(DataSource.ALL, "cancel-running-jobs", "cancels all currently running jobs"),
        DELETES_FOLIO_ALL(DataSource.FOLIO_DELETE, "folio-all","delete all suppressed Lane and SUL records (slow)"),
        DELETES_FOLIO_HOURLY(DataSource.FOLIO_DELETE, "folio-hourly","delete recently suppressed Lane and SUL records"),
        DELETES_FOLIO_DAILY(DataSource.FOLIO_DELETE, "folio-daily","delete recently suppressed Lane and SUL records"),
        LANE_FOLIO_RELOAD(DataSource.LANE, "folio-reload","Lane MARC and native FOLIO formats reload - nightly"),
        LANE_FOLIO_UPDATE(DataSource.LANE, "folio-update", "Lane MARC and native FOLIO formats updates - frequently during work hours"),
        LANE_RELOAD(DataSource.LANE, RELOAD,"reload all Lane resource types: MARC, classes, laneweb HTML, blog, libguides - nightly"),
        LANE_UPDATE(DataSource.LANE, UPDATE,"update all Lane resource types: MARC, classes, laneweb HTML, blog, libguides - frequently"),
        LANE_WEBSITES_RELOAD(DataSource.LANE, "websites-reload","reload laneweb HTML, classes, blog, libguides - nightly"),
        LANE_WEBSITES_UPDATE(DataSource.LANE, "websites-update","update laneweb HTML, classes, blog, libguides - frequently"),
        PMC_RELOAD(DataSource.PMC, RELOAD, "reload PMC journals - daily"),
        PUBMED_RELOAD(DataSource.PUBMED, RELOAD, "reload all PubMed records - annual or less"),
        PUBMED_UPDATE(DataSource.PUBMED, UPDATE, "updates from PubMed - a few times a day"),
        REDIVIS_RELOAD(DataSource.REDIVIS,RELOAD, "reload Redivis - monthly"),
        SFX_RELOAD(DataSource.SFX,RELOAD, "reload SFX MARC - daily"),
        SUL_RELOAD(DataSource.SUL,RELOAD, "reload SUL MARC - monthly"),
        SUL_UPDATE(DataSource.SUL, UPDATE, "update SUL MARC - daily"),
        UNDEFINED(DataSource.UNDEFINED, "undefined","for unit testing"),
        UNIT_TESTING(DataSource.LANE, "unit-test", "for unit testing"),
        PAUSE_DELETE(DataSource.FOLIO_DELETE, PAUSE,"pause/unpause FOLIO delete jobs"),
        PAUSE_LANE(DataSource.LANE, PAUSE,"pause/unpause Lane jobs"),
        PAUSE_PMC(DataSource.PMC, PAUSE,"pause/unpause PMC jobs"),
        PAUSE_PUBMED(DataSource.PUBMED, PAUSE,"pause/unpause PubMed jobs"),
        PAUSE_REDIVIS(DataSource.REDIVIS, PAUSE,"pause/unpause Redivis jobs"),
        PAUSE_SFX(DataSource.SFX, PAUSE,"pause/unpause SFX jobs"),
        PAUSE_SUL(DataSource.SUL, PAUSE,"pause/unpause SUL jobs"),
        PAUSE_UNDEFINED(DataSource.UNDEFINED, PAUSE, "pause for unit testing");

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

        Type(final DataSource dataSource, final String name, final String description) {
            this.name = name;
            this.description = description;
            this.dataSource = dataSource.toString();
        }

        public String getDataSource() {
            return this.dataSource;
        }

        public String getDescription() {
            return this.description;
        }

        public String getName() {
            return this.name;
        }

        public String getQualifiedName() {
            return this.dataSource + "/" + this.name;
        }
    }

    private static final String PAUSE = "pause";

    private static final String RELOAD = "reload";

    private static final String UPDATE = "update";

    // these don't get clickable interface links
    protected static final Collection<Type> LONG_RUNNING_JOBS = new ArrayList<>(
            List.of(Type.DELETES_FOLIO_ALL, Type.PUBMED_RELOAD, Type.SUL_RELOAD));

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
