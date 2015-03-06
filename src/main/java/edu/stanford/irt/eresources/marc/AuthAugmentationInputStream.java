package edu.stanford.irt.eresources.marc;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.Executor;

import javax.sql.DataSource;

public class AuthAugmentationInputStream extends AuthInputStream {

    private static final String LIST_QUERY = "select bib_id from cifdb.bib_index where index_code = '0359' and  normal_heading = ?";

    private String controlNumber;

    public AuthAugmentationInputStream(final String controlNumber, final DataSource dataSource, final Executor executor) {
        super(dataSource, executor);
        this.controlNumber = controlNumber;
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
