package edu.stanford.irt.eresources.pubmed;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.stanford.irt.eresources.EresourceDatabaseException;

public class AbstractPubmedDataFetcherTest {

    public class PubmedDataFetcherTest extends AbstractPubmedDataFetcher {

        public PubmedDataFetcherTest() {
            //
        }
    }

    private static final String BP = "er-test";

    private static final String TODAY = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

    PubmedDataFetcherTest fetcher;

    @AfterAll
    public static void tearDownAfterClass() throws Exception {
        FileUtils.forceDelete(new File(BP));
    }

    @BeforeEach
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
            assertTrue(false);
        }
        this.fetcher.pmidListToFiles(pmids, "baseFilename");
        // fails on drone ... not sure why since succeeded 6/5
        // assertTrue(new File(BP + "/" + TODAY + "/baseFilename1.xml").exists());
    }

    @Test
    public final void testPmidListToFilesIOError() throws Exception {
        FileUtils.forceDelete(new File(BP));
        List<String> pmids = new ArrayList<>();
        pmids.add("12345");
        if (EutilsIsReachable.eutilsIsReachable()) {

        }
        assertThrows(EresourceDatabaseException.class, () -> {
            this.fetcher.pmidListToFiles(pmids, "baseFilename");
        });

        new File(BP).mkdir();
    }

    @Test
    public final void testSetBasePathEmpty() {
        PubmedDataFetcherTest myFetcher = new PubmedDataFetcherTest();
        assertThrows(IllegalArgumentException.class, () -> {
            myFetcher.setBasePath("");
        });
    }

    @Test
    public final void testSetBasePathNull() {
        PubmedDataFetcherTest myFetcher = new PubmedDataFetcherTest();
        assertThrows(IllegalArgumentException.class, () -> {
            myFetcher.setBasePath(null);
        });
    }
}
