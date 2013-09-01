package edu.stanford.irt.eresources;

public class HistoryInputStream extends EresourceInputStream {

    private String bibQuery;

    public void setBibQuery(final String bibQuery) {
        this.bibQuery = bibQuery;
    }

    @Override
    protected String getBibQuery() {
        return "SELECT SEQNUM, RECORD_SEGMENT FROM LMLDB.BIB_DATA WHERE BIB_ID = ?";
    }

    @Override
    protected String getMfhdQuery() {
        return "SELECT NULL FROM DUAL";
    }

    @Override
    protected String getSelectIDListSQL() {
        return this.bibQuery;
    }
}
