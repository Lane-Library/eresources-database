package edu.stanford.irt.eresources.marc.type;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import edu.stanford.irt.eresources.CatalogRecordService;
import edu.stanford.irt.eresources.FileCatalogRecordService;
import edu.stanford.irt.eresources.marc.MARCRecordSupport;
import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.RecordCollection;

/**
 * there's only one TypeFactory now, but keep separate unit tests for SUL and Lane
 */
public class SulTypeFactoryTest extends MARCRecordSupport {

    RecordCollection recordCollection;

    CatalogRecordService recordService;

    @Before
    public void setUp() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.initialize();
        this.recordService = new FileCatalogRecordService("src/test/resources/edu/stanford/irt/eresources/marc/sul",
                executor);
        this.recordCollection = new RecordCollection(this.recordService.getRecordStream(0));
    }

    @Test
    public final void testGetPrimaryTypeBookPrint() {
        while (this.recordCollection.hasNext()) {
            Record rec = this.recordCollection.next();
            if ("355410".equals(getRecordId(rec)) || "8223791".equals(getRecordId(rec))
                    || "13117763".equals(getRecordId(rec)) || "11514068".equals(getRecordId(rec))
                    || "360417".equals(getRecordId(rec))) {
                assertEquals("Book Print", TypeFactory.getPrimaryType(rec));
            }
        }
    }

    @Test
    public final void testGetPrimaryTypeJournalPrint() {
        while (this.recordCollection.hasNext()) {
            Record rec = this.recordCollection.next();
            if ("2996989".equals(getRecordId(rec))) {
                assertEquals("Journal Print", TypeFactory.getPrimaryType(rec));
            }
        }
    }

    @Test
    public final void testGetPrimaryTypeJournalDigital() {
        while (this.recordCollection.hasNext()) {
            Record rec = this.recordCollection.next();
            if ("8161047".equals(getRecordId(rec)) || "8208799".equals(getRecordId(rec))) {
                assertEquals("Journal Digital", TypeFactory.getPrimaryType(rec));
            }
        }
    }

    @Test
    public final void testGetPrimaryTypeVideo() {
        while (this.recordCollection.hasNext()) {
            Record rec = this.recordCollection.next();
            if ("10009616".equals(getRecordId(rec)) || "9952520".equals(getRecordId(rec))) {
                assertEquals("Video", TypeFactory.getPrimaryType(rec));
            }
        }
    }

    @Test
    public final void testGetPrimaryTypeOther() {
        while (this.recordCollection.hasNext()) {
            Record rec = this.recordCollection.next();
            if ("10763925".equals(getRecordId(rec))) {
                assertEquals("Other", TypeFactory.getPrimaryType(rec));
            }
        }
    }

    @Test
    public final void testGetPrimaryTypeAudio() {
        while (this.recordCollection.hasNext()) {
            Record rec = this.recordCollection.next();
            if ("10931045".equals(getRecordId(rec)) || "13112673".equals(getRecordId(rec))) {
                assertEquals("Audio", TypeFactory.getPrimaryType(rec));
            }
        }
    }

    @Test
    public final void testGetPrimaryTypeBookDigital() {
        while (this.recordCollection.hasNext()) {
            Record rec = this.recordCollection.next();
            if ("303511".equals(getRecordId(rec)) || "12467871".equals(getRecordId(rec))
                    || "342999".equals(getRecordId(rec)) || "353282".equals(getRecordId(rec))) {
                assertEquals("Book Digital", TypeFactory.getPrimaryType(rec));
            }
        }
    }
}
