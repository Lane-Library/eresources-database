package edu.stanford.irt.eresources;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.BlockingQueue;

import javax.sql.DataSource;

public class UpdateEresourceHandler extends DefaultEresourceHandler {

    private EresourceSQLTranslator translator;

    public UpdateEresourceHandler(final DataSource dataSource, final BlockingQueue<DatabaseEresource> queue,
            final EresourceSQLTranslator translator) {
        super(dataSource, queue, translator);
        this.translator = translator;
    }

    @Override
    protected void insertEresource(final DatabaseEresource eresource) throws SQLException, IOException {
        Statement stmt = getStatement();
        try (ResultSet rs = stmt.executeQuery(this.translator.getEresourceIdSQL(eresource))) {
            if (rs.next()) {
                int id = rs.getInt(1);
                stmt.addBatch("DELETE FROM ERESOURCE WHERE ERESOURCE_ID = " + id);
                stmt.addBatch("DELETE FROM VERSION WHERE ERESOURCE_ID = " + id);
                stmt.addBatch("DELETE FROM LINK WHERE ERESOURCE_ID = " + id);
                stmt.addBatch("DELETE FROM TYPE WHERE ERESOURCE_ID = " + id);
                stmt.addBatch("DELETE FROM SUBSET WHERE ERESOURCE_ID = " + id);
                stmt.addBatch("DELETE FROM MESH WHERE ERESOURCE_ID = " + id);
                stmt.executeBatch();
            }
        }
        super.insertEresource(eresource);
    }
}
