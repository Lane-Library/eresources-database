package edu.stanford.irt.eresources;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateEresourceHandler extends DefaultEresourceHandler {

    private static final String DELETE_ERESOURCE= "DELETE FROM ERESOURCE WHERE ERESOURCE_ID = ";

    private static final String DELETE_LINK = "DELETE FROM LINK WHERE ERESOURCE_ID = ";

    private static final String DELETE_MESH = "DELETE FROM MESH WHERE ERESOURCE_ID = ";

    private static final String DELETE_SUBSET = "DELETE FROM SUBSET WHERE ERESOURCE_ID = ";

    private static final String DELETE_TYPE = "DELETE FROM TYPE WHERE ERESOURCE_ID = ";

    private static final String DELETE_VERSION = "DELETE FROM VERSION WHERE ERESOURCE_ID = ";

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

    @Override
    public void handleEresource(Eresource eresource) {
        Logger log = LoggerFactory.getLogger(getClass());
        log.info("enter handleEresource(" + eresource + ");");
        super.handleEresource(eresource);
    }
}
