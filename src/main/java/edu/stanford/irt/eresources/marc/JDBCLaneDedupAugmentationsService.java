package edu.stanford.irt.eresources.marc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import edu.stanford.irt.eresources.EresourceDatabaseException;

public class JDBCLaneDedupAugmentationsService implements AugmentationsService {

    private static final int JDBC_FETCH_SIZE = 10_000;

    private static final String SPACE_UNION = "    UNION\n";

    private static final String SQL = "WITH DEDUP AS (\n"
            + "    SELECT bib_id, 'ocolc' as KEY, NORMAL_HEADING as VALUE FROM LMLDB.BIB_INDEX WHERE INDEX_CODE = '035A' AND DISPLAY_HEADING LIKE '(OCoLC)%'\n"
            + SPACE_UNION
            + "    SELECT bib_id, 'lccntrln' as KEY, DISPLAY_HEADING as VALUE FROM LMLDB.BIB_INDEX WHERE INDEX_CODE = '010A'\n"
            + SPACE_UNION
            + "    SELECT DISTINCT record_id as BIB_ID, 'catkey' as KEY, regexp_replace(link,'.*/view/') AS VALUE FROM lmldb.elink_index WHERE record_type = 'B' AND LINK LIKE '%searchworks.stanford.edu/view/%' AND regexp_replace(link,'.*/view/') IS NOT NULL\n"
            + SPACE_UNION
            + "    SELECT BIB_MASTER.BIB_ID, 'title_date' as KEY, TRIM(BIB_TEXT.TITLE_BRIEF) || SUBSTR(FIELD_008,8,8) as VALUE FROM LMLDB.BIB_TEXT, LMLDB.BIB_MASTER WHERE BIB_MASTER.BIB_ID = BIB_TEXT.BIB_ID AND SUPPRESS_IN_OPAC !='Y'"
            + SPACE_UNION
            + "    SELECT DISTINCT record_id as BIB_ID, 'url' as KEY, regexp_replace(link,'(^https?://|/$)') AS VALUE FROM lmldb.elink_index WHERE record_type = 'B'\n"
            + ")\n" + "SELECT DISTINCT DEDUP.BIB_ID, KEY, VALUE FROM DEDUP, LMLDB.BIB_MASTER\n"
            + "WHERE DEDUP.BIB_ID=BIB_MASTER.BIB_ID\n" + "AND BIB_MASTER.SUPPRESS_IN_OPAC!='Y'\n" + "ORDER BY BIB_ID";

    private DataSource dataSource;

    public JDBCLaneDedupAugmentationsService(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Map<String, String> buildAugmentations() {
        Map<String, String> augmentations = new HashMap<>();
        try (Connection conn = this.dataSource.getConnection();
                PreparedStatement getListStmt = conn.prepareStatement(SQL);
                ResultSet rs = getListStmt.executeQuery()) {
            rs.setFetchSize(JDBC_FETCH_SIZE);
            while (rs.next()) {
                String key = rs.getString("KEY");
                String value = rs.getString("VALUE");
                if (null != key && null != value) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(key);
                    sb.append("->");
                    sb.append(value);
                    augmentations.put(sb.toString(), "");
                }
            }
        } catch (SQLException e) {
            throw new EresourceDatabaseException(e);
        }
        return augmentations;
    }
}
