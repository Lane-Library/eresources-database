package edu.stanford.irt.eresources;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import edu.stanford.irt.eresources.jdbc.DBUpdate;

public abstract class EresourcesETLApp {

    private boolean killPrevious;

    private Collection<ETLProcessor<?>> processors = Collections.<ETLProcessor<?>> emptyList();

    private StartDate startDate;

    public EresourcesETLApp(final List<ETLProcessor<?>> processors, final StartDate startDate) {
        this(processors, startDate, false);
    }

    public EresourcesETLApp(final List<ETLProcessor<?>> processors, final StartDate startDate, final boolean killPrevious) {
        this.processors = processors;
        this.startDate = startDate;
        this.killPrevious = killPrevious;
    }

    public static void main(final String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("edu/stanford/irt/eresources/"
                + args[0] + ".xml");
        EresourcesETLApp app = context.getBean("ETL", EresourcesETLApp.class);
        ThreadPoolTaskExecutor executor = context.getBean("executor", ThreadPoolTaskExecutor.class);
        try {
            app.run();
        } finally {
            executor.shutdown();
        }
    }

    public void run() {
        managePIDFile();
        initializeStartDate(this.startDate);
        preProcess();
        for (ETLProcessor<?> processor : this.processors) {
            processor.process();
        }
        postProcess();
    }

    protected void initializeStartDate(final StartDate startDate) {
        startDate.initialize(new Date(0));
    }

    protected void postProcess() {
    }

    protected void preProcess() {
    }

    private void managePIDFile() {
        final File pidFile = new File("eresources.pid");
        String pid = null;
        try {
            if (!pidFile.createNewFile()) {
                BufferedReader reader = new BufferedReader(new FileReader(pidFile));
                pid = reader.readLine();
                reader.close();
                if (this.killPrevious) {
                    LoggerFactory.getLogger(EresourcesETLApp.class).warn("pid " + pid + " exists, killing . . .");
                    Runtime.getRuntime().exec(new String[] { "kill", pid });
                } else {
                    IllegalStateException e = new IllegalStateException("pid " + pid + " already running");
                    LoggerFactory.getLogger(DBUpdate.class).error(e.getMessage());
                    throw e;
                }
            }
            pid = ManagementFactory.getRuntimeMXBean().getName();
            int index = pid.indexOf('@');
            pid = pid.substring(0, index);
            try (FileOutputStream out = new FileOutputStream(pidFile)) {
                out.write(pid.getBytes("UTF-8"));
            }
            Runtime.getRuntime().addShutdownHook(new Thread() {

                @Override
                public void run() {
                    if (!pidFile.delete()) {
                        throw new IllegalStateException("failed to delete pid file");
                    }
                }
            });
        } catch (IOException e) {
            throw new EresourceException(e);
        }
    }
}
