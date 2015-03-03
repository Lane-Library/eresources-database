package edu.stanford.irt.eresources.jdbc;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;

import edu.stanford.irt.eresources.ETLProcessor;
import edu.stanford.irt.eresources.StartDate;

public class JDBCEresourcesETLAppTest {

    private Connection connection;

    private DataSource dataSource;

    private JDBCEresourcesETLApp app;

    private Statement statement;

    private StartDate startDate;

    private ETLProcessor<?> processor;

    @Before
    public void setUp() {
        this.dataSource = createMock(DataSource.class);
        this.processor = createMock(ETLProcessor.class);
        this.startDate = createMock(StartDate.class);
        this.app = new JDBCEresourcesETLApp(this.dataSource, Collections.singletonList(this.processor), this.startDate, true);
        this.connection = createMock(Connection.class);
        this.statement = createMock(Statement.class);
    }

    @Test
    public void testLoad() throws SQLException {
        this.processor.process();
        expect(this.dataSource.getConnection()).andReturn(this.connection).times(2);
        expect(this.connection.createStatement()).andReturn(this.statement);
        this.statement.close();
        this.connection.close();
        expectLastCall().times(2);
        replay(this.dataSource, this.processor, this.connection, this.statement);
        this.app.run();
        verify(this.dataSource, this.processor, this.connection, this.statement);
    }
}
