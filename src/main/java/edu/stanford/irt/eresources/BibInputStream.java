package edu.stanford.irt.eresources;

public class BibInputStream extends EresourceInputStream {

    private static final String BIB_QUERY = "SELECT SEQNUM, RECORD_SEGMENT FROM LMLDB.BIB_DATA WHERE BIB_ID = ?";

    private static final String MFHD_QUERY = "SELECT SEQNUM, RECORD_SEGMENT FROM LMLDB.MFHD_DATA WHERE MFHD_ID = ?";

    private static final String QUERY = "SELECT DISTINCT bib_mfhd.bib_id, " 
            + "  bib_mfhd.mfhd_id " 
            + "FROM lmldb.bib_mfhd, " 
            + "  lmldb.mfhd_master, " 
            + "  lmldb.bib_master " 
            + "WHERE mfhd_master.mfhd_id         = bib_mfhd.mfhd_id " 
            + "AND bib_master.bib_id             = bib_mfhd.bib_id " 
            + "AND mfhd_master.suppress_in_opac != 'Y' " 
            + "AND bib_master.suppress_in_opac  != 'Y' " 
            + "ORDER BY bib_id, mfhd_id";

    @Override
    protected String getBibQuery() {
        return BIB_QUERY;
    }

    @Override
    protected String getMfhdQuery() {
        return MFHD_QUERY;
    }

    @Override
    protected String getSelectIDListSQL() {
        return QUERY;
    }
}
