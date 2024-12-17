package edu.stanford.irt.eresources.web;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    private static final String LI_CLOSE = "</li>";

    private static final String LI_OPEN = "<li>";

    // these don't get clickable interface links
    protected static final Collection<Type> LONG_RUNNING_JOBS = new ArrayList<>(
            List.of(Type.DELETES_FOLIO_ALL, Type.PUBMED_RELOAD, Type.SUL_RELOAD));

    private JobManager jobManager;

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

    @Scheduled(cron = "${eresources.schedule.cron.sfxReload}")
    public JobStatus sfxReload() {
        return this.jobManager.run(new Job(Job.Type.SFX_RELOAD, LocalDateTime.now()));
    }

    @GetMapping(value = { "/solrLoader" }, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public JobStatus solrLoader(@RequestParam("job") final String job,
            @RequestParam(value = "updateOverride", required = false) final String updateOverride) {
        if (Job.Type.CANCEL_RUNNING_JOBS.getQualifiedName().equals(job)) {
            return this.jobManager.cancelRunningJobs();
        }
        return this.jobManager.run(new Job(Job.Type.fromString(job), LocalDateTime.now(), updateOverride));
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
    public String usage() {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head>");
        sb.append("<title>eresources indexing</title>");
        sb.append("<meta http-equiv=\"refresh\" content=\"30\">");
        sb.append("<style>h1,h2{margin-bottom:0;}</style>");
        sb.append("</head><body>");
        sb.append("<iframe name=\"hidden\" style=\"display:none\"></iframe>");
        sb.append("<h1>eresources indexing</h1>");
        sb.append("<i>this page refreshes automatically</i>");
        sb.append("<h2>status</h2>");
        sb.append(
                "<iframe name=\"status\" src=\"status.txt\" style=\"border:none;margin:-15px 0;width:100%;max-height:100px;overflow:scroll;\"></iframe>");
        sb.append("<h2>jobs</h2>");
        sb.append("<pre><strong>bold jobs</strong> are currently running \n");
        sb.append("* unlinked jobs run longer than an hour \n");
        sb.append("** pubmed reload takes 8+ hours and requires baseline data from NCBI</pre>");
        sb.append("<i>updateOverride</i> argument is supported on lane and sul jobs\n");
        sb.append("<ul>");
        List<Job> runningJobs = this.jobManager.getRunningJobs();
        List<String> pausedDataSources = this.jobManager.getPausedDataSources();
        for (Job.Type t : Job.Type.values()) {
            if (Job.Type.UNDEFINED.equals(t) || Job.Type.UNIT_TESTING.equals(t) || Job.Type.PAUSE_UNDEFINED.equals(t)) {
                continue;
            }
            String jobQualifiedName = t.getQualifiedName();
            boolean running = runningJobs.stream()
                    .anyMatch((final Job j) -> t.getQualifiedName().equals(j.getType().getQualifiedName()));
            if (running) {
                jobQualifiedName = "<strong>" + jobQualifiedName + "</strong>";
            }
            if (Job.LONG_RUNNING_JOBS.contains(t)) {
                sb.append(LI_OPEN);
                sb.append(jobQualifiedName);
                sb.append(": ");
                sb.append(t.getDescription());
                sb.append(LI_CLOSE);
            } else if ("pause".equals(t.getName())) {
                String playOrPause = pausedDataSources.contains(t.getDataSource()) ? "&#x23F5;" : "&#x23F8;";
                sb.append(LI_OPEN);
                sb.append("<a target=\"hidden\" href=\"solrLoader?job=");
                sb.append(t.getQualifiedName());
                sb.append("\">");
                sb.append(playOrPause);
                sb.append("  ");
                sb.append(t.getDataSource());
                sb.append("</a>: ");
                sb.append(t.getDescription());
                sb.append(LI_CLOSE);
            } else {
                sb.append(LI_OPEN);
                sb.append("<a target=\"hidden\" href=\"solrLoader?job=");
                sb.append(t.getQualifiedName());
                sb.append("\">");
                sb.append(jobQualifiedName);
                sb.append("</a>: ");
                sb.append(t.getDescription());
                sb.append(LI_CLOSE);
            }
        }
        sb.append("</ul>");
        sb.append("</body></html>");
        return sb.toString();
    }
}
