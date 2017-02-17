package edu.stanford.irt.eresources.sax;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

public class AuthTextAugmentation {

    private static final String AUGMENTATION_FILE = "auth-augmentations.obj";

    private static final int JDBC_FETCH_SIZE = 10000;

    private static final Logger LOG = LoggerFactory.getLogger(AuthTextAugmentation.class);

    private static final int ONE_DAY = 1000 * 60 * 60 * 24;

    // verified by DM: people records won't have 450's and MeSH records won't have 400's
    private static final String SQL = "SELECT concat('Z',auth_id), LMLDB.GETALLTAGS(auth_id,'A','400 450',2) FROM LMLDB.AUTH_MASTER";

    private Map<String, String> augmentations;

    private DataSource dataSource;

    @SuppressWarnings("unchecked")
    public String getAuthAugmentations(final String controlNumber) {
        if (null == this.augmentations) {
            this.augmentations = new HashMap<>();
            File objFile = new File(AUGMENTATION_FILE);
            if (!objFile.exists() || objFile.lastModified() < System.currentTimeMillis() - ONE_DAY) {
                buildAugmentations();
            } else {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(objFile))) {
                    this.augmentations = (Map<String, String>) ois.readObject();
                } catch (IOException e) {
                    LOG.error(e.getMessage(), e);
                } catch (ClassNotFoundException e) {
                    throw new EresourceDatabaseException(e);
                }
            }
        }
        return this.augmentations.get(controlNumber);
    }

    /**
     * example input: 450:8 :$1--$aF01.829.500$xMorals // 450:8 :$1--$aK01.752.566$xMorals // 450:8 :$eIncludes
     * broader:$aMorality$3M0014053$4T026943$91977-03-17
     *
     * @param marc
     *            A {@code String} of pseudo marc from Voyager
     * @return A {@code String} of subfield a data
     */
    public String parseSubfieldAs(final String marc) {
        StringBuilder sb = new StringBuilder();
        String[] fields = marc.split("// ");
        for (String field : fields) {
            sb.append(field.replaceFirst(".*\\$a([^\\$]+)(\\$.*)?", "$1"));
            sb.append(' ');
        }
        return sb.toString().trim();
    }

    public void setDataSource(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private void buildAugmentations() {
        LOG.debug("start building authority augmentation object");
        this.augmentations = new HashMap<>();
        try (Connection conn = this.dataSource.getConnection();
                PreparedStatement getListStmt = conn.prepareStatement(SQL);
                ResultSet rs = getListStmt.executeQuery();) {
            rs.setFetchSize(JDBC_FETCH_SIZE);
            while (rs.next()) {
                String authId = rs.getString(1);
                byte[] bytes = rs.getBytes(2);
                if (null != bytes) {
                    this.augmentations.put(authId, parseSubfieldAs(new String(bytes, StandardCharsets.UTF_8)));
                }
            }
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(AUGMENTATION_FILE))) {
                oos.writeObject(this.augmentations);
            }
            LOG.debug("completed building authority augmentation object");
        } catch (SQLException | IOException e) {
            throw new EresourceDatabaseException(e);
        }
    }
}
