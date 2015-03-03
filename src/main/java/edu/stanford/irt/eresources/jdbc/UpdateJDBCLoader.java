package edu.stanford.irt.eresources.jdbc;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import edu.stanford.irt.eresources.Eresource;

public class UpdateJDBCLoader extends JDBCLoader {

    private static final String DELETE_ERESOURCE= "DELETE FROM ERESOURCE WHERE ERESOURCE_ID = ";

    private static final String DELETE_LINK = "DELETE FROM LINK WHERE ERESOURCE_ID = ";

    private static final String DELETE_MESH = "DELETE FROM MESH WHERE ERESOURCE_ID = ";

    private static final String DELETE_SUBSET = "DELETE FROM SUBSET WHERE ERESOURCE_ID = ";

    private static final String DELETE_TYPE = "DELETE FROM TYPE WHERE ERESOURCE_ID = ";

    private static final String DELETE_VERSION = "DELETE FROM VERSION WHERE ERESOURCE_ID = ";

    private EresourceSQLTranslator translator;

    public UpdateJDBCLoader(final DataSource dataSource, final EresourceSQLTranslator translator) {
        super(dataSource, translator);
        this.translator = translator;
    }

    @Override
    protected void insertEresource(final Eresource eresource) throws SQLException, IOException {
        if (!eresource.isClone()) {
            Statement stmt = getStatement();
            List<String> ids = new ArrayList<String>();
            try (ResultSet rs = stmt.executeQuery(this.translator.getEresourceIdSQL(eresource))) {
                while (rs.next()) {
                    ids.add(rs.getString(1));
                }
            }
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
        super.insertEresource(eresource);
    }
}
