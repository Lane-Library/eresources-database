package edu.stanford.irt.eresources;

public class PrintBibInputStream extends BibInputStream {

    private static final String QUERY =
            "SELECT BIB_MFHD.BIB_ID , " +
                    "  BIB_MFHD.MFHD_ID " +
                    "FROM LMLDB.BIB_MASTER, " +
                    "  LMLDB.BIB_MFHD, " +
                    "  LMLDB.MFHD_MASTER " +
                    "WHERE BIB_MASTER.BIB_ID           = BIB_MFHD.BIB_ID " +
                    "AND BIB_MFHD.MFHD_ID              = MFHD_MASTER.MFHD_ID " +
                    "AND BIB_MASTER.SUPPRESS_IN_OPAC  != 'Y' " +
                    "AND MFHD_MASTER.SUPPRESS_IN_OPAC != 'Y' " +
                    "AND BIB_MFHD.BIB_ID               = BIB_MASTER.BIB_ID " +
                    "AND BIB_MASTER.BIB_ID            IN " +
                    "  (SELECT BIB_ID " +
                    "  FROM LMLDB.BIB_INDEX " +
                    "  WHERE INDEX_CODE = '2450' " +
                    "  AND DISPLAY_HEADING LIKE '%[print]%' " +
                    "  UNION " +
                    "    (SELECT BIB_ID " +
                    "    FROM LMLDB.BIB_INDEX " +
                    "    WHERE INDEX_CODE   = '655H' " +
                    "    AND NORMAL_HEADING = 'ARTICLES' " +
                    "    MINUS " +
                    "    SELECT BIB_ID " +
                    "    FROM LMLDB.BIB_INDEX " +
                    "    WHERE INDEX_CODE   = '655H' " +
                    "    AND NORMAL_HEADING = 'LANECONNEX' " +
                    "    MINUS " +
                    "    SELECT BIB_ID " +
                    "    FROM LMLDB.BIB_INDEX " +
                    "    WHERE INDEX_CODE = '2450' " +
                    "    AND DISPLAY_HEADING LIKE '%[print%' " +
                    "    MINUS " +
                    "    SELECT BIB_ID " +
                    "    FROM LMLDB.BIB_INDEX " +
                    "    WHERE INDEX_CODE = '2450' " +
                    "    AND DISPLAY_HEADING LIKE '%[digital%' " +
//                    "    INTERSECT " +
//                    "    SELECT BIB_ID " +
//                    "    FROM LMLDB.BIB_INDEX " +
//                    "    WHERE INDEX_CODE = '008D' " +
//                    "    AND REGEXP_LIKE(NORMAL_HEADING,'^\\d{4}$') " +
//                    "    AND TO_NUMBER(TRIM(NORMAL_HEADING)) >= TO_CHAR(SYSDATE - (10 * 365),'YYYY') " +
                    "    ) " +
                    // exclude Subset, Circbib fogbugz case 96195
                    "  MINUS " +
                    "  SELECT BIB_ID " +
                    "  FROM LMLDB.BIB_INDEX " +
                    "  WHERE INDEX_CODE   = '655H' " +
                    "  AND NORMAL_HEADING = 'SUBSET CIRCBIB' " +
                    "  ) ";

    @Override
    protected String getSelectIDListSQL() {
        return QUERY;
    }
}
