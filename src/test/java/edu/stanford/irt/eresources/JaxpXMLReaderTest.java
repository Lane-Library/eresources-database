package edu.stanford.irt.eresources;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

public class JaxpXMLReaderTest {

    JaxpXMLReader reader;

    @Before
    public void setUp() throws Exception {
        this.reader = new JaxpXMLReader();
    }

    @Test
    public final void testJaxpXMLReader() {
        assertNotNull(this.reader);
    }
}
