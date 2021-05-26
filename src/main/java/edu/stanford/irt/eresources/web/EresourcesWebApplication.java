package edu.stanford.irt.eresources.web;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.HttpEncodingAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import edu.stanford.irt.status.DefaultStatusService;
import edu.stanford.irt.status.StatusProvider;
import edu.stanford.irt.status.StatusService;

@Import({ 
    HttpEncodingAutoConfiguration.class, 
    WebMvcAutoConfiguration.class,
    HttpMessageConvertersAutoConfiguration.class, 
    ServletWebServerFactoryAutoConfiguration.class,
    DispatcherServletAutoConfiguration.class, 
    EresourcesController.class,
    StatusController.class})
public class EresourcesWebApplication {

    public static void main(final String[] args) {
        SpringApplication.run(EresourcesWebApplication.class, args);
    }

    @Value("${eresources.maxJobDurationInHours}")
    protected int maxJobDurationInHours;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public JobManager jobManager(@Value("${eresources.maxJobDurationInHours}") final int maxJobDurationInHours) {
        return new JobManager(maxJobDurationInHours);
    }

    @Bean
    public StatusProvider statusProvider(final JobManager jobManager) {
        return new EresourceStatusProvider(jobManager);
    }

    @Bean
    public StatusService statusService(@Value("${eresources.version}") final String version,
            final StatusProvider statusProvider) {
        return new DefaultStatusService("eresources", version, Collections.singletonList(statusProvider));
    }
}
