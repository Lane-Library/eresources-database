package edu.stanford.irt.eresources.marc;

import java.util.concurrent.Executor;

import javax.sql.DataSource;

import edu.stanford.irt.eresources.StartDate;

public class UpdateAuthInputStream extends UpdateEresourceInputStream {

    private static final String LIST_QUERY = "SELECT BIB_MASTER.BIB_ID FROM CIFDB.BIB_INDEX, CIFDB.BIB_MASTER "
            + " WHERE BIB_MASTER.BIB_ID = BIB_INDEX.BIB_ID AND NORMAL_HEADING = 'LANECONNEX' AND INDEX_CODE = '655H'"
            + " AND (BIB_MASTER.UPDATE_DATE > ?" + " OR BIB_MASTER.CREATE_DATE > ?)";

    private static final String MFHD_QUERY = "SELECT NULL FROM DUAL";

    private static final String RECORD_QUERY = "SELECT SEQNUM, RECORD_SEGMENT FROM CIFDB.BIB_DATA WHERE BIB_ID = ? ORDER BY SEQNUM";

    public UpdateAuthInputStream(final DataSource dataSource, final Executor executor, final StartDate startDate) {
        super(dataSource, executor, startDate);
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
