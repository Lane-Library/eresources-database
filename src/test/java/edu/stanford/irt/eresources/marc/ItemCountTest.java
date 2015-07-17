package edu.stanford.irt.eresources.marc;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;

public class ItemCountTest {

    private Connection connection;

    private DataSource dataSource;

    private ItemCounter itemCounter;

    private Statement pstmt;

    private ResultSet resultSet;

    @Before
    public void setUp() {
        this.dataSource = createMock(DataSource.class);
        this.itemCounter = new ItemCounter(this.dataSource);
        this.connection = createMock(Connection.class);
        this.pstmt = createMock(Statement.class);
        this.resultSet = createMock(ResultSet.class);
    }

    @Test
    public void testItemCountInt() throws SQLException {
        expect(this.dataSource.getConnection()).andReturn(this.connection).times(2);
        expect(this.connection.createStatement()).andReturn(this.pstmt).times(2);
        expect(this.pstmt.executeQuery(isA(String.class))).andReturn(this.resultSet).times(2);
        expect(this.resultSet.next()).andReturn(true);
        expect(this.resultSet.getString(1)).andReturn("1");
        expect(this.resultSet.getInt(2)).andReturn(1);
        expect(this.resultSet.next()).andReturn(false);
        this.resultSet.close();
        this.pstmt.close();
        this.connection.close();
        expect(this.resultSet.next()).andReturn(true);
        expect(this.resultSet.getString(1)).andReturn("1");
        expect(this.resultSet.getInt(2)).andReturn(2);
        expect(this.resultSet.next()).andReturn(false);
        this.resultSet.close();
        this.pstmt.close();
        this.connection.close();
        replay(this.dataSource, this.connection, this.pstmt, this.resultSet);
        assertEquals(1, this.itemCounter.getItemCount("1").getTotal());
        assertEquals(2, this.itemCounter.getItemCount("1").getAvailable());
        verify(this.dataSource, this.connection, this.pstmt, this.resultSet);
    }
}
