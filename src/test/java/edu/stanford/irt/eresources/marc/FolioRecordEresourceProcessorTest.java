package edu.stanford.irt.eresources.marc;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import edu.stanford.irt.eresources.Eresource;
import edu.stanford.irt.eresources.EresourceHandler;
import edu.stanford.lane.catalog.FolioRecord;
import edu.stanford.lane.catalog.FolioRecordCollection;
import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.Record.Field;

public class FolioRecordEresourceProcessorTest {

    private EresourceHandler eresourceHandler;

    private FolioRecord folioRecord;

    private KeywordsStrategy keywordsStrategy;

    private HTTPLaneLocationsService locationsService;

    private Record marcRecord;

    private FolioRecordEresourceProcessor processor;

    private FolioRecordCollection recordCollection;

    private RecordCollectionFactory recordCollectionFactory;

    @Before
    public void setUp() throws Exception {
        this.eresourceHandler = mock(EresourceHandler.class);
        this.keywordsStrategy = mock(KeywordsStrategy.class);
        this.locationsService = mock(HTTPLaneLocationsService.class);
        this.recordCollectionFactory = mock(RecordCollectionFactory.class);
        this.recordCollection = mock(FolioRecordCollection.class);
        this.processor = new FolioRecordEresourceProcessor(this.eresourceHandler, this.keywordsStrategy,
                this.recordCollectionFactory, this.locationsService);
        this.marcRecord = mock(Record.class);
        this.folioRecord = mock(FolioRecord.class);
    }

    @Test
    public final void testProcessBib() {
        Field field = mock(Field.class);
        LocalDateTime ldt = LocalDateTime.now();
        this.processor.setStartDate(ldt);
        expect(this.recordCollectionFactory
                .newFolioRecordCollection(ldt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()))
                        .andReturn(this.recordCollection);
        expect(this.recordCollection.hasNext()).andReturn(true);
        expect(this.recordCollection.next()).andReturn(this.folioRecord);
        expect(this.folioRecord.getInstanceMarc()).andReturn(this.marcRecord).times(2);
        expect(this.folioRecord.getHoldingsMarc()).andReturn(Collections.emptyList());
        expect(this.marcRecord.getFields()).andReturn(Collections.singletonList(field));
        expect(field.getTag()).andReturn("249");
        expect(this.recordCollection.hasNext()).andReturn(true);
        expect(this.recordCollection.next()).andReturn(this.folioRecord);
        expect(this.folioRecord.getInstanceMarc()).andReturn(null);
        expect(this.folioRecord.getInstance()).andReturn(null);
        expect(this.recordCollection.hasNext()).andReturn(false);
        this.eresourceHandler.handleEresource(isA(Eresource.class));
        expectLastCall().times(2);
        replay(this.recordCollectionFactory, this.recordCollection, this.folioRecord, this.marcRecord, field,
                this.eresourceHandler);
        this.processor.process();
        verify(this.recordCollectionFactory, this.recordCollection, this.folioRecord, this.marcRecord, field,
                this.eresourceHandler);
    }
}
