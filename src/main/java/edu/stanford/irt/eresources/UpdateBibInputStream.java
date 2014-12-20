package edu.stanford.irt.eresources;

public class UpdateBibInputStream extends BibInputStream {

    private static final String QUERY = "WITH updated_bibs AS " + "  (SELECT bib_id " + "  FROM lmldb.bib_master "
            + "  WHERE update_date > ? " + "  OR create_date    > ? " + "  UNION " + "  SELECT bib_mfhd.bib_id "
            + "  FROM lmldb.bib_mfhd, " + "    lmldb.mfhd_master " + "  WHERE mfhd_master.mfhd_id = bib_mfhd.mfhd_id "
            + "  AND (update_date          > ? " + "  OR create_date            > ?) " + "  UNION "
            + "  SELECT bib_item.bib_id " + "  FROM lmldb.bib_item, " + "    lmldb.item_status item_status_1 "
            + "  LEFT OUTER JOIN lmldb.item_status item_status_2 "
            + "  ON (item_status_1.item_id          = item_status_2.item_id "
            + "  AND item_status_1.item_status_date < item_status_2.item_status_date) "
            + "  WHERE item_status_2.item_id       IS NULL "
            + "  AND bib_item.item_id               = item_status_1.item_id "
            + "  AND item_status_1.item_status_date > ? " + "  ) " + "SELECT bib_master.bib_id, "
            + "  mfhd_master.mfhd_id " + "FROM lmldb.bib_master, " + "  lmldb.bib_index, " + "  lmldb.mfhd_master, "
            + "  lmldb.bib_mfhd, " + "  updated_bibs " + "WHERE bib_master.bib_id          = bib_mfhd.bib_id "
            + "AND mfhd_master.mfhd_id          = bib_mfhd.mfhd_id "
            + "AND bib_index.normal_heading     = 'LANECONNEX' " + "AND bib_index.index_code         = '655H' "
            + "AND bib_master.suppress_in_opac != 'Y' " + "AND bib_master.bib_id            = bib_index.bib_id "
            + "AND mfhd_master.mfhd_id          = bib_mfhd.mfhd_id " + "AND mfhd_master.location_id     IN "
            + "  (SELECT location_id " + "  FROM lmldb.location " + "  WHERE location_name LIKE 'Digital: %' "
            + "  OR location_code LIKE 'WKST%' " + "  OR location_id = 128 " + "  OR location_id = 134 " + "  ) "
            + "AND mfhd_master.suppress_in_opac != 'Y' " + "AND updated_bibs.bib_id           = bib_master.bib_id";

    @Override
    protected String getSelectIDListSQL() {
        return QUERY;
    }
}
