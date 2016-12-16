package edu.stanford.irt.eresources;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            // case 117007: "missing", "withdrawn", etc. items should not be marked as available
            + "AND bi.item_id NOT IN "
            + "    (SELECT item_id FROM lmldb.item_status WHERE item_status >= '12' and item_status <= '17')"
            + "GROUP BY bi.bib_id";

    private static final int FETCH_SIZE = 100000;

    private static final Logger LOG = LoggerFactory.getLogger(ItemCount.class);

    private static final String TOTAL_QUERY = "SELECT bib_id, COUNT(DISTINCT item_status.item_id) "
            + "FROM lmldb.bib_item, lmldb.item_status " + "WHERE bib_item.item_id = item_status.item_id "
            + "GROUP BY bib_id";

    private Map<Integer, Integer> availables;

    private DataSource dataSource;

    private Map<Integer, Integer> totals;

    public ItemCount(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public int[] itemCount(final int bibId) {
        if (this.totals == null) {
            this.initialize();
        }
        int[] itemCount = new int[2];
        itemCount[0] = getCount(bibId, this.totals);
        if (itemCount[0] > 0) {
            itemCount[1] = getCount(bibId, this.availables);
        }
        return itemCount;
    }

    private Map<Integer, Integer> createItemCountMap(final String query) {
        LOG.info("building item count map: " + query);
        long now = System.currentTimeMillis();
        Map<Integer, Integer> map = new HashMap<>();
        // set fetch size here
        try (Connection conn = this.dataSource.getConnection();
                Statement statement = conn.createStatement();
                ResultSet rs = statement.executeQuery(query);) {
            rs.setFetchSize(FETCH_SIZE);
            while (rs.next()) {
                map.put(Integer.valueOf(rs.getInt(1)), Integer.valueOf(rs.getInt(2)));
            }
        } catch (SQLException e) {
            throw new EresourceDatabaseException(e);
        }
        LOG.info("took " + (System.currentTimeMillis() - now) + "ms to execute query: " + query);
        return map;
    }

    private int getCount(final int bibId, final Map<Integer, Integer> map) {
        Integer count = map.get(Integer.valueOf(bibId));
        return count != null ? count.intValue() : 0;
    }

    private void initialize() {
        this.totals = createItemCountMap(TOTAL_QUERY);
        this.availables = createItemCountMap(AVAILABLE_QUERY);
    }
}
