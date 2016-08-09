package edu.stanford.irt.eresources;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.Executor;

import javax.sql.DataSource;

public class AuthAugmentationInputStream extends EresourceInputStream {

    private static final String LIST_QUERY = "select bib_id from cifdb.bib_index where index_code = '0359' and  normal_heading = ?";

    private static final String MFHD_QUERY = "SELECT NULL FROM DUAL";

    private static final String RECORD_QUERY = "SELECT SEQNUM, RECORD_SEGMENT FROM CIFDB.BIB_DATA WHERE BIB_ID = ? ORDER BY SEQNUM";

    private String controlNumber;

    public AuthAugmentationInputStream(final String controlNumber, final DataSource dataSource,
            final Executor executor) {
        this.controlNumber = controlNumber;
        this.setDataSource(dataSource);
        this.setExecutor(executor);
    }

    @Override
    protected String getBibQuery() {
        return RECORD_QUERY;
    }

    @Override
    protected String getMfhdQuery() {
        return MFHD_QUERY;
    }

    @Override
    protected String getSelectIDListSQL() {
        return LIST_QUERY;
    }

    @Override
    protected void prepareListStatement(final PreparedStatement stmt) throws SQLException {
        stmt.setString(1, this.controlNumber);
    }
}
