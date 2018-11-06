package edu.stanford.irt.eresources.marc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import edu.stanford.irt.eresources.EresourceDatabaseException;
import edu.stanford.irt.eresources.TextParserHelper;

public class JDBCLaneDedupAugmentationsService implements AugmentationsService {

    private static final int JDBC_FETCH_SIZE = 10_000;

    private static final String SPACE_UNION = "    UNION\n";

    private static final String SQL = "WITH DEDUP AS (\n" + "    SELECT bib_id, '"
            + LaneDedupAugmentation.KEY_OCLC_CONTROL_NUMBER
            + "' as KEY, NORMAL_HEADING as VALUE FROM LMLDB.BIB_INDEX WHERE INDEX_CODE = '035A' AND DISPLAY_HEADING LIKE '(OCoLC)%'\n"
            + SPACE_UNION + "    SELECT bib_id, '" + LaneDedupAugmentation.KEY_LC_CONTROL_NUMBER
            + "' as KEY, DISPLAY_HEADING as VALUE FROM LMLDB.BIB_INDEX WHERE INDEX_CODE = '010A'\n" + SPACE_UNION
            + "    SELECT DISTINCT record_id as BIB_ID, '" + LaneDedupAugmentation.KEY_CATKEY
            + "' as KEY, regexp_replace(link,'.*/view/') AS VALUE FROM lmldb.elink_index WHERE record_type = 'B' "
            + "AND LINK LIKE '%searchworks.stanford.edu/view/%' AND regexp_replace(link,'.*/view/') IS NOT NULL\n"
            + SPACE_UNION + "    SELECT BIB_MASTER.BIB_ID, '" + LaneDedupAugmentation.KEY_TITLE_DATE
            + "' as KEY, TRIM(BIB_TEXT.TITLE_BRIEF) || SUBSTR(FIELD_008,8,8) as VALUE "
            + "FROM LMLDB.BIB_TEXT, LMLDB.BIB_MASTER WHERE BIB_MASTER.BIB_ID = BIB_TEXT.BIB_ID AND SUPPRESS_IN_OPAC !='Y'"
            + SPACE_UNION + "    SELECT DISTINCT record_id as BIB_ID, '" + LaneDedupAugmentation.KEY_URL
            + "' as KEY, regexp_replace(link,'(^https?://|/$)') AS VALUE FROM lmldb.elink_index WHERE record_type = 'B'\n"
            + SPACE_UNION + "    SELECT DISTINCT BIB_ID, '" + LaneDedupAugmentation.KEY_ISBN
            + "' as KEY, DISPLAY_HEADING as VALUE FROM LMLDB.BIB_INDEX WHERE INDEX_CODE IN('020A','020N','020R')\n"
            + SPACE_UNION + "    SELECT DISTINCT BIB_ID, '" + LaneDedupAugmentation.KEY_ISSN
            + "' as KEY, DISPLAY_HEADING as VALUE FROM LMLDB.BIB_INDEX WHERE INDEX_CODE IN('022A','022L')\n" + ")\n"
            + "SELECT DISTINCT DEDUP.BIB_ID, KEY, VALUE FROM DEDUP, LMLDB.BIB_MASTER\n"
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
                if (LaneDedupAugmentation.KEY_ISBN.equals(key) || LaneDedupAugmentation.KEY_ISSN.equals(key)) {
                    // check for validity as well? e.g. bibid 8118 has $35.00 value ...
                    value = TextParserHelper.cleanIsxn(value);
                }
                if (null != key && null != value) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(key);
                    sb.append(LaneDedupAugmentation.SEPARATOR);
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
