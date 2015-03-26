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

    private String deleteEresource;

    private String deleteLink;

    private String deleteMesh;

    private String deleteSubset;

    private String deleteType;

    private String deleteVersion;

    private String tablePrefix;

    private EresourceSQLTranslator translator;

    public UpdateEresourceHandler(final DataSource dataSource, final BlockingQueue<Eresource> queue,
            final EresourceSQLTranslator translator) {
        this(dataSource, queue, translator, "");
    }

    public UpdateEresourceHandler(final DataSource dataSource, final BlockingQueue<Eresource> queue,
            final EresourceSQLTranslator translator, final String tablePrefix) {
        super(dataSource, queue, translator, tablePrefix);
        this.translator = translator;
        this.tablePrefix = tablePrefix;
        this.deleteEresource = DELETE_FROM + this.tablePrefix + "ERESOURCE WHERE ERESOURCE_ID = ";
        this.deleteVersion = DELETE_FROM + this.tablePrefix + "VERSION WHERE ERESOURCE_ID = ";
        this.deleteLink = DELETE_FROM + this.tablePrefix + "LINK WHERE ERESOURCE_ID = ";
        this.deleteType = DELETE_FROM + this.tablePrefix + "TYPE WHERE ERESOURCE_ID = ";
        this.deleteSubset = DELETE_FROM + this.tablePrefix + "SUBSET WHERE ERESOURCE_ID = ";
        this.deleteMesh = DELETE_FROM + this.tablePrefix + "MESH WHERE ERESOURCE_ID = ";
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
                stmt.addBatch(this.deleteEresource + id);
                stmt.addBatch(this.deleteVersion + id);
                stmt.addBatch(this.deleteLink + id);
                stmt.addBatch(this.deleteType + id);
                stmt.addBatch(this.deleteSubset + id);
                stmt.addBatch(this.deleteMesh + id);
                stmt.executeBatch();
            }
        }
        super.insertEresource(eresource);
    }
}
