package edu.stanford.irt.eresources;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import jdepend.framework.JDepend;

class PackageCycleTest {

    @Test
    void testCycles() throws IOException {
        JDepend jdepend = new JDepend();
        jdepend.addDirectory("target/classes");
        jdepend.analyze();
        assertFalse(jdepend.containsCycles());
    }
}
