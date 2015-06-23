package edu.stanford.irt.eresources.marc;

import java.util.concurrent.Executor;

import javax.sql.DataSource;

public class SelectBibInputStream extends BibInputStream {

    public SelectBibInputStream(DataSource dataSource, Executor executor) {
        super(dataSource, executor);
    }

    private static final String QUERY =
            "SELECT BIB_MFHD.BIB_ID , "
          + "  BIB_MFHD.MFHD_ID "
          + "FROM LMLDB.BIB_MASTER, "
          + "  LMLDB.BIB_MFHD, "
          + "  LMLDB.MFHD_MASTER "
          + "WHERE BIB_MASTER.SUPPRESS_IN_OPAC  != 'Y' "
          + "AND MFHD_MASTER.SUPPRESS_IN_OPAC != 'Y' "
          + "AND BIB_MFHD.BIB_ID               = BIB_MASTER.BIB_ID "
          + "AND BIB_MFHD.MFHD_ID              = MFHD_MASTER.MFHD_ID "
          + "AND BIB_MFHD.BIB_ID IN "
          + "  (SELECT BIB_ID "
          + "  FROM LMLDB.BIB_INDEX " 
          + "  WHERE INDEX_CODE   = '655H' " 
          + "  AND NORMAL_HEADING = 'LANESELECT' " 
          + "  ) "
          + " ORDER BY BIB_MFHD.BIB_ID";

    @Override
    protected String getSelectIDListSQL() {
        return QUERY;
    }
}
