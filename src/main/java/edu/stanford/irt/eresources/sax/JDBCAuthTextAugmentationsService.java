package edu.stanford.irt.eresources.sax;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.irt.eresources.EresourceDatabaseException;

public class JDBCAuthTextAugmentationsService implements AugmentationsService {

    private static final int JDBC_FETCH_SIZE = 10_000;

    private static final Logger LOG = LoggerFactory.getLogger(JDBCAuthTextAugmentationsService.class);

    // verified by DM: people records won't have 450's and MeSH records won't have 400's
    private static final String SQL = "SELECT concat('Z',auth_id) AS AID, LMLDB.GETALLTAGS(auth_id,'A','400 450',2) AS ADATA FROM LMLDB.AUTH_MASTER";

    private DataSource dataSource;

    public JDBCAuthTextAugmentationsService(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Map<String, String> buildAugmentations() {
        LOG.debug("start building authority augmentation object");
        Map<String, String> augmentations = new HashMap<>();
        try (Connection conn = this.dataSource.getConnection();
                PreparedStatement getListStmt = conn.prepareStatement(SQL);
                ResultSet rs = getListStmt.executeQuery()) {
            rs.setFetchSize(JDBC_FETCH_SIZE);
            while (rs.next()) {
                String authId = rs.getString("AID");
                byte[] bytes = rs.getBytes("ADATA");
                if (null != bytes) {
                    augmentations.put(authId, parseSubfieldAs(new String(bytes, StandardCharsets.UTF_8)));
                }
            }
            LOG.debug("completed building authority augmentation object");
        } catch (SQLException e) {
            throw new EresourceDatabaseException(e);
        }
        return augmentations;
    }

    /**
     * example input: 450:8 :$1--$aF01.829.500$xMorals // 450:8 :$1--$aK01.752.566$xMorals // 450:8 :$eIncludes
     * broader:$aMorality$3M0014053$4T026943$91977-03-17
     *
     * @param marc
     *            A {@code String} of pseudo marc from Voyager
     * @return A {@code String} of subfield a data
     */
    private String parseSubfieldAs(final String marc) {
        StringBuilder sb = new StringBuilder();
        String[] fields = marc.split("// ");
        for (String field : fields) {
            sb.append(field.replaceFirst(".*\\$a([^\\$]+)(\\$.*)?", "$1"));
            sb.append(' ');
        }
        return sb.toString().trim();
    }
}
