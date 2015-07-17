package edu.stanford.irt.eresources.marc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import edu.stanford.irt.eresources.EresourceException;
import edu.stanford.irt.eresources.ItemCount;

public class ItemCounter {

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

    private Map<String, Integer> availables;

    private DataSource dataSource;

    private Map<String, Integer> totals;

    public ItemCounter(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public ItemCount itemCount(final String bibId) {
        if (this.totals == null) {
            this.initialize();
        }
        int total = getCount(bibId, this.totals);
        int available = 0;
        if (total > 0) {
            available = getCount(bibId, this.availables);
        }
        return new ItemCount(total, available);
    }

    private Map<String, Integer> createItemCountMap(final String query) {
        Map<String, Integer> map = new HashMap<String, Integer>();
        try (Connection conn = this.dataSource.getConnection(); Statement statement = conn.createStatement()) {
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()) {
                map.put(rs.getString(1), Integer.valueOf(rs.getInt(2)));
            }
            rs.close();
        } catch (SQLException e) {
            throw new EresourceException(e);
        }
        return map;
    }

    private int getCount(final String bibId, final Map<String, Integer> map) {
        Integer count = map.get(bibId);
        return count != null ? count.intValue() : 0;
    }

    private void initialize() {
        this.totals = createItemCountMap(TOTAL_QUERY);
        this.availables = createItemCountMap(AVAILABLE_QUERY);
    }
}
