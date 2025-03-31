package edu.stanford.irt.eresources.pubmed;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.xbib.io.ftp.client.FTPFile;

class PubmedFtpFileFilterTest {

    @Test
    final void testPubmedFtpFileFilter() {
        PubmedFtpFileFilter filter = new PubmedFtpFileFilter("src/test/resources/edu/stanford/irt/eresources/pubmed/");
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
