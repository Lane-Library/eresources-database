package edu.stanford.irt.eresources;

import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.PipedOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import edu.stanford.irt.eresources.marc.MARCRecordSupport;
import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.RecordCollection;

class FileCatalogRecordServiceTest extends MARCRecordSupport {

    ThreadPoolTaskExecutor executor;

    FileCatalogRecordService recordService;

    @BeforeEach
    void setUp() throws Exception {
        this.executor = new ThreadPoolTaskExecutor();
        this.executor.initialize();
        this.recordService = new FileCatalogRecordService("src/test/resources/edu/stanford/irt/eresources/sul-marc",
                this.executor);
    }

    @Test
    final void testGetRecordStream() {
        RecordCollection rc = new RecordCollection(this.recordService.getRecordStream(0));
        assertNotNull(rc);
        while (rc.hasNext()) {
            Record r = rc.next();
            assertEquals("8208799", getRecordId(r));
        }
    }

    @Test
    final void testGetRecordStreamNullBasePath() {
        this.recordService = new FileCatalogRecordService(null, this.executor);
        assertThrows(IllegalStateException.class, () -> {
            new RecordCollection(this.recordService.getRecordStream(0));
        });
    }

    @Test
    final void testRun() throws Exception {
        byte[] data1 = new byte[8192];
        byte[] file1data = Files
                .readAllBytes(Paths.get("src/test/resources/edu/stanford/irt/eresources/sul-marc/data/8208799.marc"));
        for (int i = 0; i < file1data.length; i++) {
            data1[i] = file1data[i];
        }
        PipedOutputStream output = mock(PipedOutputStream.class);
        this.recordService.setPipedOutputStream(output);
        output.write(data1, 0, 3409);
        expectLastCall();
        output.close();
        replay(output);
        this.recordService.run();
        verify(output);
    }
}
