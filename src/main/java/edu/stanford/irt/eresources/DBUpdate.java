package edu.stanford.irt.eresources;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;

public class DBUpdate extends DBLoader {

    private static final String SELECT = "SELECT MAX(UPDATED) FROM ERESOURCE";

    public static void main(final String[] args) throws SQLException, IOException {
        DBLoader.main(new String[] { "db-update" });
    }

    @Override
    protected Date getUpdatedDate(final Statement stmt) throws SQLException {
        Timestamp timeLastUpdated = null;
        try (ResultSet rs = stmt.executeQuery(SELECT)) {
            if (!rs.next()) {
                throw new EresourceDatabaseException("unable to get MAX(UPDATED)");
            }
            timeLastUpdated = rs.getTimestamp(1);
        }
        return new Date(timeLastUpdated == null ? 0 : timeLastUpdated.getTime());
    }
}
