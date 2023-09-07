package edu.stanford.irt.eresources.web;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.stanford.irt.eresources.web.Job.Type;

@Controller
@EnableScheduling
public class EresourcesController {

    private JobManager jobManager;

    // these don't get clickable interface links
    protected static final Collection<Type> LONG_RUNNING_JOBS = new ArrayList<>(
            List.of(Type.DELETES_FOLIO_ALL, Type.PUBMED_RELOAD, Type.SUL_RELOAD));

    @Autowired
    public EresourcesController(final JobManager jobManager) {
        this.jobManager = jobManager;
    }

    @Scheduled(cron = "${eresources.schedule.cron.deletesFolioHourly}")
    public JobStatus folioDeletesRoutine() {
        return this.jobManager.run(new Job(Job.Type.DELETES_FOLIO_HOURLY, LocalDateTime.now()));
    }

    @Scheduled(cron = "${eresources.schedule.cron.laneMarcUpdate}")
    public JobStatus laneMarcUpdate() {
        return this.jobManager.run(new Job(Job.Type.LANE_FOLIO_UPDATE, LocalDateTime.now()));
    }

    @Scheduled(cron = "${eresources.schedule.cron.laneReload}")
    public JobStatus laneReload() {
        return this.jobManager.run(new Job(Job.Type.LANE_RELOAD, LocalDateTime.now()));
    }

    @Scheduled(cron = "${eresources.schedule.cron.laneWebsitesUpdate}")
    public JobStatus laneWebsitesUpdate() {
        return this.jobManager.run(new Job(Job.Type.LANE_WEBSITES_UPDATE, LocalDateTime.now()));
    }

    @Scheduled(cron = "${eresources.schedule.cron.pmcReload}")
    public JobStatus pmcReload() {
        return this.jobManager.run(new Job(Job.Type.PMC_RELOAD, LocalDateTime.now()));
    }

    @Scheduled(cron = "${eresources.schedule.cron.pubmedDailyFtp}")
    public JobStatus pubmedDailyFtp() {
        return this.jobManager.run(new Job(Job.Type.PUBMED_UPDATE, LocalDateTime.now()));
    }

    @Scheduled(cron = "${eresources.schedule.cron.redivisReload}")
    public JobStatus redivisReload() {
        return this.jobManager.run(new Job(Job.Type.REDIVIS_RELOAD, LocalDateTime.now()));
    }

    @GetMapping(value = { "/solrLoader" }, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public JobStatus solrLoader(@RequestParam final String job) {
        if (Job.Type.CANCEL_RUNNING_JOBS.getQualifiedName().equals(job)) {
            return this.jobManager.cancelRunningJobs();
        }
        return this.jobManager.run(new Job(Job.Type.fromString(job), LocalDateTime.now()));
    }

    @Scheduled(cron = "${eresources.schedule.cron.sulReload}")
    public JobStatus sulReload() {
        return this.jobManager.run(new Job(Job.Type.SUL_RELOAD, LocalDateTime.now()));
    }

    @Scheduled(cron = "${eresources.schedule.cron.sulUpdate}")
    public JobStatus sulUpdate() {
        return this.jobManager.run(new Job(Job.Type.SUL_UPDATE, LocalDateTime.now()));
    }

    @GetMapping(value = { "*" }, produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String usage(@Value("${eresources.version}") final String version) {
        StringBuilder sb = new StringBuilder();
        sb.append("<h1>eresources indexing</h1>");
        sb.append("status: <a href=\"status.txt\">txt</a> ");
        sb.append("<a href=\"status.json\">json</a>");
        sb.append(" [" + version + "]");
        sb.append("<h2>jobs</h2>");
        sb.append("<pre>* unlinked jobs run longer than an hour \n");
        sb.append("** pubmed reload takes 8+ hours and requires baseline data from NCBI</pre>");
        sb.append("<ul>");
        for (Job.Type t : Job.Type.values()) {
            if (Job.LONG_RUNNING_JOBS.contains(t)) {
                sb.append("<li>");
                sb.append(t.getQualifiedName());
                sb.append(": ");
                sb.append(t.getDescription());
                sb.append("</li>");
            } else if (!Job.Type.UNDEFINED.equals(t) && !Job.Type.UNIT_TESTING.equals(t)) {
                sb.append("<li><a href=\"solrLoader?job=");
                sb.append(t.getQualifiedName());
                sb.append("\">");
                sb.append(t.getQualifiedName());
                sb.append("</a>: ");
                sb.append(t.getDescription());
                sb.append("</li>");
            }
        }
        sb.append("</ul>");
        return sb.toString();
    }
}
