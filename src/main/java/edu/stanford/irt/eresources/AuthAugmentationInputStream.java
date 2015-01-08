package edu.stanford.irt.eresources;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.Executor;

import javax.sql.DataSource;

public class AuthAugmentationInputStream extends AuthInputStream {

    private static final String MESH_LIST_QUERY = "select bib_id from cifdb.bib_index where index_code = '2451' and  display_heading = ?";

    private static final String PERSON_LIST_QUERY = "select bib_id from cifdb.bib_index where index_code = '2451' and display_heading like ? || '%'";

    private String tag;

    private String term;

    public AuthAugmentationInputStream(final String term, final String tag, final DataSource dataSource,
            final Executor executor) {
        super(dataSource, executor);
        this.term = term;
        this.tag = tag;
    }

    @Override
    protected String getSelectIDListSQL() {
        if ("100".equals(this.tag) || "600".equals(this.tag) || "700".equals(this.tag)) {
            return PERSON_LIST_QUERY;
        } else {
            return MESH_LIST_QUERY;
        }
    }

    @Override
    protected void prepareListStatement(final PreparedStatement stmt) throws SQLException {
        stmt.setString(1, this.term);
    }
}
