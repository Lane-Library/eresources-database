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

    private int count = 0;

    private String currentIdSQL;

    private DataSource dataSource;

    private String descriptionSQL;

    private PreparedStatement descStmt;

    private volatile boolean keepGoing = true;

    private BlockingQueue<Eresource> queue;

    private Statement stmt;

    private String textSQL;

    private PreparedStatement textStmt;

    private EresourceSQLTranslator translator;

    public DefaultEresourceHandler(final DataSource dataSource, final BlockingQueue<Eresource> queue,
            final EresourceSQLTranslator translator) {
        this(dataSource, queue, translator, "");
    }

    public DefaultEresourceHandler(final DataSource dataSource, final BlockingQueue<Eresource> queue,
            final EresourceSQLTranslator translator, final String tablePrefix) {
        this.dataSource = dataSource;
        this.queue = queue;
        this.translator = translator;
        this.currentIdSQL = "SELECT " + tablePrefix + "ERESOURCE_ID_SEQ.CURRVAL FROM DUAL";
        this.descriptionSQL = "SELECT DESCRIPTION FROM " + tablePrefix
                + "ERESOURCE WHERE ERESOURCE_ID = ? FOR UPDATE NOWAIT";
        this.textSQL = "SELECT TEXT FROM " + tablePrefix + "ERESOURCE WHERE ERESOURCE_ID = ? FOR UPDATE NOWAIT";
    }

    protected DefaultEresourceHandler() {
    }

    @Override
    public int getCount() {
        return this.count;
    }

    @Override
    public void handleEresource(final Eresource eresource) {
        for (Version version : eresource.getVersions()) {
            for (Link link : version.getLinks()) {
                link.setVersion(version);
            }
        }
        try {
            this.queue.put(eresource);
        } catch (InterruptedException e) {
            throw new EresourceException(e);
        }
        this.count++;
    }

    @Override
    public void run() {
//        try (Connection conn = this.dataSource.getConnection();
//                Statement stmt = conn.createStatement();
//                PreparedStatement textStmt = conn.prepareStatement(this.textSQL);
//                PreparedStatement descStmt = conn.prepareStatement(this.descriptionSQL)) {
//            this.stmt = stmt;
//            this.textStmt = textStmt;
//            this.descStmt = descStmt;
        try {
            synchronized (this.queue) {
                while (!this.queue.isEmpty() || this.keepGoing) {
                    try {
                        Eresource eresource = this.queue.poll(1, TimeUnit.SECONDS);
                        if (eresource != null) {
                            insertEresource(eresource);
                        }
                    } catch (InterruptedException | IOException e) {
                        throw new EresourceException("\nstop=" + this.keepGoing + "\nempty="
                                + this.queue.isEmpty(), e);
                    }
                }
                this.queue.notifyAll();
            }
        } catch (SQLException e) {
            throw new EresourceException(e);
        }
    }

    @Override
    public void stop() {
        this.keepGoing = false;
    }

    protected Statement getStatement() {
        return this.stmt;
    }

    protected void insertEresource(final Eresource eresource) throws SQLException, IOException {
        List<String> insertSQLStatements = this.translator.getInsertSQL(eresource);
        for (Iterator<String> it = insertSQLStatements.iterator(); it.hasNext();) {
            String sql = it.next();
            if (sql.indexOf("TEXT:") != 0 && sql.indexOf("DESCRIPTION:") != 0) {
//                this.stmt.addBatch(sql);
                System.out.println(sql);
                it.remove();
            }
        }
//        this.stmt.executeBatch();
        for (String clob : insertSQLStatements) {
//            insertClob(clob);
            System.out.println(clob);
        }
    }

    private void insertClob(final String sql) throws SQLException, IOException {
        String id = null;
        try (ResultSet idRs = this.stmt.executeQuery(this.currentIdSQL)) {
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
}
