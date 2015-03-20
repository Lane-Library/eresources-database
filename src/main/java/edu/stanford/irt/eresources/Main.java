package edu.stanford.irt.eresources;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    private boolean killPrevious;

    private Collection<ETLProcessor<?>> processors = Collections.<ETLProcessor<?>> emptyList();

    private String version;

    public Main(final List<ETLProcessor<?>> processors, final String version) {
        this(processors, version, false);
    }

    public Main(final List<ETLProcessor<?>> processors, final String version, final boolean killPrevious) {
        this.processors = processors;
        this.version = version;
        this.killPrevious = killPrevious;
    }

    public static void main(final String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("edu/stanford/irt/eresources/"
                + args[0] + ".xml");
        Main app = context.getBean("ETL", Main.class);
        ThreadPoolTaskExecutor executor = context.getBean("executor", ThreadPoolTaskExecutor.class);
        try {
            app.run();
            context.close();
        } catch (Exception e) {
            executor.shutdown();
            throw e;
        }
    }

    public void run() {
        LOG.info(this.version + " starting up");
        managePIDFile();
        for (ETLProcessor<?> processor : this.processors) {
            processor.process();
        }
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
                    LoggerFactory.getLogger(Main.class).warn("pid " + pid + " exists, killing . . .");
                    Runtime.getRuntime().exec(new String[] { "kill", pid });
                } else {
                    IllegalStateException e = new IllegalStateException("pid " + pid + " already running");
                    LoggerFactory.getLogger(Main.class).error(e.getMessage());
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
