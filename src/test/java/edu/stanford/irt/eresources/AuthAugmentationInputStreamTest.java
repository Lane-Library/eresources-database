package edu.stanford.irt.eresources;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.Executor;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;

public class AuthAugmentationInputStreamTest {

    private DataSource dataSource;

    private Executor executor;

    private PreparedStatement stmt;

    private AuthAugmentationInputStream stream;

    @Before
    public void setUp() {
        this.dataSource = createMock(DataSource.class);
        this.executor = createMock(Executor.class);
        this.stream = new AuthAugmentationInputStream("12", this.dataSource, this.executor);
        this.stmt = createMock(PreparedStatement.class);
    }

    @Test
    public void testGetSelectIDListSQL100() {
        assertEquals("select bib_id from cifdb.bib_index where index_code = '0359' and  normal_heading = ?",
                this.stream.getSelectIDListSQL());
    }

    @Test
    public void testGetSelectIDListSQL600() {
        this.stream = new AuthAugmentationInputStream("12", this.dataSource, this.executor);
        assertEquals("select bib_id from cifdb.bib_index where index_code = '0359' and  normal_heading = ?",
                this.stream.getSelectIDListSQL());
    }

    @Test
    public void testGetSelectIDListSQL700() {
        this.stream = new AuthAugmentationInputStream("12", this.dataSource, this.executor);
        assertEquals("select bib_id from cifdb.bib_index where index_code = '0359' and  normal_heading = ?",
                this.stream.getSelectIDListSQL());
    }

    @Test
    public void testGetSelectIDListSQLMesh() {
        this.stream = new AuthAugmentationInputStream("12", this.dataSource, this.executor);
        assertEquals("select bib_id from cifdb.bib_index where index_code = '0359' and  normal_heading = ?",
                this.stream.getSelectIDListSQL());
    }

    @Test
    public void testPrepareListStatement() throws SQLException {
        this.stmt.setString(1, "12");
        replay(this.stmt);
        this.stream.prepareListStatement(this.stmt);
        verify(this.stmt);
    }
}
