package edu.stanford.irt.eresources.marc;

import java.util.concurrent.Executor;

import javax.sql.DataSource;

import edu.stanford.irt.eresources.StartDate;

public class UpdateBibInputStream extends UpdateEresourceInputStream {

    private static final String QUERY =
            "SELECT bib_master.bib_id, " +
                    "  mfhd_master.mfhd_id " +
                    "FROM lmldb.bib_master, " +
                    "  lmldb.bib_index, " +
                    "  lmldb.mfhd_master, " +
                    "  lmldb.bib_mfhd " +
                    "WHERE bib_master.bib_id          = bib_mfhd.bib_id " +
                    "AND mfhd_master.mfhd_id          = bib_mfhd.mfhd_id " +
                    "AND bib_index.normal_heading     = 'LANECONNEX' " +
                    "AND bib_index.index_code         = '655H' " +
                    "AND bib_master.suppress_in_opac != 'Y' " +
                    "AND bib_master.bib_id            = bib_index.bib_id " +
                    "AND mfhd_master.mfhd_id          = bib_mfhd.mfhd_id " +
                    "AND mfhd_master.location_id     IN " +
                    "  (SELECT location_id " +
                    "  FROM lmldb.location " +
                    "  WHERE location_name LIKE 'Digital: %' " +
                    "  OR location_code LIKE 'WKST%' " +
                    "  OR location_id = 128 " +
                    "  OR location_id = 134 " +
                    "  ) " +
                    "AND mfhd_master.suppress_in_opac != 'Y' " +
                    "AND bib_master.bib_id            IN " +
                    "  (SELECT bib_id " +
                    "  FROM lmldb.bib_master " +
                    "  WHERE update_date > ? " +
                    "  OR create_date    > ? " +
                    "  UNION " +
                    "  SELECT bib_mfhd.bib_id " +
                    "  FROM lmldb.bib_mfhd, " +
                    "    lmldb.mfhd_master " +
                    "  WHERE mfhd_master.mfhd_id = bib_mfhd.mfhd_id " +
                    "  AND (update_date          > ? " +
                    "  OR create_date            > ?) " +
                    "  UNION " +
                    "  SELECT bib_item.bib_id " +
                    "  FROM lmldb.bib_item, " +
                    "    lmldb.item_status item_status_1 " +
                    "  LEFT OUTER JOIN lmldb.item_status item_status_2 " +
                    "  ON (item_status_1.item_id          = item_status_2.item_id " +
                    "  AND item_status_1.item_status_date < item_status_2.item_status_date) " +
                    "  WHERE item_status_2.item_id       IS NULL " +
                    "  AND bib_item.item_id               = item_status_1.item_id " +
                    "  AND item_status_1.item_status_date > ? " +
                    "  )";

    public UpdateBibInputStream(final DataSource dataSource, final Executor executor, final StartDate startDate) {
        super(dataSource, executor, startDate);
    }

    @Override
    protected String getBibQuery() {
        return BibInputStream.BIB_QUERY;
    }

    @Override
    protected String getMfhdQuery() {
        return BibInputStream.MFHD_QUERY;
    }

    @Override
    protected String getSelectIDListSQL() {
        return QUERY;
    }
}
