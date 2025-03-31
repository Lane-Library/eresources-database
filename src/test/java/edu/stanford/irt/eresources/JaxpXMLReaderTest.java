package edu.stanford.irt.eresources;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JaxpXMLReaderTest {

    JaxpXMLReader reader;

    @BeforeEach
    void setUp() throws Exception {
        this.reader = new JaxpXMLReader();
    }

    @Test
    final void testJaxpXMLReader() {
        assertNotNull(this.reader);
    }
}
