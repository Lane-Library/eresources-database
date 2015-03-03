package edu.stanford.irt.eresources.marc;

import java.util.concurrent.Executor;

import javax.sql.DataSource;

public class BibInputStream extends EresourceInputStream {

    protected static final String BIB_QUERY = "SELECT SEQNUM, RECORD_SEGMENT FROM LMLDB.BIB_DATA WHERE BIB_ID = ?";

    protected static final String MFHD_QUERY = "SELECT SEQNUM, RECORD_SEGMENT FROM LMLDB.MFHD_DATA WHERE MFHD_ID = ?";

    private static final String QUERY = "select bib_mfhd.bib_id, bib_mfhd.mfhd_id "
            + "from lmldb.bib_mfhd, lmldb.bib_master, lmldb.bib_index, lmldb.mfhd_master "
            + "where bib_master.bib_id = bib_mfhd.bib_id "
            + "and bib_master.suppress_in_opac != 'Y' "
            + "and bib_index.bib_id = bib_mfhd.bib_id "
            + "and bib_index.normal_heading = 'LANECONNEX' "
            + "and bib_index.index_code = '655H' "
            + "and mfhd_master.mfhd_id = bib_mfhd.mfhd_id "
            + "and mfhd_master.suppress_in_opac != 'Y' "
            + "and mfhd_master.location_id in ("
            + "select location_id "
            + "from lmldb.location "
            + "where location_name like 'Digital: %' "
            + "or location_code like 'WKST%' "
            + "or location_id = 128 "
            + "or location_id = 134)";

    public BibInputStream(DataSource dataSource, Executor executor) {
        super(dataSource, executor);
    }

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
