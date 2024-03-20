package edu.stanford.irt.eresources.marc.type;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

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
public class LaneTypeFactoryTest extends MARCRecordSupport {

    RecordCollection recordCollection;

    HashMap<String, Record> records = new HashMap<>();

    CatalogRecordService recordService;

    @Before
    public void setUp() throws Exception {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.initialize();
        this.recordService = new FileCatalogRecordService("src/test/resources/edu/stanford/irt/eresources/marc/lane",
                executor);
        this.recordCollection = new RecordCollection(this.recordService.getRecordStream(0));
        while (this.recordCollection.hasNext()) {
            Record rec = this.recordCollection.next();
            this.records.put(getRecordId(rec), rec);
        }
    }

    @Test
    public final void testGetPrimaryType() {
        assertEquals("Journal Digital", TypeFactory.getPrimaryType(this.records.get("55")));
        assertEquals("Image", TypeFactory.getPrimaryType(this.records.get("168269")));
        assertEquals("Software", TypeFactory.getPrimaryType(this.records.get("219590")));
        assertEquals("Article", TypeFactory.getPrimaryType(this.records.get("347355")));
        assertEquals("Video", TypeFactory.getPrimaryType(this.records.get("88090")));
        assertEquals("Other", TypeFactory.getPrimaryType(this.records.get("62326")));
        assertEquals("Book Print", TypeFactory.getPrimaryType(this.records.get("106317")));
        assertEquals("Book Print", TypeFactory.getPrimaryType(this.records.get("19354")));
        assertEquals("Other", TypeFactory.getPrimaryType(this.records.get("234974")));
        assertEquals("Database", TypeFactory.getPrimaryType(this.records.get("213409")));
        assertEquals("Book Digital", TypeFactory.getPrimaryType(this.records.get("23491")));
        assertEquals("Book Digital", TypeFactory.getPrimaryType(this.records.get("356482")));
    }

    @Test
    public final void testGetTypes() {
        assertTrue(TypeFactory.getTypes(this.records.get("55")).contains("Journal"));
        assertTrue(TypeFactory.getTypes(this.records.get("168269")).contains("Image"));
        assertTrue(TypeFactory.getTypes(this.records.get("287781")).contains("Article"));
        assertTrue(TypeFactory.getTypes(this.records.get("347355")).contains("Article"));
        assertTrue(TypeFactory.getTypes(this.records.get("88090")).contains("Video"));
        assertTrue(TypeFactory.getTypes(this.records.get("62326")).contains("Journal"));
        assertTrue(TypeFactory.getTypes(this.records.get("229725")).contains("Software"));
        assertTrue(TypeFactory.getTypes(this.records.get("143701")).contains("Software"));
        assertTrue(TypeFactory.getTypes(this.records.get("143701")).contains("Software"));
        assertTrue(TypeFactory.getTypes(this.records.get("219562")).contains("Software"));
        assertTrue(TypeFactory.getTypes(this.records.get("257445")).contains("Bassett"));
        assertTrue(TypeFactory.getTypes(this.records.get("234974")).isEmpty());
        assertTrue(TypeFactory.getTypes(this.records.get("213409")).contains("Journal"));
        assertTrue(TypeFactory.getTypes(this.records.get("356482")).contains("Book"));
        assertTrue(TypeFactory.getTypes(this.records.get("356482")).contains("Book Digital"));
    }
}
