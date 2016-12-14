package edu.stanford.irt.eresources;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.concurrent.Executor;

import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.lane.catalog.CatalogSQLException;
import edu.stanford.lane.catalog.VoyagerInputStream2;

public class EresourceInputStream extends PipedInputStream implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(EresourceInputStream.class);

    private DataSource dataSource;

    private Executor executor;

    private PipedOutputStream output;

    private InputStream sqlInputStream;

    private Timestamp startDate;

    public EresourceInputStream(final DataSource dataSource, final Executor executor,
            final InputStream sqlInputStream) {
        this.dataSource = dataSource;
        this.executor = executor;
        this.sqlInputStream = sqlInputStream;
    }

    @Override
    public int read() throws IOException {
        if (null == this.output) {
            this.output = new PipedOutputStream(this);
            this.executor.execute(this);
        }
        return super.read();
    }

    @Override
    public void run() {
        long now = System.currentTimeMillis();
        try (InputStream input = new VoyagerInputStream2(this.dataSource, prepareSql(this.sqlInputStream), 1);
                OutputStream ops = this.output) {
            IOUtils.copy(input, ops);
            LOG.info("took " + (System.currentTimeMillis() - now) + "ms to execute query: "
                    + prepareSql(this.sqlInputStream));
        } catch (CatalogSQLException | IOException e) {
            throw new EresourceDatabaseException(e);
        }
    }

    public void setStartDate(final Timestamp startDate) {
        if (null == startDate) {
            throw new IllegalArgumentException("null startDate");
        }
        this.startDate = startDate;
    }

    private String prepareSql(final InputStream sqlInputStream) throws IOException {
        String sql = IOUtils.toString(sqlInputStream, StandardCharsets.UTF_8);
        sql = sql.replaceAll("\\{timestamp\\}", this.startDate.toString());
        return sql;
    }
}
