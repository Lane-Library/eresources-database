package edu.stanford.irt.eresources;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class AbstractPubmedDataFetcherTest {

    public class PubmedDataFetcherTest extends AbstractPubmedDataFetcher {

        public PubmedDataFetcherTest() {
            //
        }
    }

    private static final String BP = "er-test";

    private static final String TODAY = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    PubmedDataFetcherTest fetcher;

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        FileUtils.forceDelete(new File(BP));
    }

    @Before
    public void setUp() throws Exception {
        new File(BP).mkdir();
        this.fetcher = new PubmedDataFetcherTest();
        this.fetcher.setBasePath(BP);
    }

    @Test
    public final void testPmidListToFiles() {
        assertFalse(new File(BP + "/" + TODAY + "/baseFilename1.xml").exists());
        List<String> pmids = new ArrayList<>();
        pmids.add("12345");
        if (!EutilsIsReachable.eutilsIsReachable()) {
            this.thrown.expect(EresourceDatabaseException.class);
        }
        this.fetcher.pmidListToFiles(pmids, "baseFilename");
        assertTrue(new File(BP + "/" + TODAY + "/baseFilename1.xml").exists());
    }

    @Test
    public final void testPmidListToFilesIOError() throws Exception {
        FileUtils.forceDelete(new File(BP));
        List<String> pmids = new ArrayList<>();
        pmids.add("12345");
        if (EutilsIsReachable.eutilsIsReachable()) {
            this.thrown.expect(EresourceDatabaseException.class);
        }
        this.fetcher.pmidListToFiles(pmids, "baseFilename");
        new File(BP).mkdir();
    }

    @Test
    public final void testSetBasePathNull() {
        this.thrown.expect(IllegalArgumentException.class);
        PubmedDataFetcherTest myFetcher = new PubmedDataFetcherTest();
        myFetcher.setBasePath(null);
    }
}
