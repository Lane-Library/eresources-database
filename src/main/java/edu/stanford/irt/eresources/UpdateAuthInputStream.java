package edu.stanford.irt.eresources;

import java.util.concurrent.Executor;

import javax.sql.DataSource;

public class UpdateAuthInputStream extends AuthInputStream {

    public UpdateAuthInputStream(DataSource dataSource, Executor executor) {
        super(dataSource, executor);
    }

    private static final String LIST_QUERY = "SELECT BIB_MASTER.BIB_ID FROM CIFDB.BIB_INDEX, CIFDB.BIB_MASTER "
            + " WHERE BIB_MASTER.BIB_ID = BIB_INDEX.BIB_ID AND NORMAL_HEADING = 'LANECONNEX' AND INDEX_CODE = '655H'"
            + " AND (BIB_MASTER.UPDATE_DATE > ?"
            + " OR BIB_MASTER.CREATE_DATE > ?)";

    @Override
    protected String getSelectIDListSQL() {
        return LIST_QUERY;
    }
}
