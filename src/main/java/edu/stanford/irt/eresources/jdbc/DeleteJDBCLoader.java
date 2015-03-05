package edu.stanford.irt.eresources.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.sql.DataSource;

import edu.stanford.irt.eresources.Eresource;
import edu.stanford.irt.eresources.EresourceException;
import edu.stanford.irt.eresources.Loader;

public class DeleteJDBCLoader implements Loader {

    private static final String DELETE_ERESOURCE = "DELETE FROM ERESOURCE WHERE ERESOURCE_ID = ";

    private static final String DELETE_LINK = "DELETE FROM LINK WHERE ERESOURCE_ID = ";

    private static final String DELETE_MESH = "DELETE FROM MESH WHERE ERESOURCE_ID = ";

    private static final String DELETE_SUBSET = "DELETE FROM SUBSET WHERE ERESOURCE_ID = ";

    private static final String DELETE_TYPE = "DELETE FROM TYPE WHERE ERESOURCE_ID = ";

    private static final String DELETE_VERSION = "DELETE FROM VERSION WHERE ERESOURCE_ID = ";

    private static final String GET_ID = "SELECT ERESOURCE_ID FROM ERESOURCE WHERE RECORD_TYPE = ? and RECORD_ID = ?";

    private static final String SELECT = "SELECT RECORD_TYPE, RECORD_ID FROM ERESOURCE";

    private DataSource dataSource;

    private Map<String, Set<Integer>> ids;

    public DeleteJDBCLoader(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

//    protected void doLoad(BlockingQueue<Eresource> queue) throws InterruptedException {
//        Logger log = LoggerFactory.getLogger(getClass());
//        log.info("enter run();");
//        // get record ids from eresources
//        getRecordIds();
//            // get all existing ids
//            subtractExistingIds(queue);
//            // deleted the remainder from eresources
//            removeRemaining();
//        log.info("return run();");
//    }

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
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new EresourceException(e);
        }
    }

//    private void subtractExistingIds(BlockingQueue<Eresource> queue) throws InterruptedException {
//        while (true) {
//                Eresource eresource = queue.take();
//                if (eresource == Eresource.EMPTY) {
//                    break;
//                } else {
//                    Set<Integer> set = this.ids.get(eresource.getRecordType());
//                    set.remove(Integer.valueOf(eresource.getRecordId()));
//                }
//        }
//    }

    @Override
    public void load(List<Eresource> eresources) {
        throw new UnsupportedOperationException();
    }
}
