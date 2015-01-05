package edu.stanford.irt.eresources;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Queue;
import java.util.concurrent.Executor;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;

public class DBLoaderTest {

    private Connection connection;

    private DataSource dataSource;

    private Executor executor;

    private EresourceHandler handler;

    private DBLoader loader;

    private Queue<Eresource> queue;

    private Statement statement;

    @Before
    public void setUp() {
        this.loader = new DBLoader();
        this.dataSource = createMock(DataSource.class);
        this.loader.setDataSource(this.dataSource);
        this.executor = createMock(Executor.class);
        this.loader.setExecutor(this.executor);
        this.handler = createMock(EresourceHandler.class);
        this.loader.setHandler(this.handler);
        this.queue = createMock(Queue.class);
        this.loader.setQueue(this.queue);
        this.connection = createMock(Connection.class);
        this.statement = createMock(Statement.class);
    }

    @Test
    public void testLoad() throws SQLException {
        expect(this.dataSource.getConnection()).andReturn(this.connection);
        expect(this.connection.createStatement()).andReturn(this.statement);
        this.connection.setAutoCommit(false);
        this.executor.execute(this.handler);
        this.handler.stop();
        expect(this.queue.isEmpty()).andReturn(true);
        this.connection.commit();
        expect(this.handler.getCount()).andReturn(2);
        this.statement.close();
        this.connection.close();
        replay(this.dataSource, this.connection, this.statement, this.executor, this.handler, this.queue);
        this.loader.load();
        verify(this.dataSource, this.connection, this.statement, this.executor, this.handler, this.queue);
    }
}
