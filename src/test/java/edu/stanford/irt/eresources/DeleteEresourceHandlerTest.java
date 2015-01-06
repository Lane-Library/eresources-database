package edu.stanford.irt.eresources;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;

public class DeleteEresourceHandlerTest {

    private Connection connection;

    private DataSource dataSource;

    private Eresource eresource;

    private DeleteEresourceHandler handler;

    private PreparedStatement pStmnt;

    private BlockingQueue<Eresource> queue;

    private ResultSet resultSet;

    private Statement stmt;

    @Before
    public void setUp() {
        this.queue = createMock(BlockingQueue.class);
        this.dataSource = createMock(DataSource.class);
        this.handler = new DeleteEresourceHandler(this.dataSource, this.queue);
        this.eresource = createMock(Eresource.class);
        this.connection = createMock(Connection.class);
        this.stmt = createMock(Statement.class);
        this.pStmnt = createMock(PreparedStatement.class);
        this.resultSet = createMock(ResultSet.class);
    }

    @Test
    public void testHandleEresource() throws InterruptedException {
        this.queue.put(this.eresource);
        replay(this.queue, this.dataSource, this.eresource);
        this.handler.handleEresource(this.eresource);
        verify(this.queue, this.dataSource, this.eresource);
    }

    @Test(expected = EresourceException.class)
    public void testHandleEresourceThrowInterrupted() throws InterruptedException {
        this.queue.put(this.eresource);
        expectLastCall().andThrow(new InterruptedException());
        replay(this.queue, this.dataSource, this.eresource);
        this.handler.handleEresource(this.eresource);
    }

    @Test
    public void testRun() throws SQLException, InterruptedException {
        expect(this.dataSource.getConnection()).andReturn(this.connection).times(2);
        expect(this.connection.createStatement()).andReturn(this.stmt).times(2);
        expect(this.stmt.executeQuery("SELECT RECORD_TYPE, RECORD_ID FROM ERESOURCE")).andReturn(this.resultSet);
        expect(this.resultSet.next()).andReturn(true);
        expect(this.resultSet.getString("RECORD_TYPE")).andReturn("recordType");
        expect(this.resultSet.getInt("RECORD_ID")).andReturn(1);
        expect(this.resultSet.next()).andReturn(true);
        expect(this.resultSet.getString("RECORD_TYPE")).andReturn("recordType");
        expect(this.resultSet.getInt("RECORD_ID")).andReturn(2);
        expect(this.resultSet.next()).andReturn(false);
        this.resultSet.close();
        expect(this.connection.prepareStatement(isA(String.class))).andReturn(this.pStmnt);
        this.pStmnt.setString(1, "recordType");
        this.pStmnt.setInt(2, 2);
        expect(this.pStmnt.executeQuery()).andReturn(this.resultSet);
        expect(this.resultSet.next()).andReturn(true);
        expect(this.resultSet.getInt(1)).andReturn(0);
        this.stmt.addBatch("DELETE FROM ERESOURCE WHERE ERESOURCE_ID = 0");
        this.stmt.addBatch("DELETE FROM VERSION WHERE ERESOURCE_ID = 0");
        this.stmt.addBatch("DELETE FROM LINK WHERE ERESOURCE_ID = 0");
        this.stmt.addBatch("DELETE FROM TYPE WHERE ERESOURCE_ID = 0");
        this.stmt.addBatch("DELETE FROM SUBSET WHERE ERESOURCE_ID = 0");
        this.stmt.addBatch("DELETE FROM MESH WHERE ERESOURCE_ID = 0");
        expect(this.stmt.executeBatch()).andReturn(null);
        expect(this.resultSet.next()).andReturn(false);
        this.resultSet.close();
        this.pStmnt.close();
        expect(this.queue.isEmpty()).andReturn(true).atLeastOnce();
        expect(this.queue.poll(1, TimeUnit.SECONDS)).andReturn(this.eresource);
        expect(this.eresource.getRecordType()).andReturn("recordType");
        expect(this.eresource.getRecordId()).andReturn(1);
        expect(this.queue.poll(1, TimeUnit.SECONDS)).andReturn(null).atLeastOnce();
        List<String> sql = new ArrayList<String>();
        sql.add("foo");
        this.stmt.close();
        expectLastCall().times(2);
        this.connection.close();
        expectLastCall().times(2);
        replay(this.eresource, this.queue, this.dataSource, this.connection, this.stmt, this.resultSet, this.pStmnt);
        Thread thread = new Thread(this.handler);
        thread.start();
        Thread.sleep(1000);
        this.handler.stop();
        thread.join();
        verify(this.eresource, this.queue, this.dataSource, this.connection, this.stmt, this.resultSet, this.pStmnt);
    }
}
