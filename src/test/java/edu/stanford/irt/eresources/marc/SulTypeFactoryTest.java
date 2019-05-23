package edu.stanford.irt.eresources.marc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import edu.stanford.irt.eresources.CatalogRecordService;
import edu.stanford.irt.eresources.SulFileCatalogRecordService;
import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.RecordCollection;

public class SulTypeFactoryTest extends MARCRecordSupport {

    RecordCollection recordCollection;

    CatalogRecordService recordService;

    SulTypeFactory typefactory;

    @Before
    public void setUp() throws Exception {
        this.typefactory = new SulTypeFactory();
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.initialize();
        this.recordService = new SulFileCatalogRecordService("src/test/resources/edu/stanford/irt/eresources/marc",
                executor);
        this.recordCollection = new RecordCollection(this.recordService.getRecordStream(0));
    }

    @Test
    public final void testGetPrimaryType() {
        while (this.recordCollection.hasNext()) {
            Record rec = this.recordCollection.next();
            if (355410 == getRecordId(rec)) {
                assertEquals("Book Print", this.typefactory.getPrimaryType(rec));
            }
            if (2996989 == getRecordId(rec)) {
                assertEquals("Journal Print", this.typefactory.getPrimaryType(rec));
            }
            if (8161047 == getRecordId(rec)) {
                assertEquals("Journal Digital", this.typefactory.getPrimaryType(rec));
            }
            if (10009616 == getRecordId(rec)) {
                assertEquals("Other", this.typefactory.getPrimaryType(rec));
            }
            if (10763925 == getRecordId(rec)) {
                assertEquals("Other", this.typefactory.getPrimaryType(rec));
            }
            if (8208799 == getRecordId(rec)) {
                assertEquals("Journal Digital", this.typefactory.getPrimaryType(rec));
            }
            if (8223791 == getRecordId(rec)) {
                assertEquals("Book Print", this.typefactory.getPrimaryType(rec));
            }
        }
    }

    @Test
    public final void testGetTypes() {
        while (this.recordCollection.hasNext()) {
            Record rec = this.recordCollection.next();
            if (10784454 == getRecordId(rec)) {
                assertTrue(this.typefactory.getTypes(rec).contains("Statistics"));
            }
        }
    }
}
