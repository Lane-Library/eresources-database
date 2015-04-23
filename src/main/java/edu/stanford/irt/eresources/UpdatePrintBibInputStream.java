package edu.stanford.irt.eresources;

public class UpdatePrintBibInputStream extends BibInputStream {

    private static final String QUERY =
            "SELECT DISTINCT bib_mfhd.bib_id, " 
                    + "  bib_mfhd.mfhd_id " 
                    + "FROM lmldb.bib_mfhd, " 
                    + "  lmldb.mfhd_master " 
                    + "WHERE mfhd_master.mfhd_id         = bib_mfhd.mfhd_id " 
                    + "AND mfhd_master.suppress_in_opac != 'Y' " 
                    + "AND bib_id                       IN " 
                    + "  ( SELECT DISTINCT bib_id " 
                    + "  FROM lmldb.bib_master " 
                    + "  WHERE bib_master.suppress_in_opac != 'Y' " 
                    + "  INTERSECT"
                    + "    (SELECT bib_id "
                    + "    FROM lmldb.bib_master "
                    + "    WHERE update_date > ? "
                    + "    OR create_date    > ? "
                    + "    UNION "
                    + "    SELECT bib_mfhd.bib_id "
                    + "    FROM lmldb.bib_mfhd, "
                    + "    lmldb.mfhd_master "
                    + "    WHERE mfhd_master.mfhd_id = bib_mfhd.mfhd_id "
                    + "    AND (update_date          > ? "
                    + "    OR create_date            > ?) "
                    + "    UNION "
                    + "    SELECT bib_item.bib_id "
                    + "    FROM lmldb.bib_item, "
                    + "    lmldb.item_status item_status_1 "
                    + "    LEFT OUTER JOIN lmldb.item_status item_status_2 "
                    + "    ON (item_status_1.item_id          = item_status_2.item_id "
                    + "    AND item_status_1.item_status_date < item_status_2.item_status_date) "
                    + "    WHERE item_status_2.item_id       IS NULL "
                    + "    AND bib_item.item_id               = item_status_1.item_id "
                    + "    AND item_status_1.item_status_date > ? "
                    + "    ) "
                    + "  MINUS " 
                    + "  SELECT DISTINCT bib_id " 
                    + "  FROM lmldb.bib_index " 
                    + "  WHERE bib_index.normal_heading = 'LANECONNEX' " 
                    + "  AND bib_index.index_code       = '655H' " 
                    + "  MINUS " 
                    + "  SELECT DISTINCT bib_id " 
                    + "  FROM lmldb.bib_mfhd, " 
                    + "    lmldb.mfhd_master, " 
                    + "    lmldb.elink_index " 
                    + "  WHERE bib_mfhd.mfhd_id            = elink_index.record_id " 
                    + "  AND bib_mfhd.mfhd_id              = mfhd_master.mfhd_id " 
                    + "  AND mfhd_master.suppress_in_opac != 'Y' " 
                    + "  AND elink_index.record_type       ='M' " 
                    + "  ) " 
                    + "ORDER BY bib_id, mfhd_id ";
    
    @Override
    protected String getSelectIDListSQL() {
        return QUERY;
    }
}
