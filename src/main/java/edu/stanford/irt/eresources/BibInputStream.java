package edu.stanford.irt.eresources;

public class BibInputStream extends EresourceInputStream {

    private static final String BIB_QUERY = "SELECT SEQNUM, RECORD_SEGMENT FROM LMLDB.BIB_DATA WHERE BIB_ID = ?";

    private static final String MFHD_QUERY = "SELECT SEQNUM, RECORD_SEGMENT FROM LMLDB.MFHD_DATA WHERE MFHD_ID = ?";

    private static final String QUERY = "with bibs as ("
            + "select bib_id from lmldb.bib_master where update_date is null and create_date > ? "
            + "union "
            + "select bib_id from lmldb.bib_master where update_date > ? "
            + "union "
            + "select bib_mfhd.bib_id from lmldb.bib_mfhd, lmldb.mfhd_master where mfhd_master.mfhd_id = bib_mfhd.mfhd_id and update_date is null and create_date > ? and mfhd_master.location_id in (select location_id from lmldb.location where location_name like 'Digital: %' or location_code like 'WKST%' or location_id = 128) "
            + "union "
            + "select bib_mfhd.bib_id from lmldb.bib_mfhd, lmldb.mfhd_master where mfhd_master.mfhd_id = bib_mfhd.mfhd_id and update_date > ? and mfhd_master.location_id in (select location_id from lmldb.location where location_name like 'Digital: %' or location_code like 'WKST%' or location_id = 128) "
            + "union "
            + "select bi.bib_id "
            + "from lmldb.bib_item bi, lmldb.item_status item_status_1 "
            + "left outer join lmldb.item_status item_status_2 "
            + "on (item_status_1.item_id = item_status_2.item_id "
            + "and item_status_1.item_status_date < item_status_2.item_status_date) "
            + "where item_status_2.item_id is null "
            + "and bi.item_id = item_status_1.item_id "
            + "and item_status_1.item_status_date > ? ) "
            + "select bib_master.bib_id, mfhd_master.mfhd_id "
            + "from lmldb.bib_master, lmldb.bib_index, lmldb.mfhd_master, lmldb.bib_mfhd, bibs "
            + "where bib_master.bib_id = bib_mfhd.bib_id "
            + "and mfhd_master.mfhd_id = bib_mfhd.mfhd_id "
            + "and bib_index.normal_heading = 'LANECONNEX' "
            + "and bib_index.index_code = '655H' "
            + "and bib_master.suppress_in_opac != 'Y' "
            + "and bib_master.bib_id = bib_index.bib_id "
            + "and mfhd_master.mfhd_id = bib_mfhd.mfhd_id "
            + "and mfhd_master.location_id in (select location_id from lmldb.location where location_name like 'Digital: %' or location_code like 'WKST%' or location_id = 128 or location_id = 134) "
            + "and mfhd_master.suppress_in_opac != 'Y' "
            + "and bibs.bib_id = bib_master.bib_id";

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
