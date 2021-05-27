package edu.stanford.irt.eresources.web;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@EnableScheduling
public class EresourcesController {

    private static final int THIRD = 3;

    private JobManager jobManager;

    @Autowired
    public EresourcesController(final JobManager jobManager) {
        this.jobManager = jobManager;
    }

    @Scheduled(cron = "${eresources.schedule.cron.laneReload}")
    public JobStatus laneReload() {
        return this.jobManager.run(new Job(Job.Type.LANE_RELOAD, LocalDateTime.now()));
    }

    @Scheduled(cron = "${eresources.schedule.cron.laneUpdate}")
    public JobStatus laneUpdate() {
        return this.jobManager.run(new Job(Job.Type.LANE_UPDATE, LocalDateTime.now()));
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
        if (Job.Type.CANCEL_RUNNING_JOB.toString().equals(job)) {
            return this.jobManager.cancelRunningJob();
        }
        return this.jobManager.run(new Job(Job.Type.CANCEL_RUNNING_JOB, LocalDateTime.now()));
    }

    @Scheduled(cron = "${eresources.schedule.cron.sulReload}")
    public JobStatus sulReload() {
        return sulReload(LocalDate.now());
    }

    @Scheduled(cron = "${eresources.schedule.cron.sulUpdate}")
    public JobStatus sulUpdate() {
        return this.jobManager.run(new Job(Job.Type.SUL_UPDATE, LocalDateTime.now()));
    }

    @GetMapping(value = { "*" }, produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String usage() {
        StringBuilder sb = new StringBuilder("/solrLoader?job=<");
        for (Job.Type t : Job.Type.values()) {
            sb.append(t.getName());
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append('>');
        return sb.toString();
    }

    /**
     * @param date
     *            parameter for unit testing only
     * @return status of job
     */
    protected JobStatus sulReload(final LocalDate date) {
        var today = LocalDate.now();
        if (null != date) {
            today = date;
        }
        // SUL full export occurs every third Saturday of the month (and completes Sunday)
        // cron scheduling doesn't support this directly, so enforce "day-of-week-in-month" check here
        // by making sure yesterday was 3rd Saturday in the month
        LocalDate yesterday = today.minus(1, ChronoUnit.DAYS);
        LocalDate thirdSaturdayOfMonth = yesterday.with(TemporalAdjusters.dayOfWeekInMonth(THIRD, DayOfWeek.SATURDAY));
        if (yesterday.isEqual(thirdSaturdayOfMonth)) {
            return this.jobManager.run(new Job(Job.Type.SUL_RELOAD, LocalDateTime.now()));
        }
        return JobStatus.SKIPPED;
    }
}
