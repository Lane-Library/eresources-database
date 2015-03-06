package edu.stanford.irt.eresources.marc;

import java.util.concurrent.Executor;

import javax.sql.DataSource;

public class AuthInputStream extends EresourceInputStream {

    private static final String LIST_QUERY = "SELECT BIB_MASTER.BIB_ID FROM CIFDB.BIB_INDEX, CIFDB.BIB_MASTER "
            + " WHERE BIB_MASTER.BIB_ID = BIB_INDEX.BIB_ID AND NORMAL_HEADING = 'LANECONNEX' AND INDEX_CODE = '655H'";

    private static final String MFHD_QUERY = "SELECT NULL FROM DUAL";

    private static final String RECORD_QUERY = "SELECT SEQNUM, RECORD_SEGMENT FROM CIFDB.BIB_DATA WHERE BIB_ID = ? ORDER BY SEQNUM";

    public AuthInputStream(final DataSource dataSource, final Executor executor) {
        super(dataSource, executor);
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
}
