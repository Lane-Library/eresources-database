package edu.stanford.irt.eresources.marc.sul;

import static org.easymock.EasyMock.mock;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import edu.stanford.irt.eresources.CatalogRecordService;
import edu.stanford.irt.eresources.FileCatalogRecordService;
import edu.stanford.irt.eresources.marc.MARCRecordSupport;
import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.Record.Field;
import edu.stanford.lane.catalog.Record.Subfield;
import edu.stanford.lane.catalog.RecordCollection;

public class AcceptableKeywordStrategyTest {

    private Field field;

    private InclusionStrategy inclusionStrategy;

    private Record marcRecord;

    private Subfield subfield;

    RecordCollection recordCollection;

    HashMap<String, Record> records = new HashMap<>();

    CatalogRecordService recordService;

    @BeforeEach
    public void setUp() throws Exception {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.initialize();
        this.recordService = new FileCatalogRecordService("src/test/resources/edu/stanford/irt/eresources/marc",
                executor);
        this.recordCollection = new RecordCollection(this.recordService.getRecordStream(0));
        while (this.recordCollection.hasNext()) {
            Record rec = this.recordCollection.next();
            this.records.put(MARCRecordSupport.getRecordId(rec), rec);
        }
        List<String> acceptableKeywords = Collections.singletonList("keyword");
        List<String> acceptablePrimaryTypes = Collections.singletonList("pType");
        this.inclusionStrategy = new AcceptableKeywordStrategy(acceptableKeywords, acceptablePrimaryTypes);
        this.marcRecord = mock(Record.class);
        this.field = mock(Field.class);
        this.subfield = mock(Subfield.class);
    }

    @Test
    public final void testIsAcceptableFiction() {
        assertFalse(this.inclusionStrategy.isAcceptable(this.records.get("8223791")));
    }

    @Test
    public final void testIsAcceptableInspectKeywordsFalse() {
        // should fail because has 050
        List<String> acceptableKeywords = Collections.singletonList("foo");
        List<String> acceptablePrimaryTypes = Collections.singletonList("Book Print");
        this.inclusionStrategy = new AcceptableKeywordStrategy(acceptableKeywords, acceptablePrimaryTypes);
        assertFalse(this.inclusionStrategy.isAcceptable(this.records.get("13117763")));
    }

    @Test
    public final void testIsAcceptableInspectKeywordsTrue() {
        List<String> acceptableKeywords = Collections.singletonList("mozambique");
        List<String> acceptablePrimaryTypes = Collections.singletonList("Other");
        this.inclusionStrategy = new AcceptableKeywordStrategy(acceptableKeywords, acceptablePrimaryTypes);
        assertTrue(this.inclusionStrategy.isAcceptable(this.records.get("10784454")));
        acceptableKeywords = Collections.singletonList("notmozambique");
        this.inclusionStrategy = new AcceptableKeywordStrategy(acceptableKeywords, acceptablePrimaryTypes);
        assertFalse(this.inclusionStrategy.isAcceptable(this.records.get("10784454")));
    }

    @Test
    public final void testIsAcceptableNLMCN() {
        Collections.singletonList("red");
        Collections.singletonList("Book Digital");
        // red book has 060 so should not be included
        assertFalse(this.inclusionStrategy.isAcceptable(this.records.get("23491")));
    }

    @Test
    public final void testIsAcceptableNotPrimaryType() {
        List<String> acceptableKeywords = Collections.singletonList("mozambique");
        List<String> acceptablePrimaryTypes = Collections.singletonList("non-existent primary type");
        this.inclusionStrategy = new AcceptableKeywordStrategy(acceptableKeywords, acceptablePrimaryTypes);
        assertFalse(this.inclusionStrategy.isAcceptable(this.records.get("10784454")));
    }
}
