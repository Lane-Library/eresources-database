package edu.stanford.irt.eresources;

public class UpdateBibInputStream extends BibInputStream {

    private static final String QUERY = "SELECT DISTINCT bib_mfhd.bib_id, " 
            + "  bib_mfhd.mfhd_id " 
            + "FROM lmldb.bib_mfhd, " 
            + "  lmldb.mfhd_master, " 
            + "  lmldb.bib_master " 
            + "WHERE mfhd_master.mfhd_id         = bib_mfhd.mfhd_id " 
            + "AND bib_master.bib_id             = bib_mfhd.bib_id " 
            + "AND mfhd_master.suppress_in_opac != 'Y' " 
            + "AND bib_master.suppress_in_opac  != 'Y' " 
            + "AND bib_master.bib_id IN " 
            + "  (SELECT bib_id "
            + "  FROM lmldb.bib_master "
            + "  WHERE update_date > ? "
            + "  OR create_date    > ? "
            + "  UNION "
            + "  SELECT bib_mfhd.bib_id "
            + "  FROM lmldb.bib_mfhd, "
            + "    lmldb.mfhd_master "
            + "  WHERE mfhd_master.mfhd_id = bib_mfhd.mfhd_id "
            + "  AND (update_date          > ? "
            + "  OR create_date            > ?) "
            + "  UNION "
            + "  SELECT bib_item.bib_id "
            + "  FROM lmldb.bib_item, "
            + "    lmldb.item_status item_status_1 "
            + "  LEFT OUTER JOIN lmldb.item_status item_status_2 "
            + "  ON (item_status_1.item_id          = item_status_2.item_id "
            + "  AND item_status_1.item_status_date < item_status_2.item_status_date) "
            + "  WHERE item_status_2.item_id       IS NULL "
            + "  AND bib_item.item_id               = item_status_1.item_id "
            + "  AND item_status_1.item_status_date > ? "
            + "  )"
            + "AND bib_master.bib_id NOT IN ( "
            + "  SELECT DISTINCT bib_id FROM lmldb.bib_index "
            + "  WHERE index_code = '0350' "
            + "  AND NORMAL_HEADING like 'PMID %' "
            + "  AND bib_id IN ( "
            + "      SELECT DISTINCT bib_id FROM lmldb.bib_index "
            + "      WHERE index_code = '655H' "
            + "      AND NORMAL_HEADING like 'ARTICLES') "
            + "  ) "
            + "ORDER BY bib_id, mfhd_id";
    
    @Override
    protected String getSelectIDListSQL() {
        return QUERY;
    }
}
