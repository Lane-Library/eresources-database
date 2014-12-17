package edu.stanford.irt.eresources;

public class UpdatePrintBibInputStream extends BibInputStream {

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
            + "  AND item_status_1.item_status_date > ? " + "  ) " + ", PRINT_BIBS AS " + "  (SELECT BIB_ID "
            + "  FROM LMLDB.BIB_INDEX " + "  WHERE INDEX_CODE = '2450' " + "  AND DISPLAY_HEADING LIKE '%[print]%' "
            + "  UNION " + "    (SELECT BIB_ID " + "    FROM LMLDB.BIB_INDEX " + "    WHERE INDEX_CODE   = '655H' "
            + "    AND NORMAL_HEADING = 'ARTICLE' " + "    MINUS " + "    SELECT BIB_ID " + "    FROM LMLDB.BIB_INDEX "
            + "    WHERE INDEX_CODE   = '655H' " + "    AND NORMAL_HEADING = 'LANECONNEX' " + "    MINUS "
            + "    SELECT BIB_ID " + "    FROM LMLDB.BIB_INDEX " + "    WHERE INDEX_CODE = '2450' "
            + "    AND DISPLAY_HEADING LIKE '%[print%' " + "    MINUS "
            + "    SELECT BIB_ID "
            + "    FROM LMLDB.BIB_INDEX "
            + "    WHERE INDEX_CODE = '2450' "
            + "    AND DISPLAY_HEADING LIKE '%[digital%' "
            + "    ) "
            + "  INTERSECT "
            + "  SELECT BIB_ID "
            + "  FROM LMLDB.BIB_INDEX "
            + "  WHERE INDEX_CODE = '008D' "
            + "  AND REGEXP_LIKE(NORMAL_HEADING,'^\\d{4}$') "
            + "  AND TO_NUMBER(TRIM(NORMAL_HEADING)) >= TO_CHAR(SYSDATE - (10 * 365),'YYYY') "
            // exclude Subset, Circbib fogbugz case 96195
            + "  MINUS " + "  SELECT BIB_ID " + "  FROM LMLDB.BIB_INDEX " + "  WHERE INDEX_CODE   = '655H' "
            + "  AND NORMAL_HEADING = 'SUBSET CIRCBIB' " + "  ) " + "SELECT BIB_MFHD.BIB_ID , " + "  BIB_MFHD.MFHD_ID "
            + "FROM updated_bibs, " + "  PRINT_BIBS, " + "  LMLDB.BIB_MASTER, " + "  LMLDB.BIB_MFHD, "
            + "  LMLDB.MFHD_MASTER " + "WHERE PRINT_BIBS.BIB_ID                = LMLDB.BIB_MASTER.BIB_ID "
            + "AND updated_bibs.bib_id                = LMLDB.BIB_MASTER.BIB_ID "
            + "AND BIB_MASTER.SUPPRESS_IN_OPAC  != 'Y' " + "AND MFHD_MASTER.SUPPRESS_IN_OPAC != 'Y' "
            + "AND BIB_MFHD.BIB_ID               = BIB_MASTER.BIB_ID "
            + "AND BIB_MFHD.MFHD_ID              = MFHD_MASTER.MFHD_ID";

    public static final void main(final String[] args) {
        System.out.println(QUERY);
    }

    @Override
    protected String getSelectIDListSQL() {
        return QUERY;
    }
}
