package edu.stanford.irt.eresources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;

class IOUtilsTest {

    @Test
    final void testGetStream() throws Exception {
        File self = new File("src/test/java/edu/stanford/irt/eresources/IOUtilsTest.java");
        InputStream is = IOUtils.getStream(new URI("file://" + self.getAbsolutePath()).toURL());
        StringWriter writer = new StringWriter();
        org.apache.commons.io.IOUtils.copy(is, writer, StandardCharsets.UTF_8);
        assertTrue(writer.toString().contains("IOUtilsTest"));
    }

    @Test
    final void testGetUpdatedFiles() {
        List<File> files = IOUtils.getUpdatedFiles(new File("src/main/resources"), ".xml", 0);
        assertTrue(files.size() >= 10);
        files = IOUtils.getUpdatedFiles(new File("src/main/resources"), ".xml", Long.MAX_VALUE);
        assertEquals(0, files.size());
    }
}
