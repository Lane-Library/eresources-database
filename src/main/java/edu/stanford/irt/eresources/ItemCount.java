package edu.stanford.irt.eresources;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

public class ItemCount {

    /*
     * this query from: http://stackoverflow.com/questions/7745609/sql-select-only-rows-with-max-value-on-a-column
     */
    private static final String AVAILABLE_QUERY = "SELECT bi.bib_id, COUNT(*) FROM lmldb.bib_item bi, "
            + "  lmldb.item_status item_status_1 LEFT OUTER JOIN lmldb.item_status item_status_2 "
            + "ON (item_status_1.item_id          = item_status_2.item_id "
            + "AND item_status_1.item_status_date < item_status_2.item_status_date) "
            + "WHERE item_status_2.item_id       IS NULL "
            + "AND bi.item_id                     = item_status_1.item_id " + "AND item_status_1.item_status      = 1 "
            + "GROUP BY bi.bib_id";

    private static final String TOTAL_QUERY = "SELECT bib_id, COUNT(DISTINCT item_status.item_id) "
            + "FROM lmldb.bib_item, lmldb.item_status " + "WHERE bib_item.item_id = item_status.item_id "
            + "GROUP BY bib_id";

    private Map<String, Integer> available = new HashMap<String, Integer>();

    private DataSource dataSource;

    private Map<String, Integer> total = new HashMap<String, Integer>();

    public ItemCount(final DataSource dataSource) {
        this.dataSource = dataSource;
        init(this.total, TOTAL_QUERY);
        init(this.available, AVAILABLE_QUERY);
    }

    private int getCount(final String bibId, final Map<String, Integer> map) {
        Integer count = new Integer(0);
        if (map.containsKey(bibId)) {
            count = map.get(bibId);
        }
        return count.intValue();
    }

    private void init(final Map<String, Integer> map, final String query) {
        try (Connection conn = this.dataSource.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            ;
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                map.put(rs.getString(1), Integer.valueOf(rs.getInt(2)));
            }
            rs.close();
        } catch (SQLException e) {
            throw new EresourceException(e);
        }
    }

    public int[] itemCount(final String bibId) {
        int[] count = new int[2];
        count[0] = getCount(bibId, this.total);
        if (count[0] > 0) {
            count[1] = getCount(bibId, this.available);
        }
        return count;
    }
}
