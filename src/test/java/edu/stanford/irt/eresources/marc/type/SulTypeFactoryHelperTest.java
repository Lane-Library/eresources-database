package edu.stanford.irt.eresources.marc.type;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import edu.stanford.irt.eresources.CatalogRecordService;
import edu.stanford.irt.eresources.FileCatalogRecordService;
import edu.stanford.irt.eresources.marc.MARCRecordSupport;
import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.RecordCollection;

class SulTypeFactoryHelperTest extends MARCRecordSupport {

    CatalogRecordService recordService;

    @BeforeEach
    void setUp() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.initialize();
        this.recordService = new FileCatalogRecordService("src/test/resources/edu/stanford/irt/eresources/marc/sul",
                executor);
    }

    @Test
    final void testGetJournalPeriodicalTypes() {
        RecordCollection rc = new RecordCollection(this.recordService.getRecordStream(0));
        while (rc.hasNext()) {
            Record rec = rc.next();
            if ("2996989".equals(getRecordId(rec)) || "4147163".equals(getRecordId(rec))
                    || "6682139".equals(getRecordId(rec)) || "8208799".equals(getRecordId(rec))
                    || "8422579".equals(getRecordId(rec)) || "8161047".equals(getRecordId(rec))
                    || "8547770".equals(getRecordId(rec))) {
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Journal/Periodical"));
            }
        }
    }

    @Test
    final void testGetDatabaseTypes() {
        RecordCollection rc = new RecordCollection(this.recordService.getRecordStream(0));
        while (rc.hasNext()) {
            Record rec = rc.next();
            if ("3480931".equals(getRecordId(rec)) || "4813250".equals(getRecordId(rec))
                    || "6632685".equals(getRecordId(rec)) || "6651616".equals(getRecordId(rec))) {
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Database"));
            }
            if ("10784454".equals(getRecordId(rec))) {
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Dataset"));
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Database"));
            }
        }
    }

    @Test
    final void testGetMapTypes() {
        RecordCollection rc = new RecordCollection(this.recordService.getRecordStream(0));
        while (rc.hasNext()) {
            Record rec = rc.next();
            if ("9671984".equals(getRecordId(rec))) {
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Map"));
            }
            if ("512334".equals(getRecordId(rec))) {
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Map"));
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Archive/Manuscript"));
            }
        }
    }

    @Test
    final void testGetArchiveManuscriptTypes() {
        RecordCollection rc = new RecordCollection(this.recordService.getRecordStream(0));
        while (rc.hasNext()) {
            Record rec = rc.next();
            if ("183932".equals(getRecordId(rec)) || "270082".equals(getRecordId(rec))
                    || "2906361".equals(getRecordId(rec))) {
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Archive/Manuscript"));
            }
            if ("270082".equals(getRecordId(rec))) {
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Music score"));
            }
        }
    }

    @Test
    final void testGetImageTypes() {
        RecordCollection rc = new RecordCollection(this.recordService.getRecordStream(0));
        while (rc.hasNext()) {
            Record rec = rc.next();
            if ("594073".equals(getRecordId(rec)) || "4668551".equals(getRecordId(rec))
                    || "4778244".equals(getRecordId(rec)) || "8695657".equals(getRecordId(rec))
                    || "10763925".equals(getRecordId(rec))) {
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Image"));
            }
        }
    }

    @Test
    final void testGetVideoTypes() {
        RecordCollection rc = new RecordCollection(this.recordService.getRecordStream(0));
        while (rc.hasNext()) {
            Record rec = rc.next();
            if ("157045".equals(getRecordId(rec)) || "3181716".equals(getRecordId(rec))
                    || "2669283".equals(getRecordId(rec)) || "12220301".equals(getRecordId(rec))) {
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Video"));
            }
        }
    }

    @Test
    final void testGetMusicRecordingTypes() {
        RecordCollection rc = new RecordCollection(this.recordService.getRecordStream(0));
        while (rc.hasNext()) {
            Record rec = rc.next();
            if ("12093927".equals(getRecordId(rec))) {
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Music recording"));
            }
        }
    }

    @Test
    final void testGetMusicScoreTypes() {
        RecordCollection rc = new RecordCollection(this.recordService.getRecordStream(0));
        while (rc.hasNext()) {
            Record rec = rc.next();
            if ("284727".equals(getRecordId(rec))) {
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Music score"));
            }
        }
    }

    @Test
    final void testGetBookTypes() {
        RecordCollection rc = new RecordCollection(this.recordService.getRecordStream(0));
        while (rc.hasNext()) {
            Record rec = rc.next();
            if ("355410".equals(getRecordId(rec)) || "593241".equals(getRecordId(rec))
                    || "5667068".equals(getRecordId(rec)) || "7139931".equals(getRecordId(rec))
                    || "7155675".equals(getRecordId(rec)) || "12283323".equals(getRecordId(rec))) {
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Book"));
            }
            if ("7811516".equals(getRecordId(rec))) {
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Book"));
                assertFalse(SulTypeFactoryHelper.getTypes(rec).contains("Archive/Manuscript"));
            }
        }
    }

    @Test
    final void testGetSoftwareMultimediaTypes() {
        RecordCollection rc = new RecordCollection(this.recordService.getRecordStream(0));
        while (rc.hasNext()) {
            Record rec = rc.next();
            if ("3491252".equals(getRecordId(rec)) || "3750867".equals(getRecordId(rec))) {
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Software/Multimedia"));
            }
        }
    }

    @Test
    final void testGetNewspaperTypes() {
        RecordCollection rc = new RecordCollection(this.recordService.getRecordStream(0));
        while (rc.hasNext()) {
            Record rec = rc.next();
            if ("12731088".equals(getRecordId(rec))) {
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Newspaper"));
            }
        }
    }

    @Test
    final void testGetObjectTypes() {
        RecordCollection rc = new RecordCollection(this.recordService.getRecordStream(0));
        while (rc.hasNext()) {
            Record rec = rc.next();
            if ("10009616".equals(getRecordId(rec))) {
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Object"));
            }
        }
    }

    @Test
    final void testGetSoundRecordingTypes() {
        RecordCollection rc = new RecordCollection(this.recordService.getRecordStream(0));
        while (rc.hasNext()) {
            Record rec = rc.next();
            if ("319407".equals(getRecordId(rec)) || "3262814".equals(getRecordId(rec))) {
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Sound recording"));
            }
        }
    }

    @Test
    final void testGetEmptyTypes() {
        RecordCollection rc = new RecordCollection(this.recordService.getRecordStream(0));
        while (rc.hasNext()) {
            Record rec = rc.next();
            if ("3943199".equals(getRecordId(rec))) {
                assertTrue(SulTypeFactoryHelper.getTypes(rec).isEmpty());
            }
        }
    }
}
