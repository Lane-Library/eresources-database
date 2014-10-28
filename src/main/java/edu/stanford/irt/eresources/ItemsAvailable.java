package edu.stanford.irt.eresources;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;


public class ItemsAvailable {
    
    /*
     * this query from:
     * http://stackoverflow.com/questions/7745609/sql-select-only-rows-with-max-value-on-a-column
     */
    private static final String ITEM_COUNT_QUERY =
            "select count(item_status_1.item_id) " +
            "from lmldb.bib_item bi, lmldb.item_status item_status_1 " +
            "left outer join lmldb.item_status item_status_2 " +
            "on (item_status_1.item_id = item_status_2.item_id " +
            "and item_status_1.item_status_date < item_status_2.item_status_date) " +
            "where item_status_2.item_id is null " +
            "and bi.item_id = item_status_1.item_id " +
            "and bi.bib_id = ?";

    private DataSource dataSource;

    public ItemsAvailable(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    public boolean itemsAvailable(int bibId) {
        boolean itemsAvailable = false;
        try (Connection conn = this.dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(ITEM_COUNT_QUERY)) {;
             pstmt.setInt(1, bibId);
             ResultSet rs = pstmt.executeQuery();
             if (rs.next()) {
                 itemsAvailable = rs.getInt(1) > 0;
             }
             rs.close();
        } catch (SQLException e) {
            throw new EresourceDatabaseException(e);
        }
        return itemsAvailable;
    }
}
