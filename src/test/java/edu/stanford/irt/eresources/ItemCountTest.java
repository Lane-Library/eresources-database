package edu.stanford.irt.eresources;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertArrayEquals;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;


public class ItemCountTest {
    
    private ItemCount itemCount;
    private DataSource dataSource;
    private Connection connection;
    private PreparedStatement pstmt;
    private ResultSet resultSet;

    @Before
    public void setUp() {
        this.dataSource = createMock(DataSource.class);
        this.itemCount = new ItemCount(this.dataSource);
        this.connection = createMock(Connection.class);
        this.pstmt = createMock(PreparedStatement.class);
        this.resultSet = createMock(ResultSet.class);
    }

    @Test
    public void testItemCountInt() throws SQLException {
        expect(this.dataSource.getConnection()).andReturn(this.connection).times(2);
        expect(this.connection.prepareStatement(isA(String.class))).andReturn(this.pstmt).times(2);
        this.pstmt.setInt(1, 1);
        expectLastCall().times(2);
        expect(this.pstmt.executeQuery()).andReturn(this.resultSet).times(2);
        expect(this.resultSet.next()).andReturn(true).times(2);
        expect(this.resultSet.getInt(1)).andReturn(1);
        this.resultSet.close();
        this.pstmt.close();
        this.connection.close();
        expect(this.resultSet.getInt(1)).andReturn(2);
        this.resultSet.close();
        this.pstmt.close();
        this.connection.close();
        replay(this.dataSource, this.connection, this.pstmt, this.resultSet);
        assertArrayEquals(new int[] {1,2}, this.itemCount.itemCount(1));
        verify(this.dataSource, this.connection, this.pstmt, this.resultSet);
    }

    @Test
    public void testItemCountString() throws SQLException {
        expect(this.dataSource.getConnection()).andReturn(this.connection).times(2);
        expect(this.connection.prepareStatement(isA(String.class))).andReturn(this.pstmt).times(2);
        this.pstmt.setInt(1, 1);
        expectLastCall().times(2);
        expect(this.pstmt.executeQuery()).andReturn(this.resultSet).times(2);
        expect(this.resultSet.next()).andReturn(true).times(2);
        expect(this.resultSet.getInt(1)).andReturn(1);
        this.resultSet.close();
        this.pstmt.close();
        this.connection.close();
        expect(this.resultSet.getInt(1)).andReturn(2);
        this.resultSet.close();
        this.pstmt.close();
        this.connection.close();
        replay(this.dataSource, this.connection, this.pstmt, this.resultSet);
        assertArrayEquals(new int[] {1,2}, this.itemCount.itemCount("1"));
        verify(this.dataSource, this.connection, this.pstmt, this.resultSet);
    }
}
