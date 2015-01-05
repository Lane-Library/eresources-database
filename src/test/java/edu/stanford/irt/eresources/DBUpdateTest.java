package edu.stanford.irt.eresources;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

public class DBUpdateTest {

    private ResultSet resultSet;

    private Statement statement;

    private Timestamp timeStamp;

    private DBUpdate update;

    @Before
    public void setUp() {
        this.update = new DBUpdate();
        this.statement = createMock(Statement.class);
        this.resultSet = createMock(ResultSet.class);
        this.timeStamp = createMock(Timestamp.class);
    }

    @Test
    public void testGetUpdatedDate() throws SQLException {
        expect(this.statement.executeQuery("SELECT MAX(UPDATED) FROM ERESOURCE")).andReturn(this.resultSet);
        expect(this.resultSet.next()).andReturn(true);
        expect(this.resultSet.getTimestamp(1)).andReturn(this.timeStamp);
        this.resultSet.close();
        expect(this.timeStamp.getTime()).andReturn(1L);
        replay(this.statement, this.resultSet, this.timeStamp);
        assertEquals(new Date(1), this.update.getUpdatedDate(this.statement));
        verify(this.statement, this.resultSet, this.timeStamp);
    }

    @Test(expected = SQLException.class)
    public void testGetUpdatedDateCloseException() throws SQLException {
        expect(this.statement.executeQuery("SELECT MAX(UPDATED) FROM ERESOURCE")).andReturn(this.resultSet);
        expect(this.resultSet.next()).andReturn(true);
        expect(this.resultSet.getTimestamp(1)).andReturn(this.timeStamp);
        this.resultSet.close();
        expectLastCall().andThrow(new SQLException());
        expect(this.timeStamp.getTime()).andReturn(1L);
        replay(this.statement, this.resultSet, this.timeStamp);
        this.update.getUpdatedDate(this.statement);
    }

    @Test(expected = EresourceException.class)
    public void testGetUpdatedDateNoResult() throws SQLException {
        expect(this.statement.executeQuery("SELECT MAX(UPDATED) FROM ERESOURCE")).andReturn(this.resultSet);
        expect(this.resultSet.next()).andReturn(false);
        replay(this.statement, this.resultSet);
        this.update.getUpdatedDate(this.statement);
    }

    @Test
    public void testGetUpdatedDateNullTimestamp() throws SQLException {
        expect(this.statement.executeQuery("SELECT MAX(UPDATED) FROM ERESOURCE")).andReturn(this.resultSet);
        expect(this.resultSet.next()).andReturn(true);
        expect(this.resultSet.getTimestamp(1)).andReturn(null);
        this.resultSet.close();
        replay(this.statement, this.resultSet);
        assertEquals(new Date(0), this.update.getUpdatedDate(this.statement));
        verify(this.statement, this.resultSet);
    }

    @Test(expected = SQLException.class)
    public void testGetUpdatedDateResultSetException() throws SQLException {
        expect(this.statement.executeQuery("SELECT MAX(UPDATED) FROM ERESOURCE")).andReturn(this.resultSet);
        expect(this.resultSet.next()).andThrow(new SQLException());
        replay(this.statement, this.resultSet);
        this.update.getUpdatedDate(this.statement);
    }

    @Test(expected = SQLException.class)
    public void testGetUpdatedDateStatementException() throws SQLException {
        expect(this.statement.executeQuery("SELECT MAX(UPDATED) FROM ERESOURCE")).andThrow(new SQLException());
        replay(this.statement);
        this.update.getUpdatedDate(this.statement);
    }

    @Test(expected = SQLException.class)
    public void testGetUpdatedDateTimestampException() throws SQLException {
        expect(this.statement.executeQuery("SELECT MAX(UPDATED) FROM ERESOURCE")).andReturn(this.resultSet);
        expect(this.resultSet.next()).andReturn(true);
        expect(this.resultSet.getTimestamp(1)).andThrow(new SQLException());
        replay(this.statement, this.resultSet);
        this.update.getUpdatedDate(this.statement);
    }
}
