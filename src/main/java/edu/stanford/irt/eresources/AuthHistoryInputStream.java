package edu.stanford.irt.eresources;

public class AuthHistoryInputStream extends HistoryInputStream {

    @Override
    protected String getBibQuery() {
        return "SELECT SEQNUM, RECORD_SEGMENT FROM CIFDB.BIB_DATA WHERE BIB_ID = ?";
    }
}
