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

    private int count = 0;

    private DataSource dataSource;

    private String deleteEresource;

    private String deleteLink;

    private String deleteMesh;

    private String deleteSubset;

    private String deleteType;

    private String deleteVersion;

    private String getID;

    private Map<String, Set<Integer>> ids;

    private boolean keepGoing = true;

    private BlockingQueue<Eresource> queue;

    private String selectSQL;

    private String tablePrefix;

    public DeleteEresourceHandler(final DataSource dataSource, final BlockingQueue<Eresource> queue) {
        this(dataSource, queue, "");
    }

    public DeleteEresourceHandler(final DataSource dataSource, final BlockingQueue<Eresource> queue,
            final String tablePrefix) {
        this.dataSource = dataSource;
        this.queue = queue;
        this.tablePrefix = tablePrefix;
        this.ids = new HashMap<String, Set<Integer>>();
        this.deleteEresource = DELETE_FROM + this.tablePrefix + "ERESOURCE WHERE ERESOURCE_ID = ";
        this.deleteVersion = DELETE_FROM + this.tablePrefix + "VERSION WHERE ERESOURCE_ID = ";
        this.deleteLink = DELETE_FROM + this.tablePrefix + "LINK WHERE ERESOURCE_ID = ";
        this.deleteType = DELETE_FROM + this.tablePrefix + "TYPE WHERE ERESOURCE_ID = ";
        this.deleteSubset = DELETE_FROM + this.tablePrefix + "SUBSET WHERE ERESOURCE_ID = ";
        this.deleteMesh = DELETE_FROM + this.tablePrefix + "MESH WHERE ERESOURCE_ID = ";
        this.getID = "SELECT ERESOURCE_ID FROM " + this.tablePrefix
                + "ERESOURCE WHERE RECORD_TYPE = ? and RECORD_ID = ?";
        this.selectSQL = "SELECT RECORD_TYPE, RECORD_ID FROM " + this.tablePrefix + "ERESOURCE";
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
        try (Connection conn = this.dataSource.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(this.selectSQL)) {
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
        synchronized (this.queue) {
            while (!this.queue.isEmpty() || this.keepGoing) {
                try {
                    Eresource eresource = this.queue.poll(1, TimeUnit.SECONDS);
                    if (eresource != null) {
                        Set<Integer> set = this.ids.get(eresource.getRecordType());
                        set.remove(Integer.valueOf(eresource.getRecordId()));
                    }
                } catch (InterruptedException e) {
                    throw new EresourceException(
                            "\nstop=" + this.keepGoing + "\nempty=" + this.queue.isEmpty(), e);
                }
            }
            try (Connection conn = this.dataSource.getConnection();
                    Statement stmt = conn.createStatement();
                    PreparedStatement pstmt = conn.prepareStatement(this.getID)) {
                for (Entry<String, Set<Integer>> entry : this.ids.entrySet()) {
                    String recordType = entry.getKey();
                    for (Integer recordId : entry.getValue()) {
                        pstmt.setString(1, recordType);
                        pstmt.setInt(2, recordId.intValue());
                        try (ResultSet rs = pstmt.executeQuery()) {
                            while (rs.next()) {
                                int id = rs.getInt(1);
                                stmt.addBatch(this.deleteEresource + id);
                                stmt.addBatch(this.deleteVersion + id);
                                stmt.addBatch(this.deleteLink + id);
                                stmt.addBatch(this.deleteType + id);
                                stmt.addBatch(this.deleteSubset + id);
                                stmt.addBatch(this.deleteMesh + id);
                                stmt.executeBatch();
                                this.count++;
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                throw new EresourceException(e);
            }
            this.queue.notifyAll();
        }
    }

    @Override
    public void stop() {
        this.keepGoing = false;
    }
}
