package edu.stanford.irt.eresources;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class EresourceDatabaseExceptionTest {

    @Test
    final void testEresourceDatabaseException() {
        assertEquals("foo", new EresourceDatabaseException("foo").getMessage());
        Throwable foo = new Throwable();
        assertEquals(foo, new EresourceDatabaseException(foo).getCause());
        EresourceDatabaseException erDbException = new EresourceDatabaseException("bar", foo);
        assertEquals(foo, erDbException.getCause());
        assertEquals("bar", erDbException.getMessage());
    }
}
