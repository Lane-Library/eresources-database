package edu.stanford.irt.eresources;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;

public class DBUpdate extends DBLoader {

    public static void main(final String[] args) throws SQLException, IOException {
        DBLoader.main(new String[]{"update"});
    }

    private String selectQuery;
    
    public DBUpdate() {
        this("");
    }
    
    public DBUpdate(String tablePrefix) {
        this.selectQuery = "SELECT MAX(UPDATED) FROM " + tablePrefix + "ERESOURCE";
    }

    @Override
    protected Date getUpdatedDate(final Statement stmt) throws SQLException {
        Timestamp timeLastUpdated = null;
        try (ResultSet rs = stmt.executeQuery(this.selectQuery)) {
            if (!rs.next()) {
                throw new EresourceDatabaseException("unable to get MAX(UPDATED)");
            }
            timeLastUpdated = rs.getTimestamp(1);
        }
        return new Date(timeLastUpdated == null ? 0 : timeLastUpdated.getTime());
    }
}
