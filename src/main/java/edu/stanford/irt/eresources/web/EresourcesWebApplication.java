package edu.stanford.irt.eresources.web;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.HttpEncodingAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.stanford.irt.eresources.SolrLoader;
import edu.stanford.irt.status.DefaultStatusService;
import edu.stanford.irt.status.RuntimeMXBeanStatusProvider;

@RestController
@EnableScheduling
@Import({ HttpEncodingAutoConfiguration.class, WebMvcAutoConfiguration.class,
        HttpMessageConvertersAutoConfiguration.class, ServletWebServerFactoryAutoConfiguration.class,
        DispatcherServletAutoConfiguration.class })
public class EresourcesWebApplication {

    private static final Logger log = LoggerFactory.getLogger(EresourcesWebApplication.class);

    private static final int THIRD = 3;

    protected boolean jobIsRunning;

    @Value("${eresources.maxJobDurationInHours}")
    protected int maxJobDurationInHours;

    private String jobRunningName = "none";

    private LocalDateTime jobStart = LocalDateTime.now();

    public static void main(final String[] args) {
        SpringApplication.run(EresourcesWebApplication.class, args);
    }

    @Scheduled(cron = "${eresources.schedule.cron.laneReload}")
    public String laneReload() {
        return solrLoader("lane/reload");
    }

    @Scheduled(cron = "${eresources.schedule.cron.laneUpdate}")
    public String laneUpdate() {
        return solrLoader("lane/update");
    }

    @Scheduled(cron = "${eresources.schedule.cron.pubmedDailyFtp}")
    public String pubmedDailyFtp() {
        return solrLoader("pubmed/run-daily-ftp");
    }

    @Scheduled(cron = "${eresources.schedule.cron.redivisReload}")
    public String redivisReload() {
        return solrLoader("redivis/reload");
    }

    @GetMapping("/solrLoader")
    public String solrLoader(@RequestParam final String job) {
        if (this.jobIsRunning) {
            log.warn("solrLoader: {} failed to jobStart; previous {} job sill jobIsRunning", job, this.jobRunningName);
            return "WARN";
        }
        this.jobStart = LocalDateTime.now();
        this.jobIsRunning = true;
        this.jobRunningName = job;
        String[] args = { job };
        try {
            SolrLoader.main(args);
        } catch (Exception e) {
            log.error("solrLoader exception ", e);
            this.jobIsRunning = false;
            return "ERROR";
        }
        final long later = ChronoUnit.MILLIS.between(this.jobStart, LocalDateTime.now());
        log.info("solrLoader: {}; executed in {}ms", job, later);
        this.jobIsRunning = false;
        return "OK";
    }

    @GetMapping(value = "/status.txt", produces = "text/plain; charset=utf-8")
    public ResponseEntity<String> status(@Value("${eresources.version}") final String version) {
        StringBuilder statusMessage = new StringBuilder(new DefaultStatusService("eresources", version,
                Collections.singletonList(new RuntimeMXBeanStatusProvider())).getStatus().toString());
        if (this.jobIsRunning
                && LocalDateTime.now().isAfter(this.jobStart.plus(Duration.ofHours(this.maxJobDurationInHours)))) {
            String msg = String.format("long-running job (%s) running for %s hours", this.jobRunningName,
                    ChronoUnit.HOURS.between(this.jobStart, LocalDateTime.now()));
            log.error(msg);
            statusMessage.append("\n").append(msg);
            return new ResponseEntity<>(statusMessage.toString(), HttpStatus.GATEWAY_TIMEOUT);
        }
        return new ResponseEntity<>(statusMessage.toString(), HttpStatus.OK);
    }

    /**
     * @param date
     *            parameter for unit testing only
     * @return status of job
     */
    @Scheduled(cron = "${eresources.schedule.cron.sulReload}")
    public String sulReload(final LocalDate date) {
        LocalDate today = LocalDate.now();
        if (null != date) {
            today = date;
        }
        // SUL full export occurs every third Saturday of the month (and completes Sunday)
        // cron scheduling doesn't support this directly, so enforce "day-of-week-in-month" check here
        // by making sure yesterday was 3rd Saturday in the month
        LocalDate yesterday = today.minus(1, ChronoUnit.DAYS);
        LocalDate thirdSaturdayOfMonth = yesterday.with(TemporalAdjusters.dayOfWeekInMonth(THIRD, DayOfWeek.SATURDAY));
        if (yesterday.isEqual(thirdSaturdayOfMonth)) {
            return solrLoader("sul/reload");
        }
        return "didn't run";
    }

    @Scheduled(cron = "${eresources.schedule.cron.sulUpdate}")
    public String sulUpdate() {
        return solrLoader("sul/update");
    }

    @GetMapping("*")
    public String usage() {
        return "/solrLoader?job="
                + "&lt;lane/update|lane/reload|pubmed/run-daily-ftp|redivis/reload|sul/update|sul/reload&gt;";
    }
}
