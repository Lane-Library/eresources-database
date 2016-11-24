package edu.stanford.irt.eresources;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executor;

import javax.sql.DataSource;

public abstract class EresourceInputStream extends PipedInputStream implements Runnable {

    private DataSource dataSource;

    private Executor executor;

    private PipedOutputStream output;

    private Timestamp startDate;

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
        try (Connection conn = this.dataSource.getConnection();
                PreparedStatement getListStmt = conn.prepareStatement(getSelectIDListSQL());
                PreparedStatement getBibStmt = conn.prepareStatement(getBibQuery());
                PreparedStatement getMfhdStmt = conn.prepareStatement(getMfhdQuery());
                OutputStream ops = this.output) {
            prepareListStatement(getListStmt);
            Map<String, Collection<String>> ids = getIdMap(getListStmt);
            for (Entry<String, Collection<String>> entry : ids.entrySet()) {
                String bibId = entry.getKey();
                getBibStmt.setString(1, bibId);
                executeQueryAndWriteBytes(getBibStmt, ops);
                for (String mfhdId : entry.getValue()) {
                    getMfhdStmt.setString(1, mfhdId);
                    executeQueryAndWriteBytes(getMfhdStmt, ops);
                }
            }
        } catch (SQLException | IOException e) {
            throw new EresourceDatabaseException(e);
        }
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

    public void setStartDate(final Timestamp startDate) {
        if (null == startDate) {
            throw new IllegalArgumentException("null startDate");
        }
        this.startDate = startDate;
    }

    protected abstract String getBibQuery();

    protected abstract String getMfhdQuery();

    protected abstract String getSelectIDListSQL();

    protected void prepareListStatement(final PreparedStatement stmt) throws SQLException {
        char[] queryString = getSelectIDListSQL().toCharArray();
        int qmarkCount = 0;
        for (char element : queryString) {
            if (element == '?') {
                qmarkCount++;
            }
        }
        for (int i = 1; i <= qmarkCount; i++) {
            stmt.setTimestamp(i, this.startDate);
        }
    }

    private void executeQueryAndWriteBytes(final PreparedStatement pStatement, final OutputStream ops)
            throws SQLException, IOException {
        try (ResultSet rs = pStatement.executeQuery();) {
            while (rs.next()) {
                ops.write(rs.getBytes(2));
            }
        }
    }

    private Map<String, Collection<String>> getIdMap(final PreparedStatement getListStmt) throws SQLException {
        Map<String, Collection<String>> ids = new HashMap<>();
        try (ResultSet rs = getListStmt.executeQuery()) {
            int columnCount = rs.getMetaData().getColumnCount();
            while (rs.next()) {
                String bibId = rs.getString(1);
                if (columnCount > 1) {
                    Collection<String> mfhds = ids.get(bibId);
                    if (mfhds == null) {
                        mfhds = new ArrayList<>();
                        ids.put(bibId, mfhds);
                    }
                    mfhds.add(rs.getString(2));
                } else {
                    ids.put(bibId, Collections.<String> emptySet());
                }
            }
        }
        return ids;
    }
}
