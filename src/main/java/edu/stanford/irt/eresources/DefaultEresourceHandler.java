package edu.stanford.irt.eresources;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

public class DefaultEresourceHandler implements EresourceHandler {

    private static final String DESCRIPTION_SQL = "SELECT DESCRIPTION FROM ERESOURCE WHERE ERESOURCE_ID = ? FOR UPDATE NOWAIT";

    private static final String TEXT_SQL = "SELECT TEXT FROM ERESOURCE WHERE ERESOURCE_ID = ? FOR UPDATE NOWAIT";

    private int count = 0;

    private DataSource dataSource;

    private PreparedStatement descStmt;

    private volatile boolean keepGoing = true;

    private BlockingQueue<DatabaseEresource> queue;

    private Statement stmt;

    private PreparedStatement textStmt;

    private EresourceSQLTranslator translator;

    public DefaultEresourceHandler(final DataSource dataSource, final BlockingQueue<DatabaseEresource> queue,
            final EresourceSQLTranslator translator) {
        this.dataSource = dataSource;
        this.queue = queue;
        this.translator = translator;
    }

    public int getCount() {
        return this.count;
    }

    public void handleEresource(final DatabaseEresource eresource) {
        for (Version version : eresource.getVersions()) {
            for (Link link : version.getLinks()) {
                ((DatabaseLink)link).setVersion(version);
            }
        }
        this.queue.add(eresource);
        this.count++;
    }

    public void run() {
        try (Connection conn = this.dataSource.getConnection();
                Statement stmt = conn.createStatement();
                PreparedStatement textStmt = conn.prepareStatement(TEXT_SQL);
                PreparedStatement descStmt = conn.prepareStatement(DESCRIPTION_SQL)) {
            this.stmt = stmt;
            this.textStmt = textStmt;
            this.descStmt = descStmt;
            synchronized (this.queue) {
                while (!this.queue.isEmpty() || this.keepGoing) {
                    try {
                        DatabaseEresource eresource = this.queue.poll(1, TimeUnit.SECONDS);
                        if (eresource != null) {
                            insertEresource(eresource);
                        }
                    } catch (InterruptedException | IOException e) {
                        throw new EresourceDatabaseException("\nstop=" + this.keepGoing + "\nempty=" + this.queue.isEmpty(), e);
                    }
                }
                this.queue.notifyAll();
            }
        } catch (SQLException e) {
            throw new EresourceDatabaseException(e);
        }
    }

    public void stop() {
        this.keepGoing = false;
    }

    private void insertClob(final String sql) throws SQLException, IOException {
        String id = null;
        try (ResultSet idRs = this.stmt.executeQuery("SELECT ERESOURCE_ID_SEQ.CURRVAL FROM DUAL")) {
            idRs.next();
            id = idRs.getString(1);
        }
        @SuppressWarnings("resource")
        PreparedStatement ps = sql.indexOf("TEXT:") == 0 ? this.textStmt : this.descStmt;
        ps.setString(1, id);
        try (ResultSet clobRs = ps.executeQuery()) {
            if (clobRs.next()) {
                Clob clob = clobRs.getClob(1);
                Reader reader = new StringReader(sql.indexOf("TEXT:") == 0 ? sql.substring(5) : sql.substring(12));
                Writer writer = clob.setCharacterStream(1);
                char[] buffer = new char[1024];
                int size = 0;
                while ((size = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, size);
                }
                writer.close();
                reader.close();
            }
        }
    }

    protected Statement getStatement() {
        return this.stmt;
    }

    protected void insertEresource(final DatabaseEresource eresource) throws SQLException, IOException {
        List<String> insertSQLStatements = this.translator.getInsertSQL(eresource);
        for (Iterator<String> it = insertSQLStatements.iterator(); it.hasNext();) {
            String sql = it.next();
            if (sql.indexOf("TEXT:") != 0 && sql.indexOf("DESCRIPTION:") != 0) {
                this.stmt.addBatch(sql);
                it.remove();
            }
        }
        this.stmt.executeBatch();
        for (String clob : insertSQLStatements) {
            insertClob(clob);
        }
    }
}
