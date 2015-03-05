package edu.stanford.irt.eresources.jdbc;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import edu.stanford.irt.eresources.Eresource;
import edu.stanford.irt.eresources.EresourceException;
import edu.stanford.irt.eresources.StartDate;

public class UpdateJDBCLoader extends JDBCLoader {

    private static final String DELETE_ERESOURCE = "DELETE FROM ERESOURCE WHERE ERESOURCE_ID = ";

    private static final String DELETE_LINK = "DELETE FROM LINK WHERE ERESOURCE_ID = ";

    private static final String DELETE_MESH = "DELETE FROM MESH WHERE ERESOURCE_ID = ";

    private static final String DELETE_SUBSET = "DELETE FROM SUBSET WHERE ERESOURCE_ID = ";

    private static final String DELETE_TYPE = "DELETE FROM TYPE WHERE ERESOURCE_ID = ";

    private static final String DELETE_VERSION = "DELETE FROM VERSION WHERE ERESOURCE_ID = ";

    private EresourceSQLTranslator translator;

    public UpdateJDBCLoader(final DataSource dataSource, final EresourceSQLTranslator translator,
            final StartDate startDate) {
        super(dataSource, translator, startDate);
        this.translator = translator;
    }

    @Override
    protected void initializeStartDate(final StartDate startDate, final Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT MAX(UPDATED) FROM ERESOURCE")) {
            if (!rs.next()) {
                throw new EresourceException("unable to get MAX(UPDATED)");
            }
            Timestamp timestamp = rs.getTimestamp(1);
            Date updated = timestamp == null ? new Date(0) : new Date(timestamp.getTime());
            startDate.initialize(updated);
        }
    }

    @Override
    protected void insertEresource(final Eresource eresource) throws SQLException, IOException {
        if (!eresource.isClone()) {
            List<String> ids = new ArrayList<String>();
            try (Statement stmt = getConnection().createStatement()) {
                ResultSet rs = stmt.executeQuery(this.translator.getEresourceIdSQL(eresource));
                while (rs.next()) {
                    ids.add(rs.getString(1));
                }
                rs.close();
                for (String id : ids) {
                    stmt.addBatch(DELETE_ERESOURCE + id);
                    stmt.addBatch(DELETE_VERSION + id);
                    stmt.addBatch(DELETE_LINK + id);
                    stmt.addBatch(DELETE_TYPE + id);
                    stmt.addBatch(DELETE_SUBSET + id);
                    stmt.addBatch(DELETE_MESH + id);
                    stmt.executeBatch();
                }
            }
        }
        super.insertEresource(eresource);
    }
}
