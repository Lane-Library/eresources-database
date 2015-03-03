package edu.stanford.irt.eresources.jdbc;

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

import javax.sql.DataSource;

import edu.stanford.irt.eresources.Eresource;
import edu.stanford.irt.eresources.EresourceException;
import edu.stanford.irt.eresources.Loader;

public class JDBCLoader implements Loader {

    private static final String CURRENT_ID_SQL = "SELECT ERESOURCE_ID_SEQ.CURRVAL FROM DUAL";

    private static final String DESCRIPTION_SQL = "SELECT DESCRIPTION FROM ERESOURCE WHERE ERESOURCE_ID = ? FOR UPDATE NOWAIT";

    private static final String TEXT_PREFIX = "TEXT:";

    private static final String TEXT_SQL = "SELECT TEXT FROM ERESOURCE WHERE ERESOURCE_ID = ? FOR UPDATE NOWAIT";

    private DataSource dataSource;

    private PreparedStatement descStmt;

    private Statement stmt;

    private PreparedStatement textStmt;

    private EresourceSQLTranslator translator;

    public JDBCLoader(final DataSource dataSource, final EresourceSQLTranslator translator) {
        this.dataSource = dataSource;
        this.translator = translator;
    }

    protected Statement getStatement() {
        return this.stmt;
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

    @Override
    public void load(Eresource... eresources) {
        try (Connection conn = this.dataSource.getConnection();
                Statement s = conn.createStatement();
                PreparedStatement t = conn.prepareStatement(TEXT_SQL);
                PreparedStatement d = conn.prepareStatement(DESCRIPTION_SQL)) {
            this.stmt = s;
            this.textStmt = t;
            this.descStmt = d;
            for (Eresource eresource : eresources) {
                    try {
                            insertEresource(eresource);
                    } catch (IOException e) {
                        throw new EresourceException(e);
                    }
            }
        } catch (SQLException e) {
            throw new EresourceException(e);
        }
    }
}
