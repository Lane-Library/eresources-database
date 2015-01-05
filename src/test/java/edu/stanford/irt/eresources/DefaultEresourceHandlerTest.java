package edu.stanford.irt.eresources;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;

public class DefaultEresourceHandlerTest {

    private Connection connection;

    private DataSource dataSource;

    private Eresource eresource;

    private DefaultEresourceHandler handler;

    private Link link;

    private PreparedStatement pStmnt;

    private BlockingQueue<Eresource> queue;

    private Statement stmt;

    private EresourceSQLTranslator translator;

    private Version version;

    @Before
    public void setUp() {
        this.dataSource = createMock(DataSource.class);
        this.queue = createMock(BlockingQueue.class);
        this.translator = createMock(EresourceSQLTranslator.class);
        this.handler = new DefaultEresourceHandler(this.dataSource, this.queue, this.translator);
        this.eresource = createMock(Eresource.class);
        this.version = createMock(Version.class);
        this.link = createMock(Link.class);
        this.connection = createMock(Connection.class);
        this.stmt = createMock(Statement.class);
        this.pStmnt = createMock(PreparedStatement.class);
    }

    @Test
    public void testHandleEresource() throws InterruptedException {
        expect(this.eresource.getVersions()).andReturn(Collections.singleton(this.version));
        expect(this.version.getLinks()).andReturn(Collections.singletonList(this.link));
        this.queue.put(this.eresource);
        replay(this.eresource, this.version, this.queue, this.dataSource, this.translator);
        this.handler.handleEresource(this.eresource);
        assertEquals(1, this.handler.getCount());
        verify(this.eresource, this.version, this.queue, this.dataSource, this.translator);
    }

    @Test(expected = EresourceException.class)
    public void testHandleEresourceInterrupted() throws InterruptedException {
        expect(this.eresource.getVersions()).andReturn(Collections.singleton(this.version));
        expect(this.version.getLinks()).andReturn(Collections.singletonList(this.link));
        this.queue.put(this.eresource);
        expectLastCall().andThrow(new InterruptedException());
        replay(this.eresource, this.version, this.queue, this.dataSource, this.translator);
        this.handler.handleEresource(this.eresource);
    }

    @Test
    public void testRun() throws InterruptedException, SQLException {
        expect(this.dataSource.getConnection()).andReturn(this.connection);
        expect(this.connection.createStatement()).andReturn(this.stmt);
        expect(this.connection.prepareStatement(isA(String.class))).andReturn(this.pStmnt).times(2);
        expect(this.queue.isEmpty()).andReturn(true).atLeastOnce();
        expect(this.queue.poll(1, TimeUnit.SECONDS)).andReturn(this.eresource);
        List<String> sql = new ArrayList<String>();
        sql.add("foo");
        expect(this.translator.getInsertSQL(this.eresource)).andReturn(sql);
        this.stmt.addBatch("foo");
        expect(this.stmt.executeBatch()).andReturn(new int[0]);
        expect(this.queue.poll(1, TimeUnit.SECONDS)).andReturn(null).atLeastOnce();
        this.stmt.close();
        this.connection.close();
        replay(this.eresource, this.version, this.queue, this.dataSource, this.translator, this.connection, this.stmt);
        Thread thread = new Thread(this.handler);
        thread.start();
        Thread.sleep(1000);
        this.handler.stop();
        thread.join();
        verify(this.eresource, this.version, this.queue, this.dataSource, this.translator, this.connection, this.stmt);
    }
}
