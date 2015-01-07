package edu.stanford.irt.eresources;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
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

public class UpdateEresourceHandlerTest {

    private Connection connection;

    private DataSource dataSource;

    private Eresource eresource;

    private UpdateEresourceHandler handler;

    private PreparedStatement pStmnt;

    private BlockingQueue<Eresource> queue;

    private ResultSet resultSet;

    private Statement stmt;

    private EresourceSQLTranslator translator;

    @Before
    public void setUp() {
        this.dataSource = createMock(DataSource.class);
        this.queue = createMock(BlockingQueue.class);
        this.translator = createMock(EresourceSQLTranslator.class);
        this.handler = new UpdateEresourceHandler(this.dataSource, this.queue, this.translator);
        this.connection = createMock(Connection.class);
        this.stmt = createMock(Statement.class);
        this.pStmnt = createMock(PreparedStatement.class);
        this.eresource = createMock(Eresource.class);
        this.resultSet = createMock(ResultSet.class);
    }

    @Test
    public void testRun() throws InterruptedException, SQLException {
        expect(this.dataSource.getConnection()).andReturn(this.connection);
        expect(this.connection.createStatement()).andReturn(this.stmt);
        expect(this.connection.prepareStatement(isA(String.class))).andReturn(this.pStmnt).times(2);
        expect(this.queue.isEmpty()).andReturn(true).atLeastOnce();
        expect(this.queue.poll(1, TimeUnit.SECONDS)).andReturn(this.eresource);
        expect(this.eresource.isClone()).andReturn(false);
        expect(this.translator.getEresourceIdSQL(this.eresource)).andReturn("eresourceIDSql");
        expect(this.stmt.executeQuery("eresourceIDSql")).andReturn(this.resultSet);
        expect(this.resultSet.next()).andReturn(true);
        expect(this.resultSet.getString(1)).andReturn("1");
        expect(this.resultSet.next()).andReturn(false);
        this.resultSet.close();
        this.stmt.addBatch("DELETE FROM ERESOURCE WHERE ERESOURCE_ID = 1");
        this.stmt.addBatch("DELETE FROM VERSION WHERE ERESOURCE_ID = 1");
        this.stmt.addBatch("DELETE FROM LINK WHERE ERESOURCE_ID = 1");
        this.stmt.addBatch("DELETE FROM TYPE WHERE ERESOURCE_ID = 1");
        this.stmt.addBatch("DELETE FROM SUBSET WHERE ERESOURCE_ID = 1");
        this.stmt.addBatch("DELETE FROM MESH WHERE ERESOURCE_ID = 1");
        expect(this.stmt.executeBatch()).andReturn(null);
        List<String> sql = new ArrayList<String>();
        sql.add("foo");
        expect(this.translator.getInsertSQL(this.eresource)).andReturn(sql);
        this.stmt.addBatch("foo");
        expect(this.stmt.executeBatch()).andReturn(new int[0]);
        expect(this.queue.poll(1, TimeUnit.SECONDS)).andReturn(null).atLeastOnce();
        this.stmt.close();
        this.connection.close();
        replay(this.eresource, this.queue, this.dataSource, this.translator, this.connection, this.stmt, this.resultSet);
        Thread thread = new Thread(this.handler);
        thread.start();
        Thread.sleep(1000);
        this.handler.stop();
        thread.join();
        verify(this.eresource, this.queue, this.dataSource, this.translator, this.connection, this.stmt, this.resultSet);
    }
}
