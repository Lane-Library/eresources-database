package edu.stanford.irt.eresources.web;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EresourcesWebApplicationTest {

    private EresourcesWebApplication application;

    @Test
    void jobManager() {
        assertNotNull(this.application.jobManager(0));
    }

    @Test
    void propertySourcesPlaceholderConfigurer() {
        assertNotNull(EresourcesWebApplication.propertySourcesPlaceholderConfigurer());
    }

    @BeforeEach
    void setUp() {
        this.application = new EresourcesWebApplication();
    }

    @Test
    void statusProvider() {
        assertNotNull(this.application.statusProvider(null));
    }

    @Test
    void statusService() {
        assertNotNull(this.application.statusService(null, null));
    }
}
