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

    private Map<Integer, Integer> available = new HashMap<Integer, Integer>();

    private DataSource dataSource;

    private Map<Integer, Integer> total = new HashMap<Integer, Integer>();

    public ItemCount(final DataSource dataSource) {
        this.dataSource = dataSource;
        init(this.total, TOTAL_QUERY);
        init(this.available, AVAILABLE_QUERY);
    }

    private int getCount(final int bibId, final Map<Integer, Integer> map) {
        Integer count = new Integer(0);
        if (map.containsKey(Integer.valueOf(bibId))) {
            count = map.get(Integer.valueOf(bibId));
        }
        return count.intValue();
    }

    private void init(final Map<Integer, Integer> map, final String query) {
        try (Connection conn = this.dataSource.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            ;
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                map.put(Integer.valueOf(rs.getInt(1)), Integer.valueOf(rs.getInt(2)));
            }
            rs.close();
        } catch (SQLException e) {
            throw new EresourceDatabaseException(e);
        }
    }

    public int[] itemCount(final int bibId) {
        int[] count = new int[2];
        count[0] = getCount(bibId, this.total);
        if (count[0] > 0) {
            count[1] = getCount(bibId, this.available);
        }
        return count;
    }
}
