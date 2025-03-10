package edu.stanford.irt.eresources;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JaxpXMLReaderTest {

    JaxpXMLReader reader;

    @BeforeEach
    public void setUp() throws Exception {
        this.reader = new JaxpXMLReader();
    }

    @Test
    public final void testJaxpXMLReader() {
        assertNotNull(this.reader);
    }
}
