package edu.stanford.irt.eresources;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.commons.net.ftp.FTPFile;
import org.junit.Test;

public class PubmedFtpFileFilterTest {

    @Test
    public final void testPubmedFtpFileFilter() {
        PubmedFtpFileFilter filter = new PubmedFtpFileFilter("src/test/resources/edu/stanford/irt/eresources/");
        FTPFile file = new FTPFile();
        file.setName("file.xml.gz");
        assertFalse(filter.accept(file));
        file.setName("file.xml");
        assertFalse(filter.accept(file));
        file.setName("4.xml.gz");
        assertTrue(filter.accept(file));
        file.setName("4.xml");
        assertTrue(filter.accept(file));
        file.setName("foo.txt");
        assertFalse(filter.accept(file));
    }
}
