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

    private static final String DELETE_ERESOURCE = "DELETE FROM ERESOURCE WHERE ERESOURCE_ID = ";

    private static final String DELETE_LINK = "DELETE FROM LINK WHERE ERESOURCE_ID = ";

    private static final String DELETE_MESH = "DELETE FROM MESH WHERE ERESOURCE_ID = ";

    private static final String DELETE_SUBSET = "DELETE FROM SUBSET WHERE ERESOURCE_ID = ";

    private static final String DELETE_TYPE = "DELETE FROM TYPE WHERE ERESOURCE_ID = ";

    private static final String DELETE_VERSION = "DELETE FROM VERSION WHERE ERESOURCE_ID = ";

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
            throw new EresourceException(e);
        }
    }

    @Override
    public void run() {
        // get record ids from eresources
        getRecordIds();
        synchronized (this.queue) {
            // get all existing ids
            subtractExistingIds();
            // deleted the remainder from eresources
            removeRemaining();
            // TODO: I don't think removeRemaining needs to be synchronized
            this.queue.notifyAll();
        }
    }

    @Override
    public void stop() {
        this.keepGoing = false;
    }

    private void getRecordIds() {
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
            throw new EresourceException(e);
        }
    }

    private void removeRemaining() {
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
                            stmt.addBatch(DELETE_ERESOURCE + id);
                            stmt.addBatch(DELETE_VERSION + id);
                            stmt.addBatch(DELETE_LINK + id);
                            stmt.addBatch(DELETE_TYPE + id);
                            stmt.addBatch(DELETE_SUBSET + id);
                            stmt.addBatch(DELETE_MESH + id);
                            stmt.executeBatch();
                            this.count++;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new EresourceException(e);
        }
    }

    private void subtractExistingIds() {
        while (!this.queue.isEmpty() || this.keepGoing) {
            try {
                Eresource eresource = this.queue.poll(1, TimeUnit.SECONDS);
                if (eresource != null) {
                    Set<Integer> set = this.ids.get(eresource.getRecordType());
                    set.remove(Integer.valueOf(eresource.getRecordId()));
                }
            } catch (InterruptedException e) {
                throw new EresourceException("\nstop=" + this.keepGoing + "\nempty=" + this.queue.isEmpty(), e);
            }
        }
    }
}
