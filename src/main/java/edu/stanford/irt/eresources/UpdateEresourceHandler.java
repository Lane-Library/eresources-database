package edu.stanford.irt.eresources;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.BlockingQueue;

import javax.sql.DataSource;

public class UpdateEresourceHandler extends DefaultEresourceHandler {

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
        this.deleteEresource = "DELETE FROM " + this.tablePrefix + "ERESOURCE WHERE ERESOURCE_ID = ";
        this.deleteVersion = "DELETE FROM " + this.tablePrefix + "VERSION WHERE ERESOURCE_ID = ";
        this.deleteLink = "DELETE FROM " + this.tablePrefix + "LINK WHERE ERESOURCE_ID = ";
        this.deleteType = "DELETE FROM " + this.tablePrefix + "TYPE WHERE ERESOURCE_ID = ";
        this.deleteSubset = "DELETE FROM " + this.tablePrefix + "SUBSET WHERE ERESOURCE_ID = ";
        this.deleteMesh = "DELETE FROM " + this.tablePrefix + "MESH WHERE ERESOURCE_ID = ";
    }

    @Override
    protected void insertEresource(final Eresource eresource) throws SQLException, IOException {
        Statement stmt = getStatement();
        try (ResultSet rs = stmt.executeQuery(this.translator.getEresourceIdSQL(eresource))) {
            while (rs.next()) {
                int id = rs.getInt(1);
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
