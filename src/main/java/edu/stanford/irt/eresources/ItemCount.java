package edu.stanford.irt.eresources;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

public class ItemCount {

    /*
     * this query from: http://stackoverflow.com/questions/7745609/sql-select-only-rows-with-max-value-on-a-column
     */
    private static final String AVAILABLE_QUERY = "select count(item_status_1.item_id) "
            + "from lmldb.bib_item bi, lmldb.item_status item_status_1 "
            + "left outer join lmldb.item_status item_status_2 " + "on (item_status_1.item_id = item_status_2.item_id "
            + "and item_status_1.item_status_date < item_status_2.item_status_date) "
            + "where item_status_2.item_id is null " + "and bi.item_id = item_status_1.item_id "
            + "and item_status_1.item_status = 1 " + "and bi.bib_id = ?";

    private static final String TOTAL_QUERY = "select count(distinct item_status.item_id) "
            + "from lmldb.bib_item, lmldb.item_status " + "where bib_item.item_id = item_status.item_id "
            + "and bib_item.bib_id = ?";

    private DataSource dataSource;

    public ItemCount(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public int[] itemCount(final int bibId) {
        int[] count = new int[2];
        count[0] = getCount(bibId, TOTAL_QUERY);
        if (count[0] > 0) {
            count[1] = getCount(bibId, AVAILABLE_QUERY);
        }
        return count;
    }

    public int[] itemCount(final String bibId) {
        return itemCount(Integer.parseInt(bibId));
    }

    private int getCount(final int bibId, final String query) {
        int total = 0;
        try (Connection conn = this.dataSource.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, bibId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                total = rs.getInt(1);
            }
            rs.close();
        } catch (SQLException e) {
            throw new EresourceException(e);
        }
        return total;
    }
}
