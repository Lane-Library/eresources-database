package edu.stanford.irt.eresources.marc;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.concurrent.Executor;

import javax.sql.DataSource;

import edu.stanford.irt.eresources.StartDate;

public abstract class UpdateEresourceInputStream extends EresourceInputStream {

    private StartDate startDate;

    public UpdateEresourceInputStream(final DataSource dataSource, final Executor executor, final StartDate startDate) {
        super(dataSource, executor);
        this.startDate = startDate;
    }

    @Override
    protected void prepareListStatement(final PreparedStatement stmt) throws SQLException {
        char[] queryString = getSelectIDListSQL().toCharArray();
        int qmarkCount = 0;
        for (char element : queryString) {
            if (element == '?') {
                qmarkCount++;
            }
        }
        long time = this.startDate.getStartDate().getTime();
        for (int i = 1; i <= qmarkCount; i++) {
            stmt.setTimestamp(i, new Timestamp(time));
        }
    }
}
