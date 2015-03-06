package edu.stanford.irt.eresources.marc;

import java.util.concurrent.Executor;

import javax.sql.DataSource;

import edu.stanford.irt.eresources.StartDate;

public class UpdatePrintBibInputStream extends UpdateBibInputStream {

    private static final String QUERY = "SELECT BIB_MFHD.BIB_ID , " + "  BIB_MFHD.MFHD_ID " + "FROM LMLDB.BIB_MASTER, "
            + "  LMLDB.BIB_MFHD, " + "  LMLDB.MFHD_MASTER " + "WHERE BIB_MASTER.SUPPRESS_IN_OPAC  != 'Y' "
            + "AND MFHD_MASTER.SUPPRESS_IN_OPAC != 'Y' " + "AND BIB_MFHD.BIB_ID               = BIB_MASTER.BIB_ID "
            + "AND BIB_MFHD.MFHD_ID              = MFHD_MASTER.MFHD_ID " + "AND BIB_MFHD.BIB_ID IN "
            + "  (SELECT BIB_ID " + "  FROM LMLDB.BIB_INDEX " + "  WHERE INDEX_CODE = '2450' "
            + "  AND DISPLAY_HEADING LIKE '%[print]%' " + "  UNION " + "    (SELECT BIB_ID "
            + "    FROM LMLDB.BIB_INDEX " + "    WHERE INDEX_CODE   = '655H' " + "    AND NORMAL_HEADING = 'ARTICLES' "
            + "    MINUS " + "    SELECT BIB_ID " + "    FROM LMLDB.BIB_INDEX " + "    WHERE INDEX_CODE   = '655H' "
            + "    AND NORMAL_HEADING = 'LANECONNEX' " + "    MINUS " + "    SELECT BIB_ID "
            + "    FROM LMLDB.BIB_INDEX " + "    WHERE INDEX_CODE = '2450' "
            + "    AND DISPLAY_HEADING LIKE '%[print%' " + "    MINUS " + "    SELECT BIB_ID "
            + "    FROM LMLDB.BIB_INDEX " + "    WHERE INDEX_CODE = '2450' "
            + "    AND DISPLAY_HEADING LIKE '%[digital%' " + "    ) " + "  INTERSECT " + "  SELECT BIB_ID "
            + "  FROM LMLDB.BIB_INDEX " + "  WHERE INDEX_CODE = '008D' "
            + "  AND REGEXP_LIKE(NORMAL_HEADING,'^\\d{4}$') "
            + "  AND TO_NUMBER(TRIM(NORMAL_HEADING)) >= TO_CHAR(SYSDATE - (10 * 365),'YYYY') " + "  INTERSECT"
            + "    (SELECT bib_id " + "    FROM lmldb.bib_master " + "    WHERE update_date > ? "
            + "    OR create_date    > ? " + "    UNION " + "    SELECT bib_mfhd.bib_id " + "    FROM lmldb.bib_mfhd, "
            + "    lmldb.mfhd_master " + "    WHERE mfhd_master.mfhd_id = bib_mfhd.mfhd_id "
            + "    AND (update_date          > ? " + "    OR create_date            > ?) " + "    UNION "
            + "    SELECT bib_item.bib_id " + "    FROM lmldb.bib_item, " + "    lmldb.item_status item_status_1 "
            + "    LEFT OUTER JOIN lmldb.item_status item_status_2 "
            + "    ON (item_status_1.item_id          = item_status_2.item_id "
            + "    AND item_status_1.item_status_date < item_status_2.item_status_date) "
            + "    WHERE item_status_2.item_id       IS NULL "
            + "    AND bib_item.item_id               = item_status_1.item_id "
            + "    AND item_status_1.item_status_date > ? " + "  ) "
            // exclude Subset, Circbib fogbugz case 96195
            + "  MINUS " + "  SELECT BIB_ID " + "  FROM LMLDB.BIB_INDEX " + "  WHERE INDEX_CODE   = '655H' "
            + "  AND NORMAL_HEADING = 'SUBSET CIRCBIB' " + "  ) ";

    public UpdatePrintBibInputStream(final DataSource dataSource, final Executor executor, final StartDate startDate) {
        super(dataSource, executor, startDate);
    }

    @Override
    protected String getSelectIDListSQL() {
        return QUERY;
    }
}
