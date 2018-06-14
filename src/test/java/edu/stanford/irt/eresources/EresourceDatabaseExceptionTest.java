package edu.stanford.irt.eresources;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class EresourceDatabaseExceptionTest {

    @Test
    public final void testEresourceDatabaseException() {
        assertEquals("foo", new EresourceDatabaseException("foo").getMessage());
        Throwable foo = new Throwable();
        assertEquals(foo, new EresourceDatabaseException(foo).getCause());
        EresourceDatabaseException erDbException = new EresourceDatabaseException("bar", foo);
        assertEquals(foo, erDbException.getCause());
        assertEquals("bar", erDbException.getMessage());
    }
}
