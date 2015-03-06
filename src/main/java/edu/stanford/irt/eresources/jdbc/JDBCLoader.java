package edu.stanford.irt.eresources.jdbc;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.irt.eresources.Eresource;
import edu.stanford.irt.eresources.EresourceException;
import edu.stanford.irt.eresources.Loader;
import edu.stanford.irt.eresources.StartDate;

public class JDBCLoader implements Loader {

    private static final String CURRENT_ID_SQL = "SELECT ERESOURCE_ID_SEQ.CURRVAL FROM DUAL";

    private static final String DESCRIPTION_SQL = "SELECT DESCRIPTION FROM ERESOURCE WHERE ERESOURCE_ID = ? FOR UPDATE NOWAIT";

    private static final Logger LOG = LoggerFactory.getLogger(JDBCLoader.class);

    private static final String TEXT_PREFIX = "TEXT:";

    private static final String TEXT_SQL = "SELECT TEXT FROM ERESOURCE WHERE ERESOURCE_ID = ? FOR UPDATE NOWAIT";

    protected int count;

    private List<String> callStatements = Collections.emptyList();

    private Connection connection;

    private List<String> createStatements = Collections.emptyList();

    private DataSource dataSource;

    private PreparedStatement descStmt;

    private StartDate startDate;

    private Statement stmt;

    private PreparedStatement textStmt;

    private EresourceSQLTranslator translator;

    private String userName;

    public JDBCLoader(final DataSource dataSource, final EresourceSQLTranslator translator, final StartDate startDate) {
        this.dataSource = dataSource;
        this.translator = translator;
        this.startDate = startDate;
    }

    @Override
    public void load(final List<Eresource> eresources) {
        for (Eresource eresource : eresources) {
            try {
                insertEresource(eresource);
            } catch (IOException | SQLException e) {
                throw new EresourceException(e);
            }
        }
    }

    public void setCallStatements(final List<String> callStatements) {
        this.callStatements = callStatements;
    }

    public void setCreateStatements(final List<String> createStatements) {
        this.createStatements = createStatements;
    }

    public void setUserName(final String userName) {
        this.userName = userName;
    }

    protected Connection getConnection() {
        return this.connection;
    }

    protected void initializeStartDate(final StartDate startDate, final Connection connection) throws SQLException {
        startDate.initialize(new Date(0));
    }

    protected void insertEresource(final Eresource eresource) throws SQLException, IOException {
        List<String> insertSQLStatements = this.translator.getInsertSQL(eresource);
        for (Iterator<String> it = insertSQLStatements.iterator(); it.hasNext();) {
            String sql = it.next();
            if (sql.indexOf(TEXT_PREFIX) != 0 && sql.indexOf("DESCRIPTION:") != 0) {
                this.stmt.addBatch(sql);
                it.remove();
            }
        }
        this.stmt.executeBatch();
        for (String clob : insertSQLStatements) {
            insertClob(clob);
        }
        this.count++;
    }

    protected void postProcess() throws SQLException {
        LOG.info("handled " + this.count + " eresources");
        if (this.count > 0) {
            for (String call : this.callStatements) {
                if ((call.indexOf("{0}") > 0) && (null != this.userName)) {
                    call = MessageFormat.format(call, new Object[] { this.userName });
                }
                executeCall(call);
            }
        }
        this.descStmt.close();
        this.textStmt.close();
        this.stmt.close();
        this.connection.commit();
        this.connection.close();
    }

    protected void preProcess() throws SQLException {
        this.connection = this.dataSource.getConnection();
        this.connection.setAutoCommit(false);
        this.stmt = this.connection.createStatement();
        this.textStmt = this.connection.prepareStatement(TEXT_SQL);
        this.descStmt = this.connection.prepareStatement(DESCRIPTION_SQL);
        for (String create : this.createStatements) {
            try {
                this.stmt.execute(create);
                LOG.info(create);
            } catch (SQLException e) {
                int errorCode = e.getErrorCode();
                if ((942 != errorCode) && (1418 != errorCode) && (2289 != errorCode)) {
                    throw e;
                }
            }
        }
        initializeStartDate(this.startDate, this.connection);
    }

    private void executeCall(final String call) throws SQLException {
        try (CallableStatement callable = this.connection.prepareCall(call)) {
            callable.execute();
            LOG.info(call);
        }
    }

    private void insertClob(final String sql) throws SQLException, IOException {
        String id = null;
        try (ResultSet idRs = this.stmt.executeQuery(CURRENT_ID_SQL)) {
            idRs.next();
            id = idRs.getString(1);
        }
        @SuppressWarnings("resource")
        PreparedStatement ps = sql.indexOf(TEXT_PREFIX) == 0 ? this.textStmt : this.descStmt;
        ps.setString(1, id);
        try (ResultSet clobRs = ps.executeQuery()) {
            if (clobRs.next()) {
                Clob clob = clobRs.getClob(1);
                Reader reader = new StringReader(sql.indexOf(TEXT_PREFIX) == 0 ? sql.substring(5) : sql.substring(12));
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
