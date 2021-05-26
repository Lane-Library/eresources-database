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
        return this.jobManager.run(new Job("lane/reload", LocalDateTime.now()));
    }

    @Scheduled(cron = "${eresources.schedule.cron.laneUpdate}")
    public JobStatus laneUpdate() {
        return this.jobManager.run(new Job("lane/update", LocalDateTime.now()));
    }

    @Scheduled(cron = "${eresources.schedule.cron.pubmedDailyFtp}")
    public JobStatus pubmedDailyFtp() {
        return this.jobManager.run(new Job("pubmed/run-daily-ftp", LocalDateTime.now()));
    }

    @Scheduled(cron = "${eresources.schedule.cron.redivisReload}")
    public JobStatus redivisReload() {
        return this.jobManager.run(new Job("redivis/reload", LocalDateTime.now()));
    }

    @GetMapping(value = { "/solrLoader" }, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public JobStatus solrLoader(@RequestParam final String job) {
        if (JobManager.CLEAR_RUNNING_JOB.equals(job)) {
            return this.jobManager.clearRunningJob();
        }
        return this.jobManager.run(new Job(job, LocalDateTime.now()));
    }

    @Scheduled(cron = "${eresources.schedule.cron.sulReload}")
    public JobStatus sulReload() {
        return sulReload(LocalDate.now());
    }

    @Scheduled(cron = "${eresources.schedule.cron.sulUpdate}")
    public JobStatus sulUpdate() {
        return this.jobManager.run(new Job("sul/update", LocalDateTime.now()));
    }

    @GetMapping(value = { "*" }, produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String usage() {
        return "/solrLoader?job="
                + "<lane/update|lane/reload|pubmed/run-daily-ftp|redivis/reload|sul/update|sul/reload|clear-running-job>";
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
            return this.jobManager.run(new Job("sul/reload", LocalDateTime.now()));
        }
        return JobStatus.SKIPPED;
    }
}
