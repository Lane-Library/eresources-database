package edu.stanford.irt.eresources.marc.sfx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.PipedOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import edu.stanford.irt.eresources.EresourceDatabaseException;
import edu.stanford.irt.eresources.marc.MARCRecordSupport;
import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.RecordCollection;

class SfxFileCatalogRecordServiceTest extends MARCRecordSupport {

    ThreadPoolTaskExecutor executor;

    SfxFileCatalogRecordService recordService;

    @BeforeEach
    void setUp() {
        this.executor = new ThreadPoolTaskExecutor();
        this.executor.initialize();
        this.recordService = new SfxFileCatalogRecordService("src/test/resources/edu/stanford/irt/eresources/marc/sfx",
                this.executor);
    }

    @Test
    final void testGetRecordStream() {
        RecordCollection rc = new RecordCollection(this.recordService.getRecordStream(0));
        assertNotNull(rc);
        while (rc.hasNext()) {
            Record mr = rc.next();
            assertTrue(mr.toString().contains("Madison: Proposed Amendments to the Constitution"));
        }
    }

    @Test
    final void testRunRead() throws Exception {
        byte[] expectedMarc = Files
                .readAllBytes(Paths.get("src/test/resources/edu/stanford/irt/eresources/marc/sfx/sfx-export.marc"));
        PipedOutputStream output = new PipedOutputStream();
        output.connect(this.recordService);
        this.recordService.setPipedOutputStream(output);

        this.recordService.run();
        byte[] marcFromGzippedXml = this.recordService.readAllBytes();
        assertEquals(new String(expectedMarc), new String(marcFromGzippedXml));
    }

    @Test
    final void testRunReadBadFile() throws Exception {
        File tempFile = File.createTempFile("bad", ".xml-marc.gz");
        this.recordService = new SfxFileCatalogRecordService(tempFile.getParent(), this.executor);
        assertThrows(EresourceDatabaseException.class, () -> {
            this.recordService.run();
        });
    }

    @Test
    final void testGetRecordStreamNullBasePath() {
        this.recordService = new SfxFileCatalogRecordService(null, this.executor);
        assertThrows(IllegalStateException.class, () -> {
            new RecordCollection(this.recordService.getRecordStream(0));
        });
    }

}
