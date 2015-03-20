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

    private static final String DELETE_FROM = "DELETE FROM ";

    private static final String DELETE_FROM_ERESOURCE = DELETE_FROM + "ERESOURCE WHERE ERESOURCE_ID = ";

    private static final String DELETE_FROM_VERSION = DELETE_FROM + "VERSION WHERE ERESOURCE_ID = ";

    private static final String DELETE_FROM_LINK = DELETE_FROM + "LINK WHERE ERESOURCE_ID = ";

    private static final String DELETE_FROM_TYPE = DELETE_FROM + "TYPE WHERE ERESOURCE_ID = ";

    private static final String DELETE_FROM_MESH = DELETE_FROM + "MESH WHERE ERESOURCE_ID = ";

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
                stmt.addBatch(DELETE_FROM_ERESOURCE + id);
                stmt.addBatch(DELETE_FROM_VERSION + id);
                stmt.addBatch(DELETE_FROM_LINK + id);
                stmt.addBatch(DELETE_FROM_TYPE + id);
                stmt.addBatch(DELETE_FROM_MESH + id);
                stmt.executeBatch();
            }
        }
        super.insertEresource(eresource);
    }
}
