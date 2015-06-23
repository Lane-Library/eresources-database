package edu.stanford.irt.eresources.marc;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executor;

import javax.sql.DataSource;

import edu.stanford.irt.eresources.EresourceException;

public abstract class EresourceInputStream extends PipedInputStream implements Runnable {

    private DataSource dataSource;

    private Executor executor;

    private PipedOutputStream output;

    public EresourceInputStream(final DataSource dataSource, final Executor executor) {
        this.dataSource = dataSource;
        this.executor = executor;
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
        try (Connection conn = this.dataSource.getConnection();
                PreparedStatement getBibStmt = conn.prepareStatement(getBibQuery());
                PreparedStatement getMfhdStmt = conn.prepareStatement(getMfhdQuery());
                OutputStream out = this.output) {
            Map<String, Collection<String>> ids = getIds(conn);
            for (Entry<String, Collection<String>> entry : ids.entrySet()) {
                String bibId = entry.getKey();
                getBibStmt.setString(1, bibId);
                try (ResultSet rs = getBibStmt.executeQuery();) {
                    sendBytes(rs);
                }
                for (String mfhdId : entry.getValue()) {
                    getMfhdStmt.setString(1, mfhdId);
                    try (ResultSet rs = getMfhdStmt.executeQuery();) {
                        sendBytes(rs);
                    }
                }
            }
        } catch (SQLException | IOException e) {
            throw new EresourceException(e);
        }
    }

    protected abstract String getBibQuery();

    protected abstract String getMfhdQuery();

    protected abstract String getSelectIDListSQL();

    protected void prepareListStatement(final PreparedStatement stmt) throws SQLException {
    }

    private Map<String, Collection<String>> getIds(final Connection conn) throws SQLException {
        try (PreparedStatement getListStmt = conn.prepareStatement(getSelectIDListSQL())) {
            prepareListStatement(getListStmt);
            Map<String, Collection<String>> ids = new HashMap<String, Collection<String>>();
            String lastBib = null;
            Collection<String> mfhds = null;
            try (ResultSet rs = getListStmt.executeQuery()) {
                int columnCount = rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    String currentBib = rs.getString(1);
                    if (columnCount > 1) {
                        String currentMfhd = rs.getString(2);
                        if (!currentBib.equals(lastBib)) {
                            lastBib = currentBib;
                            mfhds = new ArrayList<String>();
                            ids.put(currentBib, mfhds);
                        }
                        mfhds.add(currentMfhd);
                    } else {
                        ids.put(currentBib, Collections.emptySet());
                    }
                }
            }
            return ids;
        }
    }

    private void sendBytes(final ResultSet rs) throws SQLException, IOException {
        while (rs.next()) {
            this.output.write(rs.getBytes(2));
        }
    }
}
