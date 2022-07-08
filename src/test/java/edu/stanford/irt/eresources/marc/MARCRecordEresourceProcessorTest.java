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
import edu.stanford.irt.eresources.ItemService;
import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.Record.Field;
import edu.stanford.lane.catalog.RecordCollection;

public class MARCRecordEresourceProcessorTest {

    private EresourceHandler eresourceHandler;

    private ItemService itemService;

    private KeywordsStrategy keywordsStrategy;

    private HTTPLaneLocationsService locationsService;

    private Record marcRecord;

    private MARCRecordEresourceProcessor processor;

    private RecordCollection recordCollection;

    private RecordCollectionFactory recordCollectionFactory;

    private SulTypeFactory typeFactory;

    @Before
    public void setUp() throws Exception {
        this.eresourceHandler = mock(EresourceHandler.class);
        this.itemService = mock(ItemService.class);
        this.keywordsStrategy = mock(KeywordsStrategy.class);
        this.locationsService = mock(HTTPLaneLocationsService.class);
        this.recordCollectionFactory = mock(RecordCollectionFactory.class);
        this.recordCollection = mock(RecordCollection.class);
        this.typeFactory = mock(SulTypeFactory.class);
        this.processor = new MARCRecordEresourceProcessor(this.eresourceHandler, this.itemService,
                this.keywordsStrategy, this.recordCollectionFactory, this.typeFactory, this.locationsService);
        this.marcRecord = mock(Record.class);
    }

    @Test
    public final void testProcessBib() {
        Field field = mock(Field.class);
        LocalDateTime ldt = LocalDateTime.now();
        this.processor.setStartDate(ldt);
        expect(this.recordCollectionFactory
                .newRecordCollection(ldt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()))
                        .andReturn(this.recordCollection);
        expect(this.recordCollection.hasNext()).andReturn(true);
        expect(this.recordCollection.next()).andReturn(this.marcRecord);
        expect(this.recordCollection.hasNext()).andReturn(false);
        expect(this.marcRecord.getFields()).andReturn(Collections.singletonList(field));
        expect(field.getTag()).andReturn("249");
        expect(this.recordCollection.hasNext()).andReturn(false);
        this.eresourceHandler.handleEresource(isA(Eresource.class));
        expectLastCall().times(2);
        replay(this.recordCollectionFactory, this.recordCollection, this.marcRecord, field, this.eresourceHandler);
        this.processor.process();
        verify(this.recordCollectionFactory, this.recordCollection, this.marcRecord, field, this.eresourceHandler);
    }
}
