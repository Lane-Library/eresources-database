package edu.stanford.irt.eresources;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

public class DeleteEresourceHandler implements EresourceHandler {

    private static final String DELETE_FROM = "DELETE FROM ";

    private static final String DELETE_FROM_ERESOURCE = DELETE_FROM + "ERESOURCE WHERE ERESOURCE_ID = ";

    private static final String DELETE_FROM_VERSION = DELETE_FROM + "VERSION WHERE ERESOURCE_ID = ";

    private static final String DELETE_FROM_LINK = DELETE_FROM + "LINK WHERE ERESOURCE_ID = ";

    private static final String DELETE_FROM_TYPE = DELETE_FROM + "TYPE WHERE ERESOURCE_ID = ";

    private static final String DELETE_FROM_MESH = DELETE_FROM + "MESH WHERE ERESOURCE_ID = ";
    
    private static final String GET_ID = "SELECT ERESOURCE_ID FROM ERESOURCE WHERE RECORD_TYPE = ? and RECORD_ID = ?";
    
    private static final String SELECT = "SELECT RECORD_TYPE, RECORD_ID FROM ERESOURCE";

    private int count = 0;

    private DataSource dataSource;

    private Map<String, Set<Integer>> ids;

    private boolean keepGoing = true;

    private BlockingQueue<Eresource> queue;

    public DeleteEresourceHandler(final DataSource dataSource, final BlockingQueue<Eresource> queue) {
        this.dataSource = dataSource;
        this.queue = queue;
        this.ids = new HashMap<String, Set<Integer>>();
    }

    protected DeleteEresourceHandler() {
    }

    @Override
    public int getCount() {
        return this.count;
    }

    @Override
    public void handleEresource(final Eresource eresource) {
        try {
            this.queue.put(eresource);
        } catch (InterruptedException e) {
            throw new EresourceDatabaseException(e);
        }
    }

    @Override
    public void run() {
        try (Connection conn = this.dataSource.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(SELECT)) {
            while (rs.next()) {
                String recordType = rs.getString("RECORD_TYPE");
                Integer recordId = Integer.valueOf(rs.getInt("RECORD_ID"));
                if (!this.ids.containsKey(recordType)) {
                    this.ids.put(recordType, new HashSet<Integer>());
                }
                this.ids.get(recordType).add(recordId);
            }
        } catch (SQLException e) {
            throw new EresourceDatabaseException(e);
        }
        synchronized (this.queue) {
            while (!this.queue.isEmpty() || this.keepGoing) {
                try {
                    Eresource eresource = this.queue.poll(1, TimeUnit.SECONDS);
                    if (eresource != null) {
                        Set<Integer> set = this.ids.get(eresource.getRecordType());
                        set.remove(Integer.valueOf(eresource.getRecordId()));
                    }
                } catch (InterruptedException e) {
                    throw new EresourceDatabaseException(
                            "\nstop=" + this.keepGoing + "\nempty=" + this.queue.isEmpty(), e);
                }
            }
            try (Connection conn = this.dataSource.getConnection();
                    Statement stmt = conn.createStatement();
                    PreparedStatement pstmt = conn.prepareStatement(GET_ID)) {
                for (Entry<String, Set<Integer>> entry : this.ids.entrySet()) {
                    String recordType = entry.getKey();
                    for (Integer recordId : entry.getValue()) {
                        pstmt.setString(1, recordType);
                        pstmt.setInt(2, recordId.intValue());
                        try (ResultSet rs = pstmt.executeQuery()) {
                            while (rs.next()) {
                                int id = rs.getInt(1);
                                stmt.addBatch(DELETE_FROM_ERESOURCE + id);
                                stmt.addBatch(DELETE_FROM_VERSION + id);
                                stmt.addBatch(DELETE_FROM_LINK + id);
                                stmt.addBatch(DELETE_FROM_TYPE + id);
                                stmt.addBatch(DELETE_FROM_MESH + id);
                                stmt.executeBatch();
                                this.count++;
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                throw new EresourceDatabaseException(e);
            }
            this.queue.notifyAll();
        }
    }

    @Override
    public void stop() {
        this.keepGoing = false;
    }
}
