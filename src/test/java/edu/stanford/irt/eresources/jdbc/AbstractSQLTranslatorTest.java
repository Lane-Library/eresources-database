package edu.stanford.irt.eresources.jdbc;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class AbstractSQLTranslatorTest {

    private AbstractSQLTranslator translator;

    @Before
    public void setUp() {
        this.translator = new AbstractSQLTranslator();
    }

    @Test
    public void testApostrophize() {
        assertEquals("'foo ''bar''s'''", this.translator.apostrophize("foo 'bar's'"));
    }

    @Test
    public void testApostrophizeNull() {
        assertEquals("NULL", this.translator.apostrophize(null));
    }
}
