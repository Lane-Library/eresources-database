package edu.stanford.irt.eresources.web;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EresourcesWebApplicationTest {

    private EresourcesWebApplication application;

    @Test
    public void jobManager() {
        assertNotNull(this.application.jobManager(0));
    }

    @Test
    public void propertySourcesPlaceholderConfigurer() {
        assertNotNull(EresourcesWebApplication.propertySourcesPlaceholderConfigurer());
    }

    @BeforeEach
    public void setUp() {
        this.application = new EresourcesWebApplication();
    }

    @Test
    public void statusProvider() {
        assertNotNull(this.application.statusProvider(null));
    }

    @Test
    public void statusService() {
        assertNotNull(this.application.statusService(null, null));
    }
}
