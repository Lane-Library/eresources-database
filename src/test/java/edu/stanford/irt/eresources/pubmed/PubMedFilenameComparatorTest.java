package edu.stanford.irt.eresources.pubmed;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author ryanmax
 */
public class PubMedFilenameComparatorTest {

    private PubmedFilenameComparator comparator;

    @BeforeEach
    public void setUp() {
        this.comparator = new PubmedFilenameComparator();
    }

    @Test
    public void testCompare() {
        File f1 = new File("foo/b  ar");
        File f2 = new File("foo/bar");
        assertEquals(0, this.comparator.compare(f1, f2));
        File f3 = new File("1/2");
        File f4 = new File("1/2");
        assertEquals(0, this.comparator.compare(f3, f4));
        File f5 = new File("baseline/medline16n0455.xml.gz");
        File f6 = new File("baseline/medline16n0456.xml.gz");
        assertTrue(this.comparator.compare(f5, f6) < 0);
    }
}
