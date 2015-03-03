package edu.stanford.irt.eresources.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;

import edu.stanford.irt.eresources.EresourceException;

public class DBUpdate {
    
    private static final String MAX_UPDATED_QUERY = "SELECT MAX(UPDATED) FROM ERESOURCE";

    protected Date getUpdatedDate(final Statement stmt) throws SQLException {
        Timestamp timeLastUpdated = null;
        try (ResultSet rs = stmt.executeQuery(MAX_UPDATED_QUERY)) {
            if (!rs.next()) {
                throw new EresourceException("unable to get MAX(UPDATED)");
            }
            timeLastUpdated = rs.getTimestamp(1);
        }
        return new Date(timeLastUpdated == null ? 0 : timeLastUpdated.getTime());
    }
}
