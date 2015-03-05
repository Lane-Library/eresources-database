package edu.stanford.irt.eresources.jdbc;

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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;

import edu.stanford.irt.eresources.Eresource;
import edu.stanford.irt.eresources.StartDate;

public class UpdateJDBCLoaderTest {

    private Connection connection;

    private DataSource dataSource;

    private Eresource eresource;

    private UpdateJDBCLoader loader;

    private PreparedStatement pStmnt;

    private ResultSet resultSet;

    private Statement stmt;

    private EresourceSQLTranslator translator;

    private StartDate startDate;

    @Before
    public void setUp() {
        this.dataSource = createMock(DataSource.class);
        this.translator = createMock(EresourceSQLTranslator.class);
        this.startDate = createMock(StartDate.class);
        this.loader = new UpdateJDBCLoader(this.dataSource, this.translator, this.startDate);
        this.connection = createMock(Connection.class);
        this.stmt = createMock(Statement.class);
        this.pStmnt = createMock(PreparedStatement.class);
        this.eresource = createMock(Eresource.class);
        this.resultSet = createMock(ResultSet.class);
    }

    @Test
    public void testRun() throws SQLException {
        expect(this.dataSource.getConnection()).andReturn(this.connection);
        expect(this.connection.createStatement()).andReturn(this.stmt);
        expect(this.connection.prepareStatement(isA(String.class))).andReturn(this.pStmnt).times(2);
        this.connection.setAutoCommit(false);
        expect(this.connection.createStatement()).andReturn(this.stmt);
        expect(this.stmt.executeQuery("SELECT MAX(UPDATED) FROM ERESOURCE")).andReturn(this.resultSet);
        expect(this.resultSet.next()).andReturn(true);
        expect(this.resultSet.getTimestamp(1)).andReturn(new Timestamp(1));
        this.startDate.initialize(new Date(1));
        this.resultSet.close();
        this.stmt.close();
//        expect(this.queue.isEmpty()).andReturn(false);
//        expect(this.queue.poll(1, TimeUnit.SECONDS)).andReturn(this.eresource);
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
//        expect(this.queue.isEmpty()).andReturn(true);
//        expect(this.queue.poll(1, TimeUnit.SECONDS)).andReturn(null).atLeastOnce();
//        this.connection.commit();
//        this.stmt.close();
//        this.connection.close();
        replay(this.startDate, this.eresource, this.dataSource, this.translator, this.connection, this.stmt, this.resultSet);
//        Thread thread = new Thread(this.handler);
//        thread.start();
//        Thread.sleep(1000);
//        this.handler.stop();
//        thread.join();
        this.loader.preProcess();
        this.loader.load(Collections.singletonList(this.eresource));
        verify(this.startDate, this.eresource, this.dataSource, this.translator, this.connection, this.stmt, this.resultSet);
    }
}
