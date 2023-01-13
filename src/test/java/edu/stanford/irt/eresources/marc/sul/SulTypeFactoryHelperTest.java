package edu.stanford.irt.eresources.marc.sul;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import edu.stanford.irt.eresources.CatalogRecordService;
import edu.stanford.irt.eresources.SulFileCatalogRecordService;
import edu.stanford.irt.eresources.marc.MARCRecordSupport;
import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.RecordCollection;

public class SulTypeFactoryHelperTest extends MARCRecordSupport {

    CatalogRecordService recordService;

    @Before
    public void setUp() throws Exception {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.initialize();
        this.recordService = new SulFileCatalogRecordService("src/test/resources/edu/stanford/irt/eresources/marc/sul",
                executor);
    }

    @Test
    public final void testGetTypes() {
        RecordCollection rc = new RecordCollection(this.recordService.getRecordStream(0));
        while (rc.hasNext()) {
            Record rec = rc.next();
            if ("2996989".equals(getRecordId(rec))) {
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Journal/Periodical"));
            }
            if ("4147163".equals(getRecordId(rec))) {
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Journal/Periodical"));
            }
            if ("6682139".equals(getRecordId(rec))) {
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Journal/Periodical"));
            }
            if ("8208799".equals(getRecordId(rec))) {
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Journal/Periodical"));
            }
            if ("8422579".equals(getRecordId(rec))) {
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Journal/Periodical"));
            }
            if ("8161047".equals(getRecordId(rec))) {
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Journal/Periodical"));
            }
            if ("8547770".equals(getRecordId(rec))) {
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Journal/Periodical"));
            }
            if ("3480931".equals(getRecordId(rec))) {
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Database"));
            }
            if ("4813250".equals(getRecordId(rec))) {
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Database"));
            }
            if ("6632685".equals(getRecordId(rec))) {
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Database"));
            }
            if ("6651616".equals(getRecordId(rec))) {
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Database"));
            }
            if ("10784454".equals(getRecordId(rec))) {
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Dataset"));
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Database"));
            }
            if ("9671984".equals(getRecordId(rec))) {
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Map"));
            }
            if ("512334".equals(getRecordId(rec))) {
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Map"));
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Archive/Manuscript"));
            }
            if ("183932".equals(getRecordId(rec))) {
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Archive/Manuscript"));
            }
            if ("270082".equals(getRecordId(rec))) {
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Archive/Manuscript"));
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Music score"));
            }
            if ("2906361".equals(getRecordId(rec))) {
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Archive/Manuscript"));
            }
            if ("594073".equals(getRecordId(rec))) {
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Image"));
            }
            if ("4668551".equals(getRecordId(rec))) {
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Image"));
            }
            if ("4778244".equals(getRecordId(rec))) {
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Image"));
            }
            if ("8695657".equals(getRecordId(rec))) {
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Image"));
            }
            if ("10763925".equals(getRecordId(rec))) {
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Image"));
            }
            if ("157045".equals(getRecordId(rec))) {
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Video"));
            }
            if ("3181716".equals(getRecordId(rec))) {
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Video"));
            }
            if ("2669283".equals(getRecordId(rec))) {
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Video"));
            }
            if ("12220301".equals(getRecordId(rec))) {
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Video"));
            }
            if ("12093927".equals(getRecordId(rec))) {
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Music recording"));
            }
            if ("284727".equals(getRecordId(rec))) {
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Music score"));
            }
            if ("355410".equals(getRecordId(rec))) {
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Book"));
            }
            if ("593241".equals(getRecordId(rec))) {
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Book"));
            }
            if ("5667068".equals(getRecordId(rec))) {
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Book"));
            }
            if ("7139931".equals(getRecordId(rec))) {
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Book"));
            }
            if ("7155675".equals(getRecordId(rec))) {
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Book"));
            }
            if ("12283323".equals(getRecordId(rec))) {
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Book"));
            }
            if ("7811516".equals(getRecordId(rec))) {
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Book"));
                assertFalse(SulTypeFactoryHelper.getTypes(rec).contains("Archive/Manuscript"));
            }
            if ("3491252".equals(getRecordId(rec))) {
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Software/Multimedia"));
            }
            if ("3750867".equals(getRecordId(rec))) {
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Software/Multimedia"));
            }
            if ("12731088".equals(getRecordId(rec))) {
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Newspaper"));
            }
            if ("10009616".equals(getRecordId(rec))) {
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Object"));
            }
            if ("319407".equals(getRecordId(rec))) {
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Sound recording"));
            }
            if ("3262814".equals(getRecordId(rec))) {
                assertTrue(SulTypeFactoryHelper.getTypes(rec).contains("Sound recording"));
            }
            if ("3943199".equals(getRecordId(rec))) {
                assertTrue(SulTypeFactoryHelper.getTypes(rec).isEmpty());
            }
        }
    }
}
