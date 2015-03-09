package edu.stanford.irt.eresources.jdbc;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.io.StringWriter;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;

import edu.stanford.irt.eresources.Eresource;
import edu.stanford.irt.eresources.StartDate;
import edu.stanford.irt.eresources.Version;

public class JDBCLoaderTest {

    private CallableStatement callable;

    private Clob clob;

    private Connection connection;

    private DataSource dataSource;

    private Eresource eresource;

    private JDBCLoader loader;

    private PreparedStatement pStmnt;

    private ResultSet resultSet;

    private StartDate startDate;

    private Statement stmt;

    private EresourceSQLTranslator translator;

    private Version version;

    @Before
    public void setUp() {
        this.dataSource = createMock(DataSource.class);
        this.translator = createMock(EresourceSQLTranslator.class);
        this.startDate = createMock(StartDate.class);
        this.loader = new JDBCLoader(this.dataSource, this.translator, this.startDate);
        this.eresource = createMock(Eresource.class);
        this.version = createMock(Version.class);
        this.connection = createMock(Connection.class);
        this.stmt = createMock(Statement.class);
        this.pStmnt = createMock(PreparedStatement.class);
        this.resultSet = createMock(ResultSet.class);
        this.clob = createMock(Clob.class);
        this.callable = createMock(CallableStatement.class);
    }

    @Test
    public void testHandleEresource() throws SQLException {
        expect(this.dataSource.getConnection()).andReturn(this.connection);
        expect(this.connection.createStatement()).andReturn(this.stmt);
        expect(this.connection.prepareStatement(isA(String.class))).andReturn(this.pStmnt).times(2);
        this.connection.setAutoCommit(false);
        List<String> sql = new ArrayList<String>();
        sql.add("eresource");
        expect(this.translator.getInsertSQL(this.eresource)).andReturn(sql);
        this.stmt.addBatch("eresource");
        expect(this.stmt.executeBatch()).andReturn(null);
        this.connection.commit();
        this.pStmnt.close();
        this.pStmnt.close();
        this.stmt.close();
        this.connection.close();
        replay(this.pStmnt, this.stmt, this.connection, this.eresource, this.version, this.dataSource, this.translator);
        this.loader.preProcess();
        this.loader.load(Collections.singletonList(this.eresource));
        this.loader.postProcess();
        verify(this.pStmnt, this.stmt, this.connection, this.eresource, this.version, this.dataSource, this.translator);
    }

    @Test
    public void testHandleEresourceTEXT() throws SQLException {
        expect(this.dataSource.getConnection()).andReturn(this.connection);
        expect(this.connection.createStatement()).andReturn(this.stmt);
        expect(this.connection.prepareStatement(isA(String.class))).andReturn(this.pStmnt).times(2);
        this.connection.setAutoCommit(false);
        List<String> sql = new ArrayList<String>();
        sql.add("TEXT:text");
        expect(this.translator.getInsertSQL(this.eresource)).andReturn(sql);
        expect(this.stmt.executeBatch()).andReturn(null);
        expect(this.stmt.executeQuery("SELECT ERESOURCE_ID_SEQ.CURRVAL FROM DUAL")).andReturn(this.resultSet);
        expect(this.resultSet.next()).andReturn(true);
        expect(this.resultSet.getString(1)).andReturn("12");
        this.pStmnt.setString(1, "12");
        this.resultSet.close();
        expect(this.pStmnt.executeQuery()).andReturn(this.resultSet);
        expect(this.resultSet.next()).andReturn(true);
        expect(this.resultSet.getClob(1)).andReturn(this.clob);
        StringWriter writer = new StringWriter();
        expect(this.clob.setCharacterStream(1)).andReturn(writer);
        this.resultSet.close();
        this.connection.commit();
        this.pStmnt.close();
        this.pStmnt.close();
        this.stmt.close();
        this.connection.close();
        replay(this.clob, this.resultSet, this.pStmnt, this.stmt, this.connection, this.eresource, this.version,
                this.dataSource, this.translator);
        this.loader.preProcess();
        this.loader.load(Collections.singletonList(this.eresource));
        this.loader.postProcess();
        assertEquals("text", writer.toString());
        verify(this.clob, this.resultSet, this.pStmnt, this.stmt, this.connection, this.eresource, this.version,
                this.dataSource, this.translator);
    }

    @Test
    public void testPostProcess() throws SQLException {
        expect(this.dataSource.getConnection()).andReturn(this.connection);
        this.connection.setAutoCommit(false);
        expect(this.connection.createStatement()).andReturn(this.stmt);
        expect(this.connection.prepareStatement(isA(String.class))).andReturn(this.pStmnt).times(2);
        expect(this.connection.prepareCall("call")).andReturn(this.callable);
        expect(this.callable.execute()).andReturn(true);
        this.callable.close();
        this.pStmnt.close();
        this.pStmnt.close();
        this.stmt.close();
        this.connection.commit();
        this.connection.close();
        replay(this.callable, this.dataSource, this.connection, this.stmt, this.pStmnt);
        this.loader.setCallStatements(Collections.singletonList("call"));
        this.loader.preProcess();
        this.loader.count++;
        this.loader.postProcess();
        verify(this.callable, this.dataSource, this.connection, this.stmt, this.pStmnt);
    }

    @Test
    public void testPreProcess() throws SQLException {
        expect(this.dataSource.getConnection()).andReturn(this.connection);
        this.connection.setAutoCommit(false);
        expect(this.connection.createStatement()).andReturn(this.stmt);
        expect(this.connection.prepareStatement(isA(String.class))).andReturn(this.pStmnt).times(2);
        expect(this.stmt.execute("create")).andReturn(true);
        replay(this.dataSource, this.connection, this.stmt, this.pStmnt);
        this.loader.setCreateStatements(Collections.singletonList("create"));
        this.loader.preProcess();
        verify(this.dataSource, this.connection, this.stmt, this.pStmnt);
    }
}
