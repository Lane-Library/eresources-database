package edu.stanford.irt.eresources;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.Executor;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class DBLoader {

    private Collection<String> callStatements = Collections.<String> emptyList();

    private Collection<String> createStatements = Collections.<String> emptyList();

    private DataSource dataSource;

    private Executor executor;

    private EresourceHandler handler;

    private boolean killPrevious;

    private Logger log = LoggerFactory.getLogger(getClass());

    private Collection<AbstractEresourceProcessor> processors = Collections.<AbstractEresourceProcessor> emptyList();

    private Queue<DatabaseEresource> queue;

    private String userName;

    private String version;
    
    public static void main(final String[] args) throws SQLException, IOException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                "edu/stanford/irt/eresources/db-" + args[0] + ".xml");
        DBLoader loader = (DBLoader) context.getBean("dbLoader");
        ThreadPoolTaskExecutor executor = (ThreadPoolTaskExecutor) context.getBean("executor");
        try {
            loader.load();
        } finally {
            executor.shutdown();
        }
    }

    public void load() throws SQLException, IOException {
        this.log.info(this.version + " starting up");
        managePIDFile();
        try (Connection conn = this.dataSource.getConnection(); Statement stmt = conn.createStatement();) {
            conn.setAutoCommit(false);
            for (String create : this.createStatements) {
                try {
                    this.log.info(create);
                    stmt.execute(create);
                } catch (SQLException e) {
                    int errorCode = e.getErrorCode();
                    if ((942 != errorCode) && (1418 != errorCode) && (2289 != errorCode)) {
                        throw e;
                    }
                }
            }
            Date updated = getUpdatedDate(stmt);
            this.executor.execute(this.handler);
            for (AbstractEresourceProcessor processor : this.processors) {
                processor.setStartDate(updated);
                processor.process();
            }
            this.handler.stop();
            synchronized (this.queue) {
                while (!this.queue.isEmpty()) {
                    try {
                        this.queue.wait();
                    } catch (InterruptedException e) {
                        throw new EresourceDatabaseException(e);
                    }
                }
            }
            conn.commit();
            int count = this.handler.getCount();
            if (count > 0) {
                for (String call : this.callStatements) {
                    if ((call.indexOf("{0}") > 0) && (null != this.userName)) {
                        call = MessageFormat.format(call, new Object[] { this.userName });
                    }
                    CallableStatement callable = conn.prepareCall(call);
                    this.log.info(call);
                    callable.execute();
                    callable.close();
                }
            }
            this.log.info("handled " + count + " eresources.");
        }
    }

    public void setCallStatements(final Collection<String> callStatements) {
        this.callStatements = callStatements;
    }

    public void setCreateStatements(final Collection<String> createStatements) {
        if (null == createStatements) {
            throw new IllegalArgumentException("null createStatements");
        }
        this.createStatements = createStatements;
    }

    public void setDataSource(final DataSource dataSource) {
        if (null == dataSource) {
            throw new IllegalArgumentException("null dataSource");
        }
        this.dataSource = dataSource;
    }

    public void setExecutor(final Executor executor) {
        this.executor = executor;
    }

    public void setHandler(final EresourceHandler handler) {
        this.handler = handler;
    }

    public void setKillPrevious(final boolean killPrevious) {
        this.killPrevious = killPrevious;
    }

    public void setProcessors(final Collection<AbstractEresourceProcessor> processors) {
        if (null == processors) {
            throw new IllegalArgumentException("null processors");
        }
        this.processors = processors;
    }

    public void setQueue(final Queue<DatabaseEresource> queue) {
        this.queue = queue;
    }

    public void setUserName(final String userName) {
        this.userName = userName;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    private void managePIDFile() throws IOException {
        final File pidFile = new File("eresources.pid");
        String pid = null;
        if (!pidFile.createNewFile()) {
            BufferedReader reader = new BufferedReader(new FileReader(pidFile));
            pid = reader.readLine();
            reader.close();
            if (this.killPrevious) {
                LoggerFactory.getLogger(DBReload.class).warn("pid " + pid + " exists, killing . . .");
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
            out.write(pid.getBytes());
        }
        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                if (!pidFile.delete()) {
                    throw new IllegalStateException("failed to delete pid file");
                }
            }
        });
    }

    protected Date getUpdatedDate(final Statement stmt) throws SQLException {
        return new Date(0);
    }
}
