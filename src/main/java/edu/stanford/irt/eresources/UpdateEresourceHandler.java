package edu.stanford.irt.eresources;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import javax.sql.DataSource;

public class UpdateEresourceHandler extends DefaultEresourceHandler {

    private static final String DELETEFROM = "DELETE FROM ";

    private static final String DELETE_ERESOURCE = DELETEFROM + "ERESOURCE WHERE ERESOURCE_ID = ";

    private static final String DELETE_LINK = DELETEFROM + "LINK WHERE ERESOURCE_ID = ";

    private static final String DELETE_MESH = DELETEFROM + "MESH WHERE ERESOURCE_ID = ";

    private static final String DELETE_SUBSET = DELETEFROM + "SUBSET WHERE ERESOURCE_ID = ";

    private static final String DELETE_TYPE = DELETEFROM + "TYPE WHERE ERESOURCE_ID = ";

    private static final String DELETE_VERSION = DELETEFROM + "VERSION WHERE ERESOURCE_ID = ";

    private EresourceSQLTranslator translator;

    public UpdateEresourceHandler(final DataSource dataSource, final BlockingQueue<Eresource> queue,
            final EresourceSQLTranslator translator) {
        super(dataSource, queue, translator);
        this.translator = translator;
    }

    protected UpdateEresourceHandler() {
    }

    @Override
    protected void insertEresource(final Eresource eresource) throws SQLException, IOException {
        if (!eresource.isClone()) {
            Statement stmt = getStatement();
            List<String> ids = new LinkedList<String>();
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
