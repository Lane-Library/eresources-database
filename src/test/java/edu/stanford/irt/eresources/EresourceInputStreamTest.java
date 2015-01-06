package edu.stanford.irt.eresources;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.concurrent.Executor;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;

public class EresourceInputStreamTest {

    private static class TestEresourceInputStream extends EresourceInputStream {

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

    private EresourceInputStream stream;

    private Timestamp timestamp;

    @Before
    public void setUp() {
        this.stream = new TestEresourceInputStream();
        this.timestamp = createMock(Timestamp.class);
        this.stream.setStartDate(this.timestamp);
        this.dataSource = createMock(DataSource.class);
        this.stream.setDataSource(this.dataSource);
        this.executor = java.util.concurrent.Executors.newSingleThreadExecutor();
        this.stream.setExecutor(this.executor);
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
        this.pstmt.setTimestamp(1, this.timestamp);
        this.pstmt.setTimestamp(2, this.timestamp);
        replay(this.pstmt);
        this.stream.prepareListStatement(this.pstmt);
        verify(this.pstmt);
    }

    @Test
    public void testRun() throws SQLException, IOException {
        expect(this.dataSource.getConnection()).andReturn(this.connection);
        expect(this.connection.prepareStatement("bibQuery")).andReturn(this.pstmt);
        expect(this.connection.prepareStatement("mfhdQuery")).andReturn(this.pstmt);
        expect(this.connection.prepareStatement("? ?")).andReturn(this.pstmt);
        this.pstmt.setTimestamp(1, this.timestamp);
        this.pstmt.setTimestamp(2, this.timestamp);
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
        replay(this.pstmt, this.dataSource, this.connection, this.timestamp, this.resultSet, this.metaData);
        this.stream.read();
        verify(this.pstmt, this.dataSource, this.connection, this.timestamp, this.resultSet, this.metaData);
    }
}
