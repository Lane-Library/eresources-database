package edu.stanford.irt.eresources.jdbc;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;

import edu.stanford.irt.eresources.Eresource;
import edu.stanford.irt.eresources.Link;
import edu.stanford.irt.eresources.Version;

public class JDBCLoaderTest {

    private Connection connection;

    private DataSource dataSource;

    private Eresource eresource;

    private JDBCLoader loader;

    private Link link;

    private PreparedStatement pStmnt;

    private Statement stmt;

    private EresourceSQLTranslator translator;

    private Version version;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        this.dataSource = createMock(DataSource.class);
        this.translator = createMock(EresourceSQLTranslator.class);
        this.loader = new JDBCLoader(this.dataSource, this.translator);
        this.eresource = createMock(Eresource.class);
        this.version = createMock(Version.class);
        this.link = createMock(Link.class);
        this.connection = createMock(Connection.class);
        this.stmt = createMock(Statement.class);
        this.pStmnt = createMock(PreparedStatement.class);
    }

    @Test
    public void testHandleEresource() throws SQLException {
        expect(this.dataSource.getConnection()).andReturn(this.connection);
        expect(this.connection.createStatement()).andReturn(this.stmt);
        expect(this.connection.prepareStatement(isA(String.class))).andReturn(this.pStmnt).times(2);
//        expect(this.queue.isEmpty()).andReturn(false);
        expect(this.translator.getInsertSQL(this.eresource)).andReturn(Collections.<String>emptyList());
        expect(this.stmt.executeBatch()).andReturn(null);
//        expect(this.queue.isEmpty()).andReturn(true);
        this.pStmnt.close();
        this.pStmnt.close();
        this.stmt.close();
        this.connection.close();
//        expect(this.eresource.getVersions()).andReturn(Collections.singleton(this.version));
//        expect(this.version.getLinks()).andReturn(Collections.singletonList(this.link));
//        expect(this.queue.add(this.eresource)).andReturn(true);
        replay(this.pStmnt, this.stmt, this.connection, this.eresource, this.version, this.dataSource, this.translator);
        this.loader.load(this.eresource);
//        assertEquals(1, this.loader.getCount());
        verify(this.pStmnt, this.stmt, this.connection, this.eresource, this.version, this.dataSource, this.translator);
    }
}
