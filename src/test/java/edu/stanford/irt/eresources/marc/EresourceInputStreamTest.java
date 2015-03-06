package edu.stanford.irt.eresources.marc;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.Executor;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;

import edu.stanford.irt.eresources.StartDate;

public class EresourceInputStreamTest {

    private static class TestEresourceInputStream extends UpdateEresourceInputStream {

        public TestEresourceInputStream(final DataSource dataSource, final Executor executor, final StartDate startDate) {
            super(dataSource, executor, startDate);
        }

        @Override
        protected String getBibQuery() {
            return "bibQuery";
        }

        @Override
        protected String getMfhdQuery() {
            return "mfhdQuery";
        }

        @Override
        protected String getSelectIDListSQL() {
            return "? ?";
        }
    }

    private Connection connection;

    private DataSource dataSource;

    private Executor executor;

    private ResultSetMetaData metaData;

    private PreparedStatement pstmt;

    private ResultSet resultSet;

    private StartDate startDate;

    private EresourceInputStream stream;

    @Before
    public void setUp() {
        this.dataSource = createMock(DataSource.class);
        this.executor = java.util.concurrent.Executors.newSingleThreadExecutor();
        this.startDate = createMock(StartDate.class);
        this.stream = new TestEresourceInputStream(this.dataSource, this.executor, this.startDate);
        this.connection = createMock(Connection.class);
        this.pstmt = createMock(PreparedStatement.class);
        this.resultSet = createMock(ResultSet.class);
        this.metaData = createMock(ResultSetMetaData.class);
    }

    // @Test
    // public void testRead() throws IOException {
    // assertEquals(null, this.stream.read());
    // }
    @Test
    public void testPrepareListStatement() throws SQLException {
        expect(this.startDate.getStartDate()).andReturn(new Date(0));
        this.pstmt.setTimestamp(eq(1), isA(Timestamp.class));
        this.pstmt.setTimestamp(eq(2), isA(Timestamp.class));
        replay(this.pstmt, this.startDate);
        this.stream.prepareListStatement(this.pstmt);
        verify(this.pstmt, this.startDate);
    }

    @Test
    public void testRun() throws SQLException, IOException, InterruptedException {
        expect(this.dataSource.getConnection()).andReturn(this.connection);
        expect(this.connection.prepareStatement("bibQuery")).andReturn(this.pstmt);
        expect(this.connection.prepareStatement("mfhdQuery")).andReturn(this.pstmt);
        expect(this.connection.prepareStatement("? ?")).andReturn(this.pstmt);
        expect(this.startDate.getStartDate()).andReturn(new Date(0));
        this.pstmt.setTimestamp(eq(1), isA(Timestamp.class));
        this.pstmt.setTimestamp(eq(2), isA(Timestamp.class));
        expect(this.pstmt.executeQuery()).andReturn(this.resultSet);
        expect(this.resultSet.getMetaData()).andReturn(this.metaData);
        expect(this.metaData.getColumnCount()).andReturn(2);
        expect(this.resultSet.next()).andReturn(true);
        expect(this.resultSet.getString(1)).andReturn("1");
        expect(this.resultSet.getString(2)).andReturn("2");
        expect(this.resultSet.next()).andReturn(false);
        this.resultSet.close();
        expectLastCall().times(3);
        this.pstmt.close();
        expectLastCall().times(3);
        this.pstmt.setString(1, "1");
        expect(this.pstmt.executeQuery()).andReturn(this.resultSet);
        expect(this.resultSet.next()).andReturn(true);
        expect(this.resultSet.getBytes(2)).andReturn(new byte[0]);
        expect(this.resultSet.next()).andReturn(false);
        this.pstmt.setString(1, "2");
        expect(this.pstmt.executeQuery()).andReturn(this.resultSet);
        expect(this.resultSet.next()).andReturn(true);
        expect(this.resultSet.getBytes(2)).andReturn(new byte[0]);
        expect(this.resultSet.next()).andReturn(false);
        this.connection.close();
        replay(this.pstmt, this.dataSource, this.connection, this.startDate, this.resultSet, this.metaData);
        this.stream.read();
        Thread.sleep(100);
        verify(this.pstmt, this.dataSource, this.connection, this.startDate, this.resultSet, this.metaData);
    }
}
