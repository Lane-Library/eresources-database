package edu.stanford.irt.eresources;

import java.io.IOException;
import java.sql.SQLException;

public class DBReload extends DBLoader {

    public static void main(final String[] args) throws SQLException, IOException {
        DBLoader.main(new String[] { "db-reload" });
    }
}
