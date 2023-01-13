package edu.stanford.irt.eresources.marc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import edu.stanford.irt.eresources.CatalogRecordService;
import edu.stanford.irt.eresources.SulFileCatalogRecordService;
import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.RecordCollection;

public class LaneTypeFactoryTest extends MARCRecordSupport {

    RecordCollection recordCollection;

    HashMap<String, Record> records = new HashMap<>();

    CatalogRecordService recordService;

    LaneTypeFactory typefactory;

    @Before
    public void setUp() throws Exception {
        this.typefactory = new LaneTypeFactory();
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.initialize();
        this.recordService = new SulFileCatalogRecordService("src/test/resources/edu/stanford/irt/eresources/marc/lane",
                executor);
        this.recordCollection = new RecordCollection(this.recordService.getRecordStream(0));
        while (this.recordCollection.hasNext()) {
            Record rec = this.recordCollection.next();
            this.records.put(getRecordId(rec), rec);
        }
    }

    @Test
    public final void testGetPrimaryType() {
        assertEquals("Journal Digital", this.typefactory.getPrimaryType(this.records.get("55")));
        assertEquals("Image", this.typefactory.getPrimaryType(this.records.get("168269")));
        assertEquals("Software", this.typefactory.getPrimaryType(this.records.get("219590")));
        assertEquals("Article", this.typefactory.getPrimaryType(this.records.get("347355")));
        assertEquals("Video", this.typefactory.getPrimaryType(this.records.get("88090")));
        assertEquals("Other", this.typefactory.getPrimaryType(this.records.get("62326")));
        assertEquals("Book Print", this.typefactory.getPrimaryType(this.records.get("106317")));
        assertEquals("Book Print", this.typefactory.getPrimaryType(this.records.get("19354")));
        assertEquals("Other", this.typefactory.getPrimaryType(this.records.get("234974")));
        assertEquals("Database", this.typefactory.getPrimaryType(this.records.get("213409")));
        assertEquals("Book Digital", this.typefactory.getPrimaryType(this.records.get("23491")));
        assertEquals("Equipment", this.typefactory.getPrimaryType(this.records.get("357935")));
    }

    @Test
    public final void testGetTypes() {
        assertTrue(this.typefactory.getTypes(this.records.get("55")).contains("Journal"));
        assertTrue(this.typefactory.getTypes(this.records.get("168269")).contains("Image"));
        assertTrue(this.typefactory.getTypes(this.records.get("287781")).contains("Article"));
        assertTrue(this.typefactory.getTypes(this.records.get("347355")).contains("Article"));
        assertTrue(this.typefactory.getTypes(this.records.get("88090")).contains("Video"));
        assertTrue(this.typefactory.getTypes(this.records.get("62326")).contains("Journal"));
        assertTrue(this.typefactory.getTypes(this.records.get("229725")).contains("Software"));
        assertTrue(this.typefactory.getTypes(this.records.get("143701")).contains("Software"));
        assertTrue(this.typefactory.getTypes(this.records.get("143701")).contains("Software"));
        assertTrue(this.typefactory.getTypes(this.records.get("219562")).contains("Software"));
        assertTrue(this.typefactory.getTypes(this.records.get("292351")).contains("Grand Rounds"));
        assertTrue(this.typefactory.getTypes(this.records.get("257445")).contains("Bassett"));
        assertTrue(this.typefactory.getTypes(this.records.get("234974")).isEmpty());
        assertTrue(this.typefactory.getTypes(this.records.get("213409")).contains("Journal"));
        assertTrue(this.typefactory.getTypes(this.records.get("357935")).contains("Equipment"));
    }
}
